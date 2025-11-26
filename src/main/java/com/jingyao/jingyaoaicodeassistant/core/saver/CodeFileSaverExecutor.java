package com.jingyao.jingyaoaicodeassistant.core.saver;

import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;

import java.io.File;

/**
 * 代码文件保存执行器类
 * 负责根据不同的代码生成类型，调用对应的代码保存模板进行文件保存操作
 */
public class CodeFileSaverExecutor {
	
	/**
	 * HTML代码文件保存模板实例
	 * 负责处理HTML类型代码的保存逻辑
	 */
	private static final HtmlCodeFileSaverTemplate htmlCodeFileSaverTemplate = new HtmlCodeFileSaverTemplate();
	
	/**
	 * 多文件代码保存模板实例
	 * 负责处理包含HTML、CSS、JS的多文件代码保存逻辑
	 */
	private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaverTemplate =
		new MultiFileCodeFileSaverTemplate();
	
	/**
	 * 执行代码保存操作的核心方法
	 * 根据代码生成类型调用对应的保存模板，将代码结果保存为文件
	 *
	 * @param codeResult 代码结果对象，需要根据codeGenTypeEnum类型进行相应的类型转换
	 * @param codeGenTypeEnum 代码生成类型枚举，决定使用哪种保存策略
	 * @param appId 应用 ID，用于构建唯一的保存目录
	 * @return 保存成功后的文件对象，通常是包含生成代码文件的目录
	 * @throws BusinessException 当遇到不支持的代码生成类型时抛出业务异常
	 * @throws ClassCastException 当codeResult与指定的codeGenTypeEnum类型不匹配时可能抛出类型转换异常
	 */
	public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
		return switch (codeGenTypeEnum) {
			case HTML -> htmlCodeFileSaverTemplate.saveCode((HtmlCodeResult) codeResult, appId);
			case MULTI_FILE -> multiFileCodeFileSaverTemplate.saveCode((MultiFileCodeResult) codeResult, appId);
			default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenTypeEnum);
		};
	}
}