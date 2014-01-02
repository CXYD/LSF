package zhenghui.lsf.rpc.mina.service.impl;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.domain.HSFResponse;
import zhenghui.lsf.mina.client.Client;
import zhenghui.lsf.mina.client.impl.DefaultClient;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午3:52
 * 这个代码有点恶心.哈哈
 */
public class LSFMinaInvokeHandler extends IoHandlerAdapter{

    private Logger logger = LoggerFactory.getLogger(LSFMinaInvokeHandler.class);

    /**
     * 如果接受服务抛异常,那么返回该对象
     */
    private static final Object obj = new Object();

    // 当服务器发送的消息到达时:
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        HSFResponse response = (HSFResponse) message;
        if (response.isError()) {
            logger.error("调用异常." + response.getErrorMsg());
            getClient(session).putResponse(obj);
        } else {
            getClient(session).putResponse(response.getAppResponse());
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        getClient(session).putResponse(obj);
        throw new Exception(cause);
    }

    private Client getClient(IoSession session){
        return (Client) session.getAttribute(DefaultClient.SESSION_2_CLIENT);
    }

}
