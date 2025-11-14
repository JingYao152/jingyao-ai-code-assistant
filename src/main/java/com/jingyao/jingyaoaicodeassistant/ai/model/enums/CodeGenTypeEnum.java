package com.jingyao.jingyaoaicodeassistant.ai.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 代码生成类型枚举类
 * 定义了两种代码生成模式：原生HTML模式和原生多文件模式
 */
@Getter
public enum CodeGenTypeEnum {
	
	/**
	 * 原生HTML模式
	 * text: 显示文本 - "原生 HTML 模式"
	 * value: 值 - "html"
	 */
	HTML("原生 HTML 模式", "html"),
	/**
	 * 原生多文件模式
	 * text: 显示文本 - "原生多文件模式"
	 * value: 值 - "multi_file"
	 */
	MULTI_FILE("原生多文件模式", "multi_file");
	
	/**
	 * 枚举的显示文本
	 */
	private final String text;
	/**
	 * 枚举的值
	 */
	private final String value;
	
	/**
	 * 枚举构造函数
	 * @param text 显示文本
	 * @param value 枚举值
	 */
	CodeGenTypeEnum(String text, String value) {
		this.text = text;
		this.value = value;
	}
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value 枚举值的value
	 * @return 枚举值
	 */
	public static CodeGenTypeEnum getEnumByValue(String value) {
		if (ObjUtil.isEmpty(value)) {
			return null;
		}
		for (CodeGenTypeEnum anEnum : CodeGenTypeEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
}
