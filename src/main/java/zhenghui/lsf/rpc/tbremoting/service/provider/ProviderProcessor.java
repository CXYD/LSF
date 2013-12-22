package zhenghui.lsf.rpc.tbremoting.service.provider;

import com.alibaba.common.lang.diagnostic.Profiler;
import com.taobao.remoting.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.datastore.service.DataStoreService;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.domain.HSFResponse;
import zhenghui.lsf.metadata.ServiceMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 下午4:17
 */
public class ProviderProcessor implements RequestProcessor<HSFRequest> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataStoreService dataStoreService;
    
    @Override
    public Class<HSFRequest> interest() {
        return HSFRequest.class;
    }

    @Override
    public void handleRequest(HSFRequest appRequest, AppResponseOutput output) {
        HSFResponse hsfResponse = handleRequest0(appRequest, output
                .remoteHost());
        output.write(hsfResponse);
    }

    @Override
    public Executor getExecutor() {
        return (ThreadPoolExecutor) dataStoreService.get(NIOProviderServer.COMPONENT_NAME, NIOProviderServer.THREADPOOL_STORE_KEY);
    }

    @Override
    public void onRejectedExecutionException(HSFRequest appRequest, AppResponseOutput respOut) {
        
    }

    protected HSFResponse handleRequest0(final HSFRequest hsfRequest,
                                         final String remoteHost) {
        final String serviceName = hsfRequest.getTargetServiceUniqueName();
        final String methodName = hsfRequest.getMethodName();

        final Object[] methodArg = hsfRequest.getMethodArgs();

        final HSFResponse hsfResponse = new HSFResponse();
        final Object servicePOJO = getWorker(serviceName);

        if (null == servicePOJO) { // 服务不存在
            hsfResponse.setErrorMsg("HSF未找到业务服务，服务名称：" + serviceName);
            logger.warn("HSF未找到业务服务，服务名称：" + serviceName);
            return hsfResponse;
        }

        ServiceMetadata metadata = getServiceMetadata(this.dataStoreService, serviceName);

        Profiler.reset();
        Profiler.start("HSF服务开始执行...");
        Method appServiceMethod;
        try {
            StringBuilder methodKeyBuffer=new StringBuilder();
            methodKeyBuffer.append(methodName);
            String[] sig = hsfRequest.getMethodArgSigs();
            for (int i = 0; i < sig.length; i++) {
                methodKeyBuffer.append(sig[i]);
            }
            Map<String/*SrvID*/, Map<String/*MethodSig*/, Method>> workerMethods = dataStoreService.get(
                    NIOProviderServer.COMPONENT_NAME, NIOProviderServer.WORKERMETHODS_STORE_KEY);
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
                for (int i = 0; i < methodArg.length; i++) {
                    errBuilder.append(methodArg[i]);
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
                for (int i = 0; i < methodArg.length; i++) {
                    errBuilder.append(methodArg[i]);
                    errBuilder.append(",");
                }
            }
            errBuilder.append("]");
            hsfResponse.setErrorMsg(errBuilder.toString());
            logger.error(errBuilder.toString(), t);
        }
        finally {
            //记录日志
        }
        return hsfResponse;
    }

    private Object getWorker(String serviceName){
        Map<String/*SrvID*/, Object/*SrvPOJO*/> workers = dataStoreService.get(NIOProviderServer.COMPONENT_NAME, NIOProviderServer.WORKS_STORE_KEY);
        if(workers==null)
            return null;
        return workers.get(serviceName);
    }

    private static ServiceMetadata getServiceMetadata(DataStoreService dataStoreService, String serviceName){
        Map<String, ServiceMetadata> metadatas= dataStoreService.get(NIOProviderServer.COMPONENT_NAME, NIOProviderServer.METADATAS_STORE_KEY);
        return metadatas.get(serviceName);
    }

//    private Method getMethodToInjectCosumerIp(ServiceMetadata metadata, String serviceName,
//                                              Map<String/*SrvID*/, Map<String/*MethodSig*/, Method>> workerMethods) {
//        if (metadata == null) { // Should Not Happen!
//            logger.error("metadata is null! serviceName=" + serviceName);
//            return null;
//        }
//        String methodName = metadata.getProperty(ServiceMetadata.METHOD_TO_INJECT_CONSUMERIP_PROP_KEY);
//        if (methodName != null) {
//            StringBuilder methodKeyBuffer = new StringBuilder(methodName);
//            methodKeyBuffer.append(String.class.getName());
//            return workerMethods.get(serviceName).get(methodKeyBuffer.toString());
//        } else {
//            //如果HSFSpringProviderBean中没有显式配置，则不去试图调用设置客户端IP的方法
//            return null;
//        }
//    }

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
    static private final Field causeField;
    static {
        try {
            causeField = Throwable.class.getDeclaredField("cause");
            causeField.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setDataStoreService(DataStoreService dataStoreService) {
        this.dataStoreService = dataStoreService;
    }
}
