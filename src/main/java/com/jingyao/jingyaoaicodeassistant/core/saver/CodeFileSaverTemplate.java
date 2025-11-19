package com.jingyao.jingyaoaicodeassistant.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码文件保存模板抽象类
 * 提供代码文件保存的通用框架，使用模板方法模式设计
 *
 * @param <T> 代码结果类型参数，允许处理不同类型的代码结果对象
 */
public abstract class CodeFileSaverTemplate<T> {
	
	/**
	 * 文件保存的根目录路径
	 */
	protected static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";
	
	/**
	 * 保存代码的主方法
	 * 实现了保存代码文件的完整流程：验证输入、创建目录、保存文件
	 *
	 * @param result 代码结果对象，包含需要保存的代码内容
	 * @return 保存代码的目录文件对象
	 */
	public final File saveCode(T result) {
		// 验证输入参数的有效性
		validateInput(result);
		// 构建唯一的保存目录
		String baseDirPath = buildUniqueDir();
		
		// 保存代码文件（由子类实现具体逻辑）
		saveFiles(result, baseDirPath);
		// 返回保存目录的File对象
		return new File(baseDirPath);
	}
	
	/**
	 * 验证输入参数
	 * 检查代码结果对象是否为null
	 *
	 * @param result 需要验证的代码结果对象
	 * @throws BusinessException 当输入参数为null时抛出业务异常
	 */
	protected void validateInput(T result) {
		if (result == null) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
		}
	}
	
	/**
	 * 构建唯一的保存目录
	 * 根据代码类型和雪花算法生成的唯一ID创建目录名
	 *
	 * @return 构建好的目录路径
	 */
	protected final String buildUniqueDir() {
		// 获取代码类型值（由子类提供具体实现）
		String codeType = getCodeType().getValue();
		// 使用代码类型和雪花ID构建唯一目录名
		String uniqueDirName = StrUtil.format("{}_{}", codeType, IdUtil.getSnowflakeNextIdStr());
		// 构建完整的目录路径
		String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
		// 创建目录（如果不存在）
		FileUtil.mkdir(dirPath);
		return dirPath;
	}
	
	/**
	 * 将内容写入指定文件
	 * 仅当内容不为空时才执行写入操作
	 *
	 * @param dirPath 目录路径
	 * @param fileName 文件名
	 * @param content 文件内容
	 */
	protected final void writeToFile(String dirPath, String fileName, String content) {
		// 检查内容是否非空
		if (StrUtil.isNotBlank(content)) {
			// 构建完整的文件路径
			String filePath = dirPath + File.separator + fileName;
			// 使用UTF-8编码将内容写入文件
			FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
		}
	}
	
	/**
	 * 获取代码类型
	 * 抽象方法，由子类实现以提供具体的代码类型
	 *
	 * @return 代码生成类型枚举
	 */
	protected abstract CodeGenTypeEnum getCodeType();
	
	/**
	 * 保存文件的抽象方法
	 * 由子类实现具体的文件保存逻辑
	 *
	 * @param result 代码结果对象
	 * @param baseDirPath 基础目录路径
	 */
	protected abstract void saveFiles(T result, String baseDirPath);
}