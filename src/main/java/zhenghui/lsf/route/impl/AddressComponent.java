package zhenghui.lsf.route.impl;

import zhenghui.lsf.route.service.AddressService;

import java.util.List;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午4:58
 */
public class AddressComponent implements AddressService {

    @Override
    public void setServiceAddresses(String serviceUniqueName, List<String> addresses) {
        
    }

    @Override
    public String getServiceAddress(String serviceUniqueName, String methodName, String[] paramTypeStrs, Object[] args) {
        return "127.0.0.1";
    }

    @Override
    public void setServiceRouteRule(String serviceUniqueName, Object rawRouteRuleObj) {
        
    }
}
