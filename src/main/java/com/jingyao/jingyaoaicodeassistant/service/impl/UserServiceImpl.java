package com.jingyao.jingyaoaicodeassistant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.jingyao.jingyaoaicodeassistant.model.entity.User;
import com.jingyao.jingyaoaicodeassistant.mapper.UserMapper;
import com.jingyao.jingyaoaicodeassistant.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
