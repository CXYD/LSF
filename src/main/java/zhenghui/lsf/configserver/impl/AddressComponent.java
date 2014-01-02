package zhenghui.lsf.configserver.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import zhenghui.lsf.configserver.service.AddressService;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: zhenghui
 * Date: 13-12-22
 * Time: 下午4:58
 * 基于zk实现.根据简单原则,不做高级路由处理.直接用随机来做路由.
 */
public class AddressComponent extends ZookeeperWatcher implements AddressService, InitializingBean {

    private AtomicBoolean inited = new AtomicBoolean(false);

    private static final int DEFAULT_TIME_OUT = 30000;

    /**
     * 服务地址cache
     */
    private ConcurrentHashMap<String, Future<List<String>>> serviceAddressCache = new ConcurrentHashMap<String, Future<List<String>>>();

    /**
     * zk服务器的地址.
     */
    private String zkAdrress = "10.125.195.174:2181";

    @Override
    public void setServiceAddresses(String serviceUniqueName, String address) {
        if (StringUtils.isBlank(serviceUniqueName)) {
            return;
        }
        String path = DEFAULT_SERVER_PATH + separator + serviceUniqueName;
        createPath(path, address);
    }

    private void init() throws Exception {
        // 避免被初始化多次
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        createConnection(zkAdrress, DEFAULT_TIME_OUT);
    }

    @Override
    public String getServiceAddress(String serviceUniqueName) throws ExecutionException, InterruptedException {
        if (StringUtils.isBlank(serviceUniqueName)) {
            return null;
        }
        final String path = DEFAULT_SERVER_PATH + separator + serviceUniqueName;
        List<String> addressList;

        Future<List<String>> future = serviceAddressCache.get(path);
        if(future == null){
            FutureTask<List<String>> futureTask = new FutureTask(new Callable<List<String>>() {
                public List<String> call() {
                    return getChildren(path, true);
                }
            });
            Future<List<String>> old = serviceAddressCache.putIfAbsent(path, futureTask);
            if (old == null) {
                futureTask.run();
                addressList = futureTask.get();
            } else {
                addressList = old.get();
            }
        } else {
            addressList = future.get();
        }

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
