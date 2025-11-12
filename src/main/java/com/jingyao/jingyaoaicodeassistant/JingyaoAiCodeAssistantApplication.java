package com.jingyao.jingyaoaicodeassistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.jingyao.jingyaoaicodeassistant.mapper")
public class JingyaoAiCodeAssistantApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JingyaoAiCodeAssistantApplication.class, args);
	}
	
}
