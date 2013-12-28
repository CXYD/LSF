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
import zhenghui.lsf.rpc.mina.service.MinaInvokeService;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * User: zhenghui
 * Date: 13-12-27
 * Time: 下午3:48
 */
public class MinaInvokeServiceImpl implements MinaInvokeService {

    private LSFMinaInvokeHandler lsfMinaInvokeHandler;

    @Override
    public Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURL, RequestControl control) throws LSFException {
        try{
            IoConnector connector = new NioSocketConnector();
            DefaultIoFilterChainBuilder chain = connector.getFilterChain();
            ProtocolCodecFilter filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
            chain.addLast("objectFilter", filter);
            // 设定客户端的消息处理器:一个ObjectMinaClientHandler对象,
//            LSFMinaInvokeHandler handler = new LSFMinaInvokeHandler();
            connector.setHandler(lsfMinaInvokeHandler);

            // 连结到服务器:
            ConnectFuture cf = connector.connect(parseAddress(targetURL));
            // 等待连接创建完成
            cf.awaitUninterruptibly();
            cf.getSession().write(request);
            // 等待连接断开
//            cf.getSession().getCloseFuture().awaitUninterruptibly();
            // 客户端断开链接，释放资源
//            connector.dispose();
            return lsfMinaInvokeHandler.get();
        } catch (Exception e){
            throw new LSFException("zhenghui.lsf.rpc.mina.service.impl.MinaInvokeServiceImpl.invoke error",e);
        }
    }

    static private final String COLON = ":";

    private SocketAddress parseAddress(String targetURL){
        String[] ss = targetURL.split(COLON);
        return new InetSocketAddress(ss[0],Integer.parseInt(ss[1]));
    }

    public void setLsfMinaInvokeHandler(LSFMinaInvokeHandler lsfMinaInvokeHandler) {
        this.lsfMinaInvokeHandler = lsfMinaInvokeHandler;
    }
}
