package com.jingyao.jingyaoaicodeassistant.ai.tools;

import cn.hutool.core.io.FileUtil;
import com.jingyao.jingyaoaicodeassistant.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Slf4j
public class FileDirReadTool {
	/**
	 * 忽略的文件与目录
	 */
	private static final Set<String> IGNORED_NAMES = Set.of("node_modules", ".git", "dist", "build", ".DS_Store",
		".env", "target", ".mvn", ".idea", ".vscode", "coverage");
	/**
	 * 忽略的文件扩展名
	 */
	private static final Set<String> IGNORED_EXTENSIONS = Set.of(".log", ".tmp", ".cache", ".lock");
	
	/**
	 * 读取指定目录的结构信息
	 * @param relativeDirPath 目录的相对路径，如果为空则读取整个项目结构
	 * @param appId 应用ID，用于标识不同的项目
	 * @return 返回目录结构的字符串描述，如果出错则返回错误信息或null
	 */
	public String readDir(@P("目录的相对路径，为空则读取整个项目结构") String relativeDirPath, @ToolMemoryId Long appId) {
		try {
			// 创建Path对象，如果relativeDirPath为空则使用空字符串
			Path path = Paths.get(relativeDirPath == null ? "" : relativeDirPath);
			// 如果路径不是绝对路径
			if (!path.isAbsolute()) {
				// 构建项目目录名称
				String projectDirName = "vue_project_" + appId;
				// 获取项目根目录路径
				Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
				
				// 解析相对路径为绝对路径
				path = projectRoot.resolve(relativeDirPath == null ? "" : relativeDirPath);
				// 转换为File对象
				File targetDir = path.toFile();
				// 检查目录是否存在且确实是目录
				if (!targetDir.exists() || !targetDir.isDirectory()) {
					log.error("目录不存在或者不是目录：{}", relativeDirPath);
					return "错误：目录不存在或者不是目录 —— " + relativeDirPath;
				}
				// 构建目录结构字符串
				StringBuilder structure = new StringBuilder();
				structure.append("项目的目录结构如下：\n");
				// 使用Hutool工具递归获取所有文件，并过滤掉需要忽略的文件
				List<File> allFiles = FileUtil.loopFiles(targetDir, file -> !shouldIgnore(file.getName()));
				// 对文件进行排序：先按深度排序，再按路径排序
				allFiles.stream().sorted((f1, f2) -> {
					int depth1 = getRelativeDepth(targetDir, f1);
					int depth2 = getRelativeDepth(targetDir, f2);
					if (depth1 != depth2) {
						return Integer.compare(depth1, depth2);
					}
					return f1.getPath().compareTo(f2.getPath());
				}).forEach(file -> {
					// 获取文件相对于目标目录的深度
					int depth = getRelativeDepth(targetDir, file);
					// 根据深度生成缩进
					String indent = "  ".repeat(depth);
					// 添加文件名到结构字符串中
					structure.append(indent).append(file.getName());
				});
			}
			
			
		} catch (Exception e) {
			// 记录错误日志
			log.error("读取目录失败", e);
			return "读取目录结构失败：" + relativeDirPath + "，错误：" + e.getMessage();
		}
		// 如果发生异常或路径为绝对路径，返回null
		return null;
	}
	
	/**
	 * 计算文件相对于根目录的深度
	 * @param root 根目录文件对象
	 * @param file 需要计算深度的文件对象
	 * @return 返回文件相对于根目录的深度值（从0开始计数）
	 */
	private int getRelativeDepth(File root, File file) {
		// 将File对象转换为Path对象，以便使用Path类的方法
		Path rootPath = root.toPath();
		Path filePath = file.toPath();
		// 使用relativize方法获取相对路径，然后通过getNameCount获取路径名称的数量
		// 减1是因为根目录本身计数为1，而我们希望深度从0开始计数
		return rootPath.relativize(filePath).getNameCount() - 1;
	}
	
	/**
	 * 判断文件名是否应该被忽略的方法
	 * @param fileName 需要检查的文件名
	 * @return 如果文件名在忽略列表中或具有忽略的扩展名，则返回true；否则返回false
	 */
	private boolean shouldIgnore(String fileName) {
		// 检查文件名是否在忽略名称集合中
		if (IGNORED_NAMES.contains(fileName)) {
			return true;
		}
		
		return IGNORED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
	}
}
