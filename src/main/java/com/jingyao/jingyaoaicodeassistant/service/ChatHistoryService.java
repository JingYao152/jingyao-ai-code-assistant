package com.jingyao.jingyaoaicodeassistant.service;

import com.jingyao.jingyaoaicodeassistant.model.dto.chathistory.ChatHistoryQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.jingyao.jingyaoaicodeassistant.model.entity.ChatHistory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {
	
	boolean addChatMessage(Long appId, String message, String messageType, Long userId);
	
	boolean deleteByAppId(Long appId);
	
	QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);
	
	Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
	                                           LocalDateTime lastCreateTime,
	                                           User loginUser);
}
