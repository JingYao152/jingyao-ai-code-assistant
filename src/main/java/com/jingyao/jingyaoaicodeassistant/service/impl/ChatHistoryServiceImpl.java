package com.jingyao.jingyaoaicodeassistant.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.constant.UserConstant;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.model.dto.chathistory.ChatHistoryQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.entity.App;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.model.enums.ChatHistoryMessageTypeEnum;
import com.jingyao.jingyaoaicodeassistant.service.AppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.jingyao.jingyaoaicodeassistant.model.entity.ChatHistory;
import com.jingyao.jingyaoaicodeassistant.mapper.ChatHistoryMapper;
import com.jingyao.jingyaoaicodeassistant.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@Slf4j
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {
	@Autowired
	@Lazy
	private AppService appService;
	
	@Override
	public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {
		ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
		ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
		ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
		ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
		// 验证消息类型是否有效
		ChatHistoryMessageTypeEnum messageTypeEnum = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
		ThrowUtils.throwIf(messageTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的消息类型: " + messageType);
		ChatHistory chatHistory = ChatHistory.builder()
			.appId(appId)
			.message(message)
			.messageType(messageType)
			.userId(userId)
			.build();
		return this.save(chatHistory);
	}
	
	@Override
	public boolean deleteByAppId(Long appId) {
		ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
		QueryWrapper queryWrapper = QueryWrapper.create()
			.eq("appId", appId);
		return this.remove(queryWrapper);
	}
	
	/**
	 * 获取查询包装类
	 *
	 * @param chatHistoryQueryRequest 对话历史查询请求
	 * @return 查询包装类
	 */
	@Override
	public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
		QueryWrapper queryWrapper = QueryWrapper.create();
		if (chatHistoryQueryRequest == null) {
			return queryWrapper;
		}
		Long id = chatHistoryQueryRequest.getId();
		String message = chatHistoryQueryRequest.getMessage();
		String messageType = chatHistoryQueryRequest.getMessageType();
		Long appId = chatHistoryQueryRequest.getAppId();
		Long userId = chatHistoryQueryRequest.getUserId();
		LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
		String sortField = chatHistoryQueryRequest.getSortField();
		String sortOrder = chatHistoryQueryRequest.getSortOrder();
		// 拼接查询条件
		queryWrapper.eq("id", id)
			.like("message", message)
			.eq("messageType", messageType)
			.eq("appId", appId)
			.eq("userId", userId);
		// 游标查询逻辑 - 只使用 createTime 作为游标
		if (lastCreateTime != null) {
			queryWrapper.lt("createTime", lastCreateTime);
		}
		// 排序
		if (StrUtil.isNotBlank(sortField)) {
			queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
		} else {
			// 默认按创建时间降序排列
			queryWrapper.orderBy("createTime", false);
		}
		return queryWrapper;
	}
	
	@Override
	public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
	                                                  LocalDateTime lastCreateTime,
	                                                  User loginUser) {
		ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
		ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
		ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
		// 验证权限：只有应用创建者和管理员可以查看
		App app = appService.getById(appId);
		ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
		boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
		boolean isCreator = app.getUserId().equals(loginUser.getId());
		ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权查看该应用的对话历史");
		// 构建查询条件
		ChatHistoryQueryRequest queryRequest = new ChatHistoryQueryRequest();
		queryRequest.setAppId(appId);
		queryRequest.setLastCreateTime(lastCreateTime);
		QueryWrapper queryWrapper = this.getQueryWrapper(queryRequest);
		// 查询数据
		return this.page(Page.of(1, pageSize), queryWrapper);
	}
	
	
	/**
	 * 加载聊天历史到内存中
	 * @param appId 应用ID，用于标识不同的应用
	 * @param chatMemory 聊天内存窗口对象，用于存储加载的历史消息
	 * @param maxCount 最大加载的消息数量
	 * @return 成功加载的消息数量，如果发生异常则返回0
	 */
	@Override
	public int loadChatHistoryToMemory(long appId, MessageWindowChatMemory chatMemory, int maxCount) {
		try {
			// 构造查询条件，从1开始避免将用户最新的消息重复添加
			QueryWrapper queryWrapper = QueryWrapper.create()
				.eq(ChatHistory::getAppId, appId)  // 设置查询条件：应用ID
				.orderBy(ChatHistory::getCreateTime, false)  // 按创建时间降序排列
				.limit(1, maxCount);  // 设置查询范围：从第1条开始，最多查询maxCount条
			// 执行查询，获取聊天历史记录列表
			List<ChatHistory> historyList = this.list(queryWrapper);
			// 如果查询结果为空，直接返回0
			if (CollUtil.isEmpty(historyList)) {
				return 0;
			}
			// 对查询到的消息进行反转，使消息按时间正序排列
			historyList = historyList.reversed();
			
			int loadedCount = 0;  // 记录成功加载的消息数量
			chatMemory.clear();  // 清空聊天内存窗口
			// 遍历历史消息列表，将消息添加到聊天内存窗口中
			for (ChatHistory history : historyList) {
				// 判断消息类型，如果是用户消息
				if (ChatHistoryMessageTypeEnum.USER.getValue().equals(history.getMessageType())) {
					chatMemory.add(UserMessage.from(history.getMessage()));  // 添加用户消息
					loadedCount++;  // 加载计数加1
				} else if (ChatHistoryMessageTypeEnum.AI.getValue().equals(history.getMessageType())) {
					chatMemory.add(AiMessage.from(history.getMessage()));
					loadedCount++;
				}
			}
			log.info("成功加载了appId:{}的{}条历史对话", appId, loadedCount);
			return loadedCount;
		} catch (Exception e) {
			log.error("对话历史加载失败，appId:{}，error:{}", appId, e.getMessage(), e);
			return 0;
		}
	}
}
