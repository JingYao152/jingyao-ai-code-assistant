package com.jingyao.jingyaoaicodeassistant.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Data;
import lombok.Getter;

@Getter
public enum UserRoleEnum {
	USER("用户", "user"),
	ADMIN("管理员", "admin");
	
	private final String text;
	private final String value;
	
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
