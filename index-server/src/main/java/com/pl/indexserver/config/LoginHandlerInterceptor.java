package com.pl.indexserver.config;

import com.alibaba.fastjson.JSON;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.untils.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

//拦截器前处理。
@Component
public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisClient redisClient;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String header = httpServletRequest.getHeader("token");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "token");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");
        String session ;
        if ( null == header ){
            header = httpServletRequest.getParameter("token");
        }
        try {
            session = redisClient.get(header);

        } catch (Exception e) {
            session = null;
        }
        if (StringUtils.isEmpty(session)) {
            ReturnMsg returnMsg = new ReturnMsg();
            returnMsg.setCode(1);
            returnMsg.setContent("未登录");
            PrintWriter out = httpServletResponse.getWriter();
            out.append(JSON.toJSONString(returnMsg));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
