package com.jingyao.jingyaoaicodeassistant.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.common.BaseResponse;
import com.jingyao.jingyaoaicodeassistant.common.DeleteRequest;
import com.jingyao.jingyaoaicodeassistant.common.ResultUtils;
import com.jingyao.jingyaoaicodeassistant.constant.UserConstant;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppAddRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppUpdateRequest;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.jingyao.jingyaoaicodeassistant.model.entity.App;
import com.jingyao.jingyaoaicodeassistant.service.AppService;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
	
}