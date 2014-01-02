package zhenghui.lsf.rpc.mina;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.mina.service.MinaInvokeService;
import zhenghui.lsf.rpc.mina.service.MinaProviderServer;
import zhenghui.lsf.rpc.service.RPCProtocolService;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 上午11:21
 * 使用Mina2.0实现 服务.
 */
public class MinaRPCProtocolService implements RPCProtocolService {

    private final Object onlyOnceSync = new Object();

    private MinaProviderServer minaProviderServer;

    private MinaInvokeService minaInvokeService;

    @Override
    public void registerProvider(ServiceMetadata metadata) throws LSFException {
        synchronized (onlyOnceSync){
            if(!minaProviderServer.isStarted()){
                minaProviderServer.startServer();
            }
        }
        minaProviderServer.addMetadata(metadata.getUniqueName(), metadata);
        minaProviderServer.addWorker(metadata.getUniqueName(), metadata.getTarget());
    }

    @Override
    public Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURL) throws LSFException {
        return minaInvokeService.invoke(request,metadata,targetURL);
    }

    public void setMinaProviderServer(MinaProviderServer minaProviderServer) {
        this.minaProviderServer = minaProviderServer;
    }

    public void setMinaInvokeService(MinaInvokeService minaInvokeService) {
        this.minaInvokeService = minaInvokeService;
    }
}
