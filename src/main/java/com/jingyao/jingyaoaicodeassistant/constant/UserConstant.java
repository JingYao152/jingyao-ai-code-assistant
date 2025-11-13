package com.jingyao.jingyaoaicodeassistant.constant;

/**
 * 用户相关的常量接口
 * 该接口定义了用户系统中使用的常量值
 */
public interface UserConstant {
	/**
	 * 用户登录状态的键名常量
	 * 用于在会话或缓存中标识用户的登录状态
	 */
	String USER_LOGIN_STATE = "user_login";
	
	/**
	 * 默认用户角色的常量值
	 * 系统中普通用户默认的角色标识
	 */
	String DEFAULT_ROLE = "user";
	
	/**
	 * 管理员角色的常量值
	 * 用于标识系统管理员的角色
	 */
	String ADMIN_ROLE = "admin";
}
