package com.jingyao.jingyaoaicodeassistant.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {
	// TODO:大模型输出过多内容时会出现超时问题，需要优化
	@Resource
	private AiCodeGeneratorService aiCodeGeneratorService;
	
	@Test
	void generateHtmlCode() {
		String result = aiCodeGeneratorService.generateHtmlCode("做个工作记录小工具");
		Assertions.assertNotNull(result);
	}
	
	@Test
	void generateMultiFileCode() {
		String multiFileCode = aiCodeGeneratorService.generateMultiFileCode("做个留言板小工具");
		Assertions.assertNotNull(multiFileCode);
	}
}
