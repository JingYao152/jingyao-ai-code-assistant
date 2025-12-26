package com.jingyao.jingyaoaicodeassistant.ai.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * AiResponseMessage类，继承自StreamMessage，表示AI响应消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AiResponseMessage extends StreamMessage {
	// 消息数据内容
	private String data;
	
	/**
	 * 带参数的构造函数
	 * @param data AI响应的具体内容
	 */
	public AiResponseMessage(String data) {
		super(StreamMessageTypeEnum.AI_RESPONSE.getValue());
		this.data = data;
	}
}
