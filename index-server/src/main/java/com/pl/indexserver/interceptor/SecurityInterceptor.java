package com.pl.indexserver.interceptor;

import com.alibaba.fastjson.JSON;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.untils.CommonConstant;
import com.pl.indexserver.untils.GetUid;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 权限验证拦截器
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

    private static Map<String, Integer> auth;

    @Autowired
    private RedisClient redisClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 获取当前url
        String url = request.getRequestURI();
        Integer authId = auth.get(url);
        // 判断当前url是否需要验证
        if (null == authId) {
            return true;
        }
        // 获取token数据
        String token = request.getHeader("token");
        if (null == token || "".equals(token)) {
            token = request.getParameter("token");
        }
        TmUser tmUser = null;
        tmUser = GetUid.getUID(request, redisClient);
        if (StringUtils.isEmpty(tmUser)) {
            logger.info(url + "接口收到无登录信息的请求！！！");
            updateResponse(response);
            return false;
        }
        String username = tmUser.getUsername();
        String userid = tmUser.getUserid();
        // 获取当前用户的权限数据
        String userAuth = null;
        try {// daef1daad6d8bfe5f98e65ad46d7444f
            String authkey = token + "security";
            userAuth = redisClient.get(authkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 若获取不到权限数据则说明该员工无相关权限
        if (null == userAuth) {
            updateResponse(response);
            return false;
        } else {
            try {
                String[] split = userAuth.replace("\"", "").split(",");
                for (String temp : split) {
                    if (temp.equals(authId.toString()) || temp.equals("isAdmin")) {
                        return true;
                    }
                }
                updateResponse(response);
                return false;
            } catch (Exception e) {
                logger.error("用户" + username + "(内部编号" + userid + ")访问接口:  " + url + "  -->出现异常:  ", e);
                updateResponse(response);
                return false;
            }
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {

    }

    public static void setAuth(Map<String, Integer> map) {
        if (auth == null) {
            auth = map;
        }
    }

    private void updateResponse(HttpServletResponse response) throws Exception {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(CommonConstant.RETURN_NOPERMISSION);
        returnMsg.setErrorMsg("此操作无权限!");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "token");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        PrintWriter out = response.getWriter();
        out.append(JSON.toJSONString(returnMsg));
    }
}
