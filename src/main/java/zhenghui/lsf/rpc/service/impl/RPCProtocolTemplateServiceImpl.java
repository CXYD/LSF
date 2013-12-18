package zhenghui.lsf.rpc.service.impl;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.service.RPCProtocolService;
import zhenghui.lsf.rpc.service.RPCProtocolTemplateService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午5:06
 */
public class RPCProtocolTemplateServiceImpl implements RPCProtocolTemplateService {


    private Map<String, RPCProtocolService> rpcServices=new HashMap<String, RPCProtocolService>();

    @Override
    public void registerProvider(String protocol, ServiceMetadata metadata) throws HSFException {
        rpcServices.get(protocol).registerProvider(metadata);
    }

    @Override
    public boolean isNeedTarget(String protocol, ServiceMetadata metadata, HSFRequest request) {
        return false;
    }

    @Override
    public boolean validTarget(String protocol, String targetURL) {
        return false;
    }

    @Override
    public Object invokeWithMethodObject(String protocol, ServiceMetadata metadata, Method method, Object[] args) throws HSFException, Exception {
        return null;
    }

    @Override
    public Object invokeWithMethodInfos(String protocol, ServiceMetadata metadata, String methodName, String[] parameterTypes, Object[] args) throws HSFException, Exception {
        return null;
    }
}
