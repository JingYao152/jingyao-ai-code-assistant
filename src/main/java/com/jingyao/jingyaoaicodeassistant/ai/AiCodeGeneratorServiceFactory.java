package com.jingyao.jingyaoaicodeassistant.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI代码生成器服务工厂类
 * 用于配置和创建AI代码生成器服务的Bean
 */
@Configuration
public class AiCodeGeneratorServiceFactory {
	
	/**
	 * 注入ChatModel Bean
	 * 用于与AI模型进行交互
	 */
	@Resource
	private ChatModel chatModel;
	
	@Resource
	private StreamingChatModel streamingChatModel;
	
	/**
	 * 创建并配置AiCodeGeneratorService Bean
	 * 使用AiServices工厂类创建AI代码生成器服务实例
	 *
	 * @return 配置好的AiCodeGeneratorService实例
	 */
	@Bean
	public AiCodeGeneratorService aiCodeGeneratorService() {
		return AiServices.builder(AiCodeGeneratorService.class)
			.chatModel(chatModel)
			.streamingChatModel(streamingChatModel)
			.build();
	}
}
