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
 * 文件写入工具类
 * 提供将内容写入指定路径文件的功能，支持相对路径和绝对路径
 */
@Slf4j
public class FileWriteTool {
	/**
	 * 将内容写入指定路径的文件
	 * @param relativeFilePath 文件的相对路径
	 * @param content 待写入文件的内容
	 * @param appId 应用ID，用于确定项目根目录
	 * @return 操作结果信息，成功或失败原因
	 */
	@Tool("写入文件到指定路径")
	public String writeFile(@P("文件的相对路径") String relativeFilePath, @P("待写入文件的内容") String content,
	                        @ToolMemoryId Long appId) {
		try {
			// 将路径字符串转换为Path对象
			Path path = Paths.get(relativeFilePath);
			// 如果是相对路径，则拼接完整的项目根路径
			if (!path.isAbsolute()) {
				// 构建项目根目录名称
				String projectDirName = "vue_project_" + appId;
				// 获取项目根目录路径
				Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
				// 解析为完整路径
				path = projectRoot.resolve(relativeFilePath);
			}
			// 获取文件父目录
			Path parentDir = path.getParent();
			// 如果父目录存在，则创建所有不存在的父目录
			if (parentDir != null) {
				Files.createDirectories(parentDir);
			}
			
			// 将内容写入文件，如果文件不存在则创建，如果存在则截断
			Files.write(path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			// 记录成功日志
			log.info("成功写入文件：{}", path.toAbsolutePath());
			return "文件写入成功：" + relativeFilePath;
		} catch (IOException e) {
			// 构建错误信息
			String errorMessage = "文件写入失败：" + relativeFilePath + "，错误：" + e.getMessage();
			// 记录错误日志
			log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
