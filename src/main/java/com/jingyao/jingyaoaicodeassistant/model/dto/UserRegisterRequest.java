package com.jingyao.jingyaoaicodeassistant.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求类
 * 实现Serializable接口以支持序列化
 */
@Data
public class UserRegisterRequest implements Serializable {
	
	@Serial
	// 序列化版本UID，用于唯一标识可序列化类的版本
	private static final long serialVersionUID = 3191241716373120793L;
	
	
	// 用户账号，用于登录系统的唯一标识
	private String userAccount;
	
	
	// 用户密码，用于系统登录的身份验证
	private String userPassword;
	
	
	// 校验密码，用于确认用户输入的密码是否正确
	private String checkPassword;
}
