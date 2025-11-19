package com.jingyao.jingyaoaicodeassistant.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jingyao.jingyaoaicodeassistant.ai.model.HtmlCodeResult;
import com.jingyao.jingyaoaicodeassistant.ai.model.MultiFileCodeResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * JSON代码解析器
 * 专门用于解析流式输出的JSON格式代码内容
 * 支持从JSON字符串中提取HTML、CSS和JavaScript代码
 */
@Slf4j
public class JsonCodeParser {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 解析JSON格式的HTML单文件代码
	 *
	 * @param jsonContent JSON格式的代码内容
	 * @return HTML代码结果对象
	 */
	public static HtmlCodeResult parseJsonHtmlCode(String jsonContent) {
		// 检查输入参数是否为null
		if (jsonContent == null) {
			log.error("解析JSON HTML代码失败：输入内容为null");
			return new HtmlCodeResult();
		}
		
		HtmlCodeResult result = new HtmlCodeResult();
		try {
			JsonNode rootNode = objectMapper.readTree(jsonContent);
			if (rootNode.has("htmlCode")) {
				String htmlCode = rootNode.get("htmlCode").asText();
				result.setHtmlCode(htmlCode != null ? htmlCode.trim() : "");
				log.debug("成功从JSON中提取HTML代码，长度: {}", htmlCode != null ? htmlCode.length() : 0);
			} else {
				log.debug("JSON中未找到htmlCode字段");
				// 尝试使用整个内容作为HTML
				result.setHtmlCode(jsonContent.trim());
			}
		} catch (IOException e) {
			log.error("解析JSON HTML代码失败: {}", e.getMessage(), e);
			// 如果JSON解析失败，尝试将整个内容作为HTML
			result.setHtmlCode(jsonContent.trim());
		}
		return result;
	}
	
	/**
	 * 解析JSON格式的多文件代码（HTML + CSS + JS）
	 *
	 * @param jsonContent JSON格式的代码内容
	 * @return 多文件代码结果对象
	 */
	public static MultiFileCodeResult parseJsonMultiFileCode(String jsonContent) {
		// 检查输入参数是否为null
		if (jsonContent == null) {
			log.error("解析JSON多文件代码失败：输入内容为null");
			return new MultiFileCodeResult();
		}
		
		MultiFileCodeResult result = new MultiFileCodeResult();
		try {
			JsonNode rootNode = objectMapper.readTree(jsonContent);
			
			// 提取HTML代码
			if (rootNode.has("htmlCode")) {
				String htmlCode = rootNode.get("htmlCode").asText();
				if (htmlCode != null && !htmlCode.trim().isEmpty()) {
					result.setHtmlCode(htmlCode.trim());
					log.debug("成功从JSON中提取HTML代码，长度: {}", htmlCode.length());
				} else {
					log.debug("JSON中htmlCode字段为空");
				}
			} else {
				log.debug("JSON中未找到htmlCode字段");
			}
			
			// 提取CSS代码
			if (rootNode.has("cssCode")) {
				String cssCode = rootNode.get("cssCode").asText();
				if (cssCode != null && !cssCode.trim().isEmpty()) {
					result.setCssCode(cssCode.trim());
					log.debug("成功从JSON中提取CSS代码，长度: {}", cssCode.length());
				} else {
					log.debug("JSON中cssCode字段为空");
				}
			} else {
				log.debug("JSON中未找到cssCode字段");
			}
			
			// 提取JS代码
			if (rootNode.has("jsCode")) {
				String jsCode = rootNode.get("jsCode").asText();
				if (jsCode != null && !jsCode.trim().isEmpty()) {
					result.setJsCode(jsCode.trim());
					log.debug("成功从JSON中提取JS代码，长度: {}", jsCode.length());
				} else {
					log.debug("JSON中jsCode字段为空");
				}
			} else {
				log.debug("JSON中未找到jsCode字段");
			}
			
			// 提取描述信息（如果需要）
			if (rootNode.has("description")) {
				String description = rootNode.get("description").asText();
				log.debug("成功从JSON中提取描述信息: {}", description);
			}
			
		} catch (IOException e) {
			log.error("解析JSON多文件代码失败: {}", e.getMessage(), e);
			// JSON解析失败时，结果对象各字段保持默认值（null）
		}
		return result;
	}
	
	/**
	 * 检查字符串是否为有效的JSON格式
	 *
	 * @param content 要检查的字符串
	 * @return 如果是有效JSON返回true，否则返回false
	 */
	public static boolean isJson(String content) {
		if (content == null) {
			return false;
		}
		
		try {
			objectMapper.readTree(content);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}