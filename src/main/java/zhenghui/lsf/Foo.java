package zhenghui.lsf;

/**
 * User: zhenghui
 * Date: 13-12-2
 * Time: 下午1:36
 */
public class Foo{


    public void bar(){
        for(;;){
            new Thread(new Runnable() {
                @Override
                public void run() {
                   stop();
                }
            }).run();
        }
    }

    private void stop(){
        for(;;){
        }
    }

    public static void main(String[] args){
        new Foo().bar();
    }
}
