package com.jingyao.jingyaoaicodeassistant.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * CodeFileSaver 类用于保存生成的代码文件到本地文件系统
 * 支持保存HTML代码和多文件代码结果（HTML、CSS、JS）
 */
@Slf4j
public class CodeFileSaver {
	
	// 文件保存根目录，使用系统当前用户目录下的/tmp/code_output作为根目录
	private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";
	
	/**
	 * 保存 HtmlCodeResult
	 */
	public static File saveHtmlCodeResult(HtmlCodeResult result) {
		if (result == null) {
			log.error("保存失败：HtmlCodeResult对象为null");
			throw new IllegalArgumentException("HtmlCodeResult cannot be null");
		}
		String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
		writeToFile(baseDirPath, "index.html", result.getHtmlCode());
		log.info("HTML代码保存成功，路径为：{}", baseDirPath);
		return new File(baseDirPath);
	}
	
	/**
	 * 保存 MultiFileCodeResult
	 */
	public static File saveMultiFileCodeResult(MultiFileCodeResult result) {
		if (result == null) {
			log.error("保存失败：MultiFileCodeResult对象为null");
			throw new IllegalArgumentException("MultiFileCodeResult cannot be null");
		}
		String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
		writeToFile(baseDirPath, "index.html", result.getHtmlCode());
		writeToFile(baseDirPath, "style.css", result.getCssCode());
		writeToFile(baseDirPath, "script.js", result.getJsCode());
		log.info("多文件代码保存成功，路径为：{}", baseDirPath);
		return new File(baseDirPath);
	}
	
	/**
	 * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
	 */
	private static String buildUniqueDir(String bizType) {
		String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
		String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
		FileUtil.mkdir(dirPath);
		return dirPath;
	}
	
	/**
	 * 写入单个文件
	 */
	private static void writeToFile(String dirPath, String filename, String content) {
		String filePath = dirPath + File.separator + filename;
		// 检查content是否为null，如果为null则使用空字符串代替
		if (content == null) {
			log.warn("文件内容为null，使用空字符串代替，文件：{}", filename);
			content = "";
		}
		FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
		log.debug("文件保存成功：{}", filePath);
	}
}