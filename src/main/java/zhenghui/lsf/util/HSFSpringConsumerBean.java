package zhenghui.lsf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.process.ProcessService;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午3:22
 * 描述：提供给Spring配置调用HSF Service的FactoryBean，为单元测试、开发环境部署以及正式部署提供支持
 */
public class HSFSpringConsumerBean implements FactoryBean, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(ProcessService.class);

    static private final String DEFAULT_VERSION = "1.0.0";

    private final ServiceMetadata metadata = new ServiceMetadata();
    private AtomicBoolean inited = new AtomicBoolean(false);

    private ProcessService processService;

    public HSFSpringConsumerBean() {
        metadata.setGroup("HSF"); // 服务所属的组别, 默认的组别名称为HSF
        metadata.setVersion(DEFAULT_VERSION);
    }

    public void init() throws Exception{
        // 避免被初始化多次
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        try {
            metadata.setTarget(processService.consume(metadata));
            logger.warn("成功生成对接口为[" + metadata.getInterfaceName() + "]版本为[" + metadata.getVersion() + "]的HSF服务调用的代理！");
        } catch (Exception e) {
            logger.error("生成对接口为[" + metadata.getInterfaceName() + "]版本为[" + metadata.getVersion() + "]的HSF服务调用的代理失败",
                    e);
        }
    }


    @Override
    public Object getObject() throws Exception {
        return metadata.getTarget();
    }

    @Override
    public Class getObjectType() {
        if (metadata.getInterfaceName() == null) {
            return HSFSpringConsumerBean.class;
        }

        if (null == metadata.getIfClazz()) {
            return HSFSpringConsumerBean.class;
        } else {
            return metadata.getIfClazz();
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 设置接口名.如果该接口无法装载到，则抛出{@link IllegalArgumentException}
     */
    public void setInterfaceName(String interfaceName) {
        try {
            Class<?> clazz = Class.forName(interfaceName);
            metadata.setIfClazz(clazz);
            metadata.setInterfaceName(interfaceName);
        }
        catch (ClassNotFoundException cnfe) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("ProviderBean中指定的接口类不存在[");
            errorMsg.append(interfaceName).append("].");
            illArgsException(errorMsg.toString());
        }
    }

    /**
     * 设置调用的服务的版本
     */
    public void setVersion(String version) {
        metadata.setVersion(version);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    /**
     * 设置调用的服务的目标地址
     */
    public void setTarget(String target) {
        if ((target != null) && (!"".equals(target.trim()))) {
            // 将调用的目标地址放进去
            metadata.addProperty("target", target);
        }
    }

    private void illArgsException(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
