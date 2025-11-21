package com.jingyao.jingyaoaicodeassistant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.jingyao.jingyaoaicodeassistant.model.entity.App;
import com.jingyao.jingyaoaicodeassistant.mapper.AppMapper;
import com.jingyao.jingyaoaicodeassistant.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/jingyao152">JINGYAO</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

}
