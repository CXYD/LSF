package zhenghui.lsf.datastore.service;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 下午7:29
 *
 * 做一些简单的缓存
 */
public interface DataStoreService {

    public void put(String componentName, String key, Object value);

    public <T> T get(String componentName, String key);

    public void remove(String componentName, String key);
}
