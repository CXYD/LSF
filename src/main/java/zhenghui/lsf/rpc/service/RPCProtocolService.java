package zhenghui.lsf.rpc.service;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 下午5:13
 */
public interface RPCProtocolService {

    /**
     * 注册服务提供者
     *
     * @param metadata 服务元信息
     * @throws Exception 抛出异常
     */
    public void registerProvider(ServiceMetadata metadata) throws HSFException;

    /**
     * 具体的远程调用，已提供目标地址
     * @param request
     * @param metadata
     * @param targetURL
     *
     * @return Object
     * @throws HSFException
     */
    public Object invoke(HSFRequest request,
                         ServiceMetadata metadata,
                         String targetURL) throws HSFException;

}
