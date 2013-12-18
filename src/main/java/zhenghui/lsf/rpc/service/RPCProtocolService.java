package zhenghui.lsf.rpc.service;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午5:16
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
     * 调用是否需要目标地址
     *
     * @param metadata
     * @param request
     * @return 是否需要目标地址
     */
    public boolean isNeedTarget(ServiceMetadata metadata,HSFRequest request);

    /**
     * 校验目标地址的可用性
     *
     * @param targetURL
     * @return 目标地址是否可用，如不可用，外部将自动进行其他的选址动作
     */
    public boolean validTarget(String targetURL);

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

    /**
     * RPC协议的类型，统一大写
     *
     * @return String
     */
    public String getType();
}
