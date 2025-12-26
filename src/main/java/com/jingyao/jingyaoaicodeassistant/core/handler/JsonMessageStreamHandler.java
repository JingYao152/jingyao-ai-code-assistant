package com.jingyao.jingyaoaicodeassistant.core.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.message.*;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.model.enums.ChatHistoryMessageTypeEnum;
import com.jingyao.jingyaoaicodeassistant.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

/**
 * JSON消息流处理器
 * 处理VUE项目类型的复杂流式响应，包含工具调用信息
 */
@Slf4j
@Component
public class JsonMessageStreamHandler {
	/**
	 * 处理JSON消息流
	 * @param originFlux 原始消息流
	 * @param chatHistoryService 聊天历史服务
	 * @param appId 应用ID
	 * @param loginUser 登录用户
	 * @return 处理后的消息流
	 */
	public Flux<String> handle(Flux<String> originFlux, ChatHistoryService chatHistoryService, long appId,
	                           User loginUser) {
		// 收集信息用于生成后端记忆格式
		StringBuilder chatHistoryStringBuilder = new StringBuilder();
		// 用于记录已处理的工具ID，避免重复处理
		Set<String> seenToolIds = new HashSet<>();
		return originFlux.map(chunk -> {
				// 解析每个JSON消息块并返回处理结果
				return handleJsonMessageChunk(chunk, chatHistoryStringBuilder, seenToolIds);
			}).filter(StrUtil::isNotEmpty)  // 过滤空字符串
			.doOnComplete(() -> {
				// 流式响应完成后，添加AI消息到对话历史
				String aiResponse = chatHistoryStringBuilder.toString();
				chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(),
					loginUser.getId());
			}).doOnError(error -> {
				// 处理流式响应过程中的错误
				String errorMessage = "AI回复失败：" + error.getMessage();
				chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(),
					loginUser.getId());
			});
	}
	
	/**
	 * 处理单个JSON消息块
	 * @param chunk JSON消息块
	 * @param chatHistoryStringBuilder 用于构建聊天历史的字符串构建器
	 * @param seenToolIds 已处理的工具ID集合
	 * @return 处理后的消息内容
	 */
	private String handleJsonMessageChunk(String chunk, StringBuilder chatHistoryStringBuilder,
	                                      Set<String> seenToolIds) {
		// 解析JSON为StreamMessage对象
		StreamMessage streamMessage = JSONUtil.toBean(chunk, StreamMessage.class);
		// 获取消息类型枚举
		StreamMessageTypeEnum typeEnum = StreamMessageTypeEnum.getEnumByValue(streamMessage.getType());
		
		if (typeEnum != null) {
			switch (typeEnum) {
				case AI_RESPONSE -> {
					// 处理AI响应消息
					AiResponseMessage aiResponseMessage = JSONUtil.toBean(chunk, AiResponseMessage.class);
					String data = aiResponseMessage.getData();
					// 直接拼接响应内容
					chatHistoryStringBuilder.append(data);
					return data;
				}
				case TOOL_REQUEST -> {
					// 处理工具请求消息
					ToolRequestMessage toolRequestMessage = JSONUtil.toBean(chunk, ToolRequestMessage.class);
					String toolId = toolRequestMessage.getId();
					
					// 如果是新工具ID，则处理并记录
					if (toolId != null && !seenToolIds.contains(toolId)) {
						seenToolIds.add(toolId);
						return "\n\n[选择工具] 写入文件\n\n";
					} else {
						return "";
					}
				}
				case TOOL_EXECUTED -> {
					// 处理工具执行结果消息
					ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(chunk, ToolExecutedMessage.class);
					JSONObject jsonObject = JSONUtil.parseObj(toolExecutedMessage.getArguments());
					String relativeFilePath = jsonObject.getStr("relativeFilePath");
					String suffix = FileUtil.getSuffix(relativeFilePath);
					String content = jsonObject.getStr("content");
					String result = String.format("""
						[工具调用] 写入文件 %s
						```%s
						%s
						```
						""", relativeFilePath, suffix, content);
					
					// 输出前端和要持久化的内容
					String output = String.format("\n\n%s\n\n", result);
					chatHistoryStringBuilder.append(output);
					return output;
				}
				default -> {
					log.error("不支持的消息类型：{}", typeEnum);
					return "";
				}
			}
		} else {
			return "";
		}
	}
}
