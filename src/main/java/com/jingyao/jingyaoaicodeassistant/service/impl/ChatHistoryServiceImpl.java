package com.jingyao.jingyaoaicodeassistant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.jingyao.jingyaoaicodeassistant.model.entity.ChatHistory;
import com.jingyao.jingyaoaicodeassistant.mapper.ChatHistoryMapper;
import com.jingyao.jingyaoaicodeassistant.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

}
