package zhenghui.lsf.configserver.service;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午4:57
 * 集群注册服务.
 */
public interface AddressService {

    /**
     * 设置目标服务的地址
     *
     */
    public void setServiceAddresses(String serviceUniqueName,
                                    String address);

    /**
     * 获取目标服务的地址
     *
     * @param serviceUniqueName
     * @return String 当没有可用的服务地址的时候，将会返回null
     */
    public String getServiceAddress(String serviceUniqueName);

}
