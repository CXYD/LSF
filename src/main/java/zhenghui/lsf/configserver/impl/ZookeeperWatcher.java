package zhenghui.lsf.configserver.impl;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhenghui.lsf.exception.LSFException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: zhenghui
 * Date: 13-12-25
 * Time: 下午4:13
 * 一些zk的封装
 * 这里注意,别忘记初始化zk的path.比如创建节点的时候,path是 "/zhenghui/lsf/address/interfacename:1.0.0" 那么请保证 "/zhenghui/lsf/address"节点是存在的,否则会报错.
 */
public abstract class ZookeeperWatcher implements Watcher {

    private Logger logger = LoggerFactory.getLogger(ZookeeperWatcher.class);

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zk;

    protected static final String DEFAULT_SERVER_PATH = "/zhenghui/lsf/address";

    /**
     * 节点path的后缀
     */
    private static final String DEFAULT_PATH_SUFFIX = "zhenghui";

    protected static final String separator = "/";

    private static final String charset_utf8 = "utf-8";

    private Stat stat = new Stat();

    /**
     * 用来记录watch被调用次数
     */
    AtomicInteger seq = new AtomicInteger();

    /**
     * 地址变更,需要做对应的处理.比如缓存清理等
     */
    abstract protected void addressChangeHolder(String path);

    /**
     * 创建zk连接
     *
     */
    protected void createConnection(String connectString, int sessionTimeout) throws LSFException {
        //先关闭连接
        releaseConnection();
        try {
            zk = new ZooKeeper(connectString, sessionTimeout, this);
            logger.info(connectString + "开始连接ZK服务器");
            connectedSemaphore.await();
        } catch (Exception e) {
            logger.error("zhenghui.lsf.configserver.impl.AddressComponent.createConnection error");
            throw new LSFException("zhenghui.lsf.configserver.impl.ZookeeperWatcher.createConnection error", e);
        }
    }

    /**
     * 关闭ZK连接
     */
    protected void releaseConnection() {
        if (zk != null) {
            try {
                this.zk.close();
            } catch (Exception e) {
                logger.error("zhenghui.lsf.configserver.impl.AddressComponent.releaseConnection error");
            }
        }
    }

    /**
     * 创建对应的节点.
     */
    protected boolean createPath(String path, String data) {
        try {
            //先判断path是否存在
            Stat stat = exists(path, true);
            //如果不存在,则创建
            if(stat == null){
                this.zk.create(path,"zhenghui".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("父节点创建成功.path= " + path);
            }
            String childPath = path + separator + DEFAULT_PATH_SUFFIX;
            this.zk.create(childPath,data.getBytes(charset_utf8),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("子节点创建成功.path= " + childPath);
            return true;
        } catch (Exception e) {
            logger.error("zhenghui.lsf.configserver.impl.ZookeeperWatcher.createPath",e);
            return false;
        }
    }

    protected Stat exists(String path, boolean needWatch) {
        try {
            return this.zk.exists(path, needWatch);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取子节点
     *
     * @param path 节点path
     */
    protected List<String> getChildren(String path, boolean needWatch) {
        try {
            List<String> newServerList = new ArrayList<String>();
            List<String> subList = this.zk.getChildren(path, needWatch);
            if(subList != null && !subList.isEmpty()){
                for (String subNode : subList) {
                    // 获取每个子节点下关联的server地址
                    byte[] data = zk.getData(path + separator + subNode, false, stat);
                    newServerList.add(new String(data, charset_utf8));
                }
            }
            return newServerList;
        } catch (Exception e) {
            logger.error("zhenghui.lsf.configserver.impl.ZookeeperWatcher.getChildren", e);
            return null;
        }
    }

    @Override
    public void process(WatchedEvent event){
//        try {
//            Thread.sleep(300);
//        } catch (Exception e) {}
        if (event == null) return;

        String logPrefix = "Watch-" + seq.incrementAndGet() + ":";
        logger.info(logPrefix + event.toString());
        // 连接状态
        Watcher.Event.KeeperState keeperState = event.getState();
        // 事件类型
        Watcher.Event.EventType eventType = event.getType();
        // 受影响的path
        String path = event.getPath();
        if (Watcher.Event.KeeperState.SyncConnected == keeperState) {
            // 成功连接上ZK服务器
            if (Watcher.Event.EventType.None == eventType) {
                logger.info(logPrefix + "成功连接上ZK服务器");
                connectedSemaphore.countDown();
            }  else if (Watcher.Event.EventType.NodeChildrenChanged == eventType) {
                logger.info(logPrefix + "子节点变更");
                //如果是 DEFAULT_SERVER_PATH下面的接口变动,则说明是新增接口,不需要触发holder
                if(!path.equals(DEFAULT_SERVER_PATH)){
                    addressChangeHolder(path);
                }
            }
        }
        //下面可以做一些重连的工作.
        else if (Watcher.Event.KeeperState.Disconnected == keeperState) {
            logger.error(logPrefix + "与ZK服务器断开连接");
        } else if (Watcher.Event.KeeperState.AuthFailed == keeperState) {
            logger.error(logPrefix + "权限检查失败");
        } else if (Watcher.Event.KeeperState.Expired == keeperState) {
            logger.error(logPrefix + "会话失效");
        }
    }
}
