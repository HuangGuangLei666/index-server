/*package com.pl.indexserver.config;

import com.alibaba.fastjson.JSON;
import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TUserinfoService;
import com.pl.model.wx.TUserinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import sun.management.counter.perf.PerfInstrumentation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

*//**
 * @author HuangGuangLei
 * @Date 2019/11/14
 *//*
@Component
public class MemberHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private TUserinfoService tUserinfoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String openid = request.getParameter("openid");
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        if (StringUtils.isEmpty(tUserinfo.getIsMembership())){
            CheckSmsCodeResp resp = new CheckSmsCodeResp();
            resp.setRetCode(1);
            resp.setRetDesc("不好意思，您还不是会员不能浏览更多");
            PrintWriter out = response.getWriter();
            out.append(JSON.toJSONString(resp));
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}*/
