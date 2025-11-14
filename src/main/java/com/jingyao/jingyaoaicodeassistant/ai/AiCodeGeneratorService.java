package com.jingyao.jingyaoaicodeassistant.ai;

import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {
	
	String generateCode(String userMessage);
	
	/**
	 * 生成 HTML 代码
	 *
	 * @param userMessage 用户消息
	 * @return 生成的代码结果
	 */
	@SystemMessage(fromResource = "prompt/html-system-prompt.txt")
	HtmlCodeResult generateHtmlCode(String userMessage);
	
	/**
	 * 生成多文件代码
	 *
	 * @param userMessage 用户消息
	 * @return 生成的代码结果
	 */
	@SystemMessage(fromResource = "prompt/multi-file-system-prompt.txt")
	MultiFileCodeResult generateMultiFileCode(String userMessage);
	
	/**
	 * 生成 HTML 代码（流式）
	 *
	 * @param userMessage 用户消息
	 * @return 生成的代码结果
	 */
	@SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
	Flux<String> generateHtmlCodeStream(String userMessage);
	
	/**
	 * 生成多文件代码（流式）
	 *
	 * @param userMessage 用户消息
	 * @return 生成的代码结果
	 */
	@SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
	Flux<String> generateMultiFileCodeStream(String userMessage);
	
}
