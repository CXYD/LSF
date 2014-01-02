package zhenghui.lsf.mina.client.response;

import zhenghui.lsf.exception.LSFException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * User: zhenghui
 * Date: 13-12-31
 * Time: 下午5:28
 */
public class ResponseFuture {

    private volatile boolean isDone = false;

    private Object response;

    private CountDownLatch latch = new CountDownLatch(1);

//    public Object get() throws LSFException {
//        try{
//            synchronized (this) {
//                while (!isDone) {
//                    wait();
//                }
//            }
//            return response;
//        }catch (Exception e){
//            throw new LSFException("ResponseFuture.get error",e);
//        }
//    }

    public Object get(long timeoutMs) throws LSFException{
        try {
            if (timeoutMs <= 0) {
                latch.await();
            }else if (timeoutMs > 0) {
                latch.await(timeoutMs, TimeUnit.MILLISECONDS);
            }

            if (!isDone) {
                return new Object();
            }
            return response;
        }catch (Exception e){
            throw new LSFException("ResponseFuture.get error",e);
        }
    }

    public void setResponse(Object response){
        this.response = response;
        isDone = true;
        latch.countDown();
    }
}
