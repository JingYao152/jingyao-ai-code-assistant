package com.jingyao.jingyaoaicodeassistant.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 项目下载服务接口
 */
public interface ProjectDownloadService {
	/**
	 * 将指定项目路径下的项目下载为ZIP文件
	 *
	 * @param projectPath 项目路径
	 * @param downloadFileName 下载的文件名
	 * @param response HTTP响应对象
	 */
	void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
