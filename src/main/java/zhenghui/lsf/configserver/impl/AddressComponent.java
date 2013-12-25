package zhenghui.lsf.configserver.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import zhenghui.lsf.configserver.service.AddressService;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午4:58
 * 基于zk实现.根据简单原则,不做高级路由处理.直接用随机来做路由.
 */
public class AddressComponent extends ZookeeperWatcher implements AddressService, InitializingBean {

    private AtomicBoolean inited = new AtomicBoolean(false);

    private static final String DEFAULT_SERVER_PATH = "/zhenghui/lsf/address/";

    private static final int DEFAULT_TIME_OUT  = 30000;

    /**
     * 服务地址cache
     */
    private Map<String,List<String>> serviceAddressCache = new ConcurrentHashMap<String, List<String>>();

    /**
     * zk服务器的地址.
     */
    private String zkAdrress = "10.125.195.174:2181";

    @Override
    public void setServiceAddresses(String serviceUniqueName, String address) {
        if(StringUtils.isBlank(serviceUniqueName)){
            return;
        }
        String path = DEFAULT_SERVER_PATH  + serviceUniqueName;
        createPath(path,address);
    }

    private void init() throws Exception{
        // 避免被初始化多次
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        createConnection(zkAdrress,DEFAULT_TIME_OUT);
    }

    @Override
    public String getServiceAddress(String serviceUniqueName) {
        if(StringUtils.isBlank(serviceUniqueName)){
            return null;
        }
        String path = DEFAULT_SERVER_PATH  + serviceUniqueName;
        List<String> addressList = serviceAddressCache.get(path) == null ? getChildren(path,true) : serviceAddressCache.get(path);
        if(addressList == null || addressList.isEmpty()){
            return null;
        }
        serviceAddressCache.put(path,addressList);
        return addressList.get(new Random().nextInt(addressList.size()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void setZkAdrress(String zkAdrress) {
        this.zkAdrress = zkAdrress;
    }

    @Override
    protected void addressChangeHolder(String path) {
        serviceAddressCache.remove(path);
    }
}
