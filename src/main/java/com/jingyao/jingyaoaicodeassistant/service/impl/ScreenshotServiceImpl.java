package com.jingyao.jingyaoaicodeassistant.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.manager.CosManager;
import com.jingyao.jingyaoaicodeassistant.service.ScreenshotService;
import com.jingyao.jingyaoaicodeassistant.utils.WebScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {
	
	@Resource
	private CosManager cosManager;
	
	/**
	 * 生成网页截图并上传到对象存储服务
	 * @param webUrl 需要生成截图的网页URL
	 * @return 返回上传到对象存储后的URL地址
	 */
	@Override
	public String generateAndUploadScreenshot(String webUrl) {
		// 检查网页URL是否为空，如果为空则抛出参数错误异常
		ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网页URL不能为空");
		log.info("开始生成网页截图，URL:{}", webUrl);
		// 生成本地截图
		String localScreenshotPath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
		ThrowUtils.throwIf(StrUtil.isBlank(localScreenshotPath), ErrorCode.OPERATION_ERROR, "本地截图生成失败");
		// 上传到COS
		try {
			String cosUrl = uploadScreenshotToCos(localScreenshotPath);
			ThrowUtils.throwIf(StrUtil.isBlank(cosUrl), ErrorCode.OPERATION_ERROR, "截图上传对象存储失败");
			log.info("网页截图生成并上传成功:{}->{}", webUrl, cosUrl);
			return cosUrl;
		} finally {
			cleanupLocalFile(localScreenshotPath);
		}
	}
	
	private String uploadScreenshotToCos(String localScreenshotPath) {
		if (StrUtil.isBlank(localScreenshotPath)) {
			return null;
		}
		File screenshotFile = new File(localScreenshotPath);
		if (!screenshotFile.exists()) {
			log.error("截图文件不存在:{}", localScreenshotPath);
			return null;
		}
		// 生成COS对象键
		String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
		String cosKey = generateScreenshotKey(fileName);
		return cosManager.uploadFile(cosKey, screenshotFile);
	}
	
	/**
	 * 生成截图对象的存储键
	 * @param fileName 文件名
	 * @return /screenshots/yyyy/MM/dd/fileName.jpg
	 */
	private String generateScreenshotKey(String fileName) {
		String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		return String.format("/screenshots/%s/%s", datePath, fileName);
	}
	
	/**
	 * 清理本地截图文件的方法
	 * @param localFilePath 本地文件路径
	 */
	private void cleanupLocalFile(String localFilePath) {
		// 根据路径创建File对象
		File localFile = new File(localFilePath);
		// 判断文件是否存在
		if (localFile.exists()) {
			// 获取文件所在父目录
			File parentDir = localFile.getParentFile();
			// 删除父目录及其下所有文件
			FileUtil.del(parentDir);
			// 记录日志信息
			log.info("本地截图文件已清理:{}", localFilePath);
		}
	}
}
