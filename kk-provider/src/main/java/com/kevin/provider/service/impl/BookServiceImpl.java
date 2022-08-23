package com.kevin.provider.service.impl;

import com.kevin.client.dto.BookDTO;
import com.kevin.client.service.IBookService;

public class BookServiceImpl implements IBookService {

	@Override
	public BookDTO getBookInfo(int id) {
		if (id == 1) {
			BookDTO bookDTO = new BookDTO();
			bookDTO.setId(1);
			bookDTO.setName("仙逆");
			bookDTO.setDesc("顺为凡, 逆为仙, 只在心中一念间.");
			bookDTO.setAuthor("耳根");
			return bookDTO;
		} else {
			return new BookDTO();
		}
	}

}
