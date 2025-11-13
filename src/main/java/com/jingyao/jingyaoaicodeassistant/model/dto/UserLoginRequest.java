package com.jingyao.jingyaoaicodeassistant.model.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求类，实现了Serializable接口以支持序列化
 */
public class UserLoginRequest implements Serializable {
	
	// 序列化版本UID，用于控制版本兼容性
	@Serial
	private static final long serialVersionUID = 3143535165143645L;
	
	// 用户账号
	private String userAccount;
	
	// 用户密码
	private String userPassword;
}
