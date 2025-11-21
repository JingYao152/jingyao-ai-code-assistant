package com.jingyao.jingyaoaicodeassistant.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.ai.model.enums.CodeGenTypeEnum;
import com.jingyao.jingyaoaicodeassistant.common.BaseResponse;
import com.jingyao.jingyaoaicodeassistant.common.ResultUtils;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.model.dto.app.AppAddRequest;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.jingyao.jingyaoaicodeassistant.model.entity.App;
import com.jingyao.jingyaoaicodeassistant.service.AppService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
	
	
}
