package zhenghui.lsf.metadata;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: zhenghui
 * Date: 13-12-1
 * Time: ����5:06
 * HSF�����Ԫ������Ϣ��������Ԫ������Ϣ������������Լ�������Ϣ�ķ����붩��
 */
public class ServiceMetadata implements Serializable {

    /**
     * ����汾��
     */
    private String version;

    /**
     * ����
     */
    private String group;

    /**
     * ���Կ�,����һЩ��tbremoting��ص�����ֵ.
     * ������� �ͻ��˵��ó�ʱʱ��,�ͻ������ӿ��г�ʱʱ��,���л���ʽ�ȵ�.
     */
    private Properties serviceProps = new Properties();

    /**
     * �ӿ���
     */
    private String interfaceName;

    /**
     * �ӿڶ��ڵ�ʵ����
     * ����Ҫ���л�
     */
    private transient Object target;

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
     * Ψһ��ʶ
     * �ӿ�:�汾
     */
    public String getUniqueName() {
        return new StringBuffer(interfaceName).append(":").append(version).toString();
    }
}
