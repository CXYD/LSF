package zhenghui.lsf.process;

import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午4:34
 */
public interface ProcessService {

    /**
     * 对外提供HSF服务
     *
     * @param metadata 服务模型对象
     *
     * @throws zhenghui.lsf.exception.LSFException 当服务发布失败时，抛出此异常
     */
    public void publish(ServiceMetadata metadata) throws LSFException;

    /**
     * 生成调用远程HSF服务的代理<br>
     * 此代理的效果为生成ServiceMetadata中指定的interface的代理，调用时可将代理转型为服务接口，并进行直接的对象调用<br>
     * 代理将完成对于远程HSF的调用
     *
     * @param metadata 服务模型对象
     *
     * @throws zhenghui.lsf.exception.LSFException 当生成调用远程HSF服务的代理失败时，抛出此异常
     */
    public Object consume(ServiceMetadata metadata) throws LSFException;
}
