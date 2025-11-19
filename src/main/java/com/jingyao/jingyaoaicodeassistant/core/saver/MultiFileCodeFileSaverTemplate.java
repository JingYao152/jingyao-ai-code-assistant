package com.jingyao.jingyaoaicodeassistant.core.saver;

import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;

/**
 * 多文件代码保存模板实现类
 * 用于保存HTML、CSS、JS多文件代码结果到本地文件系统
 */
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
	
	/**
	 * 获取代码类型
	 * 重写父类抽象方法，指定当前实现处理的是多文件类型代码
	 *
	 * @return 代码生成类型枚举，这里返回MULTI_FILE类型
	 */
	@Override
	protected CodeGenTypeEnum getCodeType() {
		return CodeGenTypeEnum.MULTI_FILE;
	}
	
	/**
	 * 保存多文件代码
	 * 重写父类抽象方法，实现将HTML、CSS、JS三种代码分别保存到对应文件的逻辑
	 *
	 * @param result 多文件代码结果对象，包含HTML、CSS、JS三种代码内容
	 * @param baseDirPath 基础目录路径，用于确定文件保存位置
	 */
	@Override
	protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
		// 保存HTML文件
		writeToFile(baseDirPath, "index.html", result.getHtmlCode());
		// 保存CSS文件
		writeToFile(baseDirPath, "style.css", result.getCssCode());
		// 保存JS文件
		writeToFile(baseDirPath, "script.js", result.getJsCode());
	}
	
	/**
	 * 验证输入参数
	 * 重写父类方法，在基本验证基础上增加对HTML代码内容的验证
	 *
	 * @param result 多文件代码结果对象
	 * @throws BusinessException 当HTML代码为空时抛出业务异常
	 */
	@Override
	protected void validateInput(MultiFileCodeResult result) {
		// 验证输入参数是否为空（调用父类方法）
		super.validateInput(result);
		// 至少要有HTML代码
		if (result.getHtmlCode() == null || result.getHtmlCode().trim().isEmpty()) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
		}
	}
}