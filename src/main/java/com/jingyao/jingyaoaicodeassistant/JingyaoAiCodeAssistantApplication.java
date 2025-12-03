package com.jingyao.jingyaoaicodeassistant;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.jingyao.jingyaoaicodeassistant.mapper")
public class JingyaoAiCodeAssistantApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JingyaoAiCodeAssistantApplication.class, args);
	}
	
}
