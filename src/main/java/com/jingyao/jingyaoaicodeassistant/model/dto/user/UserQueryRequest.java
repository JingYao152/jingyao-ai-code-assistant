package com.jingyao.jingyaoaicodeassistant.model.dto.user;

import com.jingyao.jingyaoaicodeassistant.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求类，继承自分页请求类，实现了序列化接口
 * 使用了Lombok的注解来自动生成equals、hashCode、getter和setter方法
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private Long id;
	/**
	 * 用户昵称
	 */
	private String userName;
	/**
	 * 账号
	 */
	private String userAccount;
	/**
	 * 简介
	 */
	private String userProfile;
	/**
	 * 用户角色:user/admin
	 */
	private String userRole;
}
