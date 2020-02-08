package com.pl.indexserver;

import com.pl.indexserver.interceptor.SecurityInterceptor;
import com.pl.indexserver.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author liuqiongyu
 * @create 2018-05-14
 * @description 加载权限数据
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //处理系统权限相关
        AuthService authService = contextRefreshedEvent.getApplicationContext().getBean(AuthService.class);
        Map<String, Integer> authMap;
		try {
			authMap = authService.selectIsValidate();
			SecurityInterceptor.setAuth(authMap);
            logger.info("加载权限,权限接口数量：{}", authMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}


    }
}