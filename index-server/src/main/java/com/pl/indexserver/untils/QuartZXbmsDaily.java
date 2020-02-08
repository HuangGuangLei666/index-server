package com.pl.indexserver.untils;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.service.TDialogService;
import com.pl.indexserver.service.TPhonetypeService;
import com.pl.indexserver.service.TUserinfoService;
import com.pl.indexserver.service.WxService;
import com.pl.model.TDialog;
import com.pl.model.wx.InterceptSta;
import com.pl.model.wx.TUserinfo;
import com.pl.model.wx.TemplateSendRequestDto;
import com.pl.model.wx.XBMSConstant;
import com.pl.thirdparty.dto.request.TemplateParamRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author HuangGuangLei
 * @Date 2019/11/13
 */
@Component
public class QuartZXbmsDaily {

    @Autowired
    private TUserinfoService tUserinfoService;
    @Autowired
    private TDialogService tDialogService;
    @Autowired
    private TPhonetypeService tPhonetypeService;
    @Autowired
    private WxService wxService;

    private static final Logger logger = LoggerFactory.getLogger(QuartZXbmsDaily.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 定时器（日报推送）
     */
    //每天晚上9点半执行定时器
    //"0 30 21 * * ?"
    @Scheduled(cron = "0 30 21 * * ?")
    private void runing() {
        logger.info("=========日报推送启动=====时间={}==========", dateFormat.format(new Date()));
        List<TUserinfo> tUserinfoList = tUserinfoService.selectByPhoneIsnotNull();
        for (TUserinfo tUserinfo : tUserinfoList) {
            String openid = tUserinfo.getOpenid();
            String phonenumber = tUserinfo.getPhonenumber();
            dailyPush(openid, phonenumber);
        }
        /*String openid = "opNfewsLHoAkgEj9dpx6ej2zIt0M";
        String phonenumber = "18750031121";
        dailyPush(openid, phonenumber);*/
    }


    /**
     * 日报推送
     * @param openid
     * @param phonenumber
     * @return
     */
    public String dailyPush(String openid, String phonenumber) {
//        DecimalFormat df = new DecimalFormat("0.00");
        //初始化通话时长
        int totalSeconds = 0;
        //查询今天的拦截数
        List<InterceptSta> interceptStas = tPhonetypeService.selectIntercepCount(phonenumber, format.format(new Date()));
        //查询全部用户今天的接听清单
        List<TDialog> tDialogList = tDialogService.selectByTaskIdOfToday(phonenumber, format.format(new Date()));
        for (TDialog tDialog : tDialogList) {
            totalSeconds += tDialog.getTotal_seconds();
        }
        TemplateSendRequestDto templateSendRequestDto = new TemplateSendRequestDto();

        String title = "您的小兵秘书日报来啦~~";
        TemplateParamRequest templateParamRequest = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest1 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest2 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest4 = new TemplateParamRequest();

        templateParamRequest.setValue(title);
        templateParamRequest1.setValue("小兵秘书日报");
        templateParamRequest1.setColor("#1c4587");

        templateParamRequest2.setValue(dateFormat.format(new Date()));
        templateParamRequest2.setColor("#1c4587");

        templateParamRequest4.setValue("日报内容：\n" +
                "1.今天为您接听了" + tDialogList.size() + "个来电，节省了" + totalSeconds/60 + "分钟\n" +
                "2.今天共拦截" + interceptStas.size() + "个黑名单来电\n" +
                "有小兵陪伴的每一天，你都不需要烦恼哦。");
        templateParamRequest4.setColor("#1c4587");

        Map<String, TemplateParamRequest> data = new HashMap<>();
        data.put("first", templateParamRequest);
        data.put("keyword1", templateParamRequest1);
        data.put("keyword2", templateParamRequest2);
        data.put("remark", templateParamRequest4);

//        String url = String.format("https://ai.yousayido.net/wxgzh/templates/login.html");
        templateSendRequestDto.setTouser(openid);
        templateSendRequestDto.setTemplate_id(XBMSConstant.XBMS_WX_DAILYTEMPLATE_ID);
//        templateSendRequestDto.setUrl(url);
        templateSendRequestDto.setData(data);

        String accessToken = wxService.getAccessToken();
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

        //控制不能重复推送
        String req = HttpUtil.postRequest(requestUrl, templateSendRequestDto.toJsonString());

        logger.info("========req=========" + req);
        logger.info("========templateSendReq=========" + templateSendRequestDto.toJsonString());
        logger.info("========requestUrl=========" + requestUrl);

        if ("ok".equals(JSONObject.parseObject(req).get("errmsg"))) {
            return "日报推送成功~~";
        }
        return "日报推送失败~~";
    }

    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("0.00");
        int s = 658;

        System.out.println(df.format((float)s/60));
    }

}
