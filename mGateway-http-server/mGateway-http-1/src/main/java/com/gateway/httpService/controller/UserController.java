package com.gateway.httpService.controller;

import com.gateway.client.api.ApiInvoker;
import com.gateway.client.api.ApiProtocol;
import com.gateway.client.api.ApiService;
import com.gateway.common.constant.FilterConst;
import com.gateway.common.utils.jwt.JWTUtil;
import com.gateway.httpService.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RestController
@ApiService(serviceId = "user-server", protocol = ApiProtocol.HTTP, patternPath = "/user/**")
@Slf4j
public class UserController {

	/**
	 * 模拟登录接口
	 */
	@ApiInvoker(path = "/user/login")
	@GetMapping("/user/login")
	public String login(@RequestParam("phoneNumber") String phoneNumber,
						@RequestParam("code") String code,
						HttpServletResponse response) {

		Map<String, Object> params = new HashMap<>();
		params.put(FilterConst.TOKEN_USERID_KEY, String.valueOf(phoneNumber + code));
		String token = JWTUtil.generateToken(params, FilterConst.TOKEN_SECRET);

		log.info("token:{}", token);

		response.addCookie(new Cookie(FilterConst.COOKIE_KEY, token));
		return token;
	}

	@ApiInvoker(path = "/user/userInfo")
	@GetMapping("/user/userInfo")
	public UserInfo getUserInfo(@RequestHeader("userId") String userId) {
		log.info("userId :{}", userId);

		return UserInfo.builder()
				.id(Integer.parseInt(userId))
				.name("yu")
				.phoneNumber("1234")
				.build();
	}

}