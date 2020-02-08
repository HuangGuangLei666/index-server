package com.pl.indexserver.untils;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.service.TUserinfoService;
import com.pl.indexserver.service.WxService;
import com.pl.model.wx.TBook;
import com.pl.model.wx.TUserinfo;
import com.pl.model.wx.TemplateSendRequestDto;
import com.pl.model.wx.XBMSConstant;
import com.pl.thirdparty.dto.request.TemplateParamRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HuangGuangLei
 * @Date 2019/11/13
 */
@Component
public class QuartZXbmsUnRegister {

    @Autowired
    private TUserinfoService tUserinfoService;
    @Autowired
    private WxService wxService;

    private static final Logger logger = LoggerFactory.getLogger(QuartZXbmsUnRegister.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 定时器（定时给未完成注册的用户推送信息）
     */
    //每天晚上9点执行一次定时器
    @Scheduled(cron = "0 0 21 * * ?")
    private void runing() {
        logger.info("=========推送未完成注册信息=====时间={}==========",dateFormat.format(new Date()));
        List<TUserinfo> tUserinfoList = tUserinfoService.selectByPhoneIsNull();
        for (TUserinfo tUserinfo : tUserinfoList) {
            String openid = tUserinfo.getOpenid();
            String nickname = tUserinfo.getNickname();
            String remark = tUserinfo.getRemark();
            Date createTime = tUserinfo.getCreateTime();
            unRegisterPush(openid,nickname,createTime,remark);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public String unRegisterPush(String openid, String nickname, Date createTime, String remark) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        TemplateSendRequestDto templateSendRequestDto = new TemplateSendRequestDto();

        String title = "您好，您当前还未完成注册，可继续完成注册，让小兵秘书更好的为您服务。";
        TemplateParamRequest templateParamRequest = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest1 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest2 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest3 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest4 = new TemplateParamRequest();

        templateParamRequest.setValue(title);
        templateParamRequest1.setValue(nickname);
        templateParamRequest1.setColor("#1c4587");

        templateParamRequest2.setValue("需绑定您的手机号，才可正常使用服务。");
        templateParamRequest2.setColor("#1c4587");

        templateParamRequest3.setValue(formatter.format(createTime));
        templateParamRequest3.setColor("#1c4587");

        templateParamRequest4.setValue("请尽快完成设置，否则无法正常使用小兵秘书！点击查看详情完善设置！");
        templateParamRequest4.setColor("#1c4587");

        Map<String, TemplateParamRequest> data = new HashMap<>();
        data.put("first", templateParamRequest);
        data.put("keyword1", templateParamRequest1);
        data.put("keyword2", templateParamRequest2);
        data.put("keyword3", templateParamRequest3);
        data.put("remark", templateParamRequest4);

        String url = String.format("https://ai.yousayido.net/wxgzh/templates/login.html");
        templateSendRequestDto.setTouser(openid);
        templateSendRequestDto.setTemplate_id(XBMSConstant.XBMS_WX_UNREGISTERTEMPLATE_ID);
        templateSendRequestDto.setUrl(url);
        templateSendRequestDto.setData(data);

        String accessToken = wxService.getAccessToken();
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

        //控制不能重复推送
        if (!"99999".equals(remark)){
            String req = HttpUtil.postRequest(requestUrl, templateSendRequestDto.toJsonString());

            logger.info("========req=========" + req);
            logger.info("========templateSendReq=========" + templateSendRequestDto.toJsonString());
            logger.info("========requestUrl=========" + requestUrl);

            if ("ok".equals(JSONObject.parseObject(req).get("errmsg"))){
                //更新推送状态
                tUserinfoService.updateRegisterStatus(openid);
                return "未完成注册推送成功~~";
            }
            return "未完成注册推送失败~~";
        }
        return "不能重复推送~~";
    }

}
