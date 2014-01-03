package zhenghui.lsf.domain;

import java.io.Serializable;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午5:04
 */
public class HSFRequest implements Serializable {

    // Instance Variables ------------------------------------------------------

    private static final long serialVersionUID = -7323141575870688636L;

    private String methodName;

    private String[] methodArgSigs;

    private Object[] methodArgs;

    private String targetServiceUniqueName;

    private String localAddr;

    /**
     * 存放调用上下文序列化之后的结果
     * 直接存放bytes，服务端就不需要因为反序列化而包含客户端的Context类了
     */
    private byte[] invokeContext;

    private boolean isNeedReliableCallback;

    /**
     * 每次请求的标示符
     */
    private String requestId;


    // Public Method ------------------------------------------------------------

    /**
     * 所需调用的方法名
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * 所需调用的方法的参数
     */
    public Object[] getMethodArgs() {
        return methodArgs;
    }

    /**
     * 所需调用的方法参数的类型
     */
    public String[] getMethodArgSigs() {
        return methodArgSigs;
    }

    /**
     * 所需调用的目标服务名
     */
    public String getTargetServiceUniqueName() {
        return targetServiceUniqueName;
    }

    /**
     * 消费者IP地址
     */
    public String getLocalAddr() {
        return localAddr;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setMethodArgs(Object[] methodArgs) {
        this.methodArgs = methodArgs;
    }

    public void setMethodArgSigs(String[] methodArgSigs) {
        this.methodArgSigs = methodArgSigs;
    }

    public void setTargetServiceUniqueName(String targetServiceUniqueName) {
        this.targetServiceUniqueName = targetServiceUniqueName;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public byte[] getInvokeContext() {
        return invokeContext;
    }

    public void setInvokeContext(byte[] invokeContext) {
        this.invokeContext = invokeContext;
    }

    public boolean isNeedReliableCallback() {
        return isNeedReliableCallback;
    }

    public void setNeedReliableCallback(boolean isNeedReliableCallback) {
        this.isNeedReliableCallback = isNeedReliableCallback;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HSFRequest[");
        sb.append("服务名=").append(targetServiceUniqueName).append(", ");
        sb.append("方法名=").append(methodName).append(", ");
        sb.append("方法参数=[");
        if (null != methodArgs) {
            for (Object arg : methodArgs) {
                sb.append(arg).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]]");
        return sb.toString();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
