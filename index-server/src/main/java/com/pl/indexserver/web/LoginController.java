package com.pl.indexserver.web;


import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.LoginService;
import com.pl.indexserver.service.UserService;
import com.pl.indexserver.untils.RestClientUtil;
import com.pl.model.TmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

@RestController
@RequestMapping("/busiManagement/user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Value("${weixin.AppId}")
    private String AppID;

    @Value("${weixin.AppSecret}")
    private String AppSecret;

    @Value("${weixin.tokenUrl}")
    private String tokenUrl;

    @Value("${weixin.bindUrl}")
    private String jumpPage;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * @api {POST} /busiManagement/user/login  登录接口
     * @apiName loginUser
     * @apiGroup LoginController
     * @apiParam {String} username
     * @apiParam {String} password
     * @apiSuccess {String} code 0:发送成功 -1:发送失败
     * @apiSuccess {String} content 0成功 -1失败
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "companyName": "测试_公司名称",
     * "username": "luo"
     * },
     * "token": "pl_tm:70a01bfa7f2e16ecb3be2c78e5518d82",
     * "errorMsg": null
     * }
     * @apiVersion 0.0.0
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ReturnMsg loginUser(@RequestParam("username") String username,
                               @RequestParam("password") String password) {

        return loginService.login(username, password);

    }

    /**
     * @api {POST} /busiManagement/user/updatePassword  修改密码
     * @apiName updatePassword
     * @apiGroup LoginController
     * @apiParam {String} oldPassword    旧密码
     * @apiParam {String} newPassword    新密码
     * @apiSuccess {String} code 0:发送成功 -1:发送失败
     * @apiSuccess {String} content 0成功 -1失败
     * @apiSuccess {String} errorMsg 返回错误说明
     * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": "0",
     * "content":"1",
     * "errorMsg": null
     * }
     * @apiVersion 0.0.0
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public ReturnMsg updatePassword(@RequestParam("new_password") String new_password,
                                    @RequestParam("old_password") String old_password, HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        ReturnMsg returnMsg = new ReturnMsg();
        if (StringUtils.isEmpty(header)) {
            returnMsg.setCode(1);
            returnMsg.setContent("Authorization is null!");
        } else {
            returnMsg = loginService.updateUser(header, new_password, old_password);
        }
        return returnMsg;
    }

    /**
     * @api {POST} /busiManagement/user/getToken  修改密码
     * @apiName getToken
     * @apiGroup LoginController
     * @apiParam {String} code    微信返回的code
     * @apiVersion 0.0.0
     */
    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    public void getAccessToken(String code, HttpServletResponse response) {
        logger.info("code：" + code);
        //--------------------------调用微信后台服务器----------------------------
        String url = tokenUrl;
        String parm = String.format("appid=%s&secret=%s&code=%s&grant_type=authorization_code", AppID, AppSecret, code);
        //String parm = "appid=" + AppID + "&secret=" + AppSecret + "&code=" + code + "&grant_type=authorization_code";
        try {
            String putData = RestClientUtil.postData(url, parm);
            logger.info("putData：" + putData);
            if (putData.contains("errcode")) {
                logger.info(putData);
            } else {
                JSONObject object = JSONObject.parseObject(putData);
                //获取所需要的信息
                String accessToken = object.getString("access_token");
                String openID = object.getString("openid");
                String refreshToken = object.getString("refresh_token");
                long expires_in = object.getLong("expires_in");   //获取腾讯过期时间
                // 判断当前的openid是否有绑定的账号，有就直接登录，没有就跳转到绑定页面
                TmUser user = userService.selectByOpenid(openID);
                if (null != user && !StringUtils.isEmpty(user.getWeixinOpenid())) { // 跳转到直接登录页面
                    ReturnMsg msgt = loginService.login(user.getUsername(), user.getPassword());
                    if (msgt.getCode() == 0) {
                        response.sendRedirect(jumpPage+"?token=" + msgt.getToken()
                                +"&content="+ URLEncoder.encode(msgt.getContent().toString(), "utf-8"));
                    }
                } else { // 跳转到绑定后登录页面
                    response.sendRedirect(jumpPage+"?openid=" + openID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @api {POST} /busiManagement/user/bindUserToOpenid  账号与微信opendid绑定
     * @apiName bindUserToOpenid
     * @apiGroup LoginController
     * @apiParam {String} username    用户名
     * @apiParam {String} password    密码
     * @apiParam {String} weixinOpenid    微信用户的openid
     * * @apiSuccessExample {json}  成功返回示例
     * {
     * "code": 0,
     * "content": {
     * "companyName": "测试_公司名称",
     * "username": "luo"
     * },
     * "token": "pl_tm:70a01bfa7f2e16ecb3be2c78e5518d82",
     * "errorMsg": null
     * }
     * @apiVersion 0.0.0
     */
    @RequestMapping(value = "/bindUserToOpenid", method = RequestMethod.POST)
    public ReturnMsg bindUserToOpenid(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      @RequestParam("weixinOpenid") String openid) {
        ReturnMsg msg = new ReturnMsg();
        msg.setCode(0);
        if (StringUtils.isEmpty(openid)) {
            // 没有把opendid传过来
            msg.setCode(2);
            msg.setErrorMsg("绑定登录失败，请重新扫描微信登录");
        }
        ReturnMsg msgt = loginService.login(username, password);
        if (msgt.getCode() == 0) {// 账号密码正确
            // 把openid设置到账号上
            userService.updateOpenidByNameAndPassword(openid, username, password);
            msg = msgt;
        } else {
            msg.setCode(1);
            msg.setErrorMsg("帐号或密码错误或者已经冻结，请重试！");
            msg = msgt;
        }
        return msg;
    }
}
