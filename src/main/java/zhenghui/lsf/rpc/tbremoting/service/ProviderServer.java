package zhenghui.lsf.rpc.tbremoting.service;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.domain.HSFResponse;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;

import java.util.Map;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 上午11:34
 */
public interface ProviderServer {

    public void startHSFServer() throws HSFException;

    public void stopHSFServer() throws HSFException;

    public void addWorker(String serviceName, Object workerPOJO);

    public Object getWorker(String serviceName);

    public void removeWorker(String workerName);

    public void setMetadatas(Map<String, ServiceMetadata> metadatas);

    public void addMetadata(String key, ServiceMetadata metadata);

    public boolean isStarted();

    public HSFResponse handleRequest(HSFRequest request) throws HSFException;
}
