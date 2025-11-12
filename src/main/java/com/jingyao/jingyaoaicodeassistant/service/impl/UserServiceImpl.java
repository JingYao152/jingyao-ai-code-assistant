package com.jingyao.jingyaoaicodeassistant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.model.enums.UserRoleEnum;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.mapper.UserMapper;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
	
}
