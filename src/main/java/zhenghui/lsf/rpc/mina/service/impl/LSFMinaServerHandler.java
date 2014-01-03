package zhenghui.lsf.rpc.mina.service.impl;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.datastore.service.DataStoreService;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.domain.HSFResponse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午2:03
 * 服务端拦截器处理类
 */
public class LSFMinaServerHandler extends IoHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(LSFMinaServerHandler.class);

    private DataStoreService dataStoreService;

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        HSFRequest request = (HSFRequest) message;
        HSFResponse response = handleRequest0(request,session.getRemoteAddress().toString());
        session.write(response);
    }

    protected HSFResponse handleRequest0(final HSFRequest hsfRequest,
                                         final String remoteHost) {
        final String serviceName = hsfRequest.getTargetServiceUniqueName();
        final String methodName = hsfRequest.getMethodName();

        final Object[] methodArg = hsfRequest.getMethodArgs();

        final HSFResponse hsfResponse = new HSFResponse();
        hsfResponse.setRequestId(hsfRequest.getRequestId());
        final Object servicePOJO = getTarget(serviceName);

        if (null == servicePOJO) { // 服务不存在
            hsfResponse.setErrorMsg("HSF未找到业务服务，服务名称：" + serviceName);
            logger.warn("HSF未找到业务服务，服务名称：" + serviceName);
            return hsfResponse;
        }

        Method appServiceMethod;
        try {
            StringBuilder methodKeyBuffer=new StringBuilder();
            methodKeyBuffer.append(methodName);
            String[] sig = hsfRequest.getMethodArgSigs();
            for(String s : sig){
                methodKeyBuffer.append(s);
            }
            Map<String/*SrvID*/, Map<String/*MethodSig*/, Method>> workerMethods = dataStoreService.get(
                    MinaProviderServerImpl.COMPONENT_NAME, MinaProviderServerImpl.WORKERMETHODS_STORE_KEY);
            appServiceMethod = workerMethods.get(serviceName).get(methodKeyBuffer.toString());
            if(appServiceMethod==null)
                throw new NoSuchMethodException("未找到需要调用的方法："+methodName+"；服务名为："+serviceName);
//            Method m2InjectCosumerIp = getMethodToInjectCosumerIp(metadata, serviceName, workerMethods);
//            InjectCosumerIp(m2InjectCosumerIp, servicePOJO, remoteHost);
            Object appResp = appServiceMethod.invoke(servicePOJO, methodArg);
            hsfResponse.setAppResponse(appResp);
        }
        catch (InvocationTargetException ivke) {
            // 对于上层业务抛出的业务异常，要完整返回到客户端
            final Throwable bizException = ivke.getCause();
            cutCause(bizException);
            hsfResponse.setAppResponse(bizException);
            StringBuilder errBuilder=new StringBuilder();
            errBuilder.append("执行HSF服务[");
            errBuilder.append(serviceName);
            errBuilder.append("]的方法[");
            errBuilder.append(methodName);
            errBuilder.append("]时出现业务异常，发起请求的地址为：[");
            errBuilder.append(remoteHost);
            errBuilder.append("]，执行的参数为：[");
            if (methodArg != null) {
                for (Object aMethodArg : methodArg) {
                    errBuilder.append(aMethodArg);
                    errBuilder.append(",");
                }
            }
            errBuilder.append("]");
            logger.error(errBuilder.toString(), bizException);
        }
        catch (Throwable t) {
            StringBuilder errBuilder=new StringBuilder();
            errBuilder.append("执行HSF服务[");
            errBuilder.append(serviceName);
            errBuilder.append("]的方法[");
            errBuilder.append(methodName);
            errBuilder.append("]时出现未知异常：").append(t.getMessage()).append("，发起请求的地址为：[");
            errBuilder.append(remoteHost);
            errBuilder.append("]，执行的参数为：[");
            if (methodArg != null) {
                for (Object aMethodArg : methodArg) {
                    errBuilder.append(aMethodArg);
                    errBuilder.append(",");
                }
            }
            errBuilder.append("]");
            hsfResponse.setErrorMsg(errBuilder.toString());
            logger.error(errBuilder.toString(), t);
        }
        return hsfResponse;
    }

    private Object getTarget(String serviceName){
        Map<String, Object> workers = dataStoreService.get(MinaProviderServerImpl.COMPONENT_NAME, MinaProviderServerImpl.WORKS_STORE_KEY);
        if(workers==null){
            return null;
        }
        return workers.get(serviceName);
    }

    static private final Field causeField;
    static {
        try {
            causeField = Throwable.class.getDeclaredField("cause");
            causeField.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 把业务层抛出的业务异常或者RuntimeException/Error，
     * 截断Cause，以免客户端因为无法找到cause类而出现反序列化失败.
     */
    private void cutCause(Throwable bizException) {
        Throwable rootCause = bizException;
        while (null != rootCause.getCause()) {
            rootCause = rootCause.getCause();
        }

        if (rootCause != bizException) {
            bizException.setStackTrace(rootCause.getStackTrace());
            try {
                causeField.set(bizException, bizException); // SELF-CAUSE
            } catch (Exception e) {
                logger.warn("切断业务连环异常时出现异常.", e);
            }
        }
    }

    public void setDataStoreService(DataStoreService dataStoreService) {
        this.dataStoreService = dataStoreService;
    }
}
