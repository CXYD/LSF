package zhenghui.lsf.process.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.process.ProcessService;
import zhenghui.lsf.route.service.AddressService;
import zhenghui.lsf.rpc.service.RPCProtocolService;
import zhenghui.lsf.rpc.service.RPCProtocolTemplateService;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Properties;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午4:39
 */
public class ProcessComponent implements ProcessService {

    private Logger logger = LoggerFactory.getLogger(ProcessService.class);

//    private RPCProtocolTemplateService protocolTemplateService;

    private RPCProtocolService rpcProtocolService;

    private AddressService addressService;

    static private final int RETRY_TIMES = 3;


    @Override
    public void publish(ServiceMetadata metadata) throws HSFException {

        try {
            rpcProtocolService.registerProvider(metadata);
        } catch (HSFException e) {
            logger.error("发布HSF服务时出现错误，请确认服务：" + metadata.getUniqueName() + "的rpc属性的配置！");
            throw e;
        }

        //todo 向configserver注册服务信息

    }

    @Override
    public Object consume(ServiceMetadata metadata) throws HSFException {
        // 生成调用远程HSF服务的代理
        Class<?> interfaceClass=null;
        try {
            interfaceClass=Class.forName(metadata.getInterfaceName());
        }
        catch (ClassNotFoundException e) {
            throw new HSFException("无法加载HSF服务接口类，请确定此类是否存在："+metadata.getInterfaceName());
        }
        InvocationHandler handler=new HSFServiceProxy(metadata);
        Object proxyObj= Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{interfaceClass}, handler);

        // todo 向configserver订阅服务信息

        return proxyObj;
    }

    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    class HSFServiceProxy implements InvocationHandler{

        static private final String TOSTRING_METHOD = "toString";

        private ServiceMetadata metadata;

        public HSFServiceProxy(ServiceMetadata metadata){
            this.metadata=metadata;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            String methodName = method.getName();
            if (TOSTRING_METHOD.equalsIgnoreCase(methodName)) {
                return proxy.getClass().getName();
            }
            String[] methodArgSigs = createParamSignature(method.getParameterTypes());
            final String serviceName = metadata.getUniqueName();
            // 组装HSFRequest
            final HSFRequest request = new HSFRequest();
            request.setTargetServiceUniqueName(serviceName);
            request.setMethodName(methodName);
            request.setMethodArgSigs(methodArgSigs);
            request.setMethodArgs(args);

            //先看是否有配置对应的服务地址
            String targetURL = metadata.getProperty("target");
            // 当target不为null，或者重试次数已到达最大重试次数时，退出寻找可用的目标服务地址的过程
            for (int i = 0; (isBlank(targetURL)) && (i < RETRY_TIMES); i++) {
                if (null == addressService) {
                    throw new HSFException("地址路由服务暂时不可用！");
                }
                targetURL = addressService.getServiceAddress(serviceName, methodName, methodArgSigs, args);
            }
            // 如这个时候targetURL仍然为null，抛出异常
            if (isBlank(targetURL)) {
                throw new HSFException("未找到需要调用的服务的目标地址", "需要调用的目标服务为："+ serviceName);
            }
            Object appResponse = rpcProtocolService.invoke(request, metadata, targetURL);
            checkAppRespForException(appResponse);
            return appResponse;
        }

    }

    /*
     * 获取参数类型
     */
    private String[] createParamSignature(Class<?>[] args) {
        if (args == null || args.length == 0) {
            return new String[] {};
        }
        String[] paramSig = new String[args.length];
        for (int x = 0; x < args.length; x++) {
            paramSig[x] = args[x].getName();
        }
        return paramSig;
    }

    private boolean isBlank(String string) {
        return string == null || "".equals(string.trim());
    }

    /*
     * 检查对端返回的业务层对象: 如果返回的是异常对象，则重新抛出异常
     */
    private void checkAppRespForException(Object appResp)
            throws Exception {
        if (appResp instanceof Exception) {
            throw (Exception) appResp;
        }
    }

//    public void setProtocolTemplateService(RPCProtocolTemplateService protocolTemplateService) {
//        this.protocolTemplateService = protocolTemplateService;
//    }

    public void setRpcProtocolService(RPCProtocolService rpcProtocolService) {
        this.rpcProtocolService = rpcProtocolService;
    }
}
