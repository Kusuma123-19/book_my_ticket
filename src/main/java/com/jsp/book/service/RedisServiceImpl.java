package com.jsp.book.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jsp.book.dto.UserDto;
import com.jsp.book.entity.BookedTicket;

@Service
public class RedisServiceImpl implements RedisService {

	private final Map<String, Object> cache = new ConcurrentHashMap<>();

	private static final String USER_DTO_KEY = "dto-";
	private static final String OTP_KEY = "otp-";

	@Override
	@Async
	public void saveUserDto(String email, UserDto userDto) {
		cache.put(USER_DTO_KEY + email, userDto);
	}

	@Override
	@Async
	public void saveOtp(String email, int otp) {
		cache.put(OTP_KEY + email, otp);
	}

	@Override
	public UserDto getUserDto(String email) {
		Object value = cache.get(USER_DTO_KEY + email);
		return (value instanceof UserDto dto) ? dto : null;
	}

	@Override
	public int getOtp(String email) {
		Object value = cache.get(OTP_KEY + email);
		return (value instanceof Integer otp) ? otp : 0;
	}

	@Override
	public void saveTicket(String orderId, BookedTicket ticket) {
		cache.put(orderId, ticket);
	}

	@Override
	public BookedTicket getTicket(String orderId) {
		Object value = cache.get(orderId);
		return (value instanceof BookedTicket ticket) ? ticket : null;
	}
}
