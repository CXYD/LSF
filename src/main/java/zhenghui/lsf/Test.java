package zhenghui.lsf;

/**
 * User: zhenghui
 * Date: 13-11-29
 * Time: 下午1:12
 */
public class Test <T>{

    private T x;


    public void setX(T x) {
        this.x = x;
    }

    public static void main(String[] args) {
        Test<Integer> t = new Test<Integer>();
        t.setX(1);
    }
}
