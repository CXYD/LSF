package zhenghui.lsf.domain;

import java.io.Serializable;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 上午11:35
 */
public class HSFResponse implements Serializable{

    private static final long serialVersionUID = 1624285659459109754L;
    /* 这个字段表示HSF层是否出现异常。
        * 如果HSF层未出现异常，而上层业务抛出异常，那么这个字段应该填false。
        */
    private boolean isError = false;
    private String errorMsg;

    private String requestId;

    // 业务层的返回值或抛出的异常
    private Object appResponse;

    public Object getAppResponse() {
        return appResponse;
    }
    public void setAppResponse(Object response) {
        appResponse = response;
    }

    public boolean isError() {
        return isError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String error) {
        errorMsg = error;
        isError = true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HSFResponse[");
        sb.append("HSF出现异常=").append(isError).append(", ");
        sb.append("HSF异常消息=").append(errorMsg).append(", ");
        sb.append("业务层响应=").append(appResponse).append("]");
        return sb.toString();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
