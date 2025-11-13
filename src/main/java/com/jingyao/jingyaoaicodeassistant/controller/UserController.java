package com.jingyao.jingyaoaicodeassistant.controller;

import com.jingyao.jingyaoaicodeassistant.common.BaseResponse;
import com.jingyao.jingyaoaicodeassistant.common.ResultUtils;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.model.dto.UserLoginRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.UserRegisterRequest;
import com.jingyao.jingyaoaicodeassistant.model.vo.LoginUserVO;
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
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 保存用户。
	 *
	 * @param user 用户
	 * @return {@code true} 保存成功，{@code false} 保存失败
	 */
	@PostMapping("save")
	public boolean save(@RequestBody User user) {
		return userService.save(user);
	}
	
	/**
	 * 根据主键删除用户。
	 *
	 * @param id 主键
	 * @return {@code true} 删除成功，{@code false} 删除失败
	 */
	@DeleteMapping("remove/{id}")
	public boolean remove(@PathVariable Long id) {
		return userService.removeById(id);
	}
	
	/**
	 * 根据主键更新用户。
	 *
	 * @param user 用户
	 * @return {@code true} 更新成功，{@code false} 更新失败
	 */
	@PutMapping("update")
	public boolean update(@RequestBody User user) {
		return userService.updateById(user);
	}
	
	/**
	 * 查询所有用户。
	 *
	 * @return 所有数据
	 */
	@GetMapping("list")
	public List<User> list() {
		return userService.list();
	}
	
	/**
	 * 根据主键获取用户。
	 *
	 * @param id 用户主键
	 * @return 用户详情
	 */
	@GetMapping("getInfo/{id}")
	public User getInfo(@PathVariable Long id) {
		return userService.getById(id);
	}
	
	/**
	 * 分页查询用户。
	 *
	 * @param page 分页对象
	 * @return 分页对象
	 */
	@GetMapping("page")
	public Page<User> page(Page<User> page) {
		return userService.page(page);
	}
	
	/**
	 * 用户注册
	 * 该接口处理用户注册请求，验证参数并调用服务层方法完成用户注册
	 *
	 * @param userRegisterRequest 用户注册请求，包含用户账号、密码和确认密码信息
	 * @return 注册结果
	 */
	@PostMapping("register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		long result = userService.userRegister(userAccount, userPassword, checkPassword);
		return ResultUtils.success(result);
	}
	
	/**
	 * 用户登录接口
	 * @param userLoginRequest 用户登录请求体，包含用户账号和密码
	 * @param request HTTP请求对象，用于获取请求相关信息
	 * @return BaseResponse<LoginUserVO> 返回登录结果，包含用户信息和登录状态
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
	                                           HttpServletRequest request) {
		// 检查请求参数是否为空，为空则抛出参数错误异常
		ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
		// 从请求体中获取用户账号
		String userAccount = userLoginRequest.getUserAccount();
		// 从请求体中获取用户密码
		String userPassword = userLoginRequest.getUserPassword();
		// 调用用户服务层处理登录逻辑，返回登录用户信息
		LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
		// 返回登录成功的响应结果
		return ResultUtils.success(loginUserVO);
	}
	
	/**
	 * 获取当前登录用户的接口
	 * 通过HTTP GET请求访问此端点可以获取当前登录用户的信息
	 *
	 * @param request HTTP请求对象，用于获取请求中的用户信息
	 * @return 返回一个BaseResponse，其中包含LoginUserVO对象，表示当前登录用户的信息
	 */
	@GetMapping("/get/login")
	public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
		// 通过userService获取当前登录用户对象
		User loginUser = userService.getLoginUser(request);
		// 使用ResultUtils工具类构建成功响应，并将用户信息转换为VO脱敏对象返回
		return ResultUtils.success(userService.getLoginUserVO(loginUser));
	}
	
	/**
	 * 处理用户登出请求的接口方法
	 *
	 * @param request HttpServletRequest对象，包含请求相关信息
	 * @return BaseResponse<Boolean> 返回操作结果，包含登出操作是否成功
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
		// 检查请求参数是否为空，如果为空则抛出参数错误异常
		ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
		// 调用 userService 的 userLogout 方法处理登出逻辑，获取操作结果
		boolean result = userService.userLogout(request);
		// 返回操作成功结果，将 result 包装成成功响应返回
		return ResultUtils.success(result);
	}
}
