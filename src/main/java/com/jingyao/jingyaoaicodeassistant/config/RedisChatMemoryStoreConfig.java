package com.jingyao.jingyaoaicodeassistant.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Redis聊天记忆存储配置类
 * 用于配置和创建RedisChatMemoryStore实例
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisChatMemoryStoreConfig {
	/**
	 * Redis服务器主机地址
	 */
	private String host;
	
	/**
	 * Redis服务器端口号
	 */
	private int port;
	
	/**
	 * Redis服务器密码
	 */
	private String password;
	
	/**
	 * 记忆存储的生存时间(TTL)，单位为毫秒
	 */
	private long ttl;
	
	/**
	 * 创建并配置RedisChatMemoryStore实例
	 * @return 配置好的RedisChatMemoryStore实例
	 */
	public RedisChatMemoryStore redisChatMemoryStoreConfig() {
		return RedisChatMemoryStore.builder()
			.host(host)
			.port(port)
			.ttl(ttl)
			.password(password)
			.build();
	}
}
