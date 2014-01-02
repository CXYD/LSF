package zhenghui.lsf.rpc.mina.service.impl;

import com.taobao.remoting.RequestControl;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import zhenghui.lsf.domain.HSFRequest;
import zhenghui.lsf.exception.LSFException;
import zhenghui.lsf.metadata.ServiceMetadata;
import zhenghui.lsf.mina.client.Client;
import zhenghui.lsf.mina.client.ClientManager;
import zhenghui.lsf.rpc.mina.service.MinaInvokeService;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午3:48
 */
public class MinaInvokeServiceImpl implements MinaInvokeService {

    private static final long TIME_OUT_MS = 3000;

    @Override
    public Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURL, RequestControl control) throws LSFException {
        try{
            Client client = ClientManager.getInstance().getClient(targetURL);
            return client.invoke(request,TIME_OUT_MS);
        } catch (Exception e){
            throw new LSFException("zhenghui.lsf.rpc.mina.service.impl.MinaInvokeServiceImpl.invoke error",e);
        }
    }

    static private final String COLON = ":";

    private SocketAddress parseAddress(String targetURL){
        String[] ss = targetURL.split(COLON);
        return new InetSocketAddress(ss[0],Integer.parseInt(ss[1]));
    }

}
