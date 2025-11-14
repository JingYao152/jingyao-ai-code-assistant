package com.jingyao.jingyaoaicodeassistant.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 多文件代码结果类
 * 用于存储和返回包含HTML、CSS、JS代码以及描述的复合结果
 */
@Data
@Description("生成多个代码文件的结果")
public class MultiFileCodeResult {
	@Description("HTML代码")
	private String htmlCode;  // HTML代码内容
	@Description("CSS代码")
	private String cssCode;   // CSS样式代码内容
	@Description("JavaScript代码")
	private String jsCode;    // JavaScript脚本代码内容
	@Description("生成代码的描述")
	private String description;  // 对代码的描述或说明
}
