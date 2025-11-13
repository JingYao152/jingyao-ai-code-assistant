package com.jingyao.jingyaoaicodeassistant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.model.dto.user.UserQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.enums.UserRoleEnum;
import com.jingyao.jingyaoaicodeassistant.model.vo.LoginUserVO;
import com.jingyao.jingyaoaicodeassistant.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.mapper.UserMapper;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jingyao.jingyaoaicodeassistant.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
	
	/**
	 * 用户注册方法
	 * @param userAccount 用户账号
	 * @param userPassword 用户密码
	 * @param checkPassword 确认密码
	 * @return 新注册用户的ID
	 */
	@Override
	public long userRegister(String userAccount, String userPassword, String checkPassword) {
		// 检查参数是否为空
		if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		// 检查用户账号长度是否过短
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
		}
		// 检查密码长度是否过短
		if (userPassword.length() < 8 || checkPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
		}
		// 检查两次输入的密码是否一致
		if (!userPassword.equals(checkPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
		}
		// 查询数据库中是否已存在该账号
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("userAccount", userAccount);
		long count = this.mapper.selectCountByQuery(queryWrapper);
		if (count > 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
		}
		// 对密码进行加密处理
		String encryptPassword = getEncryptPassword(userPassword);
		// 创建新用户对象
		User user = new User();
		user.setUserAccount(userAccount);
		user.setUserPassword(encryptPassword);
		// 默认用户昵称
		user.setUserName("无名");
		// 保存用户信息到数据库
		user.setUserRole(UserRoleEnum.USER.getValue());
		boolean saveResult = this.save(user);
		if (!saveResult) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
		}
		// 返回新注册用户的ID
		return user.getId();
	}
	
	/**
	 * 获取加密后的密码
	 * 使用MD5加密算法，并结合固定的盐值(SALT)对用户密码进行加密处理
	 *
	 * @param userPassword 用户输入的原始密码
	 * @return 返回经过MD5加密后的密码字符串
	 */
	@Override
	public String getEncryptPassword(String userPassword) {
		// 定义固定的盐值，用于增强密码安全性
		final String SALT = "1145141919180";
		// 将盐值与用户密码拼接后进行MD5加密，并返回十六进制格式的加密结果
		return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
	}
	
	/**
	 * 获取脱敏的用户对象
	 *
	 * @param user 用户实体对象
	 * @return 返回脱敏的用户对象
	 */
	@Override
	public LoginUserVO getLoginUserVO(User user) {
		// 检查用户对象是否为空
		if (user == null) {
			return null;
		}
		// 创建登录用户视图对象
		LoginUserVO loginUserVO = new LoginUserVO();
		// 将用户对象的属性复制到登录用户视图对象中
		// 不存在的字段会被自动过滤掉
		BeanUtil.copyProperties(user, loginUserVO);
		// 返回转换后的登录用户视图对象
		return loginUserVO;
	}
	
	
	/**
	 * 用户登录方法
	 * @param userAccount 用户账号
	 * @param userPassword 用户密码
	 * @param request HTTP请求对象，用于获取session
	 * @return LoginUserVO 登录用户信息对象
	 */
	@Override
	public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		// 检查参数是否为空
		if (StrUtil.hasBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		// 检查账号长度是否合法
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
		}
		// 检查密码长度是否合法
		if (userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
		}
		// 对密码进行加密处理
		String encryptPassword = getEncryptPassword(userPassword);
		// 创建查询条件，查询用户账号和密码匹配的用户
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("userAccount", userAccount);
		queryWrapper.eq("userPassword", encryptPassword);
		User user = this.mapper.selectOneByQuery(queryWrapper);
		// 如果用户不存在或密码错误，抛出异常
		if (user == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
		}
		// 将用户信息存入session中
		request.getSession().setAttribute(USER_LOGIN_STATE, user);
		// 返回登录用户信息视图对象
		return this.getLoginUserVO(user);
	}
	
	/**
	 * 获取当前登录用户信息
	 * @param request HTTP请求对象，用于获取当前登录用户的会话信息
	 * @return 返回当前登录用户的实体对象
	 */
	@Override
	public User getLoginUser(HttpServletRequest request) {
		// 从会话中获取当前登录用户信息
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		User currentUser = (User) userObj;
		// 如果会话中不存在登录用户信息，抛出异常
		if (currentUser == null || currentUser.getId() == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		// 返回当前登录用户的实体对象
		return currentUser;
	}
	
	/**
	 * 用户退出登录方法
	 * @param request HttpServletRequest对象，用于获取会话信息
	 * @return boolean 返回true表示退出登录成功
	 * @throws BusinessException 当用户未登录时抛出业务异常
	 */
	@Override
	public boolean userLogout(HttpServletRequest request) {
		// 从会话中获取用户登录状态信息
		Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
		// 如果用户未登录，抛出业务异常
		if (userObj == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
		}
		// 从会话中移除用户登录状态信息，实现退出登录
		request.getSession().removeAttribute(USER_LOGIN_STATE);
		// 返回true表示退出登录成功
		return true;
	}
	
	/**
	 * 重写父类方法，将User对象转换为UserVO对象
	 * @param user 用户实体对象
	 * @return 返回转换后的UserVO视图对象，如果传入的user为null则返回null
	 */
	@Override
	public UserVO getUserVO(User user) {
		// 参数校验，如果user为null则直接返回null
		if (user == null) {
			return null;
		}
		// 创建UserVO对象
		UserVO userVO = new UserVO();
		// 使用BeanUtil工具类将user对象的属性值复制到userVO对象中
		BeanUtil.copyProperties(user, userVO);
		// 返回转换后的UserVO对象
		return userVO;
	}
	
	
	/**
	 * 将用户实体列表转换为用户视图对象列表
	 * @param userList 用户实体列表，包含完整的用户信息
	 * @return 用户视图对象列表，仅包含需要展示给用户的信息
	 */
	@Override
	public List<UserVO> getUserVOList(List<User> userList) {
		// 如果传入的用户列表为空，则返回一个空列表，避免空指针异常
		if (CollUtil.isEmpty(userList)) {
			return new ArrayList<>();
		}
		// 使用Java 8 Stream API将用户实体列表转换为视图对象列表
		// 通过map操作将每个User对象转换为UserVO对象
		// 最后通过collect将结果收集为List返回
		return userList.stream().map(this::getUserVO).collect(Collectors.toList());
	}
	
	
	/**
	 * 根据用户查询请求构建查询条件包装器
	 * @param userQueryRequest 用户查询请求对象，包含查询条件
	 * @return QueryWrapper 构建好的查询条件包装器，用于数据库查询
	 * @throws BusinessException 当请求参数为空时抛出业务异常
	 */
	@Override
	public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
		// 检查请求参数是否为空，若为空则抛出业务异常
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		// 从请求对象中获取查询条件
		Long id = userQueryRequest.getId();
		String userAccount = userQueryRequest.getUserAccount();
		String userName = userQueryRequest.getUserName();
		String userProfile = userQueryRequest.getUserProfile();
		String userRole = userQueryRequest.getUserRole();
		String sortField = userQueryRequest.getSortField();
		String sortOrder = userQueryRequest.getSortOrder();
		// 创建查询条件包装器，并设置查询条件和排序规则
		return QueryWrapper.create()
			.eq("id", id) // 精确匹配ID
			.eq("userRole", userRole) // 精确匹配用户角色
			.like("userAccount", userAccount) // 模糊匹配用户账号
			.like("userName", userName) // 模糊匹配用户名
			.like("userProfile", userProfile) // 模糊匹配用户简介
			.orderBy(sortField, "ascend".equals(sortOrder)); // 根据指定字段和排序方式进行排序
	}
	
}
