package com.jingyao.jingyaoaicodeassistant.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求类，用于封装用户更新操作的相关数据
 * 实现了Serializable接口，使得该类可以被序列化
 */
@Data
public class UserUpdateRequest implements Serializable {
	
	
	/**
	 * 序列化版本UID，用于版本控制
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 用户ID，用于唯一标识一个用户
	 */
	private Long id;
	/**
	 * 用户名，用于显示和登录的用户名称
	 */
	private String userName;
	/**
	 * 用户头像，存储用户头像的URL或路径信息
	 */
	private String userAvatar;
	/**
	 * 用户个人简介，描述用户的基本信息或个人介绍
	 */
	private String userProfile;
	/**
	 * 用户角色，标识用户在系统中的角色和权限
	 */
	private String userRole;
}
