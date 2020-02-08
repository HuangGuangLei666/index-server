package com.pl.indexserver.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.AuthService;
import com.pl.indexserver.service.LoginService;
import com.pl.indexserver.service.UserService;
import com.pl.indexserver.untils.MD5;
import com.pl.indexserver.untils.RedisClient;
import com.pl.model.TmUser;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private static final int expire_time = 3600*24;

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private RedisClient redisClient;

    //登录验证
    @Override
    public ReturnMsg login(String username, String password) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            TmUser user = userService.getUser(username,password);
            if (!StringUtils.isEmpty(user)) {
                JSONObject jsonObject = new JSONObject();
                String local_key = "pl_tm:" + MD5.MD5_32bit( username + password + System.currentTimeMillis());
                returnMsg.setCode(0);
                jsonObject.put("companyName",userService.getCompanyNameCompany_id(user.getCompanyId()));
                jsonObject.put("companyId", user.getCompanyId());
                jsonObject.put("username", user.getUsername());
                returnMsg.setContent(jsonObject);
                user.setPassword("");
                //user.setCompanyName(userService.getCompanyNameCompany_id(user.getCompanyId()));
                //处理登录用户的权限
                String userAuth = authService.getUserAuth(user.getUserid());
                int isAdmin = user.getIsAdmin();
                if(isAdmin==1){
                	userAuth+="isAdmin";
                }
                String authkey = local_key + "security";
                redisClient.set(authkey,userAuth);
                redisClient.expire(authkey,expire_time);
                
                returnMsg.setToken(local_key);
                redisClient.set(local_key,JSON.toJSONString(user));
                redisClient.expire(local_key,expire_time);
                logger.info("新的登录账户：{}",username);
            } else {
                returnMsg.setCode(1);
                returnMsg.setErrorMsg("帐号或密码错误或者已经冻结，请重试！");
            }
        } catch (Exception e) {
            returnMsg.setCode(1);
            returnMsg.setErrorMsg("登录出错！");
            logger.warn("login error.",e);
        }
        return returnMsg;
    }

    @Override
    public ReturnMsg updateUser(String token, String new_password,String old_password) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            String ll_key = redisClient.get(token);
            if (StringUtils.isEmpty(ll_key)){
                returnMsg.setCode(1);
                returnMsg.setContent("登录过期，请重新登录！");
            }else {
                TmUser user1 =JSON.parseObject(ll_key,TmUser.class); //用token从redis获取user信息。
                TmUser user = userService.getUser(user1.getUsername(),old_password);
                if (!StringUtils.isEmpty(user)){
                    int ok = userService.updatePassword(user.getUserid(),new_password);
                    returnMsg.setCode(0);
                    returnMsg.setContent(ok);
                    redisClient.expire(ll_key,expire_time);
                }else {
                    returnMsg.setCode(1);
                    returnMsg.setContent("您输入的信息有误，请重新输入！");
                }
            }
        }catch (Exception e){
            returnMsg.setCode(1);
            returnMsg.setContent("您输入的信息有误，请重新输入！");
            returnMsg.setErrorMsg(e.getMessage());
        }
        return returnMsg;
    }

}
