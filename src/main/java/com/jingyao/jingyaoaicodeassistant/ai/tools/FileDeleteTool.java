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
 * 文件删除工具
 */
@Slf4j
public class FileDeleteTool {
	/**
	 * 删除指定文件的工具方法
	 * @param relativeFilePath 文件的相对路径
	 * @param appId 应用ID，用于确定项目目录
	 * @return 操作结果信息
	 */
	@Tool("删除指定文件")
	public String deleteFile(@P("文件的相对路径") String relativeFilePath, @ToolMemoryId Long appId) {
		try {
			// 将相对路径转换为Path对象
			Path path = Paths.get(relativeFilePath);
			// 如果路径不是绝对路径，则拼接项目根目录路径
			if (!path.isAbsolute()) {
				// 根据appId构建项目目录名称
				String projectDirName = "vue_project_" + appId;
				// 构建项目根目录路径
				Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
				// 解析得到完整路径
				path = projectRoot.resolve(relativeFilePath);
			}
			// 检查文件是否存在
			if (!Files.exists(path)) {
				return "文件不存在";
			}
			// 检查路径是否为普通文件
			if (!Files.isRegularFile(path)) {
				return "路径不是文件";
			}
			// 进行安全检查，确保不会删除重要文件
			String fileName = path.getFileName().toString();
			if (!isImportantFile(fileName)) {
				return "错误！不允许删除重要文件";
			}
			// 删除文件
			Files.delete(path);
			log.info("文件删除成功: {}", path.toAbsolutePath());
			return "文件删除成功" + relativeFilePath;
		} catch (IOException e) {
			String errorMessage = "删除文件失败" + relativeFilePath + "，错误信息：" + e.getMessage();
			log.error(errorMessage, e);
			return errorMessage;
		}
	}
	
	/**
	 * 判断文件名是否为重要文件
	 * @param fileName 要检查的文件名
	 * @return 如果是重要文件返回true，否则返回false
	 */
	private boolean isImportantFile(String fileName) {
		// 定义重要文件名的数组
		String[] importantFileNames = {"package.json", "package-lock.json", "yarn.lock", "pnpm-lock.yaml", "vite" +
			".config.js", "vite.config.ts", "vue.config.js", "tsconfig.json", "tsconfig.app.json", "tsconfig.node" +
			".json", "index" + ".html", "main.js", "main.ts", "App.vue", ".gitignore", "README.md"};
		// 遍历重要文件名数组
		for (String importantFileName : importantFileNames) {
			// 检查输入的文件名是否与当前重要文件名匹配（不区分大小写）
			if (importantFileName.equalsIgnoreCase(fileName)) {
				// 如果匹配，返回true
				return true;
			}
		}
		// 如果没有匹配的文件名，返回false
		return false;
	}
}
