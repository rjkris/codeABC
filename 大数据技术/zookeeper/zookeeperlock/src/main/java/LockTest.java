import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @author 10701
 * DistributedLock测试程序，需要开多个main程序去竞争锁
 */
public class LockTest {
    private void doWithLock() throws InterruptedException, IOException, KeeperException {
        DistributedLock distributedLock = new DistributedLock();
        distributedLock.acquireLock();
        int sleepTime = (int)(Math.random()*1000);
        System.out.println("执行结束");
        Thread.sleep(sleepTime);
        distributedLock.releaseLock();
    }

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        LockTest lockTest = new LockTest();
        for (int i = 0; i < 10; i++) {
            lockTest.doWithLock();
        }
    }
}
