package zhenghui.lsf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import zhenghui.lsf.constant.CommonConstant;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.process.ProcessService;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: zhenghui
 * Date: 13-12-1
 * Time: 下午4:07
 * 提供给Spring使用的发布spring bean为HSF Service的bean
 */
public class HSFSpringProviderBean implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private ServiceMetadata metadata = new ServiceMetadata();

    private ProcessService processService;

    /**
     * 初始化标识
     */
    private AtomicBoolean inited=new AtomicBoolean(false);

    private void init() throws Exception{
        // 避免被初始化多次
        if(!inited.compareAndSet(false, true)){
            return;
        }
        checkConfig();
        try {
            processService.publish(metadata);
            logger.warn("接口[" + metadata.getInterfaceName() + "]版本[" + metadata.getVersion() + "]发布为HSF服务成功！");
        } catch (Exception e) {
            logger.error("接口[" + metadata.getInterfaceName() + "]版本[" + metadata.getVersion() + "]发布为HSF服务失败", e);
            throw e;
        }
    }

    public HSFSpringProviderBean(){
        metadata.setVersion("1.0.0");
        metadata.setGroup("HSF"); // 服务所属的组别, 默认的组别名称为HSF
//        metadata.setSupportAsyncall("false"); // 默认不支持异步调用

        metadata.addProperty(CommonConstant.CLIENTRETRYCONNECTIONTIMES_KEY, "3");
        metadata.addProperty(CommonConstant.CLIENTRETRYCONNECTIONTIMEOUT_KEY, "1000");
    }

    /**
     * 检查业务配置
     */
    private void checkConfig(){
        String serviceInterface = metadata.getInterfaceName();
        Object target = metadata.getTarget();

        StringBuilder errorMsg = new StringBuilder();
        if(target == null){
            errorMsg.append("未配置需要发布为服务的Object，服务名为: ").append(metadata.getUniqueName());
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

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }
}
