package com.jingyao.jingyaoaicodeassistant.aop;

import com.jingyao.jingyaoaicodeassistant.annotation.AuthCheck;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.model.enums.UserRoleEnum;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {
	
	@Resource
	private UserService userService;
	
	/**
	 * 执行拦截
	 *
	 * @param joinPoint 切入点
	 * @param authCheck 权限校验注解
	 */
	@Around("@annotation(authCheck)")
	public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
		// 获取注解中的必须角色
		String mustRole = authCheck.mustRole();
		// 获取当前请求的属性
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		// 从请求属性中获取HttpServletRequest对象
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		// 当前登录用户
		User loginUser = userService.getLoginUser(request);
		UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
		// 不需要权限，放行
		if (mustRoleEnum == null) {
			return joinPoint.proceed();
		}
		// 以下为：必须有该权限才通过
		// 获取当前用户具有的权限
		UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
		// 没有权限，拒绝
		if (userRoleEnum == null) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 要求必须有管理员权限，但用户没有管理员权限，拒绝
		if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 通过权限校验，放行
		return joinPoint.proceed();
	}
}
