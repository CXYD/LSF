package zhenghui.lsf.rpc.mina.service.impl;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.mina.client.Client;
import zhenghui.lsf.mina.client.ClientManager;
import zhenghui.lsf.rpc.mina.service.MinaInvokeService;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午3:48
 */
public class MinaInvokeServiceImpl implements MinaInvokeService {

    private static final long TIME_OUT_MS = 3000;

    @Override
    public Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURL) throws LSFException {
        try{
            Client client = ClientManager.getInstance().getClient(targetURL);
            return client.invoke(request,TIME_OUT_MS);
        } catch (Exception e){
            throw new LSFException("zhenghui.lsf.rpc.mina.service.impl.MinaInvokeServiceImpl.invoke error",e);
        }
    }

}
