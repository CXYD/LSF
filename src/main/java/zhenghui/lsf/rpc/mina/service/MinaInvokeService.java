package zhenghui.lsf.rpc.mina.service;

import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午3:36
 * 用mina实现的远程调用
 */
public interface MinaInvokeService {

    public Object invoke(HSFRequest request, ServiceMetadata metadata,
                         String targetURL) throws LSFException;
}
