package zhenghui.lsf.datastore.impl;

import zhenghui.lsf.datastore.service.DataStoreService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: zhenghui
 * Date: 13-12-19
 * Time: 下午7:30
 */
public class ThreadNotSafeDataStoreComponent implements DataStoreService {


    private Map<String/**组件类名*/, Map<String/**数据名*/, Object/**数据值*/>> datas=new HashMap<String, Map<String,Object>>();

    /* (non-Javadoc)
     * @see com.taobao.hsf.datastore.service.DataStoreService#get(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String componentName, String key) {
        if(!datas.containsKey(componentName)){
            return null;
        }
        return (T) datas.get(componentName).get(key);
    }

    /* (non-Javadoc)
     * @see com.taobao.hsf.datastore.service.DataStoreService#put(java.lang.String, java.lang.String, java.lang.Object)
     */
    public void put(String componentName, String key, Object value) {
        Map<String, Object> componentDatas=null;
        if(!datas.containsKey(componentName)){
            componentDatas=new HashMap<String, Object>();
        }
        else{
            componentDatas=datas.get(componentName);
        }
        componentDatas.put(key, value);
        datas.put(componentName, componentDatas);
    }

    /* (non-Javadoc)
     * @see com.taobao.hsf.datastore.service.DataStoreService#remove(java.lang.String, java.lang.String)
     */
    public void remove(String componentName, String key) {
        if(!datas.containsKey(componentName)){
            return;
        }
        datas.get(componentName).remove(key);
    }
}
