package zhenghui.lsf;

import java.lang.reflect.Method;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午2:48
 * demo 测试实现类
 */
public class MessageServiceImpl implements MessageService{
    @Override
    public String sayHello(String name) {
        System.out.println("hello,"+ name);
        return "return "+ name;
    }

    public static void main(String[] args) throws Exception {
        Method method = MessageServiceImpl.class.getMethod("sayHello",String.class);
        Object obj  = method.invoke(new MessageServiceImpl(),"hello");
    }
}
