import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 10701
 * 多线程去测试
 */
public class Test {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        int times = 3;
        for (int i = 0; i < times; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        doWithLock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread thread = new Thread(runnable, "Thread"+i);
            thread.start();
        }
    }

    public static void doWithLock() throws IOException, InterruptedException, KeeperException {
        DistributedLock distributedLock = new DistributedLock();
        distributedLock.acquireLock();
        Thread.sleep(2000);
        distributedLock.releaseLock();
    }
}

