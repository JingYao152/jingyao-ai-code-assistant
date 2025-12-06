package com.jingyao.jingyaoaicodeassistant.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
public class ReasoningStreamChatModelConfig {
	
	private String baseUrl;
	
	private String apiKey;
	
	@Bean
	public StreamingChatModel reasoningStreamChatModel() {
		final String modelName = "deepseek-chat";
		final int maxTokens = 8192;
		return OpenAiStreamingChatModel.builder()
			.apiKey(apiKey)
			.modelName(modelName)
			.maxTokens(maxTokens)
			.baseUrl(baseUrl)
			.logRequests(true)
			.logResponses(true)
			.build();
	}
}
