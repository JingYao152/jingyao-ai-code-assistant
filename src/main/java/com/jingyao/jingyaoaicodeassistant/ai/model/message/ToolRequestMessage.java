package com.jingyao.jingyaoaicodeassistant.ai.model.message;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 继承自StreamMessage的工具请求消息类
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ToolRequestMessage extends StreamMessage {
	// 工具请求的唯一标识符
	private String id;
	
	// 工具的名称
	private String name;
	
	// 工具执行的参数，以字符串形式存储
	private String arguments;
	
	/**
	 * 根据工具执行请求创建工具请求消息的构造函数
	 * @param toolExecutionRequest 包含工具执行所需信息的请求对象
	 */
	public ToolRequestMessage(ToolExecutionRequest toolExecutionRequest) {
		super(StreamMessageTypeEnum.TOOL_REQUEST.getValue()); // 调用父类构造函数，设置消息类型为工具请求
		this.id = toolExecutionRequest.id(); // 设置工具ID
		this.name = toolExecutionRequest.name(); // 设置工具名称
		this.arguments = toolExecutionRequest.arguments(); // 设置工具参数
	}
}
