import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author 10701
 * zookeeper原生接口实现分布式锁
 */
public class DistributedLock {
    private final ZooKeeper zkClient;
    private static final String LOCK_PATH = "/lock";
    private static final String NODE_NAME = "/node";
    private String curLockPath;

    /**
     * 监听前节点的watcher
     */
    private final Watcher watcher = new Watcher() {
        public void process(WatchedEvent watchedEvent) {
            synchronized (this){
                notifyAll();
            }
        }
    };
    public DistributedLock() throws IOException {
        zkClient = new ZooKeeper("localhost:2181", 5000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("连接断开");
                }
            }
        });
    }

    public void acquireLock() throws KeeperException, InterruptedException {
        createLock();
        attemptLock();
    }

    public void createLock() throws KeeperException, InterruptedException {
        // 若lock根节点不存在，则先创建根节点，在其下创建临时顺序节点
        Stat stat = zkClient.exists(LOCK_PATH, false);
        if (stat == null){
            zkClient.create(LOCK_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String lockPath = zkClient.create(LOCK_PATH+NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(Thread.currentThread().getName() + "创建锁" + lockPath);
        this.curLockPath = lockPath;
    }

    public void attemptLock() throws KeeperException, InterruptedException {
        List<String> lockPaths = zkClient.getChildren(LOCK_PATH, false);
        Collections.sort(lockPaths);
        int index = lockPaths.indexOf(this.curLockPath.substring(LOCK_PATH.length()+1));
        // 如果curLockPath是序号最小的临时节点，获得锁
        if (index == 0){
            System.out.println(Thread.currentThread().getName() + "获得锁" + curLockPath);
        }
        // 监听前一个节点
        else{
            Stat stat = zkClient.exists(LOCK_PATH + "/" + lockPaths.get(index-1), watcher);
            if (stat != null) {
                System.out.println(Thread.currentThread().getName() + "正在等待前节点释放锁");
                synchronized (watcher) {
                    watcher.wait();
                }
            }
            attemptLock();
        }

    }

    public void releaseLock() throws KeeperException, InterruptedException {
        zkClient.delete(curLockPath, -1);
        zkClient.close();
        System.out.println(Thread.currentThread().getName() + "释放锁");
    }



}
