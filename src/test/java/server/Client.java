package server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import zhenghui.lsf.MessageService;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午1:45
 * 客户端
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {

        ApplicationContext factory = new ClassPathXmlApplicationContext(new String[] {"biz/lsf-client.xml"});
        MessageService messageService = (MessageService) factory.getBean("messageServiceLSF");
        new MethodInvoke(messageService,"shaoman").run();
        new MethodInvoke(messageService,"shaoman2").run();
    }

}
class MethodInvoke implements Runnable{

    MethodInvoke(MessageService messageService, String message) {
        this.messageService = messageService;
        this.message = message;
    }

    MessageService messageService;

    String message;

    @Override
    public void run() {
        messageService.sayHello(message);
    }
}
