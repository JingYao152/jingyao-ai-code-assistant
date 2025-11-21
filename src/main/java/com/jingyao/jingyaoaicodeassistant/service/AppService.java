package com.jingyao.jingyaoaicodeassistant.service;

import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.vo.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.jingyao.jingyaoaicodeassistant.model.entity.App;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
public interface AppService extends IService<App> {
	AppVO getAppVO(App app);
	
	QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);
	
	List<AppVO> getAppVOList(List<App> appList);
}
