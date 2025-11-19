package com.jingyao.jingyaoaicodeassistant.core.parser;

import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;

/**
 * 代码解析器执行器类，用于根据不同的代码生成类型执行相应的代码解析操作。
 */
public class CodeParserExecutor {
	
	/**
	 * HTML代码解析器实例，用于解析HTML类型的代码。
	 */
	private static final HtmlCodeParser HTML_CODE_PARSER = new HtmlCodeParser();
	
	/**
	 * 多文件代码解析器实例，用于解析多文件类型的代码。
	 */
	private static final MultiFileCodeParser MULTI_FILE_CODE_PARSER = new MultiFileCodeParser();
	
	/**
	 * 根据代码生成类型执行相应的代码解析操作。
	 *
	 * @param codeContent 要解析的代码内容
	 * @param codeGenTypeEnum 代码生成类型枚举，支持HTML和多文件类型
	 * @return 解析后的结果对象
	 * @throws BusinessException 当遇到不支持的代码解析类型时抛出
	 */
	public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {
		return switch (codeGenTypeEnum) {
			case HTML -> HTML_CODE_PARSER.parseCode(codeContent);
			case MULTI_FILE -> MULTI_FILE_CODE_PARSER.parseCode(codeContent);
			default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码解析类型: " + codeGenTypeEnum);
		};
	}
}
