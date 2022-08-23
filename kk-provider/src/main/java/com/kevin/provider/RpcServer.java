package com.kevin.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * RPC监听服务, 监听consumer远程调用的tcp连接
 */
public class RpcServer {
	private static Logger logger = LoggerFactory.getLogger(RpcServer.class);
	private Map<String, Class> serviceMap;

	public RpcServer(Map<String, Class> serviceMap) {
		this.serviceMap = serviceMap;
	}

	/**
	 * 启动RPC监听服务
	 */
	public void start() {
		//监听端口, 处理rpc请求
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(12000);
			logger.info("RPC监听服务启动...");
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new ServerHandler(socket, serviceMap)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
