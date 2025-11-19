package com.jingyao.jingyaoaicodeassistant.core.parser;

import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML代码解析器，用于从文本内容中提取HTML代码块。
 * 该类实现了CodeParser接口，专门处理包含HTML代码的文本内容。
 */
@Slf4j
public class HtmlCodeParser implements CodeParser<HtmlCodeResult> {
	
	private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```",
		Pattern.CASE_INSENSITIVE);
	
	/**
	 * 解析输入的代码内容并返回HTML代码结果
	 *
	 * @param codeContent 包含HTML代码的原始字符串
	 * @return HtmlCodeResult 包含解析后的HTML代码的结果对象
	 */
	@Override
	public HtmlCodeResult parseCode(String codeContent) {
		HtmlCodeResult result = new HtmlCodeResult();
		// 从原始内容中提取HTML代码块
		String htmlCode = extractHtmlCode(codeContent);
		if (htmlCode != null && !htmlCode.trim().isEmpty()) {
			result.setHtmlCode(htmlCode.trim());
			log.debug("提取到 HTML 代码: {}", htmlCode);
		} else {
			result.setHtmlCode(codeContent.trim());
			log.debug("未找到 HTML 代码块，将整个内容作为HTML: {}", codeContent);
		}
		return result;
	}
	
	/**
	 * 从输入内容中提取HTML代码片段
	 *
	 * @param content 包含HTML代码的原始字符串
	 * @return 匹配到的HTML代码片段，如果没有找到则返回null
	 */
	private String extractHtmlCode(String content) {
		Matcher matcher = HTML_CODE_PATTERN.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}

