package com.jingyao.jingyaoaicodeassistant.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StreamMessageTypeEnum {
	AI_RESPONSE("ai_response", "AI响应"),
	TOOL_REQUEST("tool_request", "工具请求"),
	TOOL_EXECUTED("tool_executed", "工具执行结果");
	
	private final String value;
	private final String text;
	
	/**
	 * 根据传入的值获取对应的枚举类型
	 * @param value 要匹配的字符串值
	 * @return 匹配到的枚举类型，如果未找到则返回null
	 */
	public static StreamMessageTypeEnum getEnumByValue(String value) {
		// 遍历所有枚举值
		for (StreamMessageTypeEnum typeEnum : values()) {
			// 检查当前枚举的值是否与传入的值相等
			if (typeEnum.getValue().equals(value)) {
				// 如果匹配成功，返回当前枚举
				return typeEnum;
			}
		}
		// 如果遍历结束仍未找到匹配的枚举，返回null
		return null;
	}
}
