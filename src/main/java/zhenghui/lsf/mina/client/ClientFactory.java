package zhenghui.lsf.mina.client;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import zhenghui.lsf.mina.client.impl.DefaultClient;
import zhenghui.lsf.rpc.mina.service.impl.LSFMinaInvokeHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * User: zhenghui
 * Date: 13-12-29
 * Time: 下午8:56
 * 创建client对象的工程方法
 */
public class ClientFactory {

    IoConnector connector;


    Client createClient(String targetUrl) throws Exception{
        return new ClientFuture(connet(targetUrl)).get(-1);
    }

    void destroyClient(){
        if(connector != null && connector.isActive()){
            connector.dispose();
        }
    }

    private ConnectFuture connet(String targetUrl){
        IoConnector connector = new NioSocketConnector();
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        ProtocolCodecFilter filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
        chain.addLast("objectFilter", filter);
        // 设定客户端的消息处理器:一个ObjectMinaClientHandler对象,
        connector.setHandler(new LSFMinaInvokeHandler());

        // 连结到服务器:
        return connector.connect(parseAddress(targetUrl));
    }

    static private final String COLON = ":";

    private SocketAddress parseAddress(String targetURL){
        String[] ss = targetURL.split(COLON);
        return new InetSocketAddress(ss[0],Integer.parseInt(ss[1]));
    }

    class ClientFuture{

        private ConnectFuture connectFuture;

        ClientFuture(ConnectFuture connectFuture) {
            this.connectFuture = connectFuture;
        }

        public boolean isDone(){
            return connectFuture.isDone();
        }

        public Client get(long timeoutMs) throws Exception{
            if(timeoutMs <= 0){
                connectFuture.awaitUninterruptibly();
            } else {
                connectFuture.await(timeoutMs);
            }
            if(!isDone()){
                return null;
            }
            return new DefaultClient(connectFuture.getSession());
        }
    }
}
