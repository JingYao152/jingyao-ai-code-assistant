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

/**
 * 文件读取工具类，用于读取指定路径下的文件内容
 */
@Slf4j
public class FileReadTool {
	/**
	 * 读取文件内容的方法
	 * @param relativeFilePath 文件的相对路径
	 * @param appId 应用ID，用于生成项目目录名称
	 * @return 返回文件内容字符串，如果出错则返回错误信息
	 */
	@Tool
	public String readFile(@P("文件的相对路径") String relativeFilePath, @ToolMemoryId Long appId) {
		
		try {
			// 将字符串路径转换为Path对象
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
			// 检查文件是否存在且是普通文件
			if (!Files.exists(path) || Files.isRegularFile(path)) {
				return "错误！文件不存在或者不是文件 — " + relativeFilePath;
			}
			
			// 读取文件内容并返回
			return Files.readString(path);
		} catch (IOException e) {
			// 构造错误信息并记录日志
			String errorMessage = "读取文件失败：" + relativeFilePath + ",错误：" + e.getMessage();
			log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
