package com.jingyao.jingyaoaicodeassistant.ai.model.message;


import dev.langchain4j.service.tool.ToolExecution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 继承自StreamMessage的工具执行消息类
 * 用于封装工具执行的相关信息，包括ID、名称、参数和结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ToolExecutedMessage extends StreamMessage {
	
	// 工具执行的唯一标识符
	private String id;
	// 工具的名称
	private String name;
	// 工具执行所需的参数
	private String arguments;
	// 工具执行的结果
	private String result;
	
	/**
	 * 根据工具执行对象创建消息实例
	 * @param toolExecution 包含工具执行信息的对象
	 */
	public ToolExecutedMessage(ToolExecution toolExecution) {
		super(StreamMessageTypeEnum.TOOL_EXECUTED.getValue());
		this.id = toolExecution.request().id();
		this.name = toolExecution.request().name();
		this.arguments = toolExecution.request().arguments();
		this.result = toolExecution.result();
	}
}
