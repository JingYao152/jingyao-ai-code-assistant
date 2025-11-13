package com.jingyao.jingyaoaicodeassistant.controller;

import cn.hutool.core.bean.BeanUtil;
import com.jingyao.jingyaoaicodeassistant.annotation.AuthCheck;
import com.jingyao.jingyaoaicodeassistant.common.BaseResponse;
import com.jingyao.jingyaoaicodeassistant.common.DeleteRequest;
import com.jingyao.jingyaoaicodeassistant.common.ResultUtils;
import com.jingyao.jingyaoaicodeassistant.constant.UserConstant;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.exception.ThrowUtils;
import com.jingyao.jingyaoaicodeassistant.model.dto.UserLoginRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.UserRegisterRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.user.UserAddRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.user.UserQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.dto.user.UserUpdateRequest;
import com.jingyao.jingyaoaicodeassistant.model.vo.LoginUserVO;
import com.jingyao.jingyaoaicodeassistant.model.vo.UserVO;
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
	
	/**
	 * 创建用户
	 * 只有拥有管理员权限的用户才能访问此接口
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
		// 检查请求参数是否为空，如果为空则抛出参数错误异常
		ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
		// 创建一个新的User对象
		User user = new User();
		// 将请求参数中的属性复制到User对象中
		BeanUtil.copyProperties(userAddRequest, user);
		// 默认密码 12345678
		final String DEFAULT_PASSWORD = "12345678";
		String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
		user.setUserPassword(encryptPassword);
		boolean result = userService.save(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(user.getId());
	}
	
	/**
	 * 根据 id 获取用户
	 * 该接口需要管理员权限才能访问
	 */
	@GetMapping("/get")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<User> getUserById(long id) {  // 方法：根据用户ID获取用户信息，返回基础响应对象
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);  // 参数校验：ID必须大于0，否则抛出参数错误异常
		User user = userService.getById(id);  // 调用服务层方法，根据ID获取用户信息
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);  // 校验用户是否存在，不存在则抛出未找到错误异常
		return ResultUtils.success(user);  // 返回成功响应，包含用户信息
	}
	
	/**
	 * 根据 id 获取用户视图对象
	 *
	 * @return 返回一个BaseResponse类型的对象，其中包含用户视图对象(UserVO)数据
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserVO> getUserVOById(long id) {
		// 调用getUserById方法获取基础用户信息，返回一个包含用户数据的BaseResponse对象
		BaseResponse<User> response = getUserById(id);
		// 从响应对象中提取用户数据
		User user = response.getData();
		// 使用ResultUtils工具类成功响应，并调用userService的getUserVO方法将用户对象转换为视图对象
		return ResultUtils.success(userService.getUserVO(user));
	}
	
	/**
	 * 删除用户的接口方法
	 * 需要管理员权限才能访问
	 *
	 * @param deleteRequest 包含要删除用户ID的请求对象
	 * @return 返回操作结果，成功则返回删除操作是否成功的布尔值
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {  // 接收请求体中的DeleteRequest对象
		// 参数校验：检查请求对象是否为空或ID是否无效（小于等于0）
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);  // 如果参数无效，抛出参数错误异常
		}
		// 调用userService的removeById方法，根据ID删除用户
		boolean b = userService.removeById(deleteRequest.getId());
		// 返回操作结果，使用ResultUtils工具类包装成功响应
		return ResultUtils.success(b);
	}
	
	/**
	 * 更新用户
	 * 需要管理员权限
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
		// 检查请求参数是否有效
		if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// 创建新的User对象并复制请求参数
		User user = new User();
		BeanUtil.copyProperties(userUpdateRequest, user);
		// 执行更新操作，如果失败则抛出异常
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回成功响应
		return ResultUtils.success(true);
	}
	
	/**
	 * 分页获取用户封装列表
	 * 该接口允许管理员分页查询用户信息，并返回经过封装和脱敏处理的用户视图对象（UserVO）
	 *
	 * @param userQueryRequest 查询请求参数，包含分页信息和查询条件
	 * @return BaseResponse<Page < UserVO> > 返回分页后的用户视图对象列表，包含总记录数和当前页数据
	 */
	@PostMapping("/list/page/vo")  // HTTP POST请求映射到/list/page/vo路径
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // 权限检查，仅管理员可访问
	public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
		// 参数校验，如果请求参数为空则抛出参数错误异常
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
		
		
		// 从请求参数中获取分页信息
		long pageNum = userQueryRequest.getPageNum();    // 当前页码
		long pageSize = userQueryRequest.getPageSize();  // 每页大小
		// 使用MyBatis-Plus的分页查询功能获取用户数据
		Page<User> userPage = userService.page(Page.of(pageNum, pageSize),
			userService.getQueryWrapper(userQueryRequest));
		// 数据脱敏
		Page<UserVO> userVOPage = new Page<>(pageNum, pageSize, userPage.getTotalRow());
		List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
		userVOPage.setRecords(userVOList);
		return ResultUtils.success(userVOPage);
	}
	
}
