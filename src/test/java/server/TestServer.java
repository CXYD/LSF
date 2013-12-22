package server;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import zhenghui.lsf.util.HSFSpringProviderBean;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午1:45
 */
public class TestServer {

    public static void main(String[] args){

        ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] {"biz/lsf-server.xml"});
        BeanFactory factory = (BeanFactory) context;

        HSFSpringProviderBean hsfSpringProviderBean = (HSFSpringProviderBean) factory.getBean("hsfSpringProviderBean");

    }
}
