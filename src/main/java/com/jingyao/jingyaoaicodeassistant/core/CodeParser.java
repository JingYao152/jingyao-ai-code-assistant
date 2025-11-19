package com.jingyao.jingyaoaicodeassistant.core;

import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码解析器
 * 提供静态方法解析不同类型的代码内容
 */
@Slf4j
public class CodeParser {
	
	private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```",
		Pattern.CASE_INSENSITIVE);
	
	/**
	 * 解析 HTML 单文件代码
	 */
	public static HtmlCodeResult parseHtmlCode(String codeContent) {
		// 检查输入参数是否为null
		if (codeContent == null) {
			log.error("解析HTML代码失败：输入内容为null");
			return new HtmlCodeResult(); // 返回空的结果对象而不是null
		}
		
		HtmlCodeResult result = new HtmlCodeResult();
		// 提取 HTML 代码
		String htmlCode = extractHtmlCode(codeContent);
		if (htmlCode != null && !htmlCode.trim().isEmpty()) {
			result.setHtmlCode(htmlCode.trim());
			log.debug("成功从代码块中提取HTML代码，长度: {}", htmlCode.length());
		} else {
			// 如果没有找到代码块，将整个内容作为HTML
			result.setHtmlCode(codeContent.trim());
			log.debug("未找到HTML代码块，使用整个内容作为HTML，长度: {}", codeContent.length());
		}
		return result;
	}
	
	/**
	 * 解析多文件代码（HTML + CSS + JS）
	 */
	public static MultiFileCodeResult parseMultiFileCode(String codeContent) {
		// 检查输入参数是否为null
		if (codeContent == null) {
			log.error("解析多文件代码失败：输入内容为null");
			return new MultiFileCodeResult(); // 返回空的结果对象而不是null
		}
		
		MultiFileCodeResult result = new MultiFileCodeResult();
		// 提取各类代码
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
	 * 提取HTML代码内容
	 *
	 * @param content 原始内容
	 * @return HTML代码
	 */
	private static String extractHtmlCode(String content) {
		if (content == null) {
			return null;
		}
		Matcher matcher = HTML_CODE_PATTERN.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	/**
	 * 根据正则模式提取代码
	 *
	 * @param content 原始内容
	 * @param pattern 正则模式
	 * @return 提取的代码
	 */
	private static String extractCodeByPattern(String content, Pattern pattern) {
		if (content == null) {
			return null;
		}
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}