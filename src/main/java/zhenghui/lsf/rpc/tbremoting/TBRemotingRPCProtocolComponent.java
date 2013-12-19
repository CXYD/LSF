package zhenghui.lsf.rpc.tbremoting;

import zhenghui.lsf.datastore.service.DataStoreService;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.service.RPCProtocolService;
import zhenghui.lsf.rpc.tbremoting.service.ProviderServer;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 上午11:28
 */
public class TBRemotingRPCProtocolComponent implements RPCProtocolService {

    private final Object onlyOnceSync = new Object();

    private ProviderServer providerServer;

    @Override
    public void registerProvider(ServiceMetadata metadata) throws HSFException {
        // 仅启动一次HSF SERVER
        synchronized (onlyOnceSync) {
            if (!providerServer.isStarted()) {
                try {
                    providerServer.startHSFServer();
                } catch (Exception e) {
                    throw new HSFException("启动HSF SERVER失败.", e);
                }
            }
        }

        providerServer.addMetadata(metadata.getUniqueName(), metadata);
        providerServer.addWorker(metadata.getUniqueName(), metadata.getTarget());
    }

    @Override
    public boolean isNeedTarget(ServiceMetadata metadata, HSFRequest request) {
        return false;  
    }

    @Override
    public boolean validTarget(String targetURL) {
        return false;  
    }

    @Override
    public Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURL) throws HSFException {
        return null;  
    }

    @Override
    public String getType() {
        return null;  
    }

    public void setProviderServer(ProviderServer providerServer) {
        this.providerServer = providerServer;
    }

}
