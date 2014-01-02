package zhenghui.lsf.mina.client;

import zhenghui.lsf.domain.HSFRequest;

/**
 * User: zhenghui
 * Date: 13-12-29
 * Time: 下午7:58
 * 觊觎mina的一个执行器.
 */
public interface Client {

    /**
     * 同步调用请求.
     */
    Object invoke(HSFRequest request,long timeoutms);


    /**
     * 回调的时候,设置返回
     * @param response
     */
    public void putResponse(Object response);


}
