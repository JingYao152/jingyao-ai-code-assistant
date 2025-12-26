package com.jingyao.jingyaoaicodeassistant.core.builder;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Vue项目构建器
 * <p>
 * 该组件负责执行Vue项目的构建流程，包括依赖安装和项目打包。
 * 支持同步和异步两种构建方式，能够自动识别操作系统并执行相应的命令。
 * </p>
 * <p>主要功能：</p>
 * <ul>
 *   <li>执行npm install安装项目依赖</li>
 *   <li>执行npm run build打包项目</li>
 *   <li>验证构建结果（检查dist目录）</li>
 *   <li>支持异步构建操作</li>
 * </ul>
 *
 * @author jingyao
 * @since 1.0.0
 */
@Component
@Slf4j
public class VueProjectBuilder {
	
	/**
	 * 在指定工作目录中执行命令
	 * <p>
	 * 该方法会启动一个新的进程来执行指定的命令，并等待进程完成。
	 * 如果命令执行时间超过指定的超时时间，将强制终止进程。
	 * </p>
	 *
	 * @param workingDir     工作目录，命令将在该目录下执行
	 * @param command        要执行的命令字符串
	 * @param timeoutSeconds 超时时间（秒），超过此时间将强制终止进程
	 * @return 命令是否执行成功，true表示成功，false表示失败或超时
	 * @throws RuntimeException 当命令执行过程中发生异常时抛出
	 */
	private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
		try {
			log.info("在目录{}中执行命令：{}", workingDir.getAbsolutePath(), command);
			// 使用RuntimeUtil执行命令，将命令字符串按空格分割为参数数组
			Process process = RuntimeUtil.exec(null, workingDir, command.split("\\s+"));
			// 等待进程完成，设置超时时间
			boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
			if (!finished) {
				log.error("命令执行超时({}秒)，强制终止进程", timeoutSeconds);
				process.destroyForcibly();
				return false;
			}
			// 获取进程退出码，0表示正常退出
			int exitCode = process.exitValue();
			if (exitCode == 0) {
				log.info("命令执行成功：{}", command);
				return true;
			} else {
				log.error("命令执行失败，退出码:{}", exitCode);
				return false;
			}
		} catch (Exception e) {
			log.error("执行命令失败：{}，错误信息：{}", command, e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 执行npm install命令安装项目依赖
	 * <p>
	 * 该方法会在指定的项目目录下执行npm install命令，
	 * 用于安装package.json中定义的所有依赖项。
	 * </p>
	 *
	 * @param projectDir Vue项目根目录
	 * @return npm install是否执行成功
	 */
	private boolean executeNpmInstall(File projectDir) {
		log.info("执行npm install...");
		// 构建适合当前操作系统的npm命令
		String command = String.format("%s install", buildCommand("npm"));
		// 执行npm install命令，设置5分钟超时限制
		return executeCommand(projectDir, command, 300);
	}
	
	/**
	 * 执行npm run build命令构建项目
	 * <p>
	 * 该方法会在指定的项目目录下执行npm run build命令，
	 * 用于将Vue项目打包成生产环境可用的静态资源。
	 * </p>
	 *
	 * @param projectDir Vue项目根目录
	 * @return npm run build是否执行成功
	 */
	private boolean executeNpmBuild(File projectDir) {
		log.info("执行npm run build...");
		// 构建适合当前操作系统的npm命令
		String command = String.format("%s run build", buildCommand("npm"));
		// 执行npm run build命令，设置3分钟超时限制
		return executeCommand(projectDir, command, 180);
	}
	
	/**
	 * 判断当前操作系统是否为Windows
	 *
	 * @return 如果是Windows系统返回true，否则返回false
	 */
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
	
	/**
	 * 根据当前操作系统构建适合的命令
	 * <p>
	 * 在Windows系统下，需要在命令后添加.cmd后缀，
	 * 而在Unix-like系统下直接使用原命令。
	 * </p>
	 *
	 * @param baseCommand 基础命令，如"npm"
	 * @return 适合当前操作系统的完整命令
	 */
	private String buildCommand(String baseCommand) {
		if (isWindows()) {
			return baseCommand + ".cmd";
		}
		return baseCommand;
	}
	
	/**
	 * 构建 Vue 项目
	 * <p>
	 * 该方法执行完整的Vue项目构建流程：
	 * <ol>
	 *   <li>检查项目目录和package.json文件是否存在</li>
	 *   <li>执行npm install安装依赖</li>
	 *   <li>执行npm run build打包项目</li>
	 *   <li>验证dist目录是否成功生成</li>
	 * </ol>
	 * </p>
	 *
	 * @param projectPath 项目根目录路径
	 * @return 构建是否成功，true表示成功，false表示失败
	 */
	public boolean buildProject(String projectPath) {
		File projectDir = new File(projectPath);
		// 检查项目目录是否存在且为目录
		if (!projectDir.exists() || !projectDir.isDirectory()) {
			log.error("项目目录不存在: {}", projectPath);
			return false;
		}
		// 检查 package.json 是否存在
		File packageJson = new File(projectDir, "package.json");
		if (!packageJson.exists()) {
			log.error("package.json 文件不存在: {}", packageJson.getAbsolutePath());
			return false;
		}
		log.info("开始构建 Vue 项目: {}", projectPath);
		// 执行 npm install
		if (!executeNpmInstall(projectDir)) {
			log.error("npm install 执行失败");
			return false;
		}
		// 执行 npm run build
		if (!executeNpmBuild(projectDir)) {
			log.error("npm run build 执行失败");
			return false;
		}
		// 验证 dist 目录是否生成
		File distDir = new File(projectDir, "dist");
		if (!distDir.exists()) {
			log.error("构建完成但 dist 目录未生成: {}", distDir.getAbsolutePath());
			return false;
		}
		log.info("Vue 项目构建成功，dist 目录: {}", distDir.getAbsolutePath());
		return true;
	}
	
	/**
	 * 异步构建 Vue 项目
	 * <p>
	 * 该方法使用虚拟线程在后台异步执行项目构建，
	 * 不会阻塞调用线程。构建过程中的异常会被捕获并记录日志。
	 * </p>
	 *
	 * @param projectPath 项目根目录路径
	 */
	public void buildProjectAsync(String projectPath) {
		// 使用虚拟线程执行异步构建任务
		Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis()).start(() -> {
			try {
				boolean result = buildProject(projectPath);
			} catch (Exception e) {
				log.error("异步构建Vue项目时发生异常：{}", e.getMessage(), e);
			}
		});
	}
}
