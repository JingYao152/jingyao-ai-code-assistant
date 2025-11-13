package com.jingyao.jingyaoaicodeassistant.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring MVC Json 配置
 */
@JsonComponent
public class JsonConfig {
	
	/**
	 * 配置Jackson的ObjectMapper实例，用于JSON序列化和反序列化
	 * @param builder Jackson2ObjectMapperBuilder，用于构建ObjectMapper
	 * @return 配置好的ObjectMapper实例
	 */
	@Bean
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		// 创建ObjectMapper实例，禁用XML映射功能
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		// 创建一个简单的模块，用于添加自定义的序列化器
		SimpleModule module = new SimpleModule();
		// 添加Long类型的序列化器，将Long类型转换为String类型，防止前端精度丢失
		module.addSerializer(Long.class, ToStringSerializer.instance);
		// 添加long基本类型的序列化器，将long类型转换为String类型，防止前端精度丢失
		module.addSerializer(Long.TYPE, ToStringSerializer.instance);
		// 注册自定义模块到ObjectMapper中
		objectMapper.registerModule(module);
		// 返回配置好的ObjectMapper实例
		return objectMapper;
	}
}
