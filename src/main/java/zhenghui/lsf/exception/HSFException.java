package zhenghui.lsf.exception;

/**
 * User: zhenghui
 * Date: 13-12-18
 * Time: 下午4:36
 */
public class HSFException extends Exception {

    static final long serialVersionUID = -3387516993121229948L;

    /**
     * <p>
     * <p/>
     * 平台无关的分行符,从sun的<code>PrintStream</code>那里抄来的
     * </p>
     */
    private final static String lineSeparator = System
            .getProperty("line.separator");

    /**
     * HSF Exception reason and solution
     */
    private final String reasonAndSolution;

    /**
     *
     */
    private final String hsfExceptionDesc;

    /**
     * @param code HSF Exception Code ,HSF-004 etc.
     * @param desc description of HSF Exception
     * @param e
     */
    public HSFException(String hsfExceptionCode, String hsfExceptionDesc,
                        Throwable e) {
        super(hsfExceptionDesc, e);
        this.reasonAndSolution = hsfExceptionCode;
        this.hsfExceptionDesc = hsfExceptionDesc;
    }

    /**
     * @param code
     * @param e
     */
    public HSFException(String hsfExceptionCode, Throwable e) {
        this(hsfExceptionCode, null, e);
    }

    public HSFException(String hsfExceptionCode, String hsfExceptionDesc) {
        super(hsfExceptionCode);
        this.reasonAndSolution = hsfExceptionCode;
        this.hsfExceptionDesc = hsfExceptionDesc;
    }

    public HSFException(String hsfExceptionCode) {
        this(hsfExceptionCode, "");
    }

    public String toString() {
        String res = this.reasonAndSolution;
        if (this.hsfExceptionDesc != null) {
            StringBuilder strBuilder=new StringBuilder(res);
            strBuilder.append(lineSeparator);
            strBuilder.append("描述信息：");
            strBuilder.append(hsfExceptionDesc);
            return strBuilder.toString();
        }
        return res;
    }

}
