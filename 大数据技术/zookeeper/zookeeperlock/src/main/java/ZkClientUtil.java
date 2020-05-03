import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author 10701
 * curator连接工具类
 */
public class ZkClientUtil {
    private static final int SLEEP_TIME_MS = 5000;
    private static final int MAX_RETRIES = 3;
    private static final int SESSION_TIME_OUT = 10000;
    private static final String SERVER_URI = "localhost:2181";
    public static CuratorFramework build(){
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(SLEEP_TIME_MS, MAX_RETRIES);
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString(SERVER_URI)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(SESSION_TIME_OUT)
                .build();
        return client;
    }
}
