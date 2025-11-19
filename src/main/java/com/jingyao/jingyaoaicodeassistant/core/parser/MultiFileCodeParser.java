package com.jingyao.jingyaoaicodeassistant.core.parser;

import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多文件代码解析器，用于从包含多种语言代码的文本中提取并分离HTML、CSS和JavaScript代码。
 */
@Slf4j
public class MultiFileCodeParser implements CodeParser<MultiFileCodeResult> {
	private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```",
		Pattern.CASE_INSENSITIVE);
	
	
	/**
	 * 解析输入的代码内容，提取其中的HTML、CSS和JavaScript代码片段。
	 *
	 * @param codeContent 包含HTML、CSS和JavaScript代码的原始字符串
	 * @return MultiFileCodeResult 包含解析后的HTML、CSS和JavaScript代码的结果对象
	 */
	@Override
	public MultiFileCodeResult parseCode(String codeContent) {
		MultiFileCodeResult result = new MultiFileCodeResult();
		// 提取代码
		String htmlCode = extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
		String cssCode = extractCodeByPattern(codeContent, CSS_CODE_PATTERN);
		String jsCode = extractCodeByPattern(codeContent, JS_CODE_PATTERN);
		// 设置HTML代码
		if (htmlCode != null && !htmlCode.trim().isEmpty()) {
			result.setHtmlCode(htmlCode.trim());
			log.debug("成功提取HTML代码，长度: {}", htmlCode.length());
		} else {
			log.debug("未提取到有效HTML代码");
		}
		// 设置CSS代码
		if (cssCode != null && !cssCode.trim().isEmpty()) {
			result.setCssCode(cssCode.trim());
			log.debug("成功提取CSS代码，长度: {}", cssCode.length());
		} else {
			log.debug("未提取到有效CSS代码");
		}
		
		// 设置JS代码
		if (jsCode != null && !jsCode.trim().isEmpty()) {
			result.setJsCode(jsCode.trim());
			log.debug("成功提取JS代码，长度: {}", jsCode.length());
		} else {
			log.debug("未提取到有效JS代码");
		}
		
		return result;
	}
	
	/**
	 * 根据正则表达式模式从内容中提取代码片段
	 *
	 * @param content 待处理的字符串内容
	 * @param pattern 用于匹配的正则表达式模式
	 * @return 匹配到的代码片段，如果没有找到则返回null
	 */
	private String extractCodeByPattern(String content, Pattern pattern) {
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}
