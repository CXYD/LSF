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
 * Time: ����4:07
 * �ṩ��Springʹ�õķ���spring beanΪHSF Service��bean
 */
public class HSFSpringProviderBean implements InitializingBean {

    private ServiceMetadata metadata = new ServiceMetadata();

    /**
     * ��ʼ����ʶ
     */
    private AtomicBoolean inited=new AtomicBoolean(false);

    private void init(){
        // ���ⱻ��ʼ�����
        if(!inited.compareAndSet(false, true)){
            return;
        }
        checkConfig();
    }

    public HSFSpringProviderBean(){
        metadata.setVersion("1.0.0");
        metadata.setGroup("HSF"); // �������������, Ĭ�ϵ��������ΪHSF
//        metadata.setSupportAsyncall("false"); // Ĭ�ϲ�֧���첽����
        metadata.addProperty(TRConstants.TIMEOUT_TYPE_KEY, "3000"); // Ĭ�Ͽͻ��˵��ó�ʱʱ�䣺3s
        metadata.addProperty(TRConstants.IDLE_TIMEOUT_KEY, "600"); // Ĭ�ϵĿͻ������ӿ��г�ʱʱ�䣺600��
        metadata.addProperty(TRConstants.SERIALIZE_TYPE_KEY,
                CommonConstant.HESSIAN_SERIALIZE); // ���л����ͣ�Ĭ��ΪHESSIAN

        metadata.addProperty(CommonConstant.CLIENTRETRYCONNECTIONTIMES_KEY, "3");
        metadata.addProperty(CommonConstant.CLIENTRETRYCONNECTIONTIMEOUT_KEY, "1000");
    }

    /**
     * ���ҵ������
     */
    private void checkConfig(){
        String serviceInterface = metadata.getInterfaceName();
        Object target = metadata.getTarget();
        String serializeType = metadata
                .getProperty(TRConstants.SERIALIZE_TYPE_KEY);

        StringBuilder errorMsg = new StringBuilder();
        if(target == null){
            errorMsg.append("δ������Ҫ����Ϊ�����Object��������Ϊ: ").append(metadata.getUniqueName());
            invalidDeclaration(errorMsg.toString());
        }
        if (!CommonConstant.HESSIAN_SERIALIZE.equals(serializeType)
                && !CommonConstant.JAVA_SERIALIZE.equals(serializeType)) {
            errorMsg.append("����ʶ������л�����[").append(serializeType).append("].");
            invalidDeclaration(errorMsg.toString());
        }

        Class interfaceClass = null;

        //�ж��Ƿ����
        try {
            interfaceClass = Class.forName(serviceInterface);
        } catch (ClassNotFoundException e) {
            errorMsg.append("ProviderBean��ָ���Ľӿ��಻����[");
            errorMsg.append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }
        //�ж��Ƿ�Ϊ�ӿ�
        if(!interfaceClass.isInterface()){
            errorMsg.append("ProviderBean��ָ���ķ������Ͳ��ǽӿ�[");
            errorMsg.append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }
        //�ж϶�Ӧ��targetʵ�����Ӧ�Ľӿ�
        if(!interfaceClass.isAssignableFrom(target.getClass())){
            errorMsg.append("��ʵ�ķ������[").append(target);
            errorMsg.append("]û��ʵ��ָ���ӿ�[").append(serviceInterface).append("].");
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
     * ���ýӿ���
     */
    public void setServiceInterface(String serviceInterface) {
        metadata.setInterfaceName(serviceInterface);
    }

    /**
     * ���ýӿڵ�ʵ����
     */
    public void setTarget(Object target) {
        metadata.setTarget(target);
    }

    /**
     * ���÷���汾��
     */
    public void setServiceVersion(String serviceVersion) {
        metadata.setVersion(serviceVersion);
    }

    /**
     * �������л�����
     */
    public void setSerializeType(String serializeType) {
        metadata.addProperty(TRConstants.SERIALIZE_TYPE_KEY, serializeType);
    }
}
