package com.jingyao.jingyaoaicodeassistant.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.ai.tools.FileWriteTool;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI代码生成器服务工厂类
 * 用于配置和创建AI代码生成器服务的Bean
 */
@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {
	
	private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.expireAfterWrite(Duration.ofMinutes(30))
		.expireAfterAccess(Duration.ofMinutes(10))
		.removalListener((key, value, cause) -> {
			log.debug("AI服务实例被移出，缓存键:{},原因:{}", key, cause);
		})
		.build();
	/**
	 * 注入ChatModel Bean
	 * 用于与AI模型进行交互
	 */
	@Resource
	private ChatModel chatModel;
	@Resource
	private StreamingChatModel openAiStreamingChatModel;
	@Resource
	private RedisChatMemoryStore redisChatMemoryStore;
	@Autowired
	private ChatHistoryService chatHistoryService;
	@Resource
	private StreamingChatModel reasoningStreamingChatModel;
	
	/**
	 * 创建并配置AiCodeGeneratorService Bean
	 * 使用AiServices工厂类创建AI代码生成器服务实例
	 *
	 * @return 配置好的AiCodeGeneratorService实例
	 */
	@Bean
	public AiCodeGeneratorService aiCodeGeneratorService() {
		return getAiCodeGeneratorService(0L);
	}
	
	private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenTypeEnum) {
		MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
			.id(appId)
			.chatMemoryStore(redisChatMemoryStore)
			.maxMessages(40)
			.build();
		
		chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 40);
		return switch (codeGenTypeEnum) {
			case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
				.streamingChatModel(reasoningStreamingChatModel)
				.chatMemoryProvider(memoryId -> chatMemory)
				.tools(new FileWriteTool())
				.hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(toolExecutionRequest, "Error: 没有一种工具叫做 " + toolExecutionRequest.name())).build();
			
			case HTML, MULTI_FILE -> AiServices.builder(AiCodeGeneratorService.class)
				.chatModel(chatModel)
				.streamingChatModel(openAiStreamingChatModel)
				.chatMemory(chatMemory)
				.build();
			
			default ->
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型：" + codeGenTypeEnum.getValue());
		};
	}
	
	public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
		return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
	}
	
	/**
	 * 根据 appId 和代码生成类型获取服务（带缓存）
	 */
	public AiCodeGeneratorService getAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
		String cacheKey = buildCacheKey(appId, codeGenType);
		return serviceCache.get(cacheKey, key -> createAiCodeGeneratorService(appId, codeGenType));
	}
	
	/**
	 * 构建缓存键
	 */
	private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType) {
		return appId + "_" + codeGenType.getValue();
	}
}
