import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author 10701
 * TODO:curator分布式锁测试
 */
public class CuratorTest {
    private static final String lockPath = "/cu_lock";
    private static int SESSION_TIMEOUT = 5000;
    private static int CONNECTION_TIMEOUT = 5000;
    private static String CONNECT_SERVER = "localhost:2181";

    public static void doWithLock(CuratorFramework zkClient) throws Exception {
        System.out.println(Thread.currentThread().getName() + "尝试获取锁");
        // 实例化 zk分布式锁
        InterProcessMutex lock = new InterProcessMutex(zkClient, lockPath);
        if (lock.acquire(5, TimeUnit.SECONDS)){
            System.out.println(Thread.currentThread().getName() + "获取到了锁");
            Thread.sleep(1000);
        }
        lock.release();
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(5);
        for (int i = 0; i < 5; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        semaphore.acquire();
                        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(CONNECT_SERVER, SESSION_TIMEOUT, CONNECTION_TIMEOUT, new RetryNTimes(10, 5000));
                        zkClient.start();
                        doWithLock(zkClient);
                        semaphore.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            executorService.execute(runnable);
        }
        executorService.shutdown();
    }
}
