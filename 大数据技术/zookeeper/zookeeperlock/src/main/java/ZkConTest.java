import org.apache.zookeeper.*;

/**
 * @author 10701
 * zk连接测试
 */
public class ZkConTest {
    private static ZooKeeper zkClient;
    private static String LOCK_PATH = "/lock";
    public ZkConTest() throws Exception{
        zkClient = new ZooKeeper("localhost:2181", 10000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState()==Event.KeeperState.Disconnected){
                    System.out.println("断开连接");
                }
            }

        });
    }

    public static void main(String[] args) throws Exception {
        ZkConTest zkConTest = new ZkConTest();
        zkClient.create(LOCK_PATH+"/n1",new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }
}
