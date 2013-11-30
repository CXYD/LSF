package zhenghui.lsf.tbremoting.demo;

import com.taobao.remoting.*;
import com.taobao.remoting.impl.DefaultServer;
import com.taobao.remoting.impl.RequestControlImpl;
import com.taobao.remoting.util.DIYExecutor;

import java.util.concurrent.Executor;

/**
 * User: zhenghui
 * Date: 13-11-29
 * Time: 下午1:29
 */
public class HelloWorld {

    private Server server;

    void startServer() throws RemotingException {
        //新建一个服务器实例,端口为1234
        server = new DefaultServer(1234);
        //设置处理器
        server.registerProcessor(new MyProcessor());
        //启动服务器
        server.start();
    }

    public static void main(String[] args) throws Exception {
        HelloWorld serverDemo = new HelloWorld();
        serverDemo.startServer();

        long timeoutMs = 3000;
        //建立一个连接
        Client client = ClientManager.getImpl().get("test", "localhost:1234");
        //同步发送请求 并得到结果
        Message message = new Message();
        message.setName("zhenghui");
        Object r = client.invokeWithSync(message, new RequestControlImpl(
                timeoutMs));
        System.out.println("the return is:" + r);
    }

    /**
     * 一个简单的处理器
     *
     * @author tianxiang
     */
    class MyProcessor implements RequestProcessor<Message> {

        @Override
        public Class<Message> interest() {
            return Message.class;
        }

        //处理接收到的请求
        @Override
        public void handleRequest(Message appRequest, AppResponseOutput respOut) {
            //写出数据到客户端
            respOut.write("I received the message.the name is " + appRequest.getName());
        }

        //处理请求的线程池，不能返回为null，否则报错
        @Override
        public Executor getExecutor() {
            return DIYExecutor.getInstance();
        }

        //线程池抛出异常时此方法被执行
        @Override
        public void onRejectedExecutionException(Message appRequest,
                                                 AppResponseOutput respOut) {
        }
    }
}
