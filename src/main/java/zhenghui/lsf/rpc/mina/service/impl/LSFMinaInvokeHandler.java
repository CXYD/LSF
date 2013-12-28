package zhenghui.lsf.rpc.mina.service.impl;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.domain.HSFResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午3:52
 * 这个代码有点恶心.哈哈
 */
public class LSFMinaInvokeHandler extends IoHandlerAdapter implements Future<Object> {

    private Logger logger = LoggerFactory.getLogger(LSFMinaInvokeHandler.class);

    private AtomicBoolean isDone = new AtomicBoolean(false);

    private Object retrunObj;

    /**
     * 如果接受服务抛异常,那么返回该对象
     */
    private static final Object obj = new Object();

    // 当服务器发送的消息到达时:
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (isDone.get()) {
            return;
        }
        HSFResponse response = (HSFResponse) message;
        if (response.isError()) {
            logger.error("调用异常." + response.getErrorMsg());
            retrunObj = obj;
        } else {
            retrunObj = response.getAppResponse();
        }
        isDone.set(Boolean.TRUE);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        isDone.set(Boolean.TRUE);
        retrunObj = obj;
        logger.error("LSFMinaInvokeHandler error", cause);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone.get();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        while (!isDone()) {
        }
        return retrunObj;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

        return null;
    }
}
