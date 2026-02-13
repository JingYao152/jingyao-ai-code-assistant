package com.jingyao.jingyaoaicodeassistant.ai.tools;

import com.jingyao.jingyaoaicodeassistant.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件修改工具类
 * 提供修改文件内容的功能，可以替换文件中指定的旧内容为新内容
 */
@Slf4j
public class FileModifyTool {
	/**
	 * 修改文件内容
	 *
	 * @param relativeFilePath 待修改文件的相对路径
	 * @param oldContent 要替换的旧内容
	 * @param newContent 要替换的新内容
	 * @param appId 应用ID，用于确定项目目录
	 * @return 操作结果信息，成功或失败原因
	 */
	@Tool("修改文件内容，用新内容替换指定的旧内容")
	public String modifyFile(@P("待修改文件的相对路径") String relativeFilePath, @P("要替换的旧内容") String oldContent,
	                         @P("要替换的新内容") String newContent, @ToolMemoryId Long appId) {
		try {
			// 将文件路径转换为Path对象
			Path path = Paths.get(relativeFilePath);
			// 如果是相对路径，则拼接项目根目录路径
			if (!path.isAbsolute()) {
				// 根据appId生成项目目录名称
				String projectDirName = "vue_project_" + appId;
				// 拼接完整的项目根目录路径
				Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
				// 解析为绝对路径
				path = projectRoot.resolve(relativeFilePath);
			}
			// 检查文件是否存在且为普通文件
			if (!Files.exists(path) || !Files.isRegularFile(path)) {
				return "错误！文件不存在或者不是文件 - " + relativeFilePath;
			}
			
			// 读取文件原始内容
			String originalContent = Files.readString(path);
			// 检查文件中是否包含待替换的旧内容
			if (!originalContent.contains(oldContent)) {
				return "错误！文件中不包含要替换的内容 - " + oldContent;
			}
			
			// 执行内容替换
			String modifiedContent = originalContent.replace(oldContent, newContent);
			// 检查替换后内容是否有变化
			if (originalContent.equals(modifiedContent)) {
				return "错误！替换后文件内容没有变化 - " + relativeFilePath;
			}
			
			// 将修改后的内容写回文件
			Files.writeString(path, modifiedContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			// 记录成功日志
			log.info("成功修改文件:{}", relativeFilePath);
			return "文件修改成功：" + relativeFilePath;
		} catch (IOException e) {
			// 捕获并处理IO异常，返回错误信息
			String errorMessage = "修改文件失败：" + relativeFilePath + ",错误：" + e.getMessage();
			// 记录错误日志
			log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
