package zhenghui.lsf.process.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.exception.HSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.process.ProcessService;
import zhenghui.lsf.rpc.service.RPCProtocolService;
import zhenghui.lsf.rpc.service.RPCProtocolTemplateService;

import java.util.Map;
import java.util.Properties;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午4:39
 */
public class ProcessComponent implements ProcessService {

    private Logger logger = LoggerFactory.getLogger(ProcessService.class);

//    private RPCProtocolTemplateService protocolTemplateService;

    private RPCProtocolService rpcProtocolService;


    @Override
    public void publish(ServiceMetadata metadata) throws HSFException {

        try {
            rpcProtocolService.registerProvider(metadata);
        } catch (HSFException e) {
            logger.error("发布HSF服务时出现错误，请确认服务：" + metadata.getUniqueName() + "的rpc属性的配置！");
            throw e;
        }

        //todo 向configserver注册服务信息

    }

    @Override
    public Object consume(ServiceMetadata metadata) throws HSFException {
        return null;
    }

//    public void setProtocolTemplateService(RPCProtocolTemplateService protocolTemplateService) {
//        this.protocolTemplateService = protocolTemplateService;
//    }

    public void setRpcProtocolService(RPCProtocolService rpcProtocolService) {
        this.rpcProtocolService = rpcProtocolService;
    }
}
