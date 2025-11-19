package com.jingyao.jingyaoaicodeassistant.core.saver;

import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;

/**
 * HTML代码文件保存模板实现类
 * 用于保存HTML单文件代码结果到本地文件系统
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {
	/**
	 * 获取代码类型
	 * 重写父类抽象方法，指定当前实现处理的是HTML类型代码
	 *
	 * @return 代码生成类型枚举，这里返回HTML类型
	 */
	@Override
	protected CodeGenTypeEnum getCodeType() {
		return CodeGenTypeEnum.HTML;
	}
	
	/**
	 * 保存HTML代码文件
	 * 重写父类抽象方法，实现将HTML代码保存到index.html文件的逻辑
	 *
	 * @param result HTML代码结果对象，包含需要保存的HTML代码
	 * @param baseDirPath 基础目录路径，用于确定文件保存位置
	 */
	@Override
	protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
		// 调用父类提供的writeToFile方法保存HTML代码到index.html文件
		writeToFile(baseDirPath, "index.html", result.getHtmlCode());
	}
	
	/**
	 * 验证输入参数
	 * 重写父类方法，在基本验证基础上增加对HTML代码内容的验证
	 *
	 * @param result HTML代码结果对象
	 * @throws BusinessException 当HTML代码为空时抛出业务异常
	 */
	protected void validateInput(HtmlCodeResult result) {
		// 调用父类方法进行基本验证（检查result是否为null）
		super.validateInput(result);
		// 额外验证HTML代码内容是否为空
		if (StrUtil.isBlank(result.getHtmlCode())) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
		}
	}
}