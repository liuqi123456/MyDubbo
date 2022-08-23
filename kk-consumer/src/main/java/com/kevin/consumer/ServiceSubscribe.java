package com.kevin.consumer;

import com.kevin.client.constant.ZooKeeperConst;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ServiceSubscribe {
	private static Logger logger = LoggerFactory.getLogger(ServiceSubscribe.class);
	private ZooKeeper zk;
	private List<String> providerList;

	/**
	 * 连接ZooKeeper, 创建dubbo根节点
	 */
	public ServiceSubscribe() {
		try {
			CountDownLatch connectedSignal = new CountDownLatch(1);
			zk = new ZooKeeper(ZooKeeperConst.host, 5000, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
						connectedSignal.countDown();
					}
				}
			});
			//因为监听器是异步操作, 要保证监听器操作先完成, 即要确保先连接上ZooKeeper再返回实例.
			connectedSignal.await();

			//创建dubbo注册中心的根节点(持久节点)
			if (zk.exists(ZooKeeperConst.rootNode, false) == null) {
				zk.create(ZooKeeperConst.rootNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			logger.error("connect zookeeper server error.", e);
		}
	}

	/**
	 * 在注册中心订阅服务, 返回对应的服务url
	 * 只要第一次获取到了服务的RPC地址, 后面注册中心挂掉之后, 仍然可以继续通信.
	 * @param serviceName 服务名称
	 * @return 服务host
	 */
	public String subscribe(String serviceName) {
		//服务节点路径
		String servicePath = ZooKeeperConst.rootNode + "/" + serviceName;
		try {
			//获取服务节点下的所有子节点, 即服务的RPC地址
			providerList = zk.getChildren(servicePath, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						try {
							//循环监听
							providerList = zk.getChildren(servicePath, true);
						} catch (KeeperException | InterruptedException e) {
							logger.error("Consumer在ZooKeeper订阅服务-注册监听器报错", e);
						}
					}
				}
			});
		} catch (Exception e) {
			logger.error("从注册中心获取服务报错.", e);
		}
		logger.info(serviceName + "的服务提供者列表: " + providerList);

		//负载均衡
		RandomLoadBalance randomLoadBalance = new RandomLoadBalance();
		return randomLoadBalance.doSelect(providerList);
	}

}
