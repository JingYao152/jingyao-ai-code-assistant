package com.jingyao.jingyaoaicodeassistant.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录用户视图对象(Value Object)，用于封装用户登录后需要返回给前端的信息
 * 实现了Serializable接口，支持序列化操作
 */
public class LoginUserVO implements Serializable {
	
	@Serial // 标记序列化版本UID字段，用于序列化和反序列化时的版本控制
	private static final long serialVersionUID = 1L;
	
	private Long id; // 用户ID
	
	private String userAccount; // 用户账号
	
	private String userName; // 用户名称
	
	private String userAvatar; // 用户头像URL
	
	private String userProfile; // 用户个人简介
	
	private String userRole; // 用户角色
	
	private LocalDateTime createTime; // 创建时间
	
	private LocalDateTime updateTime; // 更新时间
}
