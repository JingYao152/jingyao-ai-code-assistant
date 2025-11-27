package com.jingyao.jingyaoaicodeassistant.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.constant.AppConstant;
import com.jingyao.jingyaoaicodeassistant.core.AiCodeGeneratorFacade;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
	@Autowired
	private final UserService userService;
	@Autowired
	private final AiCodeGeneratorFacade aiCodeGeneratorFacade;
	
	public AppServiceImpl(UserService userService, AiCodeGeneratorFacade aiCodeGeneratorFacade) {
		this.userService = userService;
		this.aiCodeGeneratorFacade = aiCodeGeneratorFacade;
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
	
	/**
	 * 批量将应用实体列表转换为视图对象列表
	 *
	 * @param appList 应用实体对象列表，包含需要转换的应用数据
	 * @return List<AppVO> 应用视图对象列表，包含完整的应用和关联用户信息
	 */
	@Override
	public List<AppVO> getAppVOList(List<App> appList) {
		// 检查应用列表是否为空或null
		if (CollUtil.isEmpty(appList)) {
			return new ArrayList<>();
		}
		
		// 批量获取用户信息，避免N+1查询问题
		// N+1查询问题：如果在循环中逐个查询用户信息，会导致1次主查询+N次子查询的性能问题
		
		// 使用Stream API提取所有应用的用户ID，并通过Collectors.toSet()自动去重
		Set<Long> userIds = appList.stream()
			.map(App::getUserId)  // 提取每个应用的用户ID
			.collect(Collectors.toSet());  // 收集到Set中自动去重
		
		// 一次性查询所有用户，避免循环查询数据库
		Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
			.collect(Collectors.toMap(
				User::getId,           // Key：用户ID
				userService::getUserVO // Value：用户视图对象
			));
		
		// 批量转换应用实体为视图对象
		return appList.stream().map(app -> {
			// 获取基础的应用视图对象（不包含用户信息）
			AppVO appVO = getAppVO(app);
			
			// 从预加载的用户映射中获取对应的用户信息
			UserVO userVO = userVOMap.get(app.getUserId());
			
			// 设置应用视图对象中的用户信息
			// 实现应用与用户的关联关系
			appVO.setUser(userVO);
			
			// 返回完整的应用视图对象
			return appVO;
		}).collect(Collectors.toList());  // 收集为列表返回
	}
	
	/**
	 * 通过对话生成代码的核心业务方法
	 *
	 * @param appId 应用ID，用于标识具体的代码生成应用，必须为正数
	 * @param message 用户输入的消息内容，包含代码生成需求描述，不能为空
	 * @param loginUser 当前登录用户，用于权限验证和代码生成上下文
	 * @return {@code Flux<String>} 返回流式字符串，包含AI生成的代码内容，支持实时推送
	 * @throws BusinessException 当应用ID无效时抛出参数错误异常
	 * @throws BusinessException 当用户消息为空时抛出参数错误异常
	 * @throws BusinessException 当应用不存在时抛出未找到错误异常
	 * @throws BusinessException 当用户无权限访问应用时抛出权限错误异常
	 * @throws BusinessException 当代码生成类型不支持时抛出系统错误异常
	 */
	@Override
	public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
		// 1. 参数校验
		ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
		ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
		// 2. 查询应用信息
		App app = this.getById(appId);
		ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
		// 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
		if (!app.getUserId().equals(loginUser.getId())) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
		}
		// 4. 获取应用的代码生成类型
		String codeGenTypeStr = app.getCodeGenType();
		CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
		if (codeGenTypeEnum == null) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
		}
		// 5. 调用 AI 生成代码
		return aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
	}
	
	/**
	 * 应用部署服务方法
	 *
	 * @param appId 应用ID，用于标识要部署的应用，必须为正数
	 * @param loginUser 当前登录用户，用于权限验证和部署记录
	 * @return {@code String} 返回部署后应用的可访问URL，格式为"部署主机/部署密钥/"
	 * @throws BusinessException 当应用ID无效时抛出参数错误异常
	 * @throws BusinessException 当用户未登录时抛出未登录错误异常
	 * @throws BusinessException 当应用不存在时抛出未找到错误异常
	 * @throws BusinessException 当用户无权限部署应用时抛出权限错误异常
	 * @throws BusinessException 当应用代码不存在时抛出系统错误异常
	 * @throws BusinessException 当文件复制失败时抛出系统错误异常
	 * @throws BusinessException 当更新部署信息失败时抛出操作错误异常
	 */
	@Override
	public String deployApp(Long appId, User loginUser) {
		// 1. 参数校验
		ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
		ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
		// 2. 查询应用信息
		App app = this.getById(appId);
		ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
		// 3. 验证用户是否有权限部署该应用，仅本人可以部署
		if (!app.getUserId().equals(loginUser.getId())) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
		}
		// 4. 检查是否已有 deployKey
		String deployKey = app.getDeployKey();
		// 没有则生成 6 位 deployKey（大小写字母 + 数字）
		if (StrUtil.isBlank(deployKey)) {
			deployKey = RandomUtil.randomString(6);
		}
		// 5. 获取代码生成类型，构建源目录路径
		String codeGenType = app.getCodeGenType();
		String sourceDirName = codeGenType + "_" + appId;
		String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
		// 6. 检查源目录是否存在
		File sourceDir = new File(sourceDirPath);
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
		}
		// 7. 复制文件到部署目录
		String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
		try {
			FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
		}
		// 8. 更新应用的 deployKey 和部署时间
		App updateApp = new App();
		updateApp.setId(appId);
		updateApp.setDeployKey(deployKey);
		updateApp.setDeployedTime(LocalDateTime.now());
		boolean updateResult = this.updateById(updateApp);
		ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
		// 9. 返回可访问的 URL
		return String.format("%s/%s/", AppConstant.CODE_DEPLOY_HOST, deployKey);
	}
	
}