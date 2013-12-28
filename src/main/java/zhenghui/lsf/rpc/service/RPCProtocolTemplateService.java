package zhenghui.lsf.rpc.service;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;

import java.lang.reflect.Method;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午5:02
 */
public interface RPCProtocolTemplateService {

    /**
     * 注册服务提供者
     *
     * @param metadata 服务元信息
     * @throws Exception 抛出异常
     */
    public void registerProvider(ServiceMetadata metadata) throws LSFException;

    /**
     * 调用是否需要目标地址
     *
     * @param metadata
     * @param request
     * @return 是否需要目标地址
     */
    public boolean isNeedTarget(String protocol,ServiceMetadata metadata,HSFRequest request);

    /**
     * 校验目标地址的可用性
     *
     * @param targetURL
     * @return 目标地址是否可用，如不可用，外部将自动进行其他的选址动作
     */
    public boolean validTarget(String protocol,String targetURL);

    /**
     * 基于反射方式调用HSF服务
     *
     * @param metadata 服务元信息对象
     * @param method 需要调用的服务的方法对象
     * @param args 调用方法的参数
     * @return Object 远程HSF服务执行后的响应对象
     * @throws zhenghui.lsf.exception.LSFException 调用远程服务时出现超时、网络、业务异常时抛出
     * @throws Exception 业务异常
     */
    public Object invokeWithMethodObject(String protocol,ServiceMetadata metadata, Method method, Object[] args)
            throws LSFException,Exception;

    /**
     * 基于非反射方式调用HSF服务
     *
     * @param protocol RPC协议
     * @param metadata 服务元信息对象
     * @param methodName 需要调用的方法名
     * @param parameterTypes 方法参数类型
     * @param args 调用方法的参数
     * @return Object 远程HSF服务执行后的响应对象
     * @throws zhenghui.lsf.exception.LSFException 调用远程服务时出现超时、网络、业务异常时抛出
     * @throws Exception 业务异常
     */
    public Object invokeWithMethodInfos(String protocol,ServiceMetadata metadata, String methodName, String[] parameterTypes, Object[] args)
            throws LSFException,Exception;
}
