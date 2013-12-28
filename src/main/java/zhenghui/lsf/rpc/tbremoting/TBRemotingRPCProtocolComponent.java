package zhenghui.lsf.rpc.tbremoting;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.service.RPCProtocolService;
import zhenghui.lsf.rpc.tbremoting.service.InvokeService;
import zhenghui.lsf.rpc.tbremoting.service.ProviderServer;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 上午11:28
 */
public class TBRemotingRPCProtocolComponent implements RPCProtocolService {

    private final Object onlyOnceSync = new Object();

    private ProviderServer providerServer;

    private InvokeService invokeService;

    @Override
    public void registerProvider(ServiceMetadata metadata) throws LSFException {
        // 仅启动一次HSF SERVER
        synchronized (onlyOnceSync) {
            if (!providerServer.isStarted()) {
                try {
                    providerServer.startHSFServer();
                } catch (Exception e) {
                    throw new LSFException("启动HSF SERVER失败.", e);
                }
            }
        }

        providerServer.addMetadata(metadata.getUniqueName(), metadata);
        providerServer.addWorker(metadata.getUniqueName(), metadata.getTarget());
    }

    @Override
    public Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURL) throws LSFException {
//        final String serviceName=metadata.getUniqueName();

        return invokeService.invoke(request, metadata, targetURL, null);
    }

    public void setProviderServer(ProviderServer providerServer) {
        this.providerServer = providerServer;
    }

    public void setInvokeService(InvokeService invokeService) {
        this.invokeService = invokeService;
    }
}
