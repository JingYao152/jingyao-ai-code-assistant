package com.jingyao.jingyaoaicodeassistant.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Data;
import lombok.Getter;

/**
 * 用户角色枚举类
 * 使用@Getter注解自动为所有字段生成getter方法
 */
@Getter
public enum UserRoleEnum {
	
	// 枚举值定义，包含中文描述和英文值
	USER("用户", "user"),    // 普通用户角色
	ADMIN("管理员", "admin"); // 管理员角色
	
	// 枚举字段：角色描述文本
	private final String text;
	// 枚举字段：角色值
	private final String value;
	
	/**
	 * 枚举构造函数
	 * @param text 角色的中文描述
	 * @param value 角色的英文值
	 */
	UserRoleEnum(String text, String value) {
		this.text = text;
		this.value = value;
	}
	
	/**
	 * 根据给定的值获取对应的用户角色枚举
	 * @param value 要查找的值
	 * @return 匹配到的用户角色枚举，如果没有匹配则返回null
	 */
	public static UserRoleEnum getUserByValue(String value) {
		// 检查输入值是否为空
		if (ObjUtil.isEmpty(value)) {
			return null;
		}
		// 遍历所有用户角色枚举值
		for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
			// 检查当前枚举的值是否与输入值匹配
			if (userRoleEnum.getValue().equals(value)) {
				// 如果匹配，则返回该枚举
				return userRoleEnum;
			}
		}
		// 如果没有匹配的枚举，则返回null
		return null;
	}
}
