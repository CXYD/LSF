package zhenghui.lsf.mina.client.impl;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.mina.client.Client;
import zhenghui.lsf.mina.client.response.ResponseFuture;

/**
 * User: zhenghui
 * Date: 13-12-29
 * Time: 下午8:00
 */
public class DefaultClient implements Client {

    private Logger logger = LoggerFactory.getLogger(DefaultClient.class);

    static public final String SESSION_2_CLIENT = "SESSION_2_CLIENT";

    private ResponseFuture responseFuture;

    public DefaultClient(IoSession ioSession) {
        this.ioSession = ioSession;
        ioSession.setAttribute(SESSION_2_CLIENT,this);
    }

    private IoSession ioSession;

    /**
     * 这里需要优化.针对同一个接口的同一个方法,只能同步顺序执行
     * @return
     */
    @Override
    public synchronized Object invoke(HSFRequest request, long timeoutms) {
        responseFuture = new ResponseFuture();
        ioSession.write(request);
        try {
            return responseFuture.get(timeoutms);
        } catch (Exception e) {
            logger.error("执行方法失败~",e);
            return null;
        }
    }

    @Override
    public void putResponse(Object response) {
        responseFuture.setResponse(response);
    }
}
