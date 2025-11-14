package com.jingyao.jingyaoaicodeassistant.ai.model;

import lombok.Data;

/**
 * 多文件代码结果类
 * 用于存储和返回包含HTML、CSS、JS代码以及描述的复合结果
 */
@Data
public class MultiFileCodeResult {
	
	private String htmlCode;  // HTML代码内容
	
	private String cssCode;   // CSS样式代码内容
	
	private String jsCode;    // JavaScript脚本代码内容
	
	private String description;  // 对代码的描述或说明
}
