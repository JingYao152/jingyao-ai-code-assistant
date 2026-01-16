package com.jingyao.jingyaoaicodeassistant.ai;

import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

/**
 * AI代码生成类型路由服务接口
 */
public interface AiCodeGenTypeRoutingService {
	/**
	 * 根据用户提示路由代码生成类型
	 * @param userPrompt 用户提示词
	 * @return 代码生成类型枚举
	 */
	@SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
	CodeGenTypeEnum routeCodeGenType(String userPrompt);
}
