package zhenghui.lsf;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午2:48
 */
public class MessageServiceImpl implements MessageService{
    @Override
    public void sayHello(String name) {
        System.out.println("hello,"+ name);
    }
}
