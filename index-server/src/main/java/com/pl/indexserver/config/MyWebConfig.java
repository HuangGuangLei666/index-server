package com.pl.indexserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.pl.indexserver.interceptor.SecurityInterceptor;

@Configuration
public class MyWebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private LoginHandlerInterceptor loginHandlerInterceptor;
    @Autowired
    private SecurityInterceptor securityInterceptor;
    /*@Autowired
    private MemberHandlerInterceptor memberHandlerInterceptor;*/

    //Cors跨域处理
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(false).maxAge(3600);
    }

    //自定义拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/busiManagement/user/login")
                .excludePathPatterns("/busiManagement/wxgzh/*")
                .excludePathPatterns("/busiManagement/xbms/*")
                .excludePathPatterns("/busiManagement/algorithm/syn/*")
                .excludePathPatterns("/busiManagement/callTask/task/downloadTemplate")
                .excludePathPatterns("/busiManagement/callTask/dialog/downRecordFile")
                .excludePathPatterns("/busiManagement/callTask/task/exportDetail")
                .excludePathPatterns("/busiManagement/callTask/task/exportReport")
                .excludePathPatterns("/busiManagement/statistics/intentionCustomer/export")
                .excludePathPatterns("/busiManagement/user/bindUserToOpenid")
                .excludePathPatterns("/busiManagement/user/getToken")
                .excludePathPatterns("/wx/busiManagement/callTask/dialog/queryDetail")
                .excludePathPatterns("/busiManagement/addphones")
                .excludePathPatterns("/busiManagement/qryCallResult")
                .excludePathPatterns("/busiManagement/node")
                .excludePathPatterns("/busiManagement/changeBusinessCallIn")
                .excludePathPatterns("/busiManagement/callInAgentList")
                .excludePathPatterns("/busiManagement/flowConfig/robot")
                .excludePathPatterns("/busiManagement/callIn/queryCallInRecordDetail")
                .excludePathPatterns("/busiManagement/manual/*");
        registry.addInterceptor(securityInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/error", "/busiManagement/user/login","/busiManagement/wxgzh/*", "/busiManagement/addphones", "/busiManagement/manual/*");
        /*registry.addInterceptor(memberHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error")
                .excludePathPatterns();*/

    }
}
