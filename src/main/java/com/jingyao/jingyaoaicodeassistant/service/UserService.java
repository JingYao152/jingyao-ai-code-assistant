package com.jingyao.jingyaoaicodeassistant.service;

import com.jingyao.jingyaoaicodeassistant.model.dto.user.UserQueryRequest;
import com.jingyao.jingyaoaicodeassistant.model.vo.LoginUserVO;
import com.jingyao.jingyaoaicodeassistant.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
public interface UserService extends IService<User> {
	/**
	 * 用户注册方法
	 * @param userAccount 用户账号，用于注册的唯一标识
	 * @param userPassword 用户密码，用于账户登录的密码
	 * @param checkPassword 确认密码，用于二次确认用户输入的密码是否正确
	 * @return 新用户ID
	 */
	long userRegister(String userAccount, String userPassword, String checkPassword);
	
	/**
	 * 获取加密后的密码字符串
	 * @param userPassword 用户原始密码字符串
	 * @return 返回加密处理后的密码字符串
	 */
	String getEncryptPassword(String userPassword);
	
	/**
	 * 获取脱敏的已登录用户对象
	 * @param user 用户实体对象
	 * @return 返回脱敏后的用户实体对象
	 */
	LoginUserVO getLoginUserVO(User user);
	
	/**
	 * 用户登录方法
	 * @param userAccount 用户账号
	 * @param userPassword 用户密码
	 * @param request HTTP请求对象，用于获取请求相关信息
	 * @return LoginUserVO 登录成功后返回的脱敏用户视图对象
	 */
	LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
	
	/**
	 * 获取当前登录用户信息
	 * @param request HTTP请求对象，用于获取当前登录用户的会话信息
	 * @return 返回当前登录用户的实体对象
	 */
	User getLoginUser(HttpServletRequest request);
	
	/**
	 * 用户登出功能方法
	 * @param request HttpServletRequest对象，包含客户端请求信息
	 * @return 返回布尔值，表示用户登出操作是否成功
	 */
	boolean userLogout(HttpServletRequest request);
	
	/**
	 * 根据User对象获取UserVO对象
	 * UserVO通常用于前端展示，是对User对象的数据封装和转换
	 *
	 * @param user 用户实体对象，包含完整的用户信息
	 * @return UserVO 用户视图对象，通常只包含前端需要展示的字段
	 */
	UserVO getUserVO(User user);
	
	/**
	 * 根据用户列表获取用户视图对象列表
	 *
	 * @param userList 用户实体对象列表
	 * @return 用户视图对象列表，包含前端展示所需的数据
	 */
	List<UserVO> getUserVOList(List<User> userList);
	
	/**
	 * 根据用户查询请求参数构建查询条件包装器
	 *
	 * @param userQueryRequest 用户查询请求对象，包含查询条件参数
	 * @return QueryWrapper 返回一个包含查询条件的MyBatis-Plus查询包装器对象
	 */
	QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);
}
