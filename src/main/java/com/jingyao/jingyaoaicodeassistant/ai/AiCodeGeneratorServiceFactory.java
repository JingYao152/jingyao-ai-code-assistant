package com.jingyao.jingyaoaicodeassistant.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jingyao.jingyaoaicodeassistant.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
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
	
	private final Cache<Long, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.expireAfterWrite(Duration.ofMinutes(30))
		.expireAfterAccess(Duration.ofMinutes(10))
		.removalListener((key, value, cause) -> {
			log.debug("AI服务实例被移出，appId:{},原因:{}", key, cause);
		})
		.build();
	/**
	 * 注入ChatModel Bean
	 * 用于与AI模型进行交互
	 */
	@Resource
	private ChatModel chatModel;
	@Resource
	private StreamingChatModel streamingChatModel;
	@Resource
	private RedisChatMemoryStore redisChatMemoryStore;
	@Autowired
	private ChatHistoryService chatHistoryService;
	
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
	
	private AiCodeGeneratorService createAiCodeGeneratorService(long appId) {
		MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
			.id(appId)
			.chatMemoryStore(redisChatMemoryStore)
			.maxMessages(20)
			.build();
		chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
		return AiServices.builder(AiCodeGeneratorService.class)
			.chatModel(chatModel)
			.streamingChatModel(streamingChatModel)
			.chatMemory(chatMemory)
			.build();
	}
	
	public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
		return serviceCache.get(appId, this::createAiCodeGeneratorService);
	}
}
