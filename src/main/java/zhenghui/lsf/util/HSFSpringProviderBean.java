package zhenghui.lsf.util;

import com.taobao.remoting.TRConstants;
import org.springframework.beans.factory.InitializingBean;
import zhenghui.lsf.constant.CommonConstant;
import zhenghui.lsf.metadata.ServiceMetadata;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: zhenghui
 * Date: 13-12-1
 * Time: 下午4:07
 * 提供给Spring使用的发布spring bean为HSF Service的bean
 */
public class HSFSpringProviderBean implements InitializingBean {

    private ServiceMetadata metadata = new ServiceMetadata();

    /**
     * 初始化标识
     */
    private AtomicBoolean inited=new AtomicBoolean(false);

    private void init(){
        // 避免被初始化多次
        if(!inited.compareAndSet(false, true)){
            return;
        }
        checkConfig();
    }

    public HSFSpringProviderBean(){
        metadata.setVersion("1.0.0");
        metadata.setGroup("HSF"); // 服务所属的组别, 默认的组别名称为HSF
//        metadata.setSupportAsyncall("false"); // 默认不支持异步调用
        metadata.addProperty(TRConstants.TIMEOUT_TYPE_KEY, "3000"); // 默认客户端调用超时时间：3s
        metadata.addProperty(TRConstants.IDLE_TIMEOUT_KEY, "600"); // 默认的客户端连接空闲超时时间：600秒
        metadata.addProperty(TRConstants.SERIALIZE_TYPE_KEY,
                CommonConstant.HESSIAN_SERIALIZE); // 序列化类型，默认为HESSIAN

        metadata.addProperty(CommonConstant.CLIENTRETRYCONNECTIONTIMES_KEY, "3");
        metadata.addProperty(CommonConstant.CLIENTRETRYCONNECTIONTIMEOUT_KEY, "1000");
    }

    /**
     * 检查业务配置
     */
    private void checkConfig(){
        String serviceInterface = metadata.getInterfaceName();
        Object target = metadata.getTarget();
        String serializeType = metadata
                .getProperty(TRConstants.SERIALIZE_TYPE_KEY);

        StringBuilder errorMsg = new StringBuilder();
        if(target == null){
            errorMsg.append("未配置需要发布为服务的Object，服务名为: ").append(metadata.getUniqueName());
            invalidDeclaration(errorMsg.toString());
        }
        if (!CommonConstant.HESSIAN_SERIALIZE.equals(serializeType)
                && !CommonConstant.JAVA_SERIALIZE.equals(serializeType)) {
            errorMsg.append("不可识别的序列化类型[").append(serializeType).append("].");
            invalidDeclaration(errorMsg.toString());
        }

        Class interfaceClass = null;

        //判断是否存在
        try {
            interfaceClass = Class.forName(serviceInterface);
        } catch (ClassNotFoundException e) {
            errorMsg.append("ProviderBean中指定的接口类不存在[");
            errorMsg.append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }
        //判断是否为接口
        if(!interfaceClass.isInterface()){
            errorMsg.append("ProviderBean中指定的服务类型不是接口[");
            errorMsg.append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }
        //判断对应的target实现类对应的接口
        if(!interfaceClass.isAssignableFrom(target.getClass())){
            errorMsg.append("真实的服务对象[").append(target);
            errorMsg.append("]没有实现指定接口[").append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }
    }

    private void invalidDeclaration(String msg) {
        throw new IllegalArgumentException(msg);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    /**
     * 设置接口名
     */
    public void setServiceInterface(String serviceInterface) {
        metadata.setInterfaceName(serviceInterface);
    }

    /**
     * 设置接口的实现类
     */
    public void setTarget(Object target) {
        metadata.setTarget(target);
    }

    /**
     * 设置服务版本号
     */
    public void setServiceVersion(String serviceVersion) {
        metadata.setVersion(serviceVersion);
    }

    /**
     * 设置序列化类型
     */
    public void setSerializeType(String serializeType) {
        metadata.addProperty(TRConstants.SERIALIZE_TYPE_KEY, serializeType);
    }
}
