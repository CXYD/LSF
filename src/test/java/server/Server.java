package server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午1:45
 * 服务端
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {

        new ClassPathXmlApplicationContext(new String[]{"biz/lsf-server.xml"});

    }
}
