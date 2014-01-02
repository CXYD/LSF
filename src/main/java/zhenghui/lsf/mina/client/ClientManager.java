package zhenghui.lsf.mina.client;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.exception.LSFException;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * User: zhenghui
 * Date: 13-12-29
 * Time: 下午8:14
 * 通过apache的commons-pool来管理Client对象.
 * 如果不使用pool的话,可以直接使用 zhenghui.lsf.mina.client.ClientFactory#createClient 方法
 */
public class ClientManager {

    private Logger logger = LoggerFactory.getLogger(ClientManager.class);

    private static final String PREFIX = "_LSF";

    private static final ClientManager clientManager = new ClientManager();

    /**
     * 缓存 GenericObjectPool 对象
     */
    private ConcurrentHashMap<String, FutureTask<GenericObjectPool>> initRightSelect = new ConcurrentHashMap<String, FutureTask<GenericObjectPool>>();

    final private ClientFactory factory = new ClientFactory();

    private ClientManager() {
    }

    public static ClientManager getInstance(){
        return clientManager;
    }

    public Client getClient(final String targetUrl) throws LSFException{
        Client client = null;
        try{
            String key = PREFIX + targetUrl;

            GenericObjectPool genericClientPool;
            Future<GenericObjectPool> future = initRightSelect.get(key);
            if(future == null){
                FutureTask<GenericObjectPool> futureTask = new FutureTask<GenericObjectPool>(new Callable<GenericObjectPool>() {
                    @Override
                    public GenericObjectPool call() throws Exception {
                        return new GenericObjectPool(new PoolableClientFactory(factory,targetUrl));
                    }
                });
                FutureTask<GenericObjectPool> old = initRightSelect.putIfAbsent(key, futureTask);
                if(old == null){
                    futureTask.run();
                    genericClientPool = futureTask.get();
                } else {
                    genericClientPool = old.get();
                }
            } else {
                genericClientPool = future.get();
            }
            synchronized (key){
                client = (Client) genericClientPool.borrowObject();
                genericClientPool.returnObject(client);
            }
        } catch (Exception e){
            logger.error("zhenghui.lsf.mina.client.ClientManager.getClient error",e);
        }
        return client;
    }

    class PoolableClientFactory implements PoolableObjectFactory{

        private ClientFactory factory;

        private String targetUrl;

        PoolableClientFactory(ClientFactory factory, String targetUrl) {
            this.factory = factory;
            this.targetUrl = targetUrl;
        }

        @Override
        public Object makeObject() throws Exception {
            return factory.createClient(targetUrl);
        }

        @Override
        public void destroyObject(Object obj) throws Exception {
            factory.destroyClient();
        }

        @Override
        public boolean validateObject(Object obj) {
            return true;
        }

        @Override
        public void activateObject(Object obj) throws Exception {
        }

        @Override
        public void passivateObject(Object obj) throws Exception {
        }
    }
}
