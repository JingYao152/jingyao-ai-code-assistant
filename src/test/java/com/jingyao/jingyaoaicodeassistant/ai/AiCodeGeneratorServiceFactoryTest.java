package com.jingyao.jingyaoaicodeassistant.ai;

import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {
	@Resource
	private AiCodeGeneratorService aiCodeGeneratorService;
	
	@Test
	void generateHtmlCode() {
		HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("做个工作记录小工具");
		Assertions.assertNotNull(result);
	}
	
	@Test
	void generateMultiFileCode() {
		MultiFileCodeResult multiFileCode = aiCodeGeneratorService.generateMultiFileCode("做个留言板小工具，代码量不超过30行");
		Assertions.assertNotNull(multiFileCode);
	}
}
