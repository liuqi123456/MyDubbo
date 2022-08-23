package com.kevin.provider;

import com.kevin.client.protocol.Invocation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * 处理RPC, 通过反射执行方法
 */
public class ServerHandler implements Runnable {
	private Socket socket;
	private Map<String, Class> serviceMap;

	public ServerHandler(Socket socket, Map<String, Class> serviceMap) {
		this.socket = socket;
		this.serviceMap = serviceMap;
	}

	@Override
	public void run() {
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			//获取Invocation对象
			Invocation invocation = (Invocation) in.readObject();
			//执行对应方法
			Class clazz = serviceMap.get(invocation.getInterfaceName());
			Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParamTypes());
			Object invoke = method.invoke(clazz.newInstance(), invocation.getParams());
			//返回方法执行结果
			out.writeObject(invoke);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socket = null;
		}
	}
}
