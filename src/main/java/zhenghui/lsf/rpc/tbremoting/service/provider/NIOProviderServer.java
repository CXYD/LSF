package zhenghui.lsf.rpc.tbremoting.service.provider;

import com.taobao.remoting.RemotingException;
import com.taobao.remoting.RequestProcessor;
import com.taobao.remoting.Server;
import com.taobao.remoting.impl.DefaultServer;
import com.taobao.remoting.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.datastore.service.DataStoreService;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.domain.HSFResponse;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.tbremoting.service.ProviderServer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 上午11:49
 */
public class NIOProviderServer implements ProviderServer {

    private Logger logger = LoggerFactory.getLogger(NIOProviderServer.class);

    static private final long DEFAULT_THREAD_ALIVE_TIME = 300L;

    private static final int HSF_SERVER_MIN_POOL_SIZE = 1;
    private static final int HSF_SERVER_MAX_POOL_SIZE = 1;

    static public final String THREADPOOL_STORE_KEY="_threadpool";

    private static final int HSF_SERVER_PORT = 9999;

    private DataStoreService dataStoreService;

    static public  final String COMPONENT_NAME=NIOProviderServer.class.getName();
    static public final String METADATAS_STORE_KEY="_metadatas";
    static public final String WORKS_STORE_KEY="_works";
    static public  final String WORKERMETHODS_STORE_KEY="_workermethods";

    private RequestProcessor<HSFRequest> processor;

    @Override
    public void startHSFServer() throws HSFException {

        try {
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                    HSF_SERVER_MIN_POOL_SIZE,
                    HSF_SERVER_MAX_POOL_SIZE,
                    DEFAULT_THREAD_ALIVE_TIME, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    new NamedThreadFactory("HSFBizProcessor"),
                    new ThreadPoolExecutor.AbortPolicy()
            );
            Server server = new DefaultServer(HSF_SERVER_PORT);
            server.start();
            server.registerProcessor(processor);
//            dataStoreService.put(COMPONENT_NAME, THREADPOOL_STORE_KEY, threadPool);
//            dataStoreService.put(COMPONENT_NAME, SERVER_STORE_KEY, server);
        } catch (Exception e) {
            throw new HSFException("启动HSF服务器端错误", e);
        }

    }

    @Override
    public void stopHSFServer() throws HSFException {

    }

    @Override
    public void addWorker(String srvID, Object workerPOJO) {
        if (null == workerPOJO) {
            illArgsException("服务[" + srvID + "]的Target为NULL.");
        }
        Map<String/*SrvID*/, Object/*SrvPOJO*/> workers = dataStoreService.get(COMPONENT_NAME, WORKS_STORE_KEY);
        if(workers == null){
            workers = new HashMap<String, Object>();
        }
        if (workers.containsKey(srvID)) {
            logger.error("已注册了名称为：" + srvID + "的处理对象");
            return;
        }

        // 分析该POJO的所有公开方法
        Map<String, Method> publicMethods = new HashMap<String, Method>();
        for (Method m : workerPOJO.getClass().getMethods()) {
            StringBuilder mSigs = new StringBuilder();
            mSigs.append(m.getName());
            for (Class<?> paramType : m.getParameterTypes()) {
                mSigs.append(paramType.getName());
            }
            publicMethods.put(mSigs.toString(), m);
        }
        Map<String/*SrvID*/, Map<String/*MethodSig*/, Method>> workerMethods = (Map<String, Map<String, Method>>) dataStoreService.get(COMPONENT_NAME, WORKERMETHODS_STORE_KEY);
        if(workerMethods==null){
            workerMethods=new HashMap<String, Map<String,Method>>();
        }
        workerMethods.put(srvID, publicMethods);
        workers.put(srvID, workerPOJO);

        dataStoreService.put(COMPONENT_NAME, WORKS_STORE_KEY, workers);
        dataStoreService.put(COMPONENT_NAME, WORKERMETHODS_STORE_KEY, workerMethods);
    }

    @Override
    public Object getWorker(String serviceName) {
        Map<String/*SrvID*/, Object/*SrvPOJO*/> workers = (Map<String, Object>) dataStoreService.get(COMPONENT_NAME, WORKS_STORE_KEY);
        if(workers==null){
            return null;
        }
        return workers.get(serviceName);
    }

    @Override
    public void removeWorker(String workerName) {
        Map<String/*SrvID*/, Object/*SrvPOJO*/> workers = dataStoreService.get(COMPONENT_NAME, WORKS_STORE_KEY);
        if(workers!=null){
            workers.remove(workerName);
        }
    }

    @Override
    public void setMetadatas(Map<String, ServiceMetadata> metadatas) {
        dataStoreService.put(COMPONENT_NAME, METADATAS_STORE_KEY, metadatas);
    }

    @Override
    public void addMetadata(String key, ServiceMetadata metadata) {
        Map<String, ServiceMetadata> metadatas=dataStoreService.get(COMPONENT_NAME, METADATAS_STORE_KEY);
        if(metadatas == null){
            metadatas = new HashMap<String, ServiceMetadata>();
        }
        metadatas.put(key, metadata);
        dataStoreService.put(COMPONENT_NAME, METADATAS_STORE_KEY, metadatas);
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public HSFResponse handleRequest(HSFRequest request) throws HSFException {
        return null;
    }

    public void setDataStoreService(DataStoreService dataStoreService) {
        this.dataStoreService = dataStoreService;
    }

    public void setProcessor(RequestProcessor<HSFRequest> processor) {
        this.processor = processor;
    }

    private void illArgsException(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
