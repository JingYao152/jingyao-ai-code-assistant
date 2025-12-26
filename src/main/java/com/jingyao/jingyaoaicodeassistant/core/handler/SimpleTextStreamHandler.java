package com.jingyao.jingyaoaicodeassistant.core.handler;

import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.model.enums.ChatHistoryMessageTypeEnum;
import com.jingyao.jingyaoaicodeassistant.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 简单文本流处理器
 * 用于处理 HTML 和 MULTI_FILE 类型的流式响应
 */
@Slf4j
public class SimpleTextStreamHandler {
	
	/**
	 * 处理流式响应数据
	 * @param originFlux 原始数据流
	 * @param chatHistoryService 聊天历史服务
	 * @param appId 应用ID
	 * @param loginUser 登录用户信息
	 * @return 处理后的字符串流
	 */
	public Flux<String> handle(Flux<String> originFlux, ChatHistoryService chatHistoryService, long appId,
	                           User loginUser) {
		// 用于构建AI的完整响应内容
		StringBuilder aiResponseBuilder = new StringBuilder();
		
		return originFlux.map(chunk -> {
			// 收集AI响应内容
			aiResponseBuilder.append(chunk);
			return chunk;
		}).doOnComplete(() -> {
			// 流式响应完成后，添加AI消息到对话历史
			String aiResponse = aiResponseBuilder.toString();
			chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(),
				loginUser.getId());
		}).doOnError(error -> {
			// AI在回复过程中出现错误，也要记录错误的信息
			String errorMessage = "AI回复失败：" + error.getMessage();
			chatHistoryService.addChatMessage(appId, errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(),
				loginUser.getId());
		});
	}
}
