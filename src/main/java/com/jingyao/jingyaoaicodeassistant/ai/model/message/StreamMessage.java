package com.jingyao.jingyaoaicodeassistant.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StreamMessage类，用于表示流消息
 */
@Data // Lombok注解，自动生成getter、setter、toString等方法
@AllArgsConstructor // Lombok注解，生成包含所有字段参数的构造方法
@NoArgsConstructor // Lombok注解，生成无参构造方法
public class StreamMessage {
	/**
	 * 消息类型字段
	 * 用于标识消息的类型
	 */
	private String type;
}
