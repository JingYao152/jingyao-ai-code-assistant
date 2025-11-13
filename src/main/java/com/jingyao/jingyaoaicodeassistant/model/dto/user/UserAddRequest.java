package com.jingyao.jingyaoaicodeassistant.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 用户添加请求类
 * 用于封装用户添加请求的数据信息，实现Serializable接口以支持序列化
 */
@Data
public class UserAddRequest implements Serializable {
	
	// 序列化版本号，用于控制版本兼容性
	@Serial
	private static final long serialVersionUID = 1L;
	// 用户名，用于展示的用户名称
	private String userName;
	// 用户账号，用户的唯一标识符
	private String userAccount;
	// 用户头像，存储头像图片的URL或路径
	private String userAvatar;
	// 用户简介，描述用户个人信息的文本
	private String userProfile;
	// 用户角色，定义用户在系统中的权限级别
	private String userRole;
}
