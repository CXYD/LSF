package zhenghui.lsf.route.service;

import java.util.List;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午4:57
 */
public interface AddressService {

    /**
     * 设置目标服务的地址
     *
     * @param serviceUniqueName
     * @param addresses
     */
    public void setServiceAddresses(String serviceUniqueName,
                                    List<String> addresses);

    /**
     * 获取目标服务的地址
     *
     * @param serviceUniqueName
     * @param methodName
     * @return String 当没有可用的服务地址的时候，将会返回null
     */
    public String getServiceAddress(String serviceUniqueName, String methodName, String[] paramTypeStrs, Object[] args);

    /**
     * 设置服务的路由规则
     *
     */
    public void setServiceRouteRule(String serviceUniqueName, Object rawRouteRuleObj);
}
