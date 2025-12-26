package com.jingyao.jingyaoaicodeassistant.core.handler;

import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class StreamHandlerExecutor {
	
	@Resource
	private JsonMessageStreamHandler jsonMessageStreamHandler;
	
	public Flux<String> doExecute(Flux<String> originFlux, ChatHistoryService chatHistoryService, long appId,
	                              User loginUser, CodeGenTypeEnum codeGenTypeEnum) {
		return switch (codeGenTypeEnum) {
			case VUE_PROJECT -> jsonMessageStreamHandler.handle(originFlux, chatHistoryService, appId, loginUser);
			case HTML, MULTI_FILE ->
				new SimpleTextStreamHandler().handle(originFlux, chatHistoryService, appId, loginUser);
		};
	}
}
