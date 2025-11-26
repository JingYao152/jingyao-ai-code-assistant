package com.jingyao.jingyaoaicodeassistant.core;


import com.jingyao.jingyaoaicodeassistant.ai.AiCodeGeneratorService;
import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.core.parser.CodeParserExecutor;
import com.jingyao.jingyaoaicodeassistant.core.saver.CodeFileSaverExecutor;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {
	
	/**
	 * AI代码生成服务，负责通过AI模型生成代码
	 */
	@Resource
	private AiCodeGeneratorService aiCodeGeneratorService;
	
	/**
	 * 处理代码流，将流式接收的代码片段进行拼接、解析和保存。
	 *
	 * @param codeStream 包含代码片段的响应式流
	 * @param codeGenTypeEnum 代码生成类型枚举，用于指定代码解析和保存的方式
	 * @return 返回原始代码流
	 */
	private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
		StringBuilder codeBuilder = new StringBuilder();
		return codeStream.doOnNext(codeBuilder::append).doOnComplete(
			() -> {
				// 流式返回完成后保存代码
				try {
					String completeCode = codeBuilder.toString();
					// 解析代码
					Object codeResult = CodeParserExecutor.executeParser(completeCode, codeGenTypeEnum);
					// 保存代码到文件
					File savedDir = CodeFileSaverExecutor.executeSaver(codeResult, codeGenTypeEnum, appId);
					log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
				} catch (Exception e) {
					log.error("保存失败: {}", e.getMessage(), e);
				}
			}
		);
	}
	
	/**
	 * 统一入口：根据类型生成并保存代码
	 *
	 * @param userMessage 用户提示词
	 * @param codeGenTypeEnum 生成类型枚举，指定生成HTML还是多文件代码
	 * @return 保存代码的目录文件对象
	 * @throws BusinessException 当生成类型为空或不支持时抛出业务异常
	 */
	public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
		if (codeGenTypeEnum == null) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
		}
		return switch (codeGenTypeEnum) {
			case HTML -> {
				// 生成HTML代码
				HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
				// 保存生成的代码到文件
				File savedDir = CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.HTML, appId);
				// 记录保存成功的日志
				log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
				yield savedDir;
			}
			case MULTI_FILE -> {
				// 生成多文件代码
				MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode(userMessage);
				// 保存生成的代码到文件
				File savedDir = CodeFileSaverExecutor.executeSaver(result, CodeGenTypeEnum.MULTI_FILE, appId);
				// 记录保存成功的日志
				log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
				yield savedDir;
			}
			default -> {
				String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
			}
		};
	}
	
	/**
	 * 统一入口：根据类型生成并保存代码（流式）
	 *
	 * @param userMessage 用户提示词
	 * @param codeGenTypeEnum 生成类型枚举，指定生成HTML还是多文件代码
	 * @return 包含生成代码片段的响应式流
	 * @throws BusinessException 当生成类型为空或不支持时抛出业务异常
	 */
	public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
		// 检查生成类型是否为空，为空则抛出业务异常
		if (codeGenTypeEnum == null) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
		}
		// 根据不同的代码生成类型进行处理
		return switch (codeGenTypeEnum) {
			// 处理HTML代码生成类型
			case HTML -> {
				// 生成HTML代码流
				Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
				// 处理代码流并返回结果
				yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
			}
			// 处理多文件代码生成类型
			case MULTI_FILE -> {
				// 生成多文件代码流
				Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
				// 处理代码流并返回结果
				yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
			}
			// 处理不支持的生成类型
			default -> {
				// 构造错误消息
				String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
				// 抛出业务异常
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
			}
		};
	}
	
}