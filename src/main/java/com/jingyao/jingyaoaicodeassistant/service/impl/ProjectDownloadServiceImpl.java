package com.jingyao.jingyaoaicodeassistant.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.service.ProjectDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

/**
 * 项目下载服务实现类，实现了ProjectDownloadService接口
 * 用于处理项目文件下载时的路径验证和过滤逻辑
 */
@Service
@Slf4j
public class ProjectDownloadServiceImpl implements ProjectDownloadService {
	
	/**
	 * 定义需要忽略的目录名称集合
	 * 这些目录通常包含不需要的文件或临时文件
	 */
	private static final Set<String> IGNORED_NAMES = Set.of(
		"node_modules",    // Node.js依赖目录
		".git",           // Git版本控制目录
		"dist",           // 构建输出目录
		"build",          // 构建目录
		".DS_Store",      // macOS系统文件
		".env",           // 环境变量文件
		"target",         // Maven构建目录
		".mvn",           // Maven配置目录
		".idea",          // IntelliJ IDEA项目文件
		".vscode"         // VSCode配置目录
	);
	
	/**
	 * 定义需要忽略的文件扩展名集合
	 * 这些扩展名的文件通常为临时文件或日志文件
	 */
	private static final Set<String> IGNORED_EXTENSIONS = Set.of(
		".log",   // 日志文件
		".tmp",   // 临时文件
		".cache"  // 缓存文件
	);
	
	/**
	 * 检查给定路径是否允许访问
	 * @param projectRoot 项目根目录路径
	 * @param fullPath 需要检查的完整路径
	 * @return 如果路径被允许则返回true，否则返回false
	 */
	private boolean isPathAllowed(Path projectRoot, Path fullPath) {
		// 获取相对于项目根目录的路径
		Path relativePath = projectRoot.relativize(fullPath);
		
		// 遍历路径中的每一部分进行检查
		for (Path part : relativePath) {
			String partName = part.toString();
			// 检查是否在忽略名称列表中
			if (IGNORED_NAMES.contains(partName)) {
				return false;
			}
			// 检查是否以忽略的扩展名结尾
			if (IGNORED_EXTENSIONS.stream().anyMatch(partName::endsWith)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
/**
 * 下载项目并打包为ZIP文件
 * @param projectPath 项目路径
 * @param downloadFileName 下载的文件名
 * @param response HTTP响应对象
 */
	public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response) {
		// 校验项目路径不能为空
		ThrowUtils.throwIf(StrUtil.isBlank(projectPath), ErrorCode.PARAMS_ERROR, "项目路径不能为空");
		// 校验下载文件名不能为空
		ThrowUtils.throwIf(StrUtil.isBlank(downloadFileName), ErrorCode.PARAMS_ERROR, "下载文件名不能为空");
		
		// 创建项目目录对象
		File projectDir = new File(projectPath);
		// 校验项目路径是否存在
		ThrowUtils.throwIf(!projectDir.exists(), ErrorCode.NOT_FOUND_ERROR, "项目路径不存在");
		// 校验指定路径是否为目录
		ThrowUtils.throwIf(!projectDir.isDirectory(), ErrorCode.PARAMS_ERROR, "指定路径不是目录");
		
		// 记录开始打包下载项目的日志
		log.info("准备打包下载项目：{}->{}.zip", projectPath, downloadFileName);
		// 设置响应状态码为200
		response.setStatus(HttpServletResponse.SC_OK);
		// 设置响应内容类型为ZIP文件
		response.setContentType("application/zip");
		// 设置响应头，指定下载文件的名称
		response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s.zip\"", downloadFileName));
		
		// 创建文件过滤器，只允许下载指定路径下的文件
		FileFilter filter = file -> isPathAllowed(projectDir.toPath(), file.toPath());
		try {
			// 使用ZipUtil工具类进行ZIP打包，并将结果写入响应输出流
			ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8, false, filter, projectDir);
			// 记录项目打包下载完成的日志
			log.info("项目打包下载完成：{}", downloadFileName);
		} catch (Exception e) {
			// 记录项目打包下载异常的日志
			log.error("项目打包下载异常", e);
			// 抛出业务异常，提示项目打包下载失败
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "项目打包下载失败");
		}
	}
}
