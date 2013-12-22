package zhenghui.lsf.rpc.tbremoting.service;

import com.taobao.remoting.RequestControl;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午5:10
 */
public interface InvokeService {

    /**
     * 调用HSF服务
     *
     */
    public Object invoke(HSFRequest request, ServiceMetadata metadata,
                         String targetURL, RequestControl control) throws HSFException;

}
