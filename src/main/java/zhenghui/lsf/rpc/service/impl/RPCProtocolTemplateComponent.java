package zhenghui.lsf.rpc.service.impl;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.service.RPCProtocolTemplateService;

import java.lang.reflect.Method;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午5:06
 */
public class RPCProtocolTemplateComponent implements RPCProtocolTemplateService {


    @Override
    public void registerProvider(ServiceMetadata metadata) throws LSFException {
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
    public Object invokeWithMethodObject(String protocol, ServiceMetadata metadata, Method method, Object[] args) throws LSFException, Exception {
        return null;
    }

    @Override
    public Object invokeWithMethodInfos(String protocol, ServiceMetadata metadata, String methodName, String[] parameterTypes, Object[] args) throws LSFException, Exception {
        return null;
    }
}
