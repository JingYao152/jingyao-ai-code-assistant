package com.jingyao.jingyaoaicodeassistant.service.impl;

import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.model.vo.AppVO;
import com.jingyao.jingyaoaicodeassistant.model.vo.UserVO;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.jingyao.jingyaoaicodeassistant.model.entity.App;
import com.jingyao.jingyaoaicodeassistant.mapper.AppMapper;
import com.jingyao.jingyaoaicodeassistant.service.AppService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
	
	private final UserService userService;
	
	public AppServiceImpl(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 将应用实体对象转换为视图对象
	 *
	 * @param app 应用实体对象，包含应用的基础数据信息
	 * @return AppVO 应用视图对象，包含应用基础信息和关联的用户信息
	 */
	@Override
	public AppVO getAppVO(App app) {
		// 确保应用实体对象不为null
		if (app == null) {
			return null;
		}
		// 创建应用视图对象
		AppVO appVO = new AppVO();
		
		// 属性拷贝：将应用实体的基础属性复制到视图对象
		// 包括应用ID、名称、描述、创建时间等基础字段
		BeanUtils.copyProperties(app, appVO);
		
		// 获取应用创建者的用户ID：用于查询关联的用户信息
		Long userId = app.getUserId();
		
		// 用户信息填充：当用户ID存在时，查询并设置关联的用户信息
		if (userId != null) {
			// 根据用户ID查询用户实体对象
			User user = userService.getById(userId);
			
			// 将用户实体转换为用户视图对象
			UserVO userVO = userService.getUserVO(user);
			
			// 设置应用视图对象中的用户信息
			appVO.setUser(userVO);
		}
		// 返回填充完整的应用视图对象
		return appVO;
	}
}