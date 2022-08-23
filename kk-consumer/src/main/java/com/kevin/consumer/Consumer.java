package com.kevin.consumer;

import com.kevin.client.dto.BookDTO;
import com.kevin.client.service.IBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {
	private static Logger logger = LoggerFactory.getLogger(Consumer.class);

	public static void main(String[] args) {
		//在注册中心订阅服务, 获取服务所在的url, 然后通过代理远程调用服务
		ServiceSubscribe serviceSubscribe = new ServiceSubscribe();
		RpcServiceProxy rpcServiceProxy = new RpcServiceProxy(serviceSubscribe);
		//获取RPC代理
		IBookService bookService = (IBookService) rpcServiceProxy.getProxy(IBookService.class);
		BookDTO bookInfo = bookService.getBookInfo(1);
		System.out.println(bookInfo);
	}

}
