package zhenghui.lsf.rpc.mina.service;

import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;


/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午1:31
 */
public interface MinaProviderServer {

    static final int SERVER_PORT = 8888;

    /**
     * 服务是否启动
     * @return
     */
    public boolean isStarted();

    /**
     * 启动服务.
     */
    public void startServer() throws LSFException;

    public void addWorker(String serviceName, Object workerPOJO);

    public void addMetadata(String key, ServiceMetadata metadata);
}
