package com.jingyao.jingyaoaicodeassistant.service.impl;

import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.model.vo.AppVO;
import com.jingyao.jingyaoaicodeassistant.model.vo.UserVO;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
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
	
	/**
	 * 构建应用查询条件包装器
	 *
	 * @param appQueryRequest 应用查询请求对象，包含所有可能的查询条件
	 * @return QueryWrapper 构建完成的查询条件包装器，可直接用于数据库查询
	 * @throws BusinessException 当查询请求对象为null时抛出参数错误异常
	 */
	@Override
	public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
		// 确保查询请求对象不为null
		if (appQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		
		// 提取查询条件：从请求对象中获取各个查询参数
		Long id = appQueryRequest.getId();                    // 应用ID
		String appName = appQueryRequest.getAppName();        // 应用名称
		String cover = appQueryRequest.getCover();            // 封面信息
		String initPrompt = appQueryRequest.getInitPrompt();    // 初始化提示
		String codeGenType = appQueryRequest.getCodeGenType(); // 代码生成类型
		String deployKey = appQueryRequest.getDeployKey();    // 部署密钥
		Integer priority = appQueryRequest.getPriority();     // 优先级
		Long userId = appQueryRequest.getUserId();            // 用户ID
		String sortField = appQueryRequest.getSortField();    // 排序字段
		String sortOrder = appQueryRequest.getSortOrder();    // 排序方式
		
		// 构建查询条件：使用链式调用构建复杂的查询条件
		return QueryWrapper.create()
			// 等值匹配条件：精确筛选特定字段
			.eq("id", id)                    // 应用ID精确匹配
			.eq("codeGenType", codeGenType)   // 代码生成类型精确匹配
			.eq("deployKey", deployKey)      // 部署密钥精确匹配
			.eq("priority", priority)         // 优先级精确匹配
			.eq("userId", userId)            // 用户ID精确匹配
			
			// 模糊匹配条件：支持关键字搜索
			.like("appName", appName)        // 应用名称模糊查询
			.like("cover", cover)            // 封面信息模糊查询
			.like("initPrompt", initPrompt)  // 初始化提示模糊查询
			
			// 排序条件：根据指定字段进行排序
			// 当sortOrder为"ascend"时升序排列，否则使用默认排序
			.orderBy(sortField, "ascend".equals(sortOrder));
	}
	
}