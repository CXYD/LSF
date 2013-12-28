package zhenghui.lsf.rpc.mina.service.impl;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.datastore.service.DataStoreService;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.mina.service.MinaProviderServer;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午1:36
 * 这里用的是最原始的mina服务.没有任何封装.
 *
 */
public class MinaProviderServerImpl implements MinaProviderServer {

    private Logger logger = LoggerFactory.getLogger(MinaProviderServerImpl.class);

    private AtomicBoolean isStart = new AtomicBoolean(false);

    private DataStoreService dataStoreService;

    private LSFMinaServerHandler lsfMinaServerHandler;

    static public  final String COMPONENT_NAME=MinaProviderServerImpl.class.getName();
    static public final String METADATAS_STORE_KEY="_metadatas";
    static public final String WORKS_STORE_KEY="_works";
    static public  final String WORKERMETHODS_STORE_KEY="_workermethods";

    @Override
    public boolean isStarted() {
        return isStart.get();
    }

    @Override
    public void startServer() throws LSFException{
        if(!isStart.compareAndSet(false,true)){
            return;
        }

        try{
            IoAcceptor acceptor = new NioSocketAcceptor();
            // 创建接收数据的过滤器
            DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
            // 序列化方式就用默认的
            ProtocolCodecFilter filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
            chain.addLast("objectFilter", filter);
            // 设定服务器端的消息处理器:一个ObjectMinaServerHandler对象,
            acceptor.setHandler(lsfMinaServerHandler);

            // 绑定端口,启动服务器
            acceptor.bind(new InetSocketAddress(SERVER_PORT));
        }catch (Exception e){
            throw new LSFException("启动服务出错.",e);
        }
    }

    @Override
    public void addWorker(String srvID, Object workerPOJO) {
        if (null == workerPOJO) {
            throw new IllegalArgumentException("服务[" + srvID + "]的Target为NULL.");
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
        Map<String/*SrvID*/, Map<String/*MethodSig*/, Method>> workerMethods = dataStoreService.get(COMPONENT_NAME, WORKERMETHODS_STORE_KEY);
        if(workerMethods==null){
            workerMethods=new HashMap<String, Map<String,Method>>();
        }
        workerMethods.put(srvID, publicMethods);
        workers.put(srvID, workerPOJO);

        dataStoreService.put(COMPONENT_NAME, WORKS_STORE_KEY, workers);
        dataStoreService.put(COMPONENT_NAME, WORKERMETHODS_STORE_KEY, workerMethods);
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

    public void setDataStoreService(DataStoreService dataStoreService) {
        this.dataStoreService = dataStoreService;
    }

    public void setLsfMinaServerHandler(LSFMinaServerHandler lsfMinaServerHandler) {
        this.lsfMinaServerHandler = lsfMinaServerHandler;
    }
}
