package zhenghui.lsf.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: zhenghui
 * Date: 13-12-1
 * Time: 下午5:06
 * HSF服务的元数据信息，包括了元数据信息本身的描述，以及服务信息的发布与订阅
 */
public class ServiceMetadata implements Serializable {

    /**
     * 服务版本号
     */
    private String version;

    /**
     * 分组
     */
    private String group;

    /**
     * 属性库,放置一些与tbremoting相关的属性值.
     * 比如放置 客户端调用超时时间,客户端连接空闲超时时间,序列化方式等等.
     */
    private Properties serviceProps = new Properties();

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 接口对于的实现类
     * 不需要序列化
     */
    private transient Object target;

    /**
     * 发布的方式，用来支持多种rpc协议
     *
     * key为发布的RPC协议的关键字，统一为大写，例如HSF、HTTP和XFIRE
     * value为Properties，用于进行RPC协议的一些特殊配置
     */
    private Map<String, Properties> exporters = new HashMap<String, Properties>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void addProperty(String propKey,String propValue){
        serviceProps.put(propKey,propValue);
    }

    public String getProperty(String propKey){
        return (String) serviceProps.get(propKey);
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * 唯一标识
     * 接口:版本
     */
    public String getUniqueName() {
        return new StringBuffer(interfaceName).append(":").append(version).toString();
    }

    public Map<String, Properties> getExporters() {
        return exporters;
    }

    public void setExporters(Map<String, Properties> exporters) {
        this.exporters = exporters;
    }

    public void addExporter(String key,Properties properties){
        exporters.put(key,properties);
    }
}
