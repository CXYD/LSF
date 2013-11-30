package zhenghui.lsf.tbremoting.demo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zhenghui
 * Date: 13-11-30
 * Time: обнГ7:35
 * To change this template use File | Settings | File Templates.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 7717993446446203989L;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
