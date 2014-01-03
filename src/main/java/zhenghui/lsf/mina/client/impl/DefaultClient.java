package zhenghui.lsf.mina.client.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.mina.client.Client;
import zhenghui.lsf.mina.client.response.ResponseFuture;

import java.util.concurrent.TimeUnit;

/**
 * User: zhenghui
 * Date: 13-12-29
 * Time: 下午8:00
 */
public class DefaultClient implements Client {

    private Logger logger = LoggerFactory.getLogger(DefaultClient.class);

    static public final String SESSION_2_CLIENT = "SESSION_2_CLIENT";

    public DefaultClient(IoSession ioSession) {
        this.ioSession = ioSession;
        ioSession.setAttribute(SESSION_2_CLIENT,this);
    }

    private IoSession ioSession;

//    private ConcurrentHashMap<String,ResponseFuture> futureStore = new ConcurrentHashMap<String,ResponseFuture>();

    /**
     * 默认是3s超时,这里默认5sfuture过期
     */
    Cache<String,ResponseFuture> futureStore = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(5, TimeUnit.SECONDS).build();

    @Override
    public Object invoke(HSFRequest request, long timeoutms) {
        ResponseFuture future = new ResponseFuture();
        futureStore.put(request.getRequestId(), future);
        if(request.getMethodArgs()[0].equals("shaoman")){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        ioSession.write(request);
        try {
            return future.get(timeoutms);
        } catch (Exception e) {
            logger.error("执行方法失败~",e);
            return null;
        } finally {
            futureStore.invalidate(request.getRequestId());
        }
    }

    @Override
    public void putResponse(String requestId,Object response) {
        ResponseFuture future = futureStore.getIfPresent(requestId);
        if(future == null){
            logger.error("执行方法超时");
            return;
        }
        future.setResponse(response);
    }
}
