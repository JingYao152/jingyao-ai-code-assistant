package com.jingyao.jingyaoaicodeassistant.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.annotation.AuthCheck;
import com.jingyao.jingyaoaicodeassistant.common.BaseResponse;
import com.jingyao.jingyaoaicodeassistant.common.DeleteRequest;
import com.jingyao.jingyaoaicodeassistant.common.ResultUtils;
import com.jingyao.jingyaoaicodeassistant.constant.AppConstant;
import com.jingyao.jingyaoaicodeassistant.constant.UserConstant;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppAddRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppAdminUpdateRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppUpdateRequest;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.model.vo.AppVO;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.jingyao.jingyaoaicodeassistant.model.entity.App;
import com.jingyao.jingyaoaicodeassistant.service.AppService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@RestController
@RequestMapping("/app")
public class AppController {
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 创建应用
	 *
	 * @param appAddRequest 创建应用请求
	 * @param request 请求对象
	 * @return 应用 id
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
		// 参数校验
		String initPrompt = appAddRequest.getInitPrompt();
		ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化 prompt 不能为空");
		// 获取当前登录用户
		User loginUser = userService.getLoginUser(request);
		// 构造入库对象
		App app = new App();
		BeanUtil.copyProperties(appAddRequest, app);
		app.setUserId(loginUser.getId());
		// 应用名称设置为 initPrompt 的前 12 位
		app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
		// 代码生成类型设置为多文件生成
		app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
		// 插入数据库
		boolean result = appService.save(app);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(app.getId());
	}
	
	/**
	 * 更新应用（用户只能更新自己的应用名称）
	 *
	 * @param appUpdateRequest 应用更新请求对象，包含要更新的应用ID和新名称
	 * @param request          HTTP请求对象，用于获取当前登录用户信息
	 * @return BaseResponse<Boolean> 返回更新操作是否成功的布尔值
	 * @throws BusinessException 当参数无效、应用不存在或无权限时抛出业务异常
	 * @throws BusinessException 当数据库更新操作失败时抛出业务异常
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest,
	                                       HttpServletRequest request) {
		// 参数合法性校验：确保请求对象和应用ID不为null
		if (appUpdateRequest == null || appUpdateRequest.getId() == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 获取当前登录用户信息，用于权限验证
		User loginUser = userService.getLoginUser(request);
		// 提取应用ID，用于数据库查询
		long id = appUpdateRequest.getId();
		// 数据存在性检查：验证要更新的应用是否存在
		App oldApp = appService.getById(id);
		ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
		// 权限验证：确保只有应用所有者才能更新应用
		if (!oldApp.getUserId().equals(loginUser.getId())) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 构建更新对象：只设置需要更新的字段
		App app = new App();
		app.setId(id);
		app.setAppName(appUpdateRequest.getAppName());
		// 设置编辑时间：自动记录当前时间为最后编辑时间
		app.setEditTime(LocalDateTime.now());
		// 执行数据库更新操作
		boolean result = appService.updateById(app);
		// 操作结果校验：确保更新操作成功执行
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回更新成功的响应
		return ResultUtils.success(true);
	}
	
	/**
	 * 删除应用（用户只能删除自己的应用）
	 *
	 * @param deleteRequest 删除请求对象，包含要删除的应用ID
	 * @param request HTTP请求对象，用于获取当前登录用户信息
	 * @return 删除结果
	 * @throws BusinessException 当参数无效时抛出参数错误异常
	 * @throws BusinessException 当应用不存在时抛出未找到错误异常
	 * @throws BusinessException 当用户无权限删除时抛出无权限错误异常
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		// 参数合法性校验：确保请求对象不为null且应用ID为正数
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 获取当前登录用户信息，用于权限验证
		User loginUser = userService.getLoginUser(request);
		// 提取应用ID，用于数据库查询
		long id = deleteRequest.getId();
		// 判断应用是否存在
		App oldApp = appService.getById(id);
		ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除应用
		if (!oldApp.getUserId().equals(loginUser.getId()) && !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 执行数据库删除操作
		boolean result = appService.removeById(id);
		// 操作结果校验：确保删除操作成功执行
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回删除成功的响应
		return ResultUtils.success(result);
	}
	
	/**
	 * 根据 id 获取应用详情
	 *
	 * @param id 应用 id
	 * @return 应用详情
	 */
	@GetMapping("/get/vo")
	public BaseResponse<AppVO> getAppVOById(long id) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		App app = appService.getById(id);
		ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取包含用户信息的封装类
		return ResultUtils.success(appService.getAppVO(app));
	}
	
	/**
	 * 分页获取当前用户创建的应用列表
	 *
	 * @param appQueryRequest 应用查询请求对象，包含分页参数和查询条件
	 * @param request         HTTP请求对象，用于获取当前登录用户信息
	 * @return {@code BaseResponse<Page<AppVO>>} 返回分页的应用视图对象列表
	 * @throws BusinessException 当查询请求参数无效时抛出参数错误异常
	 * @throws BusinessException 当每页查询数量超过20个时抛出参数错误异常
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest,
	                                                   HttpServletRequest request) {
		// 确保查询请求对象不为null
		ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
		
		// 获取当前登录用户信息，用于权限验证和数据过滤
		User loginUser = userService.getLoginUser(request);
		
		// 限制每页最多查询20个应用，防止数据库压力过大
		long pageSize = appQueryRequest.getPageSize();
		ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
		long pageNum = appQueryRequest.getPageNum();
		
		// 只查询当前用户创建的应用，确保数据隔离
		appQueryRequest.setUserId(loginUser.getId());
		
		// 根据查询请求构建MyBatis-Flex查询包装器
		QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
		
		// 获取符合条件的应用列表
		Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
		
		// 将实体对象转换为视图对象并补充用户信息
		Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
		List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
		appVOPage.setRecords(appVOList);
		
		// 返回包含分页数据和应用列表的成功响应
		return ResultUtils.success(appVOPage);
	}
	
	/**
	 * 分页获取精选应用列表
	 *
	 * @param appQueryRequest 应用查询请求对象，包含分页参数和查询条件
	 * @return {@code BaseResponse<Page<AppVO>>} 返回分页的精选应用视图对象列表
	 * @throws BusinessException 当查询请求参数无效时抛出参数错误异常
	 * @throws BusinessException 当每页查询数量超过20个时抛出参数错误异常
	 */
	@PostMapping("/good/list/page/vo")
	public BaseResponse<Page<AppVO>> listGoodAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {
		// 确保查询请求对象不为null
		ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
		
		// 确保每页查询数量不超过20个应用，防止数据库压力过大
		long pageSize = appQueryRequest.getPageSize();
		ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "每页最多查询 20 个应用");
		long pageNum = appQueryRequest.getPageNum();
		
		// 设置查询条件只获取优先级为精选的应用
		// AppConstant.GOOD_APP_PRIORITY 表示精选应用的优先级值
		appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
		
		// 根据查询请求构建MyBatis-Flex查询包装器
		// 自动包含优先级筛选条件
		QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
		
		// 获取符合条件的精选应用列表
		Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
		
		// 将实体对象转换为视图对象并补充用户信息
		Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
		List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
		appVOPage.setRecords(appVOList);
		
		// 返回包含分页数据和精选应用列表的成功响应
		return ResultUtils.success(appVOPage);
	}
	
	/**
	 * 管理员删除应用
	 *
	 * @param deleteRequest 删除请求对象，包含要删除的应用ID
	 * @return BaseResponse<Boolean> 返回删除操作是否成功的布尔值，true表示删除成功
	 * @throws BusinessException 当删除请求参数无效时抛出参数错误异常
	 * @throws BusinessException 当应用不存在时抛出未找到错误异常
	 * @throws BusinessException 当用户无管理员权限时由@AuthCheck注解处理并抛出权限异常
	 * @see AuthCheck 权限检查注解，确保只有管理员可以访问
	 */
	@PostMapping("/admin/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody DeleteRequest deleteRequest) {
		// 确保请求对象不为null且应用ID为正数
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		// 提取应用ID，用于数据库操作
		long id = deleteRequest.getId();
		
		// 验证要删除的应用是否存在
		App oldApp = appService.getById(id);
		ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
		
		// 执行数据库删除操作，无需验证应用所有者
		boolean result = appService.removeById(id);
		
		// 返回删除结果，包含操作成功状态的统一响应
		return ResultUtils.success(result);
	}
	
	/**
	 * 管理员更新应用
	 *
	 * @param appAdminUpdateRequest 管理员应用更新请求对象，包含要更新的应用ID和新的应用信息
	 * @return {@code BaseResponse<Boolean>} 返回更新操作是否成功的布尔值，true表示更新成功
	 * @throws BusinessException 当更新请求参数无效时抛出参数错误异常
	 * @throws BusinessException 当应用不存在时抛出未找到错误异常
	 * @throws BusinessException 当数据库更新操作失败时抛出操作错误异常
	 * @throws BusinessException 当用户无管理员权限时由@AuthCheck注解处理并抛出权限异常
	 * @see AppAdminUpdateRequest 管理员应用更新请求对象，包含完整的应用更新信息
	 * @see AuthCheck 权限检查注解，确保只有管理员可以访问
	 * @see LocalDateTime#now() 获取当前时间作为编辑时间
	 */
	@PostMapping("/admin/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateAppByAdmin(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
		// 确保请求对象和应用ID不为null
		if (appAdminUpdateRequest == null || appAdminUpdateRequest.getId() == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		
		// 提取应用ID，用于数据库查询和更新操作
		long id = appAdminUpdateRequest.getId();
		
		// 验证要更新的应用是否存在
		App oldApp = appService.getById(id);
		ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
		
		// 创建新的应用实体用于数据更新
		App app = new App();
		// 将请求中的所有属性拷贝到应用实体，支持批量字段更新
		BeanUtil.copyProperties(appAdminUpdateRequest, app);
		
		// 自动记录当前时间为最后编辑时间
		app.setEditTime(LocalDateTime.now());
		
		// 执行数据库更新操作，无需验证应用所有者
		boolean result = appService.updateById(app);
		
		// 操作结果校验，确保更新操作成功执行
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		
		// 返回更新成功的响应：包含操作成功状态的统一响应
		return ResultUtils.success(true);
	}
	
	/**
	 * 管理员分页获取应用列表
	 *
	 * @param appQueryRequest 应用查询请求对象，包含分页参数和查询条件
	 * @return BaseResponse<Page < AppVO>> 返回分页的应用视图对象列表，包含系统中所有用户的应用
	 * @throws BusinessException 当查询请求参数无效时抛出参数错误异常
	 * @throws BusinessException 当用户无管理员权限时由@AuthCheck注解处理并抛出权限异常
	 * @see AppQueryRequest 应用查询请求对象，支持多条件组合查询
	 * @see AuthCheck 权限检查注解，确保只有管理员可以访问
	 * @see AppVO 应用视图对象，包含完整的应用和用户信息
	 */
	@PostMapping("/admin/list/page/vo")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<AppVO>> listAppVOByPageByAdmin(@RequestBody AppQueryRequest appQueryRequest) {
		// 确保查询请求对象不为null
		ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
		
		// 获取页码和每页数量
		long pageNum = appQueryRequest.getPageNum();
		long pageSize = appQueryRequest.getPageSize();
		
		// 根据查询请求构建MyBatis-Flex查询包装器
		// 管理员可以查看所有数据，不受用户ID限制
		QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);
		
		// 获取符合条件的应用列表
		// 管理员拥有全数据访问权限，可查看所有用户的应用
		Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);
		
		// 将实体对象转换为视图对象并补充用户信息
		// 使用getAppVOList方法批量转换并避免N+1查询问题
		Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
		List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
		appVOPage.setRecords(appVOList);
		
		// 返回成功响应，包含分页数据和应用列表的统一响应
		return ResultUtils.success(appVOPage);
	}
	
	/**
	 * 管理员根据ID获取应用详情
	 *
	 * @param id 应用ID，必须为正整数
	 * @return {@code BaseResponse<AppVO>} 返回包含应用详情的统一响应对象，包含完整的应用和用户信息
	 * @throws BusinessException 当应用ID无效（小于等于0）时抛出参数错误异常
	 * @throws BusinessException 当应用不存在时抛出未找到错误异常
	 * @throws BusinessException 当用户无管理员权限时由@AuthCheck注解处理并抛出权限异常
	 * @see AppVO 应用视图对象，包含应用信息和创建者用户信息
	 * @see AuthCheck 权限检查注解，确保只有管理员可以访问
	 */
	@GetMapping("/admin/get/vo")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<AppVO> getAppVOByIdByAdmin(long id) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		App app = appService.getById(id);
		ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(appService.getAppVO(app));
	}
	
	/**
	 * 应用聊天生成代码（流式 SSE）
	 *
	 * @param appId   应用 ID
	 * @param message 用户消息
	 * @param request 请求对象
	 * @return 生成结果流
	 */
	@GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam Long appId,
	                                                   @RequestParam String message,
	                                                   HttpServletRequest request) {
		// 参数校验
		ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
		ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
		// 获取当前登录用户
		User loginUser = userService.getLoginUser(request);
		// 调用服务生成代码（流式）
		Flux<String> contentFlux = appService.chatToGenCode(appId, message, loginUser);
		// 将字符串内容包装为 ServerSentEvent 流
		return contentFlux.map(chunk -> {
				Map<String, String> wrapper = Map.of("d", chunk);
				String jsonData = JSONUtil.toJsonStr(wrapper);
				return ServerSentEvent.<String>builder()
					.data(jsonData)
					.build();
			})
			.concatWith(Mono.just(
				ServerSentEvent.<String>builder()
					.event("DONE")
					.data("")
					.build()
			));
	}
	
}