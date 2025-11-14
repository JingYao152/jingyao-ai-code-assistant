package com.jingyao.jingyaoaicodeassistant.ai.model;

import lombok.Data;

/**
 * HTML代码结果类，用于封装HTML代码及其描述信息
 */
@Data
public class HtmlCodeResult {
	
	// HTML代码内容
	private String htmlCode;
	
	// 描述信息
	private String description;
}
