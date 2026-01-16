package com.jingyao.jingyaoaicodeassistant.manager;

import com.jingyao.jingyaoaicodeassistant.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class CosManager {
	@Resource
	private CosClientConfig cosClientConfig;
	@Resource
	private COSClient cosClient;
	
	/**
	 * 上传对象
	 * @param key 键
	 * @param file 文件
	 * @return 上传结果
	 */
	public PutObjectResult putObject(String key, File file) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
		return cosClient.putObject(putObjectRequest);
	}
	
	/**
	 * 上传文件到COS并返回访问URL
	 * @param key 文件在COS中的存储键
	 * @param file 要上传的本地文件对象
	 * @return 文件在COS中的访问URL，如果上传失败则返回null
	 */
	public String uploadFile(String key, File file) {
		// 上传文件
		PutObjectResult result = putObject(key, file);
		if (result != null) {
			// 构建访问URL
			String url = String.format("%s%s", cosClientConfig.getHost(), key);
			log.info("文件上传COS成功:{}->{}", file.getName(), url);
			return url;
		} else {
			log.error("文件上传COS失败，返回结果为空");
			return null;
		}
	}
}
