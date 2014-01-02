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
        Thread.sleep(1000);
        messageService.sayHello("hello shaoman");
        messageService.sayHello("hello shaoman2");
    }
}
