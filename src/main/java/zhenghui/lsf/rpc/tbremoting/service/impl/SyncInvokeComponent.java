package zhenghui.lsf.rpc.tbremoting.service.impl;

import com.taobao.remoting.*;
import com.taobao.remoting.impl.RequestControlImpl;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.domain.HSFResponse;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.rpc.tbremoting.service.InvokeService;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午5:11
 * 同步调用服务
 */
public class SyncInvokeComponent implements InvokeService {

    public static final String APPTYPE_FORREMOTING = "_HSF";

    public static final long TIME_OUT_MS = 3000;

    @Override
    public Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURL, RequestControl control) throws HSFException {
        try {
            Client client = ClientManager.getImpl().get(
                    APPTYPE_FORREMOTING,
                    "localhost:8888");
            HSFResponse response = (HSFResponse) client.invokeWithSync(request,
                    new RequestControlImpl(TIME_OUT_MS));

            if (response.isError()) {
                throw new HSFException(response.getErrorMsg(), response
                        .getErrorMsg());
            }
            return response.getAppResponse();
        }
        // 超时
        catch (TimeoutException e) {
            throw new HSFException("time out exception", e);
        } catch (RemotingException e) {
            throw new HSFException("", e);
        } catch (InterruptedException e) {
            throw new HSFException("", e);
        }
    }

}
