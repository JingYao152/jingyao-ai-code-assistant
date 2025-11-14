package com.jingyao.jingyaoaicodeassistant.core;

import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.core.AiCodeGeneratorFacade;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class AiCodeGeneratorFacadeTest {
	
	@Resource
	private AiCodeGeneratorFacade aiCodeGeneratorFacade;
	
	@Test
	void generateAndSaveCode() {
		File file = aiCodeGeneratorFacade.generateAndSaveCode("任务记录网站", CodeGenTypeEnum.MULTI_FILE);
		Assertions.assertNotNull(file);
	}
}
