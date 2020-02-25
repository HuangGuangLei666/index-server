 package com.pl.indexserver.web;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.model.SendSmsResp;
import com.pl.indexserver.service.*;
import com.pl.indexserver.untils.*;
import com.pl.indexserver.untils.jiguang.SendSMSResult;
import com.pl.indexserver.untils.jiguang.ValidSMSResult;
import com.pl.indexserver.untils.jiguang.common.SMSClient;
import com.pl.indexserver.untils.jiguang.common.model.SMSPayload;
import com.pl.indexserver.untils.wechat.WXCore;
import com.pl.model.*;
import com.pl.model.messagetype.Image;
import com.pl.model.messagetype.ImageMessage;
import com.pl.model.messagetype.TextMessage;
import com.pl.model.wx.*;
import com.pl.thirdparty.api.VoiceGenderService;
import com.pl.thirdparty.api.VoiceService;
import com.pl.thirdparty.dto.Result;
import com.pl.thirdparty.dto.enums.GenderType;
import com.pl.thirdparty.dto.request.TemplateParamRequest;
import com.pl.thirdparty.enums.TtsVoiceNameEnum;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * @author HGL
 * @Date 2018/12/28
 */
@RestController
@RequestMapping(value = "/busiManagement/wxgzh")
public class WxController {

    private static final Logger logger = LoggerFactory.getLogger(WxController.class);
    private static int seq = (int) (System.currentTimeMillis() % 100000000);
    /*private static int payCount = 0;*/

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private WxService wxService;
    @Autowired
    private TBookService tBookService;
    @Autowired
    private TGoupService tGoupService;
    @Autowired
    private TContentService tContentService;
    @Autowired
    private TVoiceService tVoiceService;
    @Autowired
    private TUserVoiceService tUserVoiceService;
    @Autowired
    private TUserinfoService tUserinfoService;
    @Autowired
    private TSetService tSetService;
    @Autowired
    private TLabelService tLabelService;
    @Autowired //对话详情
    private TDialogService tDialogService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private TMealService tMealService;
    @Autowired //对话详情
    private TDialogDetailExtProxy tDialogDetailExtProxy;
    @Autowired
    private TMallService tMallService;
    @Autowired
    private TMechanismService tMechanismService;
    @Autowired
    private TOrderService tOrderService;
    @Autowired
    private TQctivationcodeService tQctivationcodeService;
    @Autowired
    private TSuggestionService tSuggestionService;
    @Autowired
    private TUserTagService tUserTagService;
    @Autowired
    private TGroupTagService tGroupTagService;
    @Autowired
    private KnowledgeQuestionService knowledgeQuestionService;
    @Autowired
    private KnowledgeAnswerService knowledgeAnswerService;
    @Autowired
    private TCustomerrespService tCustomerrespService;
    @Autowired
    private TRelationService tRelationService;
    @Autowired
    private TUserQaService tUserQaService;
    @Autowired
    private TPhonetypeService tPhonetypeService;
    @Autowired
    private TKeyValueService tKeyValueService;
    @Reference(version = "${thirdparty.service.version}",
            application = "${dubbo.application.id}", timeout = 180000)
    private VoiceService voiceService;

    @Reference(version = "${thirdparty.service.version}",
            application = "${dubbo.application.id}")
    private VoiceGenderService voiceGenderService;

    //极光验证短信
    @Value("${jiguang.appkey}")
    private String appKey;
    @Value("${jiguang.mastersecret}")
    private String masterSecret;
    //极光通知短信
//    @Value("${jiguangNotice.appkey}")
//    private String appkey;
//    @Value("${jiguangNotice.mastersecret}")
//    private String mastersecret;

    @Value("${xiaobingsecretary.AppId}")
    private String appId;
    @Value("${xiaobingsecretary.AppSecret}")
    private String appSecret;

    /*@Value("${xbms.orgimage}")
    private String imageUrl;*/
    //ftp
    @Value("${ftp.address}")
    private String address;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.name}")
    private String ftpName;
    @Value("${ftp.password}")
    private String password;


    /**
     * 验证消息的确来自微信服务器
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @RequestMapping(value = "/receiveMsg", method = RequestMethod.GET)
    @ResponseBody
    public String token(String signature, String timestamp, String nonce,
                        String echostr) {
        logger.info("==========signature=" + signature);

        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            logger.info("======token====OK=====" + echostr);
            return echostr;

        }
        logger.info("========token==no OK=====");

        return "";
    }


    /**
     * 关注、取消公众号(事件)
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/receiveMsg", method = RequestMethod.POST)
//    @ResponseBody
//    @RequestBody String req,
    public String receiveMsg(HttpServletRequest request, HttpServletResponse response) {

        logger.info("==========req=" + request);
        String message = "success";
        String respMessage = null;
        String imgMessage = null;
        ServletOutputStream out = null;
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            // 把微信返回的xml信息转义成map
            /*Map<String, String> map = xmlToMap(req);*/
            Map<String, String> map = MessageUtil.xmlToMap(request);

            logger.info("微信接收到的消息为:" + map.toString());
            String fromUserName = map.get("FromUserName");// 消息来源用户标识
            String toUserName = map.get("ToUserName");// 消息目的用户标识
            String msgType = map.get("MsgType");// 消息类型(event或者text)
            logger.info("消息来源于:" + fromUserName);
            logger.info("openId:" + toUserName);
            logger.info("消息类型为:" + msgType);
            String eventType = map.get("Event");// 事件类型
            String nickName = getUserNickName(fromUserName);

            if ("event".equals(msgType)) {// 如果为事件类型
                if ("subscribe".equals(eventType)) {// 处理订阅事件
                    String content = "欢迎关注,这是一个公众号测试账号,您可以回复任意消息测试,开发者郭先生,18629374628";
                    String msg = "@" + nickName + "," + content;
                    logger.info("事件类型为:" + "," + eventType);
                    logger.info("==========subscribe=" + map);
                    wxService.subscribe(map);

                    // 订阅
                    //文本消息
                    TextMessage text = new TextMessage();
                    text.setContent("你好！我是你的电话秘书“小兵闺秘”、只要你现在拥有我。\n" +
                            "\n" +
                            "防骚扰：从此你将不再担心被电话骚扰，我还还可以发起反骚扰！\n" +
                            "\n" +
                            "防诈骗：给你分析来电人的意图，分辨屏蔽诈骗嫌疑电话！\n" +
                            "\n" +
                            "防漏接：开会，休息，坐飞机，手机没电，信号不好，再也不用担心漏接电话！不丢失重要的来电信息、保留你感兴趣的来电！\n" +
                            "\n" +
                            "不想接：我将帮您应付不想接的电话，催收，借钱，统统我搞定！\n" +
                            "\n" +
                            "不能接：当你睡觉、上课上班、上飞机、运动、不方便任何场景，为你智能接听应答。\n" +
                            "\n" +
                            "不得不接：对送餐、快递、约会、领导亲人等重要电话和必须及时接听的电话及时给你处理好！");
                    text.setToUserName(fromUserName);
                    text.setFromUserName(toUserName);
                    text.setCreateTime(new Date().getTime());
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    logger.info("%%%%%%%%%%%text={}%%%%%%%%%%%%", text);

                    respMessage = MessageUtil.textMessageToXml(text);
                    logger.info("%%%%%%%%respMessage={}%%%%%%%%", respMessage);

                    //图片
                    String mediaId = wxService.getMediaId();
                    logger.info("==========mediaId={}=============", mediaId);
                    Image image = new Image();
                    ImageMessage imageMessage = new ImageMessage();
                    image.setMediaId(mediaId);
                    imageMessage.setFromUserName(toUserName);
                    imageMessage.setToUserName(fromUserName);
                    imageMessage.setCreateTime(new Date().getTime());
                    imageMessage.setImage(image);
                    imageMessage.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_IMAGE);
                    imgMessage = MessageUtil.imageMessageToXml(imageMessage);
                    logger.info("%%%%%%%%imgMessage={}%%%%%%%%", imgMessage);

                    try {
                        out = response.getOutputStream();
                        out.print(respMessage);
                        out.print(respMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage(), e);
                    } finally {
                        out.close();
                        out = null;
                    }

                } else if ("unsubscribe".equals(eventType)) {// 处理取消订阅事件
                    logger.info("事件类型为:" + eventType);
                    logger.info("==========un subscribe=" + map);
                    wxService.unsubscribe(map);

                }
            } else {
                // 微信消息分为事件推送消息和普通消息,非事件即为普通消息类型
                switch (msgType) {
                    case "text": {// 文本消息
                        String content = map.get("Content");// 用户发的消息内容
                        content = "您发的消息内容是:" + content + ",如需帮助,请联系郭先生,18629374628";
                        break;
                    }
                    case "image": {// 图片消息
                        String content = "您发的消息内容是图片,如需帮助,请联系郭先生,18629374628";
                        break;
                    }
                    default: {// 其他类型的普通消息
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("==========wxController receiveMsg =", e);
        }
        logger.info("关注微信公众号自动回复的消息内容为:" + message);
        return message;
    }

    public static String getUserNickName(String openId) {
        // String nickName = null;
        // try {
        // Map map = WeiXinUtil.cacheTokenAndTicket();
        // String token = (String)map.get("access_token");
        // String url = URL.replace("OPENID", openId).replace("ACCESS_TOKEN",
        // token);
        // JSONObject obj = HttpUtil.HttpGet(url);
        // nickName = (String)obj.get("nickname");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        return openId;
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param xml XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String xml) {
        try {
            Map<String, String> data = new HashMap<>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            stream.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 发送验证码短信
     *
     * @param phoneNumber
     * @return {
     * "retCode": 0,
     * "retDesc": "验证码发送成功",
     * "rateLimitQuota": 0,
     * "rateLimitRemaining": 0,
     * "rateLimitReset": 0,
     * "responseCode": 200,
     * "originalContent": "{\"msg_id\":\"848267378766\"}",
     * "resultOK": true
     * }
     */
    @RequestMapping(value = "/sendSms.do")
    @ResponseBody
    public SendSmsResp sendSMSCode(String phoneNumber) {
        SMSClient client = new SMSClient(masterSecret, appKey);

        SendSmsResp resp = new SendSmsResp();
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber(phoneNumber)
                .setTempId(1)
                .build();
        try {
            SendSMSResult smsResult = client.sendSMSCode(payload);
            if (StringUtils.isEmpty(smsResult)) {
                resp.setRetCode(1);
                resp.setRetDesc("验证码发送失败");
                return resp;
            }

            String originalContent = smsResult.getOriginalContent();
            JSONObject jb = JSONObject.parseObject(originalContent);
            String msgId = jb.getString("msg_id");

            logger.info("=========msgId========" + smsResult.toString());
            resp.setRetCode(0);
            resp.setRetDesc("验证码发送成功");
            resp.setSmsId(msgId);
            return resp;
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Message: " + e.getMessage());
        }
        resp.setRetCode(1);
        resp.setRetDesc("验证码发送失败");
        return resp;
    }


    /**
     * 小兵秘书发送短信测试
     * @param phoneNumber
     * @return
     */
    @RequestMapping(value = "/sendNoticeMsg.do")
    @ResponseBody
    public SendSmsResp sendNoticeMsg(String phoneNumber) {
        SMSClient client = new SMSClient("6d6d6df20cd4544a78f14d6b", "64993d8920c81dc9f109a086");

        SendSmsResp resp = new SendSmsResp();
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber(phoneNumber)
//              .setPhonenumber("123123")
                .setTempId(176562)
                .build();
        try {
            SendSMSResult smsResult = client.sendTemplateSMS(payload);
            if (StringUtils.isEmpty(smsResult)) {
                resp.setRetCode(1);
                resp.setRetDesc("小兵秘书短信发送失败");
                return resp;
            }

            String originalContent = smsResult.getOriginalContent();
            JSONObject jb = JSONObject.parseObject(originalContent);
            String msgId = jb.getString("msg_id");

            logger.info("=========msgId========" + smsResult.toString());
            resp.setRetCode(0);
            resp.setRetDesc("小兵秘书短信发送成功");
            resp.setSmsId(msgId);
            return resp;
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Message: " + e.getMessage());
        }
        resp.setRetCode(1);
        resp.setRetDesc("小兵秘书短信发送失败");
        return resp;
    }


    /**
     * 发送验证码短信（app登录）
     *
     * @param phoneNumber
     * @return {
     * "retCode": 0,
     * "retDesc": "验证码发送成功",
     * "rateLimitQuota": 0,
     * "rateLimitRemaining": 0,
     * "rateLimitReset": 0,
     * "responseCode": 200,
     * "originalContent": "{\"msg_id\":\"848267378766\"}",
     * "resultOK": true
     * }
     */
    @RequestMapping(value = "/loginSendsms.do")
    @ResponseBody
    public SendSmsResp loginSendsms(String phoneNumber) {
        SMSClient client = new SMSClient(masterSecret, appKey);

        SendSmsResp resp = new SendSmsResp();
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber(phoneNumber)
                .setTempId(1)
                .build();
//        TUserinfo userinfo = wxService.selectUserByPhoneNumber(phoneNumber);
        try {
//            if (!StringUtils.isEmpty(userinfo)) {
//                resp.setRetCode(2);
//                resp.setRetDesc("该用户已经注册过了");
//                return resp;
//            }
            SendSMSResult smsResult = client.sendSMSCode(payload);
            if (StringUtils.isEmpty(smsResult)) {
                resp.setRetCode(1);
                resp.setRetDesc("验证码发送失败");
                return resp;
            }

            String originalContent = smsResult.getOriginalContent();
            JSONObject jb = JSONObject.parseObject(originalContent);
            String msgId = jb.getString("msg_id");

            logger.info("=========msgId========" + smsResult.toString());
            resp.setRetCode(0);
            resp.setRetDesc("验证码发送成功");
            resp.setSmsId(msgId);
            return resp;
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Message: " + e.getMessage());
        }
        resp.setRetCode(1);
        resp.setRetDesc("验证码发送失败");
        return resp;
    }


    /**
     * app登录
     *
     * @param verifyCode
     * @param msgId
     * @return
     */
    @RequestMapping(value = "/login.do")
    @ResponseBody
    public CheckSmsCodeResp login(String verifyCode, String msgId) {

        CheckSmsCodeResp checkSmsCode = new CheckSmsCodeResp();
        try {
            SMSClient client = new SMSClient(masterSecret, appKey);
            ValidSMSResult res = client.sendValidSMSCode(msgId, verifyCode);
            if (StringUtils.isEmpty(res)) {
                checkSmsCode.setRetCode(1);
                checkSmsCode.setRetDesc("你输入的验证码有误");
                return checkSmsCode;
            }

            logger.info("==========res.getIsValid()===========" + res.toString());
            checkSmsCode.setRetCode(0);
            checkSmsCode.setRetDesc("验证码校验成功");
            return checkSmsCode;
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Message: " + e.getMessage());
        }

        checkSmsCode.setRetCode(1);
        checkSmsCode.setRetDesc("你输入的验证码有误");
        return checkSmsCode;
    }

    /**
     * 验证码校验接口
     *
     * @param verifyCode
     * @param msgId
     * @param openid
     * @param phoneNumber
     * @return {
     * "retCode": 0,
     * "retDesc": "验证码校验成功"
     * }
     */
    @RequestMapping(value = "/checkSmsCode.do")
    @ResponseBody
    public CheckSmsCodeResp checkSmsCode(String verifyCode,
                                         String msgId,
                                         String openid,
                                         String phoneNumber,
          /**/                           String recommenderId) {
        Integer recommenderid = 0;
        String sonId = "";
        String sonIds = "";

        if (!StringUtils.isEmpty(recommenderId)) {
            //26
            int userId = Integer.parseInt(recommenderId);
            TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
            //-0-21
            sonId = tUserinfo.getSonId();
            if (sonId == null) {
                sonIds = "-0-" + userId;
            } else {
                sonIds = sonId + "-" + userId;
            }
        }

        if (!StringUtils.isEmpty(recommenderId)) {
            /**/
            recommenderid = Integer.parseInt(recommenderId);
        }
        logger.info("=========openid========" + openid);

        CheckSmsCodeResp checkSmsCode = new CheckSmsCodeResp();
        TUserinfo userinfo = wxService.selectUserByPhoneNumber(phoneNumber);

        TUserinfo userinfoOp = wxService.selectUserByOpenId(openid);
        logger.info("==========userinfoOp===========" + userinfoOp.toString());
        if (!StringUtils.isEmpty(userinfo)) {
            checkSmsCode.setRetCode(2);
            checkSmsCode.setRetDesc("该用户已经注册了");
            return checkSmsCode;
        }
        try {
            SMSClient client = new SMSClient(masterSecret, appKey);
            ValidSMSResult res = client.sendValidSMSCode(msgId, verifyCode);
            if (StringUtils.isEmpty(res)) {
                checkSmsCode.setRetCode(1);
                checkSmsCode.setRetDesc("你输入的验证码有误");
                return checkSmsCode;
            }

            //验证成功后把号码更新到数据库表中
            //TODO
            /**/
            int i = wxService.updateByPrimaryKey(userinfoOp.getId(), phoneNumber, recommenderid, sonIds);
            logger.info("==========res.getIsValid()===========" + res.toString());
            checkSmsCode.setRetCode(0);
            checkSmsCode.setRetDesc("验证码校验成功");
            return checkSmsCode;
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Message: " + e.getMessage());
        }

        checkSmsCode.setRetCode(1);
        checkSmsCode.setRetDesc("你输入的验证码有误");
        return checkSmsCode;
    }


    /**
     * app注册接口
     *
     * @param verifyCode
     * @param msgId
     * @param phoneNumber
     * @return {
     * "retCode": 0,
     * "retDesc": "验证码校验成功"
     * }
     */
    @RequestMapping(value = "/register.do")
    @ResponseBody
    public CheckSmsCodeResp register(String verifyCode,
                                     String msgId,
                                     String phoneNumber,
          /**/                       String recommenderId) {
        Integer recommenderid = 0;
        String sonId = "";
        String sonIds = "";

        if (!StringUtils.isEmpty(recommenderId)) {
            //26
            int userId = Integer.parseInt(recommenderId);
            TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
            //-0-21
            sonId = tUserinfo.getSonId();
            if (sonId == null) {
                sonIds = "-0-" + userId;
            } else {
                sonIds = sonId + "-" + userId;
            }
        }

        if (!StringUtils.isEmpty(recommenderId)) {
            recommenderid = Integer.parseInt(recommenderId);
        }

        CheckSmsCodeResp checkSmsCode = new CheckSmsCodeResp();
        TUserinfo userinfo = wxService.selectUserByPhoneNumber(phoneNumber);

        if (!StringUtils.isEmpty(userinfo)) {
            checkSmsCode.setRetCode(0);
            checkSmsCode.setRetDesc("恭喜你，注册成功！");
            return checkSmsCode;
        }
        try {
            SMSClient client = new SMSClient(masterSecret, appKey);
            ValidSMSResult res = client.sendValidSMSCode(msgId, verifyCode);
            if (StringUtils.isEmpty(res)) {
                checkSmsCode.setRetCode(1);
                checkSmsCode.setRetDesc("你输入的验证码有误");
                return checkSmsCode;
            }

            //转化为app注册
            //验证成功后把号码更新到数据库表中
            //将注册的用户信息插入到db中
            //生成用户的openid（Android开头，后面再加个21位的随机码）
            String openid = RandomUtils.generateOpenid();
            TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
            TUserinfo user = new TUserinfo();
            if (StringUtils.isEmpty(tUserinfo)) {
                user.setOpenid(openid);
            }
            user.setPhonenumber(phoneNumber);
            user.setRecommenderId(recommenderid);
            user.setSonId(sonIds);
            tUserinfoService.addUser(user);

            logger.info("==========res.getIsValid()===========" + res.toString());
            checkSmsCode.setRetCode(0);
            checkSmsCode.setRetDesc("验证码校验成功");
            return checkSmsCode;
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Message: " + e.getMessage());
        }

        checkSmsCode.setRetCode(1);
        checkSmsCode.setRetDesc("你输入的验证码有误");
        return checkSmsCode;
    }


    /**
     * 通过code获得openid
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/wxUserDetail")
    @ResponseBody
    public TUserinfo wxUserDetail(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + XBMSConstant.XBMS_WX_APPID +
                "&secret=" + XBMSConstant.XBMS_WX_SECRET +
                "&code=" + code +
                "&grant_type=authorization_code";
        String postRequest = HttpUtil.getHttps(url);
        logger.info("=========postRequest=========" + postRequest);

        JSONObject object = JSONObject.parseObject(postRequest);
        String openid = object.getString("openid");
        return wxService.selectUserByOpenId(openid);
    }


    /**
     * 根据号码获取用户基本信息
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/getUserInfo.do")
    @ResponseBody
    public TUserinfo getUserInfo(String phone) {
        TUserinfo tUserinfo = tUserinfoService.selectByPhone(phone);
        if (StringUtils.isEmpty(tUserinfo)) {
            return new TUserinfo();
        }
        return tUserinfo;
    }


    @RequestMapping(value = "/wxUserDetailByOpenid")
    @ResponseBody
    public TUserinfo getUserinfo(String openid) {
        return wxService.selectUserByOpenId(openid);
    }


    /**
     * 添加好友
     *
     * @param openId
     * @param phone
     * @param friendName
     * @return
     */
    @RequestMapping(value = "/friendAdd.do")
    @ResponseBody
    public CheckSmsCodeResp friendAdd(String openId, String phone, String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        String regex = "0?(13|14|15|16|17|18|19)[0-9]{9}";
        if (!phone.matches(regex)) {
            resp.setRetCode(2);
            resp.setRetDesc("您输入的号码格式有误");
            return resp;
        }

        TBook book = new TBook();
        book.setOpenId(openId);
        book.setPhone(phone);
        book.setFriendName(friendName);
        int i = tBookService.insert(book);

        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("添加好友通讯录失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("添加好友通讯录成功");
        return resp;
    }


    /**
     * 删除好友
     *
     * @param openId
     * @param phone
     * @return
     */
    @RequestMapping(value = "/friendDel.do")
    @ResponseBody
    public CheckSmsCodeResp friendDel(String openId, String phone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tBookService.delFriendByOpenIdAndPhone(openId, phone);

        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除好友通讯录失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除好友通讯录成功");
        return resp;
    }


    /**
     * 修改好友
     *
     * @param openId
     * @param phone
     * @param friendName
     * @return
     */
    @RequestMapping(value = "/friendUpd.do")
    @ResponseBody
    public CheckSmsCodeResp friendUpd(@RequestParam(value = "openId") String openId, String phone,
                                      @RequestParam(value = "friendName", required = false) String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        logger.info("/friendUpd.do openId={},phone={},friendName={}", openId, phone, friendName);

        if (friendName == null || "".equals(friendName)) {
            friendName = phone;
            logger.info("friendName is null or blank, set value=" + phone);
        }
        String regex = "0?(13|14|15|16|17|18|19)[0-9]{9}";
        if (!phone.matches(regex)) {
            resp.setRetCode(2);
            resp.setRetDesc("您输入的号码格式有误");
            return resp;
        }


        int i = tBookService.updateFriendBookByPhone(phone, openId, friendName);

        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("修改好友通讯录失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("修改好友通讯录成功");
        return resp;
    }


    /**
     * 查询好友详情
     *
     * @param openId
     * @param phone
     * @return
     */
    @RequestMapping(value = "/friendQry.do")
    @ResponseBody
    public QryFriendResp friendQry(String openId, @RequestParam(required = false) String phone) {
        QryFriendResp resp = new QryFriendResp();

        logger.info("friendQry.do openId={},phone={}", openId, phone);
        List<TBook> bookList = tBookService.qryFriendDetail(openId, phone);
        List<QryFriendRespDto> qryFriendRespDtos = new ArrayList<>();
        resp.setRetData(qryFriendRespDtos);

        if (CollectionUtils.isEmpty(bookList)) {
            resp.setRetCode(2);
            resp.setRetDesc("没有此好友记录");
            return resp;
        }

        for (TBook book : bookList) {
//            //每次都new一个对象
            QryFriendRespDto friendRespDto = new QryFriendRespDto();
//
//            TSet tSet = tSetService.selectBusinessVoiceByUserIdAndValue(tUserinfo.getId(), phone);
//            Integer voiceId = tSet.getVoiceId();
//            TVoice tVoice = tVoiceService.selectByVoiceId(voiceId);
//            Integer businessId = tVoice.getBusinessId();
//            TContent tContent = tContentService.selectContentByBusinessId(businessId);
//
//            if (StringUtils.isEmpty(tContent)) {
//                resp.setRetCode(1);
//                resp.setRetDesc("此智库不存在");
//                return resp;
//            }

            friendRespDto.setPhone(book.getPhone());
            friendRespDto.setFriendName(book.getFriendName());

            qryFriendRespDtos.add(friendRespDto);
        }

        resp.setRetCode(0);
        resp.setRetDesc("查询好友通讯录成功");

        return resp;
    }


    /**
     * 添加群组
     *
     * @param openId
     * @param groupName
     * @param groupMemberPhones
     * @return
     */
    @RequestMapping(value = "/groupAdd.do")
    @ResponseBody
    public CheckSmsCodeResp groupAdd(String openId, String groupName,
                                     @RequestParam(value = "groupMemberPhones", required = false) String groupMemberPhones) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        List<TGroup> groupList = tGoupService.selectGroupNameByOpenId(openId);
        for (TGroup group : groupList) {
            if (group.getGroupName().equals(groupName)) {
                resp.setRetCode(3);
                resp.setRetDesc("已存在相同群名称");
                return resp;
            }
        }
        String regex = "0?(13|14|15|16|17|18|19)[0-9]{9}";
        String[] split = groupMemberPhones.split(",");
        for (String s : split) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            if (!s.matches(regex)) {
                resp.setRetCode(2);
                resp.setRetDesc("您输入的号码格式有误");
                return resp;
            }
        }
        TGroup group = new TGroup();
        group.setOpenId(openId);
        group.setGroupName(groupName);
        group.setGroupMemberphones(groupMemberPhones);

        int i = tGoupService.insert(group);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("添加群组失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("添加群组成功");
        return resp;
    }


    /**
     * 删除群组
     *
     * @param openId
     * @param groupName
     * @return
     */
    @RequestMapping(value = "/groupDel.do")
    @ResponseBody
    public CheckSmsCodeResp groupDel(String openId, String groupName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tGoupService.delGroupByOpenIdAndGroupName(openId, groupName);

        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除群组失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除群组成功");
        return resp;
    }


    /**
     * 修改群组
     *
     * @param id
     * @param openId
     * @param groupName
     * @param groupMemberPhones
     * @return
     */
    @RequestMapping(value = "/groupUpd.do")
    @ResponseBody
    public CheckSmsCodeResp groupUpd(int id, String openId,
                                     @RequestParam(value = "groupName", required = false) String groupName,
                                     @RequestParam(value = "groupMemberPhones", required = false) String groupMemberPhones) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        String regex = "0?(13|14|15|16|17|18|19)[0-9]{9}";
        if (!StringUtils.isEmpty(groupMemberPhones)) {
            String[] split = groupMemberPhones.split(",");
            for (String s : split) {
                if (!s.matches(regex)) {
                    resp.setRetCode(2);
                    resp.setRetDesc("您输入的号码格式有误");
                    return resp;
                }
            }
        }

        int i = tGoupService.updateGroup(id, openId, groupName, groupMemberPhones);

        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("修改群组失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("修改群组成功");
        return resp;
    }


    /**
     * 查询群组
     *
     * @param openId
     * @param groupName
     * @return
     */
    @RequestMapping(value = "/groupQry.do")
    @ResponseBody
    public QryGroupResp groupQry(String openId,
                                 @RequestParam(value = "groupName", required = false) String groupName) {
        QryGroupResp resp = new QryGroupResp();

        logger.info("/groupQry.do openId={},groupName={}", openId, groupName);
        List<GroupDto> groupDtoList = new ArrayList<>();
        List<TBook> bookList = new ArrayList<>();
        List<TGroup> groupList = tGoupService.selectGroupDetailByOpenidAndName(openId, groupName);
        if (CollectionUtils.isEmpty(groupList)) {
            resp.setRetCode(1);
            resp.setRetDesc("没有此群组记录");
            return resp;
        }
        for (TGroup group : groupList) {

            GroupDto groupDto = new GroupDto();
            String[] phoneStr = group.getGroupMemberphones().split(",");
            for (String phone : phoneStr) {
                TBook tBook = new TBook();
                TBook book = tBookService.qryFriendByOpenidAndPhone(openId, phone);
                if (book == null) {
                    continue;
                }
                tBook.setOpenId(openId);
                tBook.setPhone(book.getPhone());
                tBook.setFriendName(book.getFriendName());

                bookList.add(tBook);
            }

            groupDto.setGroupMemberPhones(bookList);
            groupDto.setId(group.getId());
            groupDto.setGroupName(group.getGroupName());
            groupDtoList.add(groupDto);
        }

        resp.setRetCode(0);
        resp.setRetDesc("查询群组成功");
        resp.setRetData(groupDtoList);
        return resp;
    }


    /**
     * 话术商城列表
     *
     * @param cpName
     * @param businessDesc
     * @return
     */
    @RequestMapping(value = "/contentQry.do")
    @ResponseBody
    public QryContentResp businessQry(@RequestParam(value = "cpName", required = false) String cpName,
                                      @RequestParam(value = "businessDesc", required = false) String businessDesc) {
        QryContentResp resp = new QryContentResp();
        List<QryContentDto> contentDtos = new ArrayList<>();
        List<TContent> contentList = tContentService.selectContentList(cpName, businessDesc);
        if (StringUtils.isEmpty(contentList)) {
            resp.setRetCode(1);
            resp.setRetDesc("商城为空");
            return resp;
        }

        for (TContent tContent : contentList) {
            QryContentDto contentDto = new QryContentDto();
            //话术
            contentDto.setId(tContent.getId());
            contentDto.setBusinessId(tContent.getBusinessId());
            contentDto.setBusinessImage(tContent.getBusinessImage());
            contentDto.setBusinessName(tContent.getBusinessName());
            contentDto.setBuyTimes(tContent.getBuyTimes());
            contentDto.setClickTimes(tContent.getClickTimes());
            contentDto.setCpId(tContent.getCpId());
            contentDto.setCpImage(tContent.getCpImage());
            contentDto.setCpName(tContent.getCpName());
            contentDto.setCreateTime(tContent.getCreateTime());
            contentDto.setBusinessDesc(tContent.getBusinessDesc());

            /*List<TVoice> tVoiceList = tVoiceService.selectByBusinessId(tContent.getBusinessId());
            if (CollectionUtils.isEmpty(tVoiceList)) {
                continue;
            }
            for (TVoice tVoice : tVoiceList) {
//                tUserVoiceService.selectVoiceStatusById(tVoice.getId());
                //声音
                contentDto.setType(tVoice.getType());
                contentDto.setVoiceId(tVoice.getId());
                contentDto.setVoiceImage(tVoice.getVoiceImage());
                contentDto.setVoiceName(tVoice.getVoiceName());
                contentDto.setVoicePath(tVoice.getVoicePath());
                contentDto.setVpId(tVoice.getVpId());
                contentDto.setVpName(tVoice.getVpName());

                contentDtos.add(contentDto);
            }*/
            contentDtos.add(contentDto);
        }

        resp.setRetCode(0);
        resp.setRetDesc("查询商城成功");
        resp.setRetData(contentDtos);
        return resp;
    }


    /**
     * 购买话术
     *
     * @param userId
     * @param voiceId
     * @return
     */
    @RequestMapping(value = "/userBuyVoice.do")
    @ResponseBody
    public CheckSmsCodeResp userBuyVoice(Integer userId, Integer voiceId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserVoice userVoice = new TUserVoice();
        userVoice.setUserId(userId);
        userVoice.setVoiceId(voiceId);

        int i = tUserVoiceService.insertVoiceToUserCenter(userVoice);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("订购声音失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("订购声音成功");
        return resp;
    }


    /**
     * 查询话术设置页面
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/speechcraftSetQry.do")
    @ResponseBody
    public SpeechcraftSetResp speechcraftSetQry(Integer userId) {

        SpeechcraftSetResp resp = new SpeechcraftSetResp();

        //群组
        LabelVo groupVo = new LabelVo();
        groupVo.setLabelVoList(new ArrayList<>());
        groupVo.setTitleId("88888888");
        groupVo.setTitleName("群组设置");
        groupVo.setImageUrl("http://thirdwx.qlogo.cn/mmopen/mDRsDt5SWPQTQL6ticKvnWDYTk5Xc54beXtxptRWV0uZRanFGLJPV2MO2HuvNea5AV7SicGibdLd6QiasMxIESKzkEEQIN4WeQ19/132");
        groupVo.setShowStyle(1);

        resp.getRetData().add(groupVo);

        TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
        List<TGroup> groupList = tGoupService.selectGroupNameByOpenId(tUserinfo.getOpenid());

        for (TGroup group : groupList) {
            LabelVo LabelVo2 = new LabelVo();
            LabelVo2.setTitleId("" + group.getId());
            LabelVo2.setTitleName(group.getGroupName());
            // LabelVo2.setImageUrl();
            groupVo.getLabelVoList().add(LabelVo2);
        }


        //好友
        LabelVo bookVo = new LabelVo();
        bookVo.setLabelVoList(new ArrayList<>());
        bookVo.setTitleId("99999999");
        bookVo.setTitleName("通讯录设置");
        bookVo.setImageUrl("http://thirdwx.qlogo.cn/mmopen/mDRsDt5SWPQTQL6ticKvnWDYTk5Xc54beXtxptRWV0uZRanFGLJPV2MO2HuvNea5AV7SicGibdLd6QiasMxIESKzkEEQIN4WeQ19/132");
        bookVo.setShowStyle(2);

        resp.getRetData().add(bookVo);


        List<TBook> tBookList = tBookService.selectFriendByOpenId(tUserinfo.getOpenid());
        for (TBook tBook : tBookList) {
            LabelVo LabelVo2 = new LabelVo();
            LabelVo2.setTitleId(tBook.getPhone());
            LabelVo2.setTitleName(tBook.getFriendName());

            bookVo.getLabelVoList().add(LabelVo2);
        }


        //默认
        List<TLabel> allList = tLabelService.selectAllData();
        LabelVo labelVo1 = null;

        for (TLabel tLabel : allList) {

            if ("1".equals(tLabel.getLevel())) {
                labelVo1 = new LabelVo();
                labelVo1.setLabelVoList(new ArrayList<>());
                labelVo1.setTitleId("" + tLabel.getId());
                labelVo1.setTitleName(tLabel.getName());
                labelVo1.setImageUrl(tLabel.getImageurl());
                labelVo1.setShowStyle(5);

                resp.getRetData().add(labelVo1);
            }
            if ("2".equals(tLabel.getLevel())) {
                LabelVo labelVo2 = new LabelVo();
                labelVo2.setTitleId("" + tLabel.getId());
                labelVo2.setTitleName(tLabel.getName());
                labelVo2.setImageUrl(tLabel.getImageurl());
                labelVo2.setShowStyle(5);

                labelVo1.getLabelVoList().add(labelVo2);
            }
        }


        //声音


        //话术
        List<Integer> businessIds = new ArrayList<>();
        Set<Integer> integerSet = new HashSet<>();
        LabelVo businessVo = new LabelVo();
        businessVo.setLabelVoList(new ArrayList<>());
        businessVo.setTitleId("77777777");
        businessVo.setTitleName("话术设置");
        businessVo.setImageUrl("http://thirdwx.qlogo.cn/mmopen/mDRsDt5SWPQTQL6ticKvnWDYTk5Xc54beXtxptRWV0uZRanFGLJPV2MO2HuvNea5AV7SicGibdLd6QiasMxIESKzkEEQIN4WeQ19/132");
        businessVo.setShowStyle(4);

        resp.getRetData().add(businessVo);

        List<TUserVoice> tUserVoiceList = tUserVoiceService.selectVoiceIdByUserId(userId);
        logger.info("==========tUserVoiceList={}===========", tUserVoiceList.size());

        for (TUserVoice userVoice : tUserVoiceList) {

            TVoice tVoice = tVoiceService.selectByVoiceId(userVoice.getVoiceId());
            Integer id = tVoice.getBusinessId();
            businessIds.add(id);
            /*TVoice tVoice = tVoiceService.selectByVoiceId(userVoice.getVoiceId());
            Integer businessId = tVoice.getBusinessId();//1,1,1
            LabelVo labelVo = new LabelVo();
            TContent tContent = tContentService.selectContentByBusinessId(businessId);
            labelVo.setTitleId("" + tContent.getBusinessId());
            labelVo.setTitleName(tContent.getBusinessName());
            labelVo.setImageUrl(tContent.getCpImage());

            businessVo.getLabelVoList().add(labelVo);*/

        }
        logger.info("======原集合====businessIds={}======{}=====", businessIds.size(), businessIds.toString());
        integerSet.addAll(businessIds);
        logger.info("======去重后====businessIds={}======{}=====", integerSet.size(), integerSet.toString());
        for (Integer businessId : integerSet) {
            LabelVo labelVo = new LabelVo();
            TContent tContent = tContentService.selectContentByBusinessId(businessId);
            labelVo.setTitleId("" + tContent.getBusinessId());
            labelVo.setTitleName(tContent.getBusinessName());
            labelVo.setImageUrl(tContent.getCpImage());

            businessVo.getLabelVoList().add(labelVo);
        }

        resp.setRetCode(0);
        resp.setRetDesc("查询话术设置页面成功");

        return resp;
    }


    /**
     * 查询商城页面
     *
     * @return
     */
    @RequestMapping(value = "/mallQry.do")
    @ResponseBody
    public MallResp mallQry(@RequestParam(value = "userId", required = false) Integer userId) {

        MallResp resp = new MallResp();
        List<TMall> defaulList = tMallService.selectAllDataDefaul();

        //默认
        List<TMall> tMallList = tMallService.selectAllData(userId);
        logger.info("========tMallList={}====tMallList.size()={}=====", tMallList, tMallList.size());
        MallVo mallVo1 = null;

        for (TMall tMall : tMallList) {
//            logger.info("=========66666==========");
            if (tMall.getLevel() == 1) {
                mallVo1 = new MallVo();
                mallVo1.setLabelVoList(new ArrayList<>());
                mallVo1.setTitleId("" + tMall.getId());
                mallVo1.setTitleName(tMall.getName());
                mallVo1.setImageUrl(tMall.getImageUrl());
                mallVo1.setShowStyle(5);
//                logger.info("=========mallVo1={}==========", mallVo1);

                resp.getRetData().add(mallVo1);
            }
            if (tMall.getLevel() == 2) {
                MallVo mallVo2 = new MallVo();
                mallVo2.setTitleId("" + tMall.getId());
                mallVo2.setTitleName(tMall.getName());
                mallVo2.setImageUrl(tMall.getImageUrl());
                mallVo2.setShowStyle(5);
//                logger.info("=========mallVo2={}==========", mallVo2);

                mallVo1.getLabelVoList().add(mallVo2);

            }

        }
        List<TMall> del = new ArrayList<>();
        for (TMall tMall : defaulList) {
            for (TMall in : tMallList) {

                if (in.getId().equals(tMall.getId()) && tMall.getLevel() == 2) {
                    del.add(tMall);
//                    logger.info("=====in.getId()={}======", in.getId());
//                    break;
                }
            }
        }
        defaulList.removeAll(del);

        for (TMall tMall : defaulList) {
//            logger.info("=========66666==========");
            if (tMall.getLevel() == 1) {
                mallVo1 = new MallVo();
                mallVo1.setLabelVoList(new ArrayList<>());
                mallVo1.setTitleId("" + tMall.getId());
                mallVo1.setTitleName(tMall.getName());
                mallVo1.setImageUrl(tMall.getImageUrl());
                mallVo1.setShowStyle(5);
//                logger.info("=========mallVo1={}==========", mallVo1);

                resp.getRetDataNo().add(mallVo1);
            }
            if (tMall.getLevel() == 2) {
                MallVo mallVo2 = new MallVo();
                mallVo2.setTitleId("" + tMall.getId());
                mallVo2.setTitleName(tMall.getName());
                mallVo2.setImageUrl(tMall.getImageUrl());
                mallVo2.setShowStyle(5);
//                logger.info("=========mallVo2={}==========", mallVo2);
                mallVo1.getLabelVoList().add(mallVo2);
            }
        }

        logger.info("=======defaulList.size={}=========", defaulList.size());
        logger.info("=======del.size={}=========", del.size());

        resp.setRetCode(0);
        resp.setRetDesc("查询商城页面成功");

        return resp;
    }


    /**
     * 勾选我感兴趣的标签
     *
     * @param id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/insertLabel.do")
    @ResponseBody
    public CheckSmsCodeResp insertlabel(Integer id, Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TMall tMall1 = tMallService.selectByIdAndDefault(id);
        Integer fatherId = tMall1.getFatherId();
        TMall tMall2 = tMallService.selectByIdAndDefault(fatherId);

        TMall tMall = new TMall();
        tMall.setId(id);
        tMall.setUserId(userId);
        tMall.setName(tMall1.getName());
        tMall.setLevel(tMall1.getLevel());
        tMall.setFatherId(tMall1.getFatherId());
        tMall.setImageUrl(tMall1.getImageUrl());

        TMall tMal2 = new TMall();
        tMal2.setId(fatherId);
        tMal2.setUserId(userId);
        tMal2.setName(tMall2.getName());
        tMal2.setLevel(tMall2.getLevel());
        tMal2.setFatherId(tMall2.getFatherId());
        tMal2.setImageUrl(tMall2.getImageUrl());

        TMall tMall3 = tMallService.selectByIdAndUserid(id, userId);
        if (!StringUtils.isEmpty(tMall3)) {
            resp.setRetCode(1);
            resp.setRetDesc("已勾选，不能重复添加");
            return resp;
        }
        int i = tMallService.insertlabel(tMall);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("添加失败");
            return resp;
        }

        TMall tMall4 = tMallService.selectByIdAndUserid(fatherId, userId);
        if (StringUtils.isEmpty(tMall4)) {
            int j = tMallService.insertlabel(tMal2);
        }
        resp.setRetCode(0);
        resp.setRetDesc("添加成功");
        return resp;
    }


    /**
     * 删除我选中的标签
     *
     * @param id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/deleteLabel.do")
    @ResponseBody
    public CheckSmsCodeResp deleteLabel(Integer id, Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        int i = tMallService.deleteLabel(id, userId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除成功");
        return resp;
    }


    @RequestMapping(value = "/myVoiceQry.do")
    @ResponseBody
    public VoiceDtoResp myVoiceQry(Integer userId) {

        List<VoiceDto> voiceDtoList = new ArrayList<>();
        VoiceDtoResp resp = new VoiceDtoResp();

        List<TVoice> tVoiceList = tVoiceService.selectByUserId(userId);
        logger.info("=========tVoiceList={}===========", tVoiceList.size());
        if (!CollectionUtils.isEmpty(tVoiceList)) {
            for (TVoice tVoice : tVoiceList) {
                VoiceDto voiceDto = new VoiceDto();
                voiceDto.setVoiceId(tVoice.getId());
                voiceDto.setVoiceName(tVoice.getVoiceName());
                voiceDto.setVoiceImage(tVoice.getVoiceImage());
                voiceDtoList.add(voiceDto);
            }

            resp.setRetCode(0);
            resp.setRetDesc("ok");
            resp.setRetData(voiceDtoList);
            return resp;
        }
        return null;
    }


    /**
     * 查看我滴设置
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/speechcraftSetInfo.do")
    @ResponseBody
    public SetInfoDtoResp speechcraftSetInfo(Integer userId) {
        SetInfoDtoResp resp = new SetInfoDtoResp();

        List<Integer> integerList = new ArrayList<>();
        List<TSet> tSetList = tSetService.selectByUserId(userId);
        logger.info("======tSetList={}=======", tSetList.size());

        int operationId = 0;


        SetInfoDto setInfoDto = null;
        for (TSet set : tSetList) {
            if (operationId != set.getOperationId()) {
                operationId = set.getOperationId();
                setInfoDto = new SetInfoDto();
                resp.getRetDataList().add(setInfoDto);
                setInfoDto.setOperationId(operationId);
                setInfoDto.setVoiceName(set.getValue());
                setInfoDto.setCreateTime(set.getCreateTime());
                setInfoDto.setContentName(set.getFileName());
                setInfoDto.setUserId(userId);
            }
            if (set.getContent() != null && set.getContent().length() > 0) {
                setInfoDto.getLabelList().add(set.getContent());
            }


        }
        logger.info("======integerList={}=======", integerList);

        resp.setRetCode(0);
        resp.setRetDesc("OK");
        return resp;

    }


    /**
     * 查询单个话术下的多个声音
     *
     * @param businessId
     * @return
     */
    @RequestMapping(value = "/businessQry.do")
    @ResponseBody
    public List<TVoice> businessQry(Integer businessId, Integer userId) {
        List<TVoice> voices = new ArrayList<>();
        if (userId != null) {
            List<TVoice> tVoiceList = tVoiceService.selectByBusinessIdAndUserId(businessId, userId);
            for (TVoice voice : tVoiceList) {
                TVoice tVoice = new TVoice();
                tVoice.setId(voice.getId());
                tVoice.setVpName(voice.getVpName());
                tVoice.setVpId(voice.getVpId());
                tVoice.setVoicePath(voice.getVoicePath());
                tVoice.setVoiceName(voice.getVoiceName());
                tVoice.setVoiceImage(voice.getVoiceImage());
                tVoice.setType(voice.getType());
                tVoice.setCreateTime(voice.getCreateTime());
                tVoice.setBusinessId(voice.getBusinessId());
                voices.add(tVoice);
            }
            return voices;
        }

        List<TVoice> tVoiceList = tVoiceService.selectByBusinessId(businessId);
        for (TVoice voice : tVoiceList) {
            TVoice tVoice = new TVoice();
            tVoice.setId(voice.getId());
            tVoice.setVpName(voice.getVpName());
            tVoice.setVpId(voice.getVpId());
            tVoice.setVoicePath(voice.getVoicePath());
            tVoice.setVoiceName(voice.getVoiceName());
            tVoice.setVoiceImage(voice.getVoiceImage());
            tVoice.setType(voice.getType());
            tVoice.setCreateTime(voice.getCreateTime());
            tVoice.setBusinessId(voice.getBusinessId());
            voices.add(tVoice);
        }
        return voices;
    }


    /**
     * 设置接口
     *
     * @param request
     * @return [
     * {
     * "titleId": "88888888",
     * "titleName": "群组设置",
     * "imageUrl": "http://thirdwx.qlogo.cn/mmopen/mDRsDt5SWPQTQL6ticKvnWDYTk5Xc54beXtxptRWV0uZRanFGLJPV2MO2HuvNea5AV7SicGibdLd6QiasMxIESKzkEEQIN4WeQ19/132",
     * "showStyle": 1,
     * "labelVoList": [
     * {
     * "titleId": "1",
     * "titleName": "45645644"
     * },
     * {
     * "titleId": "4",
     * "titleName": "leigroup"
     * },
     * {
     * "titleId": "5",
     * "titleName": "天霸动霸tua"
     * },
     * {
     * "titleId": "9",
     * "titleName": "555"
     * }
     * ]
     * },
     * {
     * "titleId": "99999999",
     * "titleName": "通讯录设置",
     * "imageUrl": "http://thirdwx.qlogo.cn/mmopen/mDRsDt5SWPQTQL6ticKvnWDYTk5Xc54beXtxptRWV0uZRanFGLJPV2MO2HuvNea5AV7SicGibdLd6QiasMxIESKzkEEQIN4WeQ19/132",
     * "showStyle": 2,
     * "labelVoList": [
     * {
     * "titleId": "13620203403",
     * "titleName": "dujuan"
     * },
     * {
     * "titleId": "15562234654",
     * "titleName": "小慧"
     * },
     * {
     * "titleId": "15625252525",
     * "titleName": "芊芊"
     * },
     * {
     * "titleId": "17687663373",
     * "titleName": "龙哥"
     * },
     * {
     * "titleId": "18750031121",
     * "titleName": "小滢子1"
     * }
     * ]
     * },
     */
    @RequestMapping(value = "/set.do")
    @ResponseBody
    public CheckSmsCodeResp set(HttpServletRequest request/*, @RequestBody LabelVo[] labelVo*/) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        long t = System.currentTimeMillis();
        Integer voiceId = 0;
        String userId = request.getParameter("userId");
        String content = request.getParameter("content");
        if (!StringUtils.isEmpty(content)) {
            try {
                content = java.net.URLDecoder.decode(content, "UTF-8");


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String voice = request.getParameter("voice");

        if (StringUtils.isEmpty(userId)) {
            resp.setRetCode(1);
            resp.setRetDesc("userId为null");
            return resp;
        }
        Integer temp = seq++;

        String fileName = "";
        try {
            if (content != null && content.trim().length() > 0) {
                String deContent = content;
                fileName = MD5.MD5_32bit(deContent) + ".wav";
                new Thread() {
                    @Override
                    public void run() {

                        String[] voices = {"", "ssFemale", "ssFemale", "ssMale", "ssFemale"};
                        String cq[] = {"玩游戏", "开会", "飞机", "运动", "上班", "上课", "睡觉", "约会", "不方便"};
                        String con = deContent.replaceAll("他正在.*，", "他正在XXX，");
                        con = deContent.replaceAll("他现在.*，", "他正在XXX，");

                        String filePath = 2122 + "/BUSINESS-" + 134680578;
                        String fileName = "";
                        String text = "";
                        for (int i = 1; i <= 4; i++) {
                            //您好，我是TA的秘书，他正在上班，请问您有什么事情吗？

                            String filePath1 = filePath + "/" + i;
                            for (String s : cq) {
                                if ("不方便".equals(s)) {
                                    text = con.replaceAll("他正在XXX", "他现在不方便");
                                } else {
                                    text = con.replaceAll("XXX", s);
                                }
                                try {
                                    fileName = MD5.MD5_32bit(text) + ".wav";
                                } catch (NoSuchAlgorithmException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                Result<String> stringResult = voiceService.ttsVoice(filePath1, fileName, text + voices[i], TtsVoiceNameEnum.PQ);

                                logger.info("小兵秘书自定义开场白调用filePath={},content={},tts合成服务返回结果:{}", filePath1, text, JSONObject.toJSONString(stringResult));
                            }
                            String text1 = con.replaceAll("他正在XXX，", "");
                            text1 = con.replaceAll("他现在XXX，", "");
                            try {
                                fileName = MD5.MD5_32bit(text1) + ".wav";
                            } catch (NoSuchAlgorithmException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Result<String> stringResult = voiceService.ttsVoice(filePath1, fileName, text1 + voices[i], TtsVoiceNameEnum.PQ);

                            logger.info("小兵秘书自定义开场白调用filePath={},content={},tts合成服务返回结果:{}", filePath1, text1, JSONObject.toJSONString(stringResult));

                        }

                    }
                }.start();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        logger.info("=======userId===={}==", userId);
//        logger.info("========labelVo===={}====", labelVo.toString());

        List<TSet> tSetList = new ArrayList<>();
        /*for (LabelVo vo : labelVo) {
            //先获取声音id
            if ("77777777".equals(vo.getTitleId())) {
                List<LabelVo> labelVoList = vo.getLabelVoList();
                LabelVo labelVo1 = labelVoList.get(0);
                voiceId = Integer.parseInt(labelVo1.getTitleId());
                logger.info("=========voiceId=={}=======", voiceId);
            }

            //群组
            if ("88888888".equals(vo.getTitleId())) {
                for (LabelVo labelVo1 : vo.getLabelVoList()) {
                    String groupId = labelVo1.getTitleId();
                    TSet tSet = new TSet();
                    tSet.setType("2");
                    tSet.setValue(groupId);
//                    tSet.setVoiceId(voiceId);
                    tSet.setUserId(Integer.parseInt(userId));
                    tSet.setContent(content);
                    tSet.setFileName(fileName);
                    tSet.setOperationId(temp);
                    tSetList.add(tSet);
                }
            }

            //通讯录
            if ("99999999".equals(vo.getTitleId())) {
                for (LabelVo labelVo1 : vo.getLabelVoList()) {
                    String phone = labelVo1.getTitleId();
                    TSet tSet = new TSet();
                    tSet.setType("1");
                    tSet.setValue(phone);
//                    tSet.setVoiceId(voiceId);
                    tSet.setUserId(Integer.parseInt(userId));
                    tSet.setContent(content);
                    tSet.setFileName(fileName);
                    tSet.setOperationId(temp);
                    tSetList.add(tSet);
                }

            }

            //系统标签
            if ("系统标签".equals(vo.getTitleName())) {
                for (LabelVo labelVo1 : vo.getLabelVoList()) {
                    String id = labelVo1.getTitleId();
                    TSet tSet = new TSet();
                    tSet.setType("3");
                    tSet.setValue(id);
//                    tSet.setVoiceId(voiceId);
                    tSet.setUserId(Integer.parseInt(userId));
                    tSet.setContent(content);
                    tSet.setFileName(fileName);
                    tSet.setOperationId(temp);
                    tSetList.add(tSet);
                }

            }

            //情景模式
            if ("情景模式".equals(vo.getTitleName())) {
                for (LabelVo labelVo1 : vo.getLabelVoList()) {
                    String id = labelVo1.getTitleId();
                    TSet tSet = new TSet();
                    tSet.setType("4");
                    tSet.setValue(id);
//                    tSet.setVoiceId(voiceId);
                    tSet.setUserId(Integer.parseInt(userId));
                    tSet.setContent(content);
                    tSet.setFileName(fileName);
                    tSet.setOperationId(temp);
                    tSetList.add(tSet);
                }
            }
        }*/

        /*for (TSet tSet : tSetList) {
            tSet.setVoiceId(voiceId);
        }
        tSetService.addTSetList(tSetList);*/
        TSet set = new TSet();
        set.setUserId(Integer.parseInt(userId));
        set.setType("2");
        set.setValue("99990");
        set.setVoiceId(666666);
        set.setOperationId(temp);
        set.setContent(content);
        set.setFileName(fileName);

        int i = tSetService.addContentSet(set);
        logger.info("=========i={}========", i);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("开场白设置失败");
            return resp;
        }

        resp.setRetCode(0);
        resp.setRetDesc("开场白设置成功");
        return resp;
    }


    /**
     * 查询用户自定义开场白
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/prologueQry.do")
    @ResponseBody
    public String prologueQry(String openid) {
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        Integer userId = tUserinfo.getId();
        logger.info("========userId={}===========", userId);
        List<TSet> tSetList = tSetService.selectContentByUserId(userId);
        logger.info("========tSetList={}===========", tSetList.size());

        if (CollectionUtils.isEmpty(tSetList)) {
            return "您暂时还没自定义开场白";
        }
        return tSetList.get(0).getContent();
    }


    /**
     * 开场白试听
     *
     * @param content
     * @param voice
     * @return
     */

    private Result<String> customerRespAdd(String content, String voice) {
        Result<String> stringResult = null;
        String fileName = null;
        String filePath = 2122 + "/BUSINESS-" + 134680578;
        String url = "https://ai.yousayido.net/recordManagement/";
        try {

            String[] voices = {"", "ssFemale", "ssFemale", "ssMale", "ssFemale"};
            for (int i = 1; i <= 4; i++) {
                //您好，我是TA的秘书，他正在上班，请问您有什么事情吗？
                String filePath1 = filePath + "/" + i;
                fileName = MD5.MD5_32bit(content) + ".wav";
                stringResult = voiceService.ttsVoice(filePath1, fileName, content + voices[i], TtsVoiceNameEnum.PQ);

                logger.info("customerRespAdd filePath={},content={},tts合成服务返回结果:{}", filePath1, content, JSONObject.toJSONString(stringResult));

            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        stringResult.setData(url + stringResult.getData());
        return stringResult;
    }


    /**
     * 开场白试听
     *
     * @param content
     * @param voice
     * @return
     */
    @RequestMapping(value = "/customPrologue.do")
    @ResponseBody
    private Result<String> customPrologue(String content, String voice) {
        Result<String> stringResult = null;
        String fileName = null;
        String filePath = 2122 + "/BUSINESS-" + 134680578;
        String url = "https://ai.yousayido.net/recordManagement/";
        try {
            fileName = MD5.MD5_32bit(content + System.currentTimeMillis()) + ".mp3";
            stringResult = voiceService.ttsVoice(filePath, fileName, content + voice, TtsVoiceNameEnum.PQ);
            logger.info("======fileName={}======stringResult={}===========", fileName, stringResult);
            long size;
            if (stringResult.isStatus()) {
                size = Long.valueOf(stringResult.getInfo());
            } else {
                size = 0L;
            }
            logger.info("小兵秘书自定义开场白调用tts合成服务返回结果:{}", JSONObject.toJSONString(stringResult));


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        stringResult.setData(url + stringResult.getData());
        return stringResult;
    }


    /**
     * 接听清单
     *
     * @param userId
     * @return {
     * "file_path": "",
     * "cycle_id": 0,
     * "answer_type": 1,
     * "info_map": {
     * "score": 0,
     * "returnMsg.getWorkNodeId()": 1569565978998,
     * "playFileList": [
     * "8088d8a2c4e2c3e6a8e3d463e814b9f8.wav"
     * ],
     * "craftId": "189cef41ddd04ebaa5bcef6ded42cce5",
     * "businessId": 134680578,
     * "focus": "",
     * "workNodeName": "开场白",
     * "manual": false,
     * "rid": "189cef41ddd04ebaa5bcef6ded42cce5",
     * "speachCraftName": "开场白"
     * },
     * "content_machine": "你好，他现在不方便接电话，请问找他有什么事吗？",
     * "file_size": 0
     * },
     * {
     * "file_path": "075586527806_3a55e949-66c1-400c-82f4-67c3e134c30a_1.wav;",
     * "cycle_id": 1,
     * "content_customer": [
     * "先给他推荐房子。 ",
     * "能给他推荐房子 ",
     * ""
     * ],
     * "answer_type": 3,
     * "info_map": {
     * "craftId": "e97e345cd4434049bed44f63529c2b8e",
     * "chatInfo.getWorkNodeId()": 1569565978998,
     * "algorithmContent": {
     * "match_question": "",
     * "msgtempl_id": 0,
     * "workflow_id": 0,
     * "company_id": 2122,
     * "focus": "",
     * "type": 2,
     * "craft_id": "e97e345cd4434049bed44f63529c2b8e",
     * "vad_eos": -1,
     * "score": 0,
     * "req_content": "先给他推荐房子。 ",
     * "reqContents": [
     * "先给他推荐房子。 ",
     * "能给他推荐房子 ",
     * ""
     * ],
     * "name": "推介买房",
     * "action": 3,
     * "match_key_word": "房子",
     * "business_id": 134680578,
     * "jump": 4177
     * },
     * "matchInfo": "匹配关键字:房子",
     * "businessId": 134680578,
     * "focus": "",
     * "workNodeName": "OpenQA问答",
     * "manual": false,
     * "rid": "e97e345cd4434049bed44f63529c2b8e",
     * "speachCraftName": "推介买房",
     * "score": 0,
     * "returnMsg.getWorkNodeId()": 1570787183810,
     * "playFileList": [
     * "4ed6b2b5e1b938b667fe089b05c6ecdb.wav"
     * ]
     * },
     * "content_machine": "你刚刚说的是哪里的房子啊",
     * "file_size": 0
     * },
     */
    @RequestMapping(value = "/answerListQry.do")
    @ResponseBody
    public List<DialogDto> answerListQry(Integer userId) {
        List<DialogDto> dialogDtoList = new ArrayList<>();
        //根据userId查询该用户的号码，得出此人的清单
        TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
        if (StringUtils.isEmpty(tUserinfo)) {
            return dialogDtoList;
        }
        String phonenumber = tUserinfo.getPhonenumber();
        //通过号码查询 通话记录
        List<TDialog> tDialogList = tDialogService.selectByTaskId(phonenumber);
        if (CollectionUtils.isEmpty(tDialogList)) {
            return dialogDtoList;
        }
        logger.info("=======tDialogList={}==========", tDialogList.size());
        for (TDialog tDialog : tDialogList) {
            DialogDto dialogDto = new DialogDto();

            //查询通话详情
            TDialogDetailExt tDialogDetailExt = tDialogDetailExtProxy.selectByDialoginId(tDialog.getId());
            logger.info("=========tDialogDetailExt={}==========", tDialogDetailExt);
            String detailRecords = tDialogDetailExt.getDetailRecords();
            logger.info("=========detailRecords={}==========", detailRecords);
            //通话详情 json数组
            JSONArray jsonArray = JSONArray.parseArray(detailRecords);
            logger.info("=========jsonArray={}==========", jsonArray);
            //查询大于两轮的有效通话详情
            //关注点 默认null
            List<String> focus = new ArrayList<>();
            //摘要 默认null
            String simpleWord = "";
            //取出用户说话大于5个字的内容
            if (jsonArray.size() >= 2) {
                for (Object obj : jsonArray) {
                    //关注点：取出走进子流程的节点名称
                    JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                    Object info_map = jsonObject.get("info_map");
                    JSONObject object = JSONObject.parseObject(info_map.toString());
                    Object algorithmContent = object.get("algorithmContent");
                    if (!StringUtils.isEmpty(algorithmContent)) {
                        JSONObject parseObject = JSONObject.parseObject(algorithmContent.toString());
                        Object name = parseObject.get("name");
                        if (!StringUtils.isEmpty(name)) {
                            focus.add(name.toString());
                        }
                    }
                    Object customer = jsonObject.get("content_customer");
                    if (!StringUtils.isEmpty(customer)) {
                        JSONArray array = JSONArray.parseArray(customer.toString());
                        simpleWord = array.get(0).toString();
                        logger.info("========simpleWord={}=========", simpleWord);
                        //取出长度大于5
                        if (simpleWord.length() > 5) {
                            break;
                        }
                    }
                }
            }

            if (jsonArray.size() >= 2) {
                //取出第二轮通话（主叫说的话）
                Object o = jsonArray.get(1);
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                Object content_customer = jsonObject.get("content_customer");
                JSONArray array = JSONArray.parseArray(content_customer.toString());
                //取更长的通话内容
                if (simpleWord.length() <= array.get(0).toString().length()) {
                    simpleWord = (String) array.get(0);
                }

                logger.info("=========simpleWord={}==========", simpleWord);

                //关注点：取出走进子流程的节点名称
                Object info_map = jsonObject.get("info_map");
                JSONObject object = JSONObject.parseObject(info_map.toString());
                Object algorithmContent = object.get("algorithmContent");
                if (!StringUtils.isEmpty(algorithmContent)) {
                    JSONObject parseObject = JSONObject.parseObject(algorithmContent.toString());
                    //如果匹配流程，摘要就为匹配流程节点的那句话
                    Object req_content = parseObject.get("req_content");
                    if (!StringUtils.isEmpty(req_content)) {
                        simpleWord = req_content.toString();
                    }
                }
            }

            String telephone = tDialog.getTelephone();
            //号码被标记的类型
            String phoneType = "";
            TUserTag tUserTag = tUserTagService.selectByPhone(tUserinfo.getOpenid(), telephone);
            if (!StringUtils.isEmpty(tUserTag)) {
                phoneType = tUserTag.getTagName();
            } else {
                TGroupTag tGroupTag = tGroupTagService.selectByOpenidAndPhone(tUserinfo.getOpenid(), telephone);
                if (!StringUtils.isEmpty(tGroupTag)) {
                    phoneType = tGroupTag.getTagName();
                }
            }

            //通讯录分类
            String groupName = "";
            TTagGroup tTagGroup = tGroupTagService.selectByOpenidPhone(tUserinfo.getOpenid(), telephone);
            if (!StringUtils.isEmpty(tTagGroup)) {
                groupName = tTagGroup.getName();
            }

            //好友姓名默认null
            String friendName = "";
            TBook tBook = tBookService.selectByPhoneAndOpenid(telephone, tUserinfo.getOpenid());
            if (!StringUtils.isEmpty(tBook)) {
                friendName = tBook.getFriendName();
            }

            dialogDto.setId(new Long(tDialog.getId()).intValue());
            dialogDto.setBeginDate(tDialog.getBeginDate());
            dialogDto.setCalledPhone(tDialog.getTaskId() + "");
            dialogDto.setCallerPhone(telephone);
            dialogDto.setCreateDate(tDialog.getCreateDate());
            dialogDto.setEndDate(tDialog.getEndDate());
            dialogDto.setTotal_seconds(tDialog.getTotal_seconds());
            dialogDto.setSimpleWord(simpleWord);
            dialogDto.setCallerPhoneType(phoneType);
            dialogDto.setFriendName(friendName);
            //通讯录分类
            dialogDto.setGroupName(groupName);
            //关注点
            dialogDto.setFocus(focus);
            //清单是否已读
            dialogDto.setIsRead(tDialog.getIntentLevel());
            dialogDtoList.add(dialogDto);
        }
        return dialogDtoList;
    }


    /**
     * 接听清单分页查询
     *
     * @param userId
     * @param pageIndex
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/answerListPageQry.do")
    @ResponseBody
    public ReturnMsg answerListPageQry(Integer userId, int pageIndex, int pageNum) {
        Page<DialogDto> page = tDialogService.getDialogListOfPage(pageIndex, pageNum, userId);

        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(0);
        returnMsg.setContent(page);
        return returnMsg;
    }


    /**
     * 接听清单分页查询（绑定的好友）
     *
     * @param openid
     * @param pageIndex
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/answerListBindingPageQry.do")
    @ResponseBody
    public ReturnMsg answerListBindingPageQry(String openid, int pageIndex, int pageNum) {
        Page<DialogDto> page = tDialogService.getBindingDialogListOfPage(pageIndex, pageNum, openid);

        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(0);
        returnMsg.setContent(page);
        return returnMsg;
    }


    /**
     * 删除接听清单
     *
     * @param dialogId
     * @return
     */
    @RequestMapping(value = "/answerListDel.do")
    @ResponseBody
    public CheckSmsCodeResp answerListDel(Long dialogId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tDialogService.delAnswerListById(dialogId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除接听清单失败");
            return resp;
        }

        tDialogDetailExtProxy.delDetailById(dialogId);
        resp.setRetCode(0);
        resp.setRetDesc("删除接听清单成功");
        return resp;
    }

    /**
     * 接听清单（绑定关系好友）
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/answerListBindingQry.do")
    @ResponseBody
    public List<DialogDto> answerListBindingQry(String openid) {
        List<DialogDto> dialogDtoList = new ArrayList<>();
        List<TUserinfo> tUserinfoList = tUserinfoService.selectByRelationOpenId(openid);
        if (CollectionUtils.isEmpty(tUserinfoList)) {
            return dialogDtoList;
        }

        for (TUserinfo tUserinfo : tUserinfoList) {
            String phonenumber = tUserinfo.getPhonenumber();
            List<TDialog> tDialogList = tDialogService.selectByTaskId(phonenumber);
            logger.info("=========tDialogList={}==========", tDialogList.size());
            if (CollectionUtils.isEmpty(tDialogList)) {
                continue;
            }
            for (TDialog tDialog : tDialogList) {
                TDialogDetailExt tDialogDetailExt = tDialogDetailExtProxy.selectByDialoginId(tDialog.getId());
                logger.info("=========tDialogDetailExt={}==========", tDialogDetailExt);
                String detailRecords = tDialogDetailExt.getDetailRecords();
                logger.info("=========detailRecords={}==========", detailRecords);
                JSONArray jsonArray = JSONArray.parseArray(detailRecords);
                //查询大于两轮的有效通话详情
                //关注点 默认null
                List<String> focus = new ArrayList<>();
                //摘要 默认null
                String simpleWord = "";
                //取出用户说话大于5个字的内容
                if (jsonArray.size() >= 2) {
                    for (Object obj : jsonArray) {
                        //关注点：取出走进子流程的节点名称
                        JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                        Object info_map = jsonObject.get("info_map");
                        JSONObject object = JSONObject.parseObject(info_map.toString());
                        Object algorithmContent = object.get("algorithmContent");
                        if (!StringUtils.isEmpty(algorithmContent)) {
                            JSONObject parseObject = JSONObject.parseObject(algorithmContent.toString());
                            Object name = parseObject.get("name");
                            if (!StringUtils.isEmpty(name)) {
                                focus.add(name.toString());
                            }
                        }
                        Object customer = jsonObject.get("content_customer");
                        if (!StringUtils.isEmpty(customer)) {
                            JSONArray array = JSONArray.parseArray(customer.toString());
                            simpleWord = array.get(0).toString();
                            logger.info("========simpleWord={}=========", simpleWord);
                            //取出长度大于5
                            if (simpleWord.length() > 5) {
                                break;
                            }
                        }
                    }
                }

                if (jsonArray.size() >= 2) {
                    //取出第二轮通话（主叫说的话）
                    Object o = jsonArray.get(1);
                    JSONObject jsonObject = JSONObject.parseObject(o.toString());
                    Object content_customer = jsonObject.get("content_customer");
                    JSONArray array = JSONArray.parseArray(content_customer.toString());
                    //取更长的通话内容
                    if (simpleWord.length() <= array.get(0).toString().length()) {
                        simpleWord = (String) array.get(0);
                    }

                    logger.info("=========simpleWord={}==========", simpleWord);

                    //关注点：取出走进子流程的节点名称
                    Object info_map = jsonObject.get("info_map");
                    JSONObject object = JSONObject.parseObject(info_map.toString());
                    Object algorithmContent = object.get("algorithmContent");
                    if (!StringUtils.isEmpty(algorithmContent)) {
                        JSONObject parseObject = JSONObject.parseObject(algorithmContent.toString());
//                        Object name = parseObject.get("name");
                        //如果匹配流程，摘要就为匹配流程节点的那句话
                        Object req_content = parseObject.get("req_content");
                        if (!StringUtils.isEmpty(req_content)) {
                            simpleWord = req_content.toString();
                        }
                    }
                }

                //号码被标记类型
                String telephone = tDialog.getTelephone();
                String phoneType = "";
                TUserTag tUserTag = tUserTagService.selectByPhone(tUserinfo.getOpenid(), telephone);
                if (!StringUtils.isEmpty(tUserTag)) {
                    phoneType = tUserTag.getTagName();
                } else {
                    TGroupTag tGroupTag = tGroupTagService.selectByOpenidAndPhone(tUserinfo.getOpenid(), telephone);
                    if (!StringUtils.isEmpty(tGroupTag)) {
                        phoneType = tGroupTag.getTagName();
                    }
                }

                //好友姓名
                String friendName = "";
                TBook tBook = tBookService.selectByPhoneAndOpenid(telephone, tUserinfo.getOpenid());
                if (!StringUtils.isEmpty(tBook)) {
                    friendName = tBook.getFriendName();
                }

                //通讯录分类
                String groupName = "";
                TTagGroup tTagGroup = tGroupTagService.selectByOpenidPhone(tUserinfo.getOpenid(), telephone);
                if (!StringUtils.isEmpty(tTagGroup)) {
                    groupName = tTagGroup.getName();
                }

                DialogDto dialogDto = new DialogDto();
                dialogDto.setId(new Long(tDialog.getId()).intValue());
                dialogDto.setBeginDate(tDialog.getBeginDate());
                dialogDto.setCalledPhone(tDialog.getTaskId() + "");
                dialogDto.setCallerPhone(telephone);
                dialogDto.setCreateDate(tDialog.getCreateDate());
                dialogDto.setEndDate(tDialog.getEndDate());
                dialogDto.setTotal_seconds(tDialog.getTotal_seconds());
                dialogDto.setSimpleWord(simpleWord);
                dialogDto.setCallerPhoneType(phoneType);
                dialogDto.setFriendName(friendName);
                dialogDto.setFocus(focus);
                dialogDto.setGroupName(groupName);
                //是否已读状态
                dialogDto.setIsRead(tDialog.getIntentLevel());
                dialogDtoList.add(dialogDto);
            }
        }
        return dialogDtoList;
    }


    /**
     * 添加撤销我的状态
     *
     * @param userId
     * @param id
     * @param isCheck
     * @return
     */
    @RequestMapping(value = "/myStatusChange.do")
    @ResponseBody
    public CheckSmsCodeResp myStatusChange(Integer userId, Integer id, boolean isCheck) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
        Integer status = tUserinfo.getStatus();
        if (id.equals(status)) {
            if (isCheck == true) {
                int i = tUserinfoService.updateMyStatus(userId, id);
                if (i < 1) {
                    resp.setRetCode(1);
                    resp.setRetDesc("添加我的状态失败");
                    return resp;
                }
                resp.setRetCode(0);
                resp.setRetDesc("添加我的状态成功");
                return resp;
            }

            int i = tUserinfoService.cancelMyStatus(userId, id);
            if (i < 1) {
                resp.setRetCode(1);
                resp.setRetDesc("撤销我的状态失败");
                return resp;
            }
            resp.setRetCode(0);
            resp.setRetDesc("撤销我的状态成功");
            return resp;
        }

        int i = tUserinfoService.cancelAndUpdateMyStatus(userId, id);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("修改我的状态失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("修改我的状态成功");
        return resp;
    }


    /**
     * 切换我的声音
     *
     * @param userId
     * @param isCheck
     * @return
     */
    @RequestMapping(value = "/myVoiceChange.do")
    @ResponseBody
    public CheckSmsCodeResp myVoiceChange(Integer userId, Integer voiceId, boolean isCheck) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        if (isCheck == true) {
            int i = tUserinfoService.updateMyVoice(userId, voiceId);
            if (i < 1) {
                resp.setRetCode(1);
                resp.setRetDesc("添加我的声音失败");
                return resp;
            }
            resp.setRetCode(0);
            resp.setRetDesc("添加我的声音成功");
            return resp;
        }

        int i = tUserinfoService.cancelMyVoice(userId, voiceId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("修改我的声音失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("修改我的声音成功");
        return resp;
    }


    /**
     * 查看我的状态(所勾选的情景模式)
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/myStatusQry.do")
    @ResponseBody
    public TMall myStatusQry(Integer userId) {
        TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
        TMall tMall1 = new TMall();
        if (!StringUtils.isEmpty(tUserinfo)) {
            Integer status = tUserinfo.getStatus();
            if (!StringUtils.isEmpty(status)) {
                TMall tMall = tMallService.selectById(status, userId);
                logger.info("=========tMall={}=========", tMall);
                if (!StringUtils.isEmpty(tMall)) {
                    tMall1.setId(tMall.getId());
                    tMall1.setFatherId(tMall.getFatherId());
                    tMall1.setImageUrl(tMall.getImageUrl());
                    tMall1.setLevel(tMall.getLevel());
                    tMall1.setName(tMall.getName());
                    tMall1.setUserId(tMall.getUserId());
                    return tMall1;
                }
            }
        }
        return new TMall(-1, "不好意思，你没有设置情景状态~~");
    }


    /**
     * 声音列表查询
     *
     * @return
     */
    @RequestMapping(value = "/voicesListQry.do")
    @ResponseBody
    public List<Voices> voicesListQry() {
        List<Voices> voicesList = tVoiceService.selectVoiceList();
        if (CollectionUtils.isEmpty(voicesList)) {
            return new ArrayList<>();
        }
        return voicesList;
    }


    /**
     * 查看我的声音(所勾选的声音)
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/myIscheckedVoiceQry.do")
    @ResponseBody
    public MyVoice myIscheckedVoiceQry(Integer userId) {
        TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
        if (!StringUtils.isEmpty(tUserinfo)) {
            Integer voiceId = tUserinfo.getVoiceId();
            if (!StringUtils.isEmpty(voiceId)) {
                if (voiceId == 1) {
                    MyVoice voice = new MyVoice();
                    voice.setVoiceId(voiceId);
                    voice.setVoiceName("小凤");
                    voice.setVoiceImage("==========");
                    return voice;
                } else if (voiceId == 2) {
                    MyVoice voice = new MyVoice();
                    voice.setVoiceId(voiceId);
                    voice.setVoiceName("小琪");
                    voice.setVoiceImage("==========");
                    return voice;
                } else if (voiceId == 3) {
                    MyVoice voice = new MyVoice();
                    voice.setVoiceId(voiceId);
                    voice.setVoiceName("TTS男声");
                    voice.setVoiceImage("==========");
                    return voice;
                } else {
                    MyVoice voice = new MyVoice();
                    voice.setVoiceId(voiceId);
                    voice.setVoiceName("TTS女声");
                    voice.setVoiceImage("==========");
                    return voice;
                }
            }
        }
        return new MyVoice(-1, "不好意思，你没有设置声音~~");
    }


    /**
     * 来电推送
     * <p>
     * {
     * "data": {
     * "keyword3": {
     * "value": "18"
     * },
     * "keyword1": {
     * "value": "13613004847"
     * },
     * "keyword2": {
     * "value": "2019-10-11 18:17:21"
     * },
     * "remark": {
     * "value": "这是一条备注信息~"
     * },
     * "first": {
     * "value": "您好，小兵秘书为您接听到一条未接来电"
     * }
     * },
     * "template_id": "tfTD0HlAsHL4v40JBaGlSltl83tIMndWJNtKPpnw4kk",
     * "touser": "opNfewsLHoAkgEj9dpx6ej2zIt0M",
     * "url": "http://ai.yousayido.net/wxgzh/templates/calldetail.html?dialogId=1197808"
     * }
     * <p>
     * {
     * "errcode": 0,
     * "errmsg": "ok",
     * "msgid": 1053774636804210700
     * }
     *
     * @param userId
     */
    @RequestMapping(value = "/callPush.do")
    @ResponseBody
    public String callPush(Integer userId) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
        String openid = tUserinfo.getOpenid();

        List<TDialog> tDialogList = tDialogService.selectAnswerListByUserId(userId);
        logger.info("========tDialogList={}=======", tDialogList);
        for (TDialog tDialog : tDialogList) {
            String telephone = tDialog.getTelephone();
            Date beginDate = tDialog.getBeginDate();
            Integer total_seconds = tDialog.getTotal_seconds();
//            TemplateSendRequest templateSendReq = new TemplateSendRequest();
            TemplateSendRequestDto templateSendRequestDto = new TemplateSendRequestDto();

            String title = "您好，小兵秘书为您接听到一条未接来电";
            TemplateParamRequest templateParamRequest = new TemplateParamRequest();
            TemplateParamRequest templateParamRequest1 = new TemplateParamRequest();
            TemplateParamRequest templateParamRequest2 = new TemplateParamRequest();
            TemplateParamRequest templateParamRequest3 = new TemplateParamRequest();
            TemplateParamRequest templateParamRequest4 = new TemplateParamRequest();

            templateParamRequest.setValue(title);
            templateParamRequest1.setValue(telephone);
            templateParamRequest1.setColor("#1c4587");

            templateParamRequest2.setValue(format.format(beginDate));
            templateParamRequest2.setColor("#1c4587");

            templateParamRequest3.setValue(total_seconds + "秒");
            templateParamRequest3.setColor("#1c4587");

            templateParamRequest4.setValue("请点击下方查看详情~~");
            templateParamRequest4.setColor("#1c4587");

            Map<String, TemplateParamRequest> data = new HashMap<>();
            data.put("first", templateParamRequest);
            data.put("keyword1", templateParamRequest1);
            data.put("keyword2", templateParamRequest2);
            data.put("keyword3", templateParamRequest3);
            data.put("remark", templateParamRequest4);

            String url = String.format("http://ai.yousayido.net/wxgzh/templates/calldetail.html?dialogId=" + tDialog.getId());
            templateSendRequestDto.setTouser(openid);
            templateSendRequestDto.setTemplate_id(XBMSConstant.XBMS_WX_TEMPLATE_ID);
            templateSendRequestDto.setUrl(url);
            templateSendRequestDto.setData(data);

            String accessToken = "26_5A4rupUUQ5iSQ7xAFrUAbmspbv2mbaac7iqmZuTNI38O3i-n1KWo7fqlueLk7sm7cXAuD6sZsctq1f-LvADgAWk4qPGsdOEMCSbFZsfJT5R3r-sr2wtfVaFUX6J7B61Xkatr1xd75C8TpyWgFPVfAIAMWC";
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

            System.out.println("========templateSendReq=========" + templateSendRequestDto.toJsonString());
            String request = HttpUtil.postRequest(requestUrl, templateSendRequestDto.toJsonString());

            JSONObject jb = JSONObject.parseObject(request);
            Object errmsg = jb.get("errmsg");
            if (!"ok".equals(errmsg)) {
                logger.info("=========request=========" + request);
                return "来电通知推送失败~~";
            }

            logger.info("=========request=========" + request);
        }

        return "来电通知推送成功~~";

    }


    /**
     * 删除操作记录列表
     *
     * @param operationId
     */
    @RequestMapping(value = "/delOperationRecord.do")
    @ResponseBody
    public void delOperationRecord(Integer operationId) {
        tSetService.delOperationRecordByOperationId(operationId);
    }


    /**
     * 緩存被叫號碼
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/demo")
    @ResponseBody
    public String test(Integer userId) {
        TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);

        if (tUserinfo == null) {
            logger.info("demo.do user is not exitst. userId=" + userId);
            return "false";
        }

        String phoneNumber = tUserinfo.getPhonenumber();
        if (phoneNumber == null || phoneNumber.length() < 3) {
            logger.info("demo.do phoneNumber is error. userId=" + userId + ",phoneNumber=" + phoneNumber);
            return "false";
        }
        try {
            redisClient.set(14, "xbms_calleeid", phoneNumber);
            logger.info("demo.do success. userId=" + userId + ",phoneNumber=" + phoneNumber);

        } catch (Exception e) {
            logger.warn("demo.do error", e);
            return "false";
        }

        return "true";

    }


    /**
     * 推荐人查询
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/recommendedByMeQry.do")
    @ResponseBody
    public RecommendResp recommendedbyMeQry(Integer userId) {
        RecommendResp resp = new RecommendResp();
        List<TUserinfoDto> tUserinfoDtos = new ArrayList<>();

        List<TUserinfo> tUserinfoList = tUserinfoService.selectBySonId(userId);
        logger.info("===========tUserinfoList={}============", tUserinfoList.size());

        for (TUserinfo tUserinfo : tUserinfoList) {

            TUserinfoDto tUserinfoDto = new TUserinfoDto();
            tUserinfoDto.setId(tUserinfo.getId());
            tUserinfoDto.setOpenId(tUserinfo.getOpenid());
            tUserinfoDto.setNickName(tUserinfo.getNickname());
            tUserinfoDto.setPhoneNumber(tUserinfo.getPhonenumber());
            tUserinfoDto.setRecommenderId(tUserinfo.getRecommenderId());
            tUserinfoDtos.add(tUserinfoDto);
        }

        resp.setRetCode(0);
        resp.setRetDesc("ok");
        resp.setRetData(tUserinfoDtos);
        return resp;

    }



    /**
     * 微信支付接口(会员续费)
     *
     * @param request
     * @param openid
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/wxPay_openMembership.do", method = RequestMethod.GET)
    @ResponseBody
    public Map openMembership(HttpServletRequest request, String openid, Integer goodsId, String totalFee) {
        //传进来的totalFee查数据库判断一下，不匹配就付款失败
        //查询此用户是否是新用户
        List<TOrder> tOrderList = tOrderService.selectByOpenidAndStatus(openid);
        TMeal tMeal = tMealService.selectBygoodsId(goodsId);
        if (!totalFee.equals(tMeal.getPrice()) && !"0.1".equals(totalFee)) {
            return new HashMap<>();
        }
        if ("0.1".equals(totalFee)) {
            if (!CollectionUtils.isEmpty(tOrderList)) {
                return new HashMap<>();
            }
        }

        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        try {
            String out_trade_no = WXPayUtil.generateNonceStr();
            logger.info("=======out_trade_no={}============", out_trade_no);
            //拼接统一下单地址参数
            Map<String, String> paraMap = new HashMap<String, String>();
            paraMap.put("appid", XBMSConstant.XBMS_WX_APPID);
            paraMap.put("body", "小兵秘书-开通会员支付");
            paraMap.put("mch_id", XBMSConstant.XBMS_WXPAY_MCH_ID);
            paraMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paraMap.put("openid", openid);
            paraMap.put("out_trade_no", out_trade_no);//订单号
            paraMap.put("spbill_create_ip", XBMSConstant.getRequestIp(request));
            paraMap.put("total_fee", BigIntegerUtil.changeFee(totalFee).toString());
            //根据openid查询此人有没有已支付的历史订单，没有则只需付款0.01
//            paraMap.put("total_fee", (CollectionUtils.isEmpty(tOrderList))?orgss.toString():bigInteger.toString());
            paraMap.put("trade_type", XBMSConstant.XBMS_WXPAY_TRADE_TYPE);
            paraMap.put("notify_url", "https://ai.yousayido.net/busiManagement/wxgzh/callback.do");// 此路径是微信服务器调用支付结果通知路径随意写

            logger.info("=======paraMap={}=========", paraMap.toString());

            //商户号的密钥
            String paternerKey = XBMSConstant.xbms_wxpay_paternerkey;
            //生成签名
            String sign = WXPayUtil.generateSignature(paraMap, paternerKey);
            paraMap.put("sign", sign);
            logger.info("========paraMap={}==========", paraMap.toString());

            String xml = WXPayUtil.mapToXml(paraMap);//将所有参数(map)转xml格式
            logger.info("========xml={}==========", xml);


            //生成预支付订单
            String goodsName = "";
            TOrder tOrder = new TOrder();
            if ("0.1".equals(totalFee)) {
                goodsName = "首月体验";
            } else if ("4.9".equals(totalFee)) {
                goodsName = "包月VIP";
            } else if ("12.9".equals(totalFee)) {
                goodsName = "包季VIP";
            } else if ("49".equals(totalFee)) {
                goodsName = "包年VIP";
            }
            tOrder.setUserId(tUserinfo.getId());
            tOrder.setOpenid(openid);
            tOrder.setGoodsId(goodsId);
            tOrder.setPrice(totalFee);
            tOrder.setNumber(1);
            tOrder.setPayMoney(totalFee);
            tOrder.setStatus(1);
            tOrder.setTradeNo(out_trade_no);
            tOrder.setType("RECHARGE");
            //套餐名
            tOrder.setGoodsName(goodsName);
            int i = tOrderService.addOrder(tOrder);
            logger.info("=======是否生成与支付订单i={}========", i);

            //统一下单url: https://api.mch.weixin.qq.com/pay/unifiedorder
            String unifiedorder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

            String xmlStr = HttpRequest.sendPost(unifiedorder_url, xml);//发送post请求"统一下单接口"返回预支付id:prepay_id
            logger.info("========xmlStr={}==========", xmlStr);

            //以下内容是返回前端页面的json数据
            String prepay_id = "";//预支付id
            if (xmlStr.indexOf("SUCCESS") != -1) {

                Map<String, String> map = WXPayUtil.xmlToMap(xmlStr);
                prepay_id = map.get("prepay_id");

            }

            Map<String, String> payMap = new HashMap<String, String>();

            payMap.put("appId", XBMSConstant.XBMS_WX_APPID);
            payMap.put("timeStamp", System.currentTimeMillis() / 1000 + "");
            payMap.put("nonceStr", WXPayUtil.generateNonceStr());
            payMap.put("signType", "MD5");
            payMap.put("package", "prepay_id=" + prepay_id);

            String paySign = WXPayUtil.generateSignature(payMap, paternerKey);
            payMap.put("paySign", paySign);

            return payMap;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 微信支付接口(小程序)
     * @param request
     * @param openid 小程序的openid
     * @param totalFee
     * @param goodsId
     * @param unionid
     * @return
     */
    @RequestMapping(value = "/wxPay_applet.do", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> wxPay(HttpServletRequest request,String openid,String totalFee,Integer goodsId,String unionid) {
        //根据unionid查询用户信息
        TUserinfo tUserinfo = tUserinfoService.selectByUnionid(unionid);
        //传进来的totalFee查数据库判断一下，不匹配就付款失败
        //查询此用户是否是新用户
        List<TOrder> tOrderList = tOrderService.selectByOpenidAndStatus(openid);
        TMeal tMeal = tMealService.selectBygoodsId(goodsId);
        if (!totalFee.equals(tMeal.getPrice()) && !"0.1".equals(totalFee)) {
            return new HashMap<>();
        }
        if ("0.1".equals(totalFee)) {
            if (!CollectionUtils.isEmpty(tOrderList)) {
                return new HashMap<>();
            }
        }
        try {
//            String openid = "ozlCq5eVaVC4EGRBXo_7NzDX_oCo";
            String traNo = WXPayUtil.generateNonceStr();
            String requestIp = XBMSConstant.getRequestIp(request);
            //生成的随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
            //商品名称
            String body = "小兵秘书小程序支付";

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", XBMSConstant.XBMS_WX_APPLET_APPID);
            packageParams.put("mch_id", XBMSConstant.XBMS_WXPAY_MCH_ID);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", traNo);//商户订单号
            packageParams.put("total_fee", BigIntegerUtil.changeFee(totalFee).toString());//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", requestIp);
            packageParams.put("notify_url", "https://ai.yousayido.net/busiManagement/wxgzh/wxNotify");//支付成功后的回调地址
            packageParams.put("trade_type", XBMSConstant.XBMS_WXPAY_TRADE_TYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = PayUtil.sign(prestr, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + XBMSConstant.XBMS_WX_APPLET_APPID + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + XBMSConstant.XBMS_WXPAY_MCH_ID + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + "https://ai.yousayido.net/busiManagement/wxgzh/wxNotify" + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + traNo + "</out_trade_no>"
                    + "<spbill_create_ip>" + requestIp + "</spbill_create_ip>"
                    + "<total_fee>" + BigIntegerUtil.changeFee(totalFee).toString() + "</total_fee>"
                    + "<trade_type>" + XBMSConstant.XBMS_WXPAY_TRADE_TYPE + "</trade_type>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            logger.info("调试模式_统一下单接口 请求XML数据：" + xml);

            //生成预支付订单
            String goodsName = "";
            TOrder tOrder = new TOrder();
            if ("0.1".equals(totalFee)) {
                goodsName = "首月体验";
            } else if ("4.9".equals(totalFee)) {
                goodsName = "包月VIP";
            } else if ("12.9".equals(totalFee)) {
                goodsName = "包季VIP";
            } else if ("49".equals(totalFee)) {
                goodsName = "包年VIP";
            }
            tOrder.setUserId(tUserinfo.getId());
            tOrder.setOpenid(tUserinfo.getOpenid());
            tOrder.setGoodsId(goodsId);
            tOrder.setPrice(totalFee);
            tOrder.setNumber(1);
            tOrder.setPayMoney(totalFee);
            tOrder.setStatus(1);
            tOrder.setTradeNo(traNo);
            tOrder.setType("RECHARGE-APPLET");
            //套餐名
            tOrder.setGoodsName(goodsName);
            int i = tOrderService.addOrder(tOrder);
            logger.info("=======是否生成与支付订单i={}========", i);

            //调用统一下单接口，并接受返回的结果
            String result = PayUtil.httpRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", xml);

            logger.info("调试模式_统一下单接口 返回XML数据：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtil.doXMLParse(result);
            logger.info("=========map={}==========", map.toString());

            String return_code = (String) map.get("return_code");//返回状态码

            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if (return_code.equals("SUCCESS")) {
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                //拼接签名需要的参数
                String stringSignTemp = "appId=" + XBMSConstant.XBMS_WX_APPLET_APPID + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id + "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtil.sign(stringSignTemp, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();

                response.put("paySign", paySign);
            }

            response.put("appid", XBMSConstant.XBMS_WX_APPLET_APPID);
            response.put("unionid", unionid);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 小程序 送秘书支付
     * @param request
     * @param openid 小程序的openid
     * @param goodsId
     * @param unionid
     * @return
     */
    @RequestMapping(value = "/applet_giveSecretary.do", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> appletGiveSecretary(HttpServletRequest request,String openid,Integer goodsId,String unionid) {
        //注意次openid为小程序的openid
        return wxService.payOfAppletGiveSecretary(request,openid,goodsId,unionid);
    }


    /**
     * 微信支付接口(送秘书)
     *
     * @param request
     * @param openid
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/wxPay_giveSecretary.do", method = RequestMethod.GET)
    @ResponseBody
    public Map giveSecretary(HttpServletRequest request, String openid, Integer goodsId) {
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        TMeal tMeal = tMealService.selectBygoodsId(goodsId);
        try {
            String out_trade_no = WXPayUtil.generateNonceStr();
            //拼接统一下单地址参数
            Map<String, String> paraMap = new HashMap<String, String>();
            paraMap.put("appid", XBMSConstant.XBMS_WX_APPID);
            paraMap.put("body", "小兵秘书-送秘书支付");
            paraMap.put("mch_id", XBMSConstant.XBMS_WXPAY_MCH_ID);
            paraMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paraMap.put("openid", openid);
            paraMap.put("out_trade_no", out_trade_no);//订单号
            paraMap.put("spbill_create_ip", XBMSConstant.getRequestIp(request));
            paraMap.put("total_fee", BigIntegerUtil.changeFee(tMeal.getPrice()).toString());
            paraMap.put("trade_type", XBMSConstant.XBMS_WXPAY_TRADE_TYPE);
            paraMap.put("notify_url", "https://ai.yousayido.net/busiManagement/wxgzh/giveSecretaryCallback.do");// 此路径是微信服务器调用支付结果通知路径随意写

            //商户号的密钥
            String paternerKey = XBMSConstant.xbms_wxpay_paternerkey;
            //生成签名
            String sign = WXPayUtil.generateSignature(paraMap, paternerKey);
            paraMap.put("sign", sign);
            logger.info("========paraMap={}==========", paraMap.toString());

            String xml = WXPayUtil.mapToXml(paraMap);//将所有参数(map)转xml格式
            logger.info("========xml={}==========", xml);


            //生成预支付订单
            String goodsName = "";
            TOrder tOrder = new TOrder();
            if (goodsId == 1) {
                goodsName = "包月VIP";
            } else if (goodsId == 2) {
                goodsName = "包季VIP";
            } else if (goodsId == 3) {
                goodsName = "包年VIP";
            }
            tOrder.setUserId(tUserinfo.getId());
            tOrder.setOpenid(openid);
            tOrder.setGoodsId(goodsId);
            tOrder.setPrice(tMeal.getPrice());
            tOrder.setNumber(1);
            tOrder.setPayMoney(tMeal.getPrice());
            tOrder.setStatus(1);
            tOrder.setTradeNo(out_trade_no);
            tOrder.setType("SEND");
            tOrder.setGoodsName(goodsName);
            int i = tOrderService.addOrder(tOrder);


            //统一下单url: https://api.mch.weixin.qq.com/pay/unifiedorder
            String unifiedorder_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

            String xmlStr = HttpRequest.sendPost(unifiedorder_url, xml);//发送post请求"统一下单接口"返回预支付id:prepay_id
            logger.info("========xmlStr={}==========", xmlStr);

            //以下内容是返回前端页面的json数据
            String prepay_id = "";//预支付id
            if (xmlStr.indexOf("SUCCESS") != -1) {

                Map<String, String> map = WXPayUtil.xmlToMap(xmlStr);
                prepay_id = map.get("prepay_id");

            }

            Map<String, String> payMap = new HashMap<String, String>();

            payMap.put("appId", XBMSConstant.XBMS_WX_APPID);
            payMap.put("timeStamp", System.currentTimeMillis() / 1000 + "");
            payMap.put("nonceStr", WXPayUtil.generateNonceStr());
            payMap.put("signType", "MD5");
            payMap.put("package", "prepay_id=" + prepay_id);

            String paySign = WXPayUtil.generateSignature(payMap, paternerKey);
            payMap.put("paySign", paySign);

            return payMap;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 微信支付回调接口（开通会员）
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/callback.do")
    @ResponseBody
    public String callBack(HttpServletRequest request, HttpServletResponse response) {

        logger.info("========进入回调接口==========");

        InputStream is = null;
        try {
            //获取请求的流信息(这里是微信发的xml格式所有只能使用流来读)
            is = request.getInputStream();
            String xml = iStreamToXML(is);

            //将微信发的xml转map
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(xml);

            if (notifyMap.get("return_code").equals("SUCCESS")) {

                if (notifyMap.get("result_code").equals("SUCCESS")) {

                    //商户订单号
                    String ordersSn = notifyMap.get("out_trade_no");

                    //实际支付的订单金额:单位 分
                    String amountpaid = notifyMap.get("total_fee");

                    //将分转换成元-实际支付金额:元
                    BigDecimal amountPay = (new BigDecimal(amountpaid).divide(new BigDecimal("100"))).setScale(2);

                    String openid = notifyMap.get("openid");

                    String trade_type = notifyMap.get("trade_type");
                    logger.info("amountpaid:" + amountpaid);
                    logger.info("amountPay:" + amountPay);
                    logger.info("ordersSn:" + ordersSn);
                    logger.info("openid:" + openid);
                    logger.info("trade_type:" + trade_type);

                    //支付成功，更新支付状态
                    tOrderService.updateOrderStatus(ordersSn);

                    //先查询是否会员，是会员的话过期时间在原来的基础上累加，不是就在当前时间上累加
                    TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
                    //查询套餐信息
                    TMeal tMeal = tMealService.selectByTradeNo(ordersSn);
                    Integer useDays = tMeal.getUseDays();
                    if (StringUtils.isEmpty(tUserinfo.getIsMembership())) {
                        //插入会员信息（之前不是会员）
                        if ("私人秘书".equals(tMeal.getType())) {
                            tUserinfoService.updateMemberinfo(openid, useDays);
                        }
                        if ("私人定制".equals(tMeal.getType())) {
                            tUserinfoService.updateMemberInfo(openid, useDays);
                        }
                    } else {
                        //插入会员信息（之前是会员）
                        if ("私人秘书".equals(tMeal.getType())) {
                            tUserinfoService.updateMemberinfoAdd(openid, useDays);
                        }
                        if ("私人定制".equals(tMeal.getType())) {
                            tUserinfoService.updateMemberInfoAdd(openid, useDays);
                        }
                    }
                }
            }

            //告诉微信服务器收到信息了，不要在调用回调action了========这里很重要回复微信服务器信息用流发送一个xml即可
            response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 小程序支付回调接口
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/wxNotify")
    @ResponseBody
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        //sb为微信返回的xml
        String notityXml = sb.toString();
        String resXml = "";
        logger.info("接收到的报文：" + notityXml);

        Map<String, String> map = PayUtil.doXMLParse(notityXml);
        //商户订单号
        String ordersSn = map.get("out_trade_no");
        //unionid
        String unionid = map.get("unionid");

        String returnCode = (String) map.get("return_code");
        if ("SUCCESS".equals(returnCode)) {
            //验证签名是否正确
            Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
            String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            String sign = PayUtil.sign(validStr, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
            //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
            if (sign.equals(map.get("sign"))) {
                /**此处添加自己的业务逻辑代码start**/

                //支付成功，更新支付状态
                tOrderService.updateOrderStatus(ordersSn);
                //先查询是否会员，是会员的话过期时间在原来的基础上累加，不是就在当前时间上累加
                TUserinfo tUserinfo = tUserinfoService.selectByUnionid(unionid);
                //查询套餐信息
                TMeal tMeal = tMealService.selectByTradeNo(ordersSn);
                Integer useDays = tMeal.getUseDays();
                if (StringUtils.isEmpty(tUserinfo.getIsMembership())) {
                    //插入会员信息（之前不是会员）
                    if ("私人秘书".equals(tMeal.getType())) {
                        tUserinfoService.updateMemberinfoByUnionid(unionid, useDays);
                    }
                    if ("私人定制".equals(tMeal.getType())) {
                        tUserinfoService.updateMemberInfoByUnionid(unionid, useDays);
                    }
                } else {
                    //插入会员信息（之前是会员）
                    if ("私人秘书".equals(tMeal.getType())) {
                        tUserinfoService.updateMemberinfoAddByUnionid(unionid, useDays);
                    }
                    if ("私人定制".equals(tMeal.getType())) {
                        tUserinfoService.updateMemberInfoAddByUnionid(unionid, useDays);
                    }
                }

                /**此处添加自己的业务逻辑代码end**/
                //通知微信服务器已经支付成功
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            }
        } else {
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        logger.info(resXml);
        logger.info("微信支付回调数据结束");


        BufferedOutputStream out = new BufferedOutputStream(
                response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }


    /**
     * 小程序送秘书回调接口
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/wxNotifyGiveSecretary")
    @ResponseBody
    public TQctivationcode wxNotifyGiveSecretary(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //业务逻辑交给impl实现层处理
        TQctivationcode tQctivationcode = wxService.wxNotifyGiveSecretary(request,response);
        return tQctivationcode;
    }


    /**
     * 微信支付回调接口（送秘书）
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/giveSecretaryCallback.do")
    @ResponseBody
    public TQctivationcode giveSecretaryCallback(HttpServletRequest request, HttpServletResponse response) {

        InputStream is = null;
        String ordersSn = "";
        try {
            //获取请求的流信息(这里是微信发的xml格式所有只能使用流来读)
            is = request.getInputStream();
            String xml = iStreamToXML(is);

            //将微信发的xml转map
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(xml);

            if (notifyMap.get("return_code").equals("SUCCESS")) {

                if (notifyMap.get("result_code").equals("SUCCESS")) {

                    //商户订单号
                    ordersSn = notifyMap.get("out_trade_no");

                    //实际支付的订单金额:单位 分
                    String amountpaid = notifyMap.get("total_fee");

                    //将分转换成元-实际支付金额:元
                    BigDecimal amountPay = (new BigDecimal(amountpaid).divide(new BigDecimal("100"))).setScale(2);

                    String openid = notifyMap.get("openid");

                    String trade_type = notifyMap.get("trade_type");
                    logger.info("amountpaid:" + amountpaid);
                    logger.info("amountPay:" + amountPay);
                    logger.info("ordersSn:" + ordersSn);
                    logger.info("openid:" + openid);
                    logger.info("trade_type:" + trade_type);

                    //更新支付状态
                    tOrderService.updateOrderStatus(ordersSn);
                    //生成激活码，插入db
                    TOrder order = tOrderService.selectByTradeNo(ordersSn);

                    /*TMeal tMeal = tMealService.selectBygoodsId(order.getGoodsId());
                    Integer useDays = tMeal.getUseDays();*/

                    TQctivationcode qctivationcode = tQctivationcodeService.selectByTradeNo(ordersSn);
                    if (!StringUtils.isEmpty(qctivationcode)) {
                        return new TQctivationcode("此订单已存在，无法二次生成激活码");
                    }
                    TQctivationcode code = new TQctivationcode();
                    String activationCode = RandomUtils.genActivationCode();
                    TQctivationcode tQctivationcode = tQctivationcodeService.selectByCode(activationCode);
                    if (!StringUtils.isEmpty(tQctivationcode)) {
                        activationCode = RandomUtils.genActivationCode();
                    }
                    code.setCodeMealId(order.getGoodsId() + "");
                    code.setAgentId(order.getUserId());
                    code.setStatus(1);
                    code.setCodeType("USER_BUY");
                    code.setTradeNo(ordersSn);
                    code.setCode(activationCode);
                    tQctivationcodeService.addTQctivationcode(code);

                    logger.info("=======code={}=======", code);
                }
            }

            //告诉微信服务器收到信息了，不要在调用回调action了========这里很重要回复微信服务器信息用流发送一个xml即可
            response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
            is.close();

            TQctivationcode tQctivationcode = tQctivationcodeService.selectByTradeNo(ordersSn);
            logger.info("=========tQctivationcode={}============", tQctivationcode);
            return tQctivationcode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TQctivationcode("出异常了~~");
    }


    /**
     * 提现
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/transfer")
    public String transfer(HttpServletRequest request, HttpServletResponse response, String openid, Integer amount) {
        // 1.0 拼凑企业支付需要的参数
        // 2.0 生成map集合
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("mch_appid", XBMSConstant.XBMS_WX_APPID); // 微信公众号的appid
        packageParams.put("mchid", XBMSConstant.XBMS_WXPAY_MCH_ID); // 商户号
        packageParams.put("nonce_str", WXPayUtil.generateNonceStr()); // 随机生成后数字，保证安全性
        packageParams.put("partner_trade_no", WXPayUtil.generateNonceStr()); // 生成商户订单号
        packageParams.put("openid", openid); // 支付给用户openid
        packageParams.put("check_name", "NO_CHECK"); // 是否验证真实姓名呢
        packageParams.put("re_user_name", "hgl");// 收款用户姓名(非必须)
        packageParams.put("amount", amount + ""); // 企业付款金额，单位为分
        packageParams.put("desc", "恭喜你，完成了一个代买订单a6w3fswq51asdf6！"); // 企业付款操作说明信息。必填。
        packageParams.put("spbill_create_ip", XBMSConstant.getRequestIp(request)); // 调用接口的机器Ip地址

        try {
            // 3.0 利用上面的参数，先去生成自己的签名
            String sign = WXPayUtil.generateSignature(packageParams, XBMSConstant.xbms_wxpay_paternerkey);
            logger.info("========sign={}===========", sign);

            // 4.0 将签名再放回map中，它也是一个参数
            packageParams.put("sign", sign);

            // 5.0将当前的map结合转化成xml格式
            String xml = WXPayUtil.mapToXml(packageParams);

            // 6.0获取需要发送的url地址
            String wxUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers"; // 获取退款的api接口


            System.out.println("发送前的xml为：" + xml);

            // 7,向微信发送请求转账请求
            String returnXml = CertHttpUtil.postData(wxUrl, xml, XBMSConstant.XBMS_WXPAY_MCH_ID, "商家证书的路径");

            System.out.println("返回的returnXml为:" + returnXml);

            // 8，将微信返回的xml结果转成map格式
            Map<String, String> returnMap = WXPayUtil.xmlToMap(returnXml);

            if (returnMap.get("return_code").equals("SUCCESS")) {
                // 付款成功
                System.out.println("returnMap为:" + returnMap);

            }

            return returnXml;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "fail";
    }


    /**
     * 套餐查询
     *
     * @return
     */
    @RequestMapping(value = "/mealListQry.do")
    @ResponseBody
    public List<TMeal> mealListQry(String openid) {
        List<TMeal> tMeals = new ArrayList<>();
        List<TMeal> tMealList = tMealService.selectMealList();

        //判断此用户是否新用户
        List<TOrder> tOrderList = tOrderService.selectByOpenidAndStatus(openid);

        if (!CollectionUtils.isEmpty(tMealList)) {
            for (TMeal meal : tMealList) {
                TMeal tMeal = new TMeal();
                tMeal.setId(meal.getId());
                //如果是新用户首单开通包月vip只需0.1
                tMeal.setPrice((CollectionUtils.isEmpty(tOrderList) && meal.getId() == 1) ? "0.1" : meal.getPrice());
                tMeal.setType(meal.getType());
                tMeal.setCreateTime(new Date());
                tMeal.setName(meal.getName());
                tMeal.setOriginalPrice(meal.getOriginalPrice());
                tMeal.setUseDays(meal.getUseDays());
                tMeals.add(tMeal);
            }
            return tMeals;
        }
        return null;
    }


    /**
     * 送秘书支付套餐ui
     * @return
     */
    @RequestMapping(value = "/sendMealListQry.do")
    @ResponseBody
    public List<TMeal> sendMealListQry() {
        return tMealService.selectMealList();
    }



    /**
     * 查询是否会员
     *
     * @return
     */
    @RequestMapping(value = "/isMembershipQry.do")
    @ResponseBody
    public UserIsMembership isMembershipQry(String openid) {
        UserIsMembership membership = new UserIsMembership();
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        if (StringUtils.isEmpty(tUserinfo)) {
            return new UserIsMembership("此用户不存在");
        }

        membership.setUserId(tUserinfo.getId());
        membership.setNickname(tUserinfo.getNickname());
        membership.setOpenid(tUserinfo.getOpenid());
        membership.setIsMembership(tUserinfo.getIsMembership());
        membership.setExpireTime(tUserinfo.getExpireTime());
        return membership;
    }


    /**
     * 激活码开通会员
     *
     * @return
     */
    @RequestMapping(value = "/openMembershipByCode.do")
    @ResponseBody
    public CheckSmsCodeResp openMembershipByCode(String code, String openid) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        TQctivationcode tQctivationcode = tQctivationcodeService.selectByCode(code);
        if (StringUtils.isEmpty(tQctivationcode)) {
            resp.setRetCode(1);
            resp.setRetDesc("没有此激活码或填写错误");
            return resp;
        }
        if (tQctivationcode.getStatus() == 2) {
            resp.setRetCode(1);
            resp.setRetDesc("此激活码已被使用");
            return resp;
        }
        TMeal tMeal = tMealService.selectByCodeMealId(code);
        String tMealType = tMeal.getType();
        Integer useDays = tMeal.getUseDays();
        logger.info("========tMealName={}==========", tMealType);

        int i = tQctivationcodeService.openMembershipByCode(code, tUserinfo.getId());
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("开通会员失败");
            return resp;
        }

        if (StringUtils.isEmpty(tUserinfo.getIsMembership())) {
            //之前不是会员
            if ("私人秘书".equals(tMeal.getType())) {
                tUserinfoService.updateMemberinfo(openid, useDays);
            }
            if ("私人定制".equals(tMeal.getType())) {
                tUserinfoService.updateMemberInfo(openid, useDays);
            }
        } else {
            //之前是会员
            if ("私人秘书".equals(tMeal.getType())) {
                tUserinfoService.updateMemberinfoAdd(openid, useDays);
            }
            if ("私人定制".equals(tMeal.getType())) {
                tUserinfoService.updateMemberInfoAdd(openid, useDays);
            }
        }

        resp.setRetCode(0);
        resp.setRetDesc("开通会员成功");
        return resp;
    }


    /**
     * 添加组织机构申请
     *
     * @param name
     * @param openId
     * @param picFacade
     * @param picBack
     * @param status
     * @return
     */
    @RequestMapping(value = "/orgApplyAdd.do")
    @ResponseBody
    public CheckSmsCodeResp orgApplyAdd(String name, String openId,
                                        @RequestParam("picFacade") MultipartFile picFacade,
                                        @RequestParam("picBack") MultipartFile picBack,
                                        Integer status) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        if (picBack.isEmpty() || picFacade.isEmpty()) {
            resp.setRetCode(2);
            resp.setRetDesc("图片不能为空");
            return resp;
        }
        String facade = picFacade.getOriginalFilename();
        String back = picBack.getOriginalFilename();

        //ftp将图片上传到服务器
        String ftpPath = "tm/xbms/orgimage/";
        InputStream is1 = null;
        InputStream is2 = null;
        try {
            is1 = picFacade.getInputStream();
            is2 = picBack.getInputStream();
            FTPClient ftpClient = FTPUtils.getFTPClient(address, port, ftpName, password, "UTF-8");
            boolean isSuccess = ftpClient.storeFile(facade, is1);
            boolean isSuccess2 = ftpClient.storeFile(back, is2);
            FTPUtils.uploadFile(ftpClient, ftpPath, facade, is1);
            FTPUtils.uploadFile(ftpClient, ftpPath, back, is2);
            logger.info("=====isSuccess={}======isSuccess2={}==========", isSuccess, isSuccess2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*facade = "/mnt/tm/xbms/orgimage/" + facade;
        back = "/mnt/tm/xbms/orgimage/" + back;

        try {
            picFacade.transferTo(new File(facade));
            picBack.transferTo(new File(back));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        TMechanism tMechanism = new TMechanism();
        String code = RandomUtils.genSixcode();
        String orgNum = RandomUtils.genEightOrgnum();
        logger.info("=======code={}===orgNum={}=========", code, orgNum);
        TMechanism mechanism = tMechanismService.selectByOrgNum(code, orgNum);
        if (StringUtils.isEmpty(mechanism)) {
            tMechanism.setCode(code);
            tMechanism.setOrgNum(orgNum);
        }

        tMechanism.setName(name);
        tMechanism.setOpenid(openId);
        tMechanism.setPicBack(back);
        tMechanism.setPicFacade(facade);
        tMechanism.setStatus(2);

        int i = tMechanismService.insertOrgApply(tMechanism);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("组织机构添加失败");
            return resp;
        }

        tUserinfoService.updateIdeByOpenid(openId);
        resp.setRetCode(0);
        resp.setRetDesc("组织机构添加成功");
        return resp;
    }


    /**
     * 申请代理
     *
     * @param code
     * @param empNum
     * @param openid
     * @return
     */
    @RequestMapping(value = "/agentAdd.do")
    @ResponseBody
    public CheckSmsCodeResp agentAdd(String code, String empNum, String openid) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(tUserinfo.getRecommenderId())) {
            resp.setRetCode(1);
            resp.setRetDesc("您已经是代理，无法申请");
            return resp;
        }

        TUserinfo userinfo = tUserinfoService.selectByCode(code);
        if (StringUtils.isEmpty(userinfo)) {
            resp.setRetCode(1);
            resp.setRetDesc("机构码不存在");
            return resp;
        }
        Integer id = userinfo.getId();
        String sonId = "-0-" + id;
        tUserinfoService.updateEmpByOpenid(id, sonId, empNum, openid);

        resp.setRetCode(0);
        resp.setRetDesc("申请代理成功");
        return resp;
    }


    /**
     * 查询是否为合伙人和代理
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/isAgentQry.do")
    @ResponseBody
    public IsAgentResp isAgentQry(String openid) {
        IsAgentResp resp = new IsAgentResp();
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        logger.info("=========tUserinfo==========" + tUserinfo);
        if (tUserinfo.getIdentity() == null) {
            resp.setOpenid(openid);
            resp.setNickname(tUserinfo.getNickname());
            resp.setIdentity(tUserinfo.getIdentity());
            return resp;
        }
        if (!"2".equals(tUserinfo.getIdentity().toString())) {
            logger.info("=======come in=========");
            resp.setOpenid(openid);
            resp.setNickname(tUserinfo.getNickname());
            resp.setIdentity(tUserinfo.getIdentity());
            return resp;
        }
        TUserinfo userinfo = tUserinfoService.selectOpenidByUserId(tUserinfo.getRecommenderId());

        TMechanism tMechanism = tMechanismService.selectByOpenId(userinfo.getOpenid());

        resp.setOpenid(openid);
        resp.setNickname(tUserinfo.getNickname());
        resp.setIdentity(tUserinfo.getIdentity());
        resp.setOrgNum(tMechanism.getOrgNum());
        return resp;
    }


    /**
     * 查询机构码和状态
     *
     * @param openId
     * @return
     */
    @RequestMapping(value = "/orgNumAndStatusQry.do")
    @ResponseBody
    public TMechanism orgNumAndStatusQry(String openId) {
        TMechanism mechanism = new TMechanism();
        TMechanism tMechanism = tMechanismService.selectByOpenId(openId);
        if (!StringUtils.isEmpty(tMechanism)) {
            mechanism.setName(tMechanism.getName());
            mechanism.setCode(tMechanism.getCode());
            mechanism.setStatus(tMechanism.getStatus());
            return mechanism;
        }
        return null;
    }


    /**
     * 查询会员过期时间
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/membershipListQry.do")
    @ResponseBody
    public CheckSmsCodeResp membershipListQry(String openid) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        if (StringUtils.isEmpty(tUserinfo)) {
            resp.setRetCode(1);
            resp.setRetDesc("没有此用户");
            return resp;
        }
        if (StringUtils.isEmpty(tUserinfo.getIsMembership())) {
            resp.setRetCode(1);
            resp.setRetDesc("普通用户");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc(sdf.format(tUserinfo.getExpireTime()));
        return resp;
    }


    /**
     * 查询用户的历史订单
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/orderHistoryQry.do")
    @ResponseBody
    public OrderHistoryResp orderHistoryQry(String openid) {
        OrderHistoryResp resp = new OrderHistoryResp();
        List<OrderHistory> orderHistories = new ArrayList<>();
        List<OrderHistory> codeOrders = new ArrayList<>();
        //无激活码订单
        List<OrderHistory> orderHistoryList = tOrderService.selectOrderHistoryByOpenid(openid);
        logger.info("========orderHistoryList={}==========", orderHistoryList.size());
        if (!StringUtils.isEmpty(orderHistoryList)) {
            for (OrderHistory order : orderHistoryList) {
                OrderHistory orderHistory1 = new OrderHistory();
                orderHistory1.setOpenid(order.getOpenid());
                orderHistory1.setMealName(order.getMealName());
                orderHistory1.setCreateTime(order.getCreateTime());
                orderHistory1.setNumber(order.getNumber());
                orderHistory1.setPayMoney(order.getPayMoney());
                orderHistory1.setPayTime(order.getPayTime());
                orderHistory1.setPrice(order.getPrice());
                orderHistory1.setStatus(order.getStatus());
                orderHistory1.setTradeNo(order.getTradeNo());
                orderHistory1.setType(order.getType());

                orderHistories.add(orderHistory1);
            }
        }
        logger.info("========orderHistories={}==========", orderHistories.size());

        //激活码订单
        List<OrderHistory> codeOrderList = tOrderService.selectCodeOrderByOpenid(openid);
        logger.info("========codeOrderList={}==========", codeOrderList.size());
        if (!StringUtils.isEmpty(codeOrderList)) {
            for (OrderHistory order : codeOrderList) {
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setOpenid(order.getOpenid());
                orderHistory.setMealName(order.getMealName());
                orderHistory.setCreateTime(order.getCreateTime());
                orderHistory.setNumber(order.getNumber());
                orderHistory.setPayMoney(order.getPayMoney());
                orderHistory.setPayTime(order.getPayTime());
                orderHistory.setPrice(order.getPrice());
                orderHistory.setStatus(order.getStatus());
                orderHistory.setTradeNo(order.getTradeNo());
                orderHistory.setCode(order.getCode());
                orderHistory.setType(order.getType());

                codeOrders.add(orderHistory);
            }
        }
        //两个集合 求并集
        orderHistories.addAll(codeOrders);
        //用比较器实现 根据createtime倒序排序
        Collections.sort(orderHistories, new ReverseSort());
        logger.info("========orderHistories={}==========", orderHistories.size());

        resp.setRetCode(0);
        resp.setRetDesc("历史订单如下（包含激活码）");
        resp.setRetData(orderHistories);
        return resp;
    }


    /**
     * 添加用户改进建议
     *
     * @param openid
     * @param sugFuntion
     * @param sugCraft
     * @param sugOther
     * @return
     */
    @RequestMapping(value = "/suggestionAdd.do")
    @ResponseBody
    public CheckSmsCodeResp suggestionAdd(String openid,
                                          String sugFuntion,
                                          String sugCraft,
                                          String sugOther) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TSuggestion suggestion = new TSuggestion();
        suggestion.setOpenid(openid);
        suggestion.setSugFuntion(sugFuntion);
        suggestion.setSugCraft(sugCraft);
        suggestion.setSugOther(sugOther);
        int i = tSuggestionService.insertSuggestion(suggestion);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("改进建议添加失败");
            return resp;
        }

        resp.setRetCode(0);
        resp.setRetDesc("改进建议添加成功");
        return resp;
    }


    /**
     * 添加用户标签
     *
     * @param openid
     * @param phone
     * @param tagId
     * @param tagName
     * @param type
     * @return
     */
    @RequestMapping(value = "/userTagAdd.do")
    @ResponseBody
    public CheckSmsCodeResp userTagAdd(String openid,
                                       String phone,
                                       Integer tagId,
                                       String tagName,
                                       String type,
                                       String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserTag tUserTag = new TUserTag();
        tUserTag.setOpenid(openid);
        tUserTag.setPhone(phone);
        tUserTag.setTagId(tagId);
        tUserTag.setTagName(tagName);
        tUserTag.setType(type);
        int i = tUserTagService.insertUserTag(tUserTag);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("用户标签添加失败");
            return resp;
        }
        TBook tBook = new TBook();
        tBook.setPhone(phone);
        tBook.setFriendName(friendName);
        tBook.setOpenId(openid);
        int insert = tBookService.insertNotFriendName(tBook);
        resp.setRetCode(0);
        resp.setRetDesc("用户标签添加成功");
        return resp;
    }


    /**
     * 添加群标签
     *
     * @param openid
     * @param phones
     * @param tagId
     * @param tagName
     * @param type
     * @return
     */
    @RequestMapping(value = "/groupTagAdd.do")
    @ResponseBody
    public CheckSmsCodeResp groupTagAdd(String openid,
                                        String phones,
                                        Integer tagId,
                                        String tagName,
                                        String type,
                                        String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        /*//初始化好友姓名（默认为电话号码）
        String name = phones;
        TBook book = tBookService.selectByPhoneAndOpenid(phones, openid);
        if (!StringUtils.isEmpty(book)) {
            //db有，则为原来的姓名
            name = book.getFriendName();
        }*/
        TGroupTag groupTag = new TGroupTag();
        groupTag.setOpenid(openid);
        groupTag.setPhones(phones);
        groupTag.setTagId(tagId);
        groupTag.setTagName(tagName);
        groupTag.setType(type);
        int i = tGroupTagService.insertGroupTag(groupTag);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("群标签添加失败");
            return resp;
        }
        TBook tBook = new TBook();
        tBook.setPhone(phones);
        tBook.setFriendName(friendName);
        tBook.setOpenId(openid);
        int insert = tBookService.insert(tBook);
        logger.info("=======insert========", insert);
        resp.setRetCode(0);
        resp.setRetDesc("群标签添加成功");
        return resp;
    }


    /**
     * 查询用户标签
     *
     * @param openid
     * @param tagName
     * @return
     */
    @RequestMapping(value = "/userTagQry.do")
    @ResponseBody
    public List<TUserTagDto> userTagQry(String openid, String tagName) {
        List<TUserTagDto> tUserTagList = new ArrayList<>();
        List<TUserTagDto> tagList = tUserTagService.selectByOpenidAndTagname(openid, tagName);
        logger.info("=======tagList={}==========", tagList.size());
        if (!CollectionUtils.isEmpty(tagList)) {
            for (TUserTagDto tUserTag : tagList) {
                String friendName = "";
                String phone = tUserTag.getPhone();
                TBook tBook = tBookService.selectByPhoneAndOpenid(phone, openid);
                if (!StringUtils.isEmpty(tBook)) {
                    friendName = tBook.getFriendName();
                }

                TUserTagDto userTag = new TUserTagDto();
                userTag.setOpenid(tUserTag.getOpenid());
                userTag.setPhone(tUserTag.getPhone());
                userTag.setTagId(tUserTag.getTagId());
                userTag.setTagName(tUserTag.getTagName());
                userTag.setType(tUserTag.getType());
                userTag.setId(tUserTag.getId());
                userTag.setCreateTime(tUserTag.getCreateTime());
                userTag.setFriendName(friendName);
                tUserTagList.add(userTag);
            }
            return tUserTagList;
        }
        return null;
    }


    /**
     * 查询群标签
     *
     * @param openid
     * @param tagName
     * @return
     */
    @RequestMapping(value = "/groupTagQry.do")
    @ResponseBody
    public List<TGroupTagDto> groupTagQry(String openid, String tagName) {
        List<TGroupTagDto> groupTagDtos = new ArrayList<>();
        //opNfewsLHoAkgEj9dpx6ej2zIt0M	1	领导	SYSTEM	领导	 小蒋蒋
        //根据openid和tagName联表查询：t_group_tag, t_tag_group, t_book
        List<TGroupTagDto> tagList = tGroupTagService.selectByOpenidAndTagname(openid, tagName);
        logger.info("=======tagList={}========", tagList.size());
        if (!CollectionUtils.isEmpty(tagList)) {
            for (TGroupTagDto tagDto : tagList) {
                logger.info("======tagDto={}======", tagDto);

                TGroupTagDto groupTagDto = new TGroupTagDto();
                groupTagDto.setOpenid(tagDto.getOpenid());
                groupTagDto.setTagId(tagDto.getTagId());
                groupTagDto.setTagName(tagDto.getTagName());
                groupTagDto.setType(tagDto.getType());
                groupTagDto.setGroupName(tagDto.getGroupName());
                groupTagDto.setFriendName(tagDto.getFriendName());
                logger.info("======groupTagDto={}======", groupTagDto.toString());
                groupTagDtos.add(groupTagDto);
            }
            logger.info("======groupTagDtos={}======", groupTagDtos.size());
            return groupTagDtos;
        }
        return null;
    }


    /**
     * 查询群分类列表
     *
     * @return
     */
    @RequestMapping(value = "/groupClassQry.do")
    @ResponseBody
    public List<TTagGroup> groupClassQry() {
        List<TTagGroup> tTagGroupList = tGroupTagService.selectGroupClassList();
        if (CollectionUtils.isEmpty(tTagGroupList)) {
            return new ArrayList<>();
        }
        return tTagGroupList;
    }


    /**
     * 查询用户标记分类
     *
     * @return
     */
    @RequestMapping(value = "/userTagListQry.do")
    @ResponseBody
    public List<TTag> userTagListQry() {
        List<TTag> tTagList = tUserTagService.selectuserTagList();
        if (CollectionUtils.isEmpty(tTagList)) {
            return new ArrayList<>();
        }
        return tTagList;
    }


    /**
     * 查询好友下的多个号码
     *
     * @param openid
     * @param tagName
     * @return
     */
    @RequestMapping(value = "/friendPhonesQry.do")
    @ResponseBody
    public List<FriendPhonesDto> friendPhonesQry(String openid, String tagName, String friendName) {
        List<FriendPhonesDto> phonesDtos = new ArrayList<>();
        //根据分类，好友名字查询好友下的多个号码
        List<FriendPhonesDto> friendPhonesDtoList = tGroupTagService.selectPhoneList(openid, tagName, friendName);
        if (CollectionUtils.isEmpty(friendPhonesDtoList)) {
            return phonesDtos;
        }
        for (FriendPhonesDto phonesDto : friendPhonesDtoList) {
            String tag = "";
            TUserTag tUserTag = tUserTagService.selectByPhone(openid, phonesDto.getPhone());
            if (!StringUtils.isEmpty(tUserTag)) {
                tag = tUserTag.getTagName();
            }
            FriendPhonesDto friendPhonesDto = new FriendPhonesDto();
            friendPhonesDto.setId(phonesDto.getId());
            friendPhonesDto.setFriendName(phonesDto.getFriendName());
            friendPhonesDto.setPhone(phonesDto.getPhone());
            friendPhonesDto.setTagName(tag);
            friendPhonesDto.setOpenid(openid);
            phonesDtos.add(friendPhonesDto);
        }
        return phonesDtos;
    }


    /**
     * 删除好友的号码
     *
     * @param openid
     * @param friendName
     * @return
     */
    @RequestMapping(value = "/friendPhonesDel.do")
    @ResponseBody
    public CheckSmsCodeResp friendPhonesDel(String openid, String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        int i = tBookService.delByOpenIdAndFriendName(openid, friendName);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除成功");
        return resp;
    }


    /**
     * 批量添加好友号码
     *
     * @param friendName
     * @param tagName
     * @param phones
     * @param openid
     * @param tagId
     * @param type
     * @return
     */
    @RequestMapping(value = "/friendPhonesAdd.do")
    @ResponseBody
    public CheckSmsCodeResp friendPhonesDel(String friendName,
                                            String tagName,
                                            String[] phones,
                                            String openid,
                                            Integer tagId,
                                            String type) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        for (String phone : phones) {
            TGroupTag tGroupTag = new TGroupTag();
            tGroupTag.setType(type);
            tGroupTag.setTagId(tagId);
            tGroupTag.setPhones(phone);
            tGroupTag.setOpenid(openid);
            tGroupTag.setTagName(tagName);
            tGroupTagService.insertGroupTag(tGroupTag);

            TBook tBook = new TBook();
            tBook.setOpenId(openid);
            tBook.setFriendName(friendName);
            tBook.setPhone(phone);
            tBookService.insert(tBook);
        }

        resp.setRetCode(0);
        resp.setRetDesc("添加好友成功");
        return resp;
    }


    /**
     * 批量编辑好友号码
     *
     * @param friendName
     * @param phones
     * @param openid
     * @return
     */
    @RequestMapping(value = "/friendPhonesUpd.do")
    @ResponseBody
    public CheckSmsCodeResp friendPhonesUpd(String openid,
//                                            String tagName,
                                            String friendName,
                                            String desName,
                                            String[] phones,
                                            Integer tagId,
                                            String type) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        List<String> phList = new ArrayList<>();
        //根据tagId查询tagName
        TTagGroup tag = tGroupTagService.selectTagNameByTagId(tagId);
        //联表查询t_book,t_group_tag
        List<FriendPhonesDto> phonesDtos = tGroupTagService.selectPhoneList(openid, tag.getName(), friendName);
        /**
         *      小滢子	12356321254	82	12356321254	opNfewsLHoAkgEj9dpx6ej2zIt0M	领导
         小滢子	15566669999	83	15566669999	opNfewsLHoAkgEj9dpx6ej2zIt0M	领导
         小滢子	17788885555	84	17788885555	opNfewsLHoAkgEj9dpx6ej2zIt0M	领导
         */

        //實現，先删除全部数据，在进行添加

        //删除t_book
        tBookService.deletePhonesDto1(openid, friendName);

        for (FriendPhonesDto phonesDto : phonesDtos) {
            phList.add(phonesDto.getPhones());
        }
        for (String s : phList) {
            //删除t_group_tag
            tGroupTagService.deletePhonesDto(openid, s, tag.getName());
        }

        //再添加新的数据
        for (String phone : phones) {
            TGroupTag tGroupTag = new TGroupTag();
            tGroupTag.setType(type);
            tGroupTag.setTagId(tagId);
            tGroupTag.setPhones(phone);
            tGroupTag.setOpenid(openid);
            tGroupTag.setTagName(tag.getName());
            tGroupTagService.insertGroupTag(tGroupTag);

            TBook tBook = new TBook();
            tBook.setOpenId(openid);
            tBook.setFriendName(desName);
            tBook.setPhone(phone);
            tBookService.insert(tBook);
        }
        resp.setRetCode(0);
        resp.setRetDesc("编辑好友成功");
        return resp;
    }


    /**
     * 解除当前用户的手机号
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/relieveUserPhone.do")
    @ResponseBody
    public CheckSmsCodeResp relieveUserPhone(String openid) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        if (StringUtils.isEmpty(tUserinfo)) {
            resp.setRetCode(1);
            resp.setRetDesc("此用户不存在");
            return resp;
        }
        int i = tUserinfoService.updatePhoneByOpenid(openid);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("解除手机号失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("解除手机号成功");
        return resp;
    }


    /**
     * 删除群标签
     *
     * @param openid
     * @param phone
     * @return
     */
    @RequestMapping(value = "/groupTagDel.do")
    @ResponseBody
    public CheckSmsCodeResp groupTagDel(String openid, String phone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        int i = tGroupTagService.deleteByOpenidAndPhone(openid, phone);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("群标签删除失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("群标签删除成功");
        return resp;
    }


    /**
     * 删除用户标签
     *
     * @param openid
     * @param phone
     * @return
     */
    @RequestMapping(value = "/userTagDel.do")
    @ResponseBody
    public CheckSmsCodeResp userTagDel(String openid, String phone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        int i = tUserTagService.deleteByOpenidAndPhone(openid, phone);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("用户标签删除失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("用户标签删除成功");
        return resp;
    }


    /**
     * 查询单个问题下的答案
     *
     * @param knowledgeId
     * @return
     */
    @RequestMapping(value = "/answersListQry.do")
    @ResponseBody
    public List<KnowledgeAnswerResp> answerListQry(String knowledgeId, String openid) {
        List<KnowledgeAnswerResp> knowledgeAnswers = new ArrayList<>();
        List<KnowledgeAnswerResp> customers = new ArrayList<>();
        //系统的
        List<KnowledgeAnswer> answerList = knowledgeAnswerService.selectByKnowledgeId(knowledgeId);
        logger.info("========answerList={}=========", answerList.size());
        if (!CollectionUtils.isEmpty(answerList)) {
            for (KnowledgeAnswer answer : answerList) {
                logger.info("=======answer.getAnswer()={}=======", answer.getAnswer());
                KnowledgeAnswerResp resp = new KnowledgeAnswerResp();
                resp.setId(answer.getId());
                resp.setKnowledgeId(answer.getKnowledgeId());
                resp.setAnswer(answer.getAnswer());
                knowledgeAnswers.add(resp);
            }
        }

        //自定义的
        List<TCustomresp> tCustomresps = tCustomerrespService.selectByOpenidAndKnowledgeId(openid, knowledgeId);
        if (!CollectionUtils.isEmpty(tCustomresps)) {
            for (TCustomresp tCustomresp : tCustomresps) {
                KnowledgeAnswerResp resp = new KnowledgeAnswerResp();
                resp.setId((long) tCustomresp.getId());
                resp.setKnowledgeId(tCustomresp.getKnowledgeId());
                resp.setAnswer(tCustomresp.getAnsContent());
                customers.add(resp);
            }
        }
        knowledgeAnswers.addAll(customers);
        return knowledgeAnswers;
    }


    /**
     * 查询所有问题
     *
     * @return
     */
    @RequestMapping(value = "/questionListQry.do")
    @ResponseBody
    public List<QuestionDto> questionListQry(String openid) {
        List<QuestionDto> questionDtos = new ArrayList<>();
        List<KnowledgeQuestion> questionList = knowledgeQuestionService.selectQuestionList();
        logger.info("========questionList={}=========", questionList.size());

        if (!CollectionUtils.isEmpty(questionList)) {
            for (KnowledgeQuestion question : questionList) {
                Long answerId = null;
                String ansContent = "";
                TUserQa tUserQa = tUserQaService.selectAnswerId(question.getKnowledgeId(), openid);
                if (!StringUtils.isEmpty(tUserQa)) {
                    logger.info("========tUserQa={}==========", tUserQa.toString());
                    answerId = tUserQa.getAnswerId();
                    try {
                        //系统定义的回答
                        KnowledgeAnswer answer = knowledgeAnswerService.selectByAnswerIdAndKnowledgeId(answerId, question.getKnowledgeId());
                        if (!StringUtils.isEmpty(answer)) {
                            ansContent = answer.getAnswer();
                        }
                        //系统回答没有
                        //用户自定义的回答
                        TCustomresp tCustomresp = tCustomerrespService.selectByOpenidAndKnowId(new Long(answerId).intValue(), openid, question.getKnowledgeId());
                        if (!StringUtils.isEmpty(tCustomresp)) {
                            ansContent = tCustomresp.getAnsContent();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                QuestionDto knowledgeQuestion = new QuestionDto();
                knowledgeQuestion.setId(question.getId());
                knowledgeQuestion.setKnowledgeId(question.getKnowledgeId());
                knowledgeQuestion.setName(question.getName());
                knowledgeQuestion.setAnswerId(answerId);
                knowledgeQuestion.setAnsContent(ansContent);
                questionDtos.add(knowledgeQuestion);
            }
            return questionDtos;
        }
        return null;
    }


    /**
     * 新增快捷自定义回复
     *
     * @param openid
     * @param knowledgeId
     * @param content
     * @return
     */
    @RequestMapping(value = "/customerRespAdd.do")
    @ResponseBody
    public CheckSmsCodeResp customerRespAdd(String openid, String knowledgeId, String content) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        String fileName = "";
        try {
            if (content != null && content.trim().length() > 0) {
                fileName = MD5.MD5_32bit(content) + ".wav";
                new Thread() {
                    @Override
                    public void run() {
                        customerRespAdd(content, "ssFemale");
                    }
                }.start();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        logger.info("=======fileName={}============", fileName);
        TCustomresp tCustomresp = new TCustomresp();
        tCustomresp.setOpenid(openid);
        tCustomresp.setKnowledgeId(knowledgeId);
        tCustomresp.setAnsContent(content);
        tCustomresp.setFileName(fileName);
        int i = tCustomerrespService.addCustomerresp(tCustomresp);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("新增自定义回复失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("新增自定义回复成功");
        return resp;
    }


    /**
     * 删除快捷自定义回复
     *
     * @return
     */
    @RequestMapping(value = "/customerRespDel.do")
    @ResponseBody
    public CheckSmsCodeResp customerRespDel(Integer id) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tCustomerrespService.deleteCustomerresp(id);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除自定义回复失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除自定义回复成功");
        return resp;
    }


    /**
     * 查询所有下级代理
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/agentAllListQry.do")
    @ResponseBody
    public List<AgentsDto> agentAllListQry(String openid) {
        int allPay = 0;
        int allAct = 0;
        //查询自己下级的数量
        List<AgentsDto> agentsDtos = new ArrayList<>();

        List<AgentsDto> agentsDtoList = new ArrayList<>();
        int payCount = 0;

        int sonPayCount = 0;
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(tUserinfo.getIsMembership())) {
            payCount = 1;
            allPay += payCount;
        }
        List<TQctivationcode> tQctivationcodes = tQctivationcodeService.selectByUserIdAndStatus(tUserinfo.getId());
        allAct = tQctivationcodes.size();
//        int actCount = tQctivationcodes.size();
        //查询自己的下一级代理
        List<TUserinfo> tUserinfoList = tUserinfoService.selectBySonId(tUserinfo.getId());

        AgentsDto agentsDto = new AgentsDto();
        agentsDto.setPayCount(payCount);
        agentsDto.setIsMembership(tUserinfo.getIsMembership());
        agentsDto.setIdentity(tUserinfo.getIdentity());
        agentsDto.setNickName("自己");
        agentsDto.setOpenid(tUserinfo.getOpenid());
        agentsDto.setActCount(tQctivationcodes.size());
        agentsDtos.add(agentsDto);

        //下级的下级
        for (TUserinfo userinfo : tUserinfoList) {
            List<TQctivationcode> qctivationcodes = tQctivationcodeService.selectByUserIdAndStatus(userinfo.getId());
            allAct += qctivationcodes.size();
            if (!StringUtils.isEmpty(userinfo.getIsMembership())) {
                sonPayCount = 1;
                allPay = allPay + 1;
            }
            AgentsDto dto = new AgentsDto();
            dto.setPayCount(sonPayCount);
            dto.setIsMembership(userinfo.getIsMembership());
            dto.setIdentity(userinfo.getIdentity());
            dto.setNickName(userinfo.getNickname());
            dto.setOpenid(userinfo.getOpenid());
            dto.setActCount(qctivationcodes.size());
            agentsDtoList.add(dto);
            sonPayCount = 0;
        }

        agentsDtos.addAll(agentsDtoList);
        //所有总和
        AgentsDto all = new AgentsDto();
        all.setPayCount(allPay);
        all.setActCount(allAct);
        all.setOpenid("all~~~~~~~~");
        all.setNickName("所有");
        all.setIdentity(10000);
        all.setIsMembership(11111);
        agentsDtos.add(all);

        return agentsDtos;
    }


    /**
     * 查询赚取奖励金
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/reward.do")
    @ResponseBody
    public RewardDto reward(String openid) {
        RewardDto rewardDto = new RewardDto();
        Integer money = 0;
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        //查询自己的下一级代理
        List<TUserinfo> tUserinfoList = tUserinfoService.selectByRecommenderid(tUserinfo.getId());
        if (CollectionUtils.isEmpty(tUserinfoList)) {
            rewardDto.setNumber(0);
            rewardDto.setMoney(0);
            return rewardDto;
        }
        for (TUserinfo userinfo : tUserinfoList) {
            Integer userId = userinfo.getId();
            //根据pay_money降序排列
            List<TOrder> tOrderList = tOrderService.selectByUserIdAndStatus(userId);
            logger.info("==========tOrderList={}==============", tOrderList.size());
            if (CollectionUtils.isEmpty(tOrderList)) {
                continue;
            }
            Float payMoney = Float.valueOf(tOrderList.get(0).getPayMoney());
            //月会员2元，季度会员5元，年会员10元
            if (payMoney > 0.1f && payMoney == 4.9f) {
                money += 2;
            } else if (payMoney > 0.1f && payMoney == 12.9f) {
                money += 5;
            } else if (payMoney > 0.1f && payMoney == 49f) {
                money += 10;
            }
        }

        rewardDto.setNumber(tUserinfoList.size());
        rewardDto.setMoney(money);
        return rewardDto;
    }


    /**
     * 绑定关系（老人防诈骗）
     *
     * @param openid
     * @param phone
     * @return
     */
    @RequestMapping(value = "/relationBinding.do")
    @ResponseBody
    public CheckSmsCodeResp relationBinding(String openid, String phone) {
        //查询申请人的手机号
        TUserinfo userinfo = tUserinfoService.selectUserIdByOpenId(openid);
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        //查询被绑定人的基本信息
        TUserinfo tUserinfo = tUserinfoService.selectByPhone(phone);
        if (StringUtils.isEmpty(tUserinfo)) {
            resp.setRetCode(1);
            resp.setRetDesc("手机号码填写错误 或没关注小兵秘书公众号");
            return resp;
        }
        //判断是否已经绑定
        TRelation tRelation = tRelationService.selectByOpenidAndPhone(openid, tUserinfo.getOpenid());
        if (!StringUtils.isEmpty(tRelation)) {
            resp.setRetCode(1);
            resp.setRetDesc("已经绑定过了，无法重复绑定");
            return resp;
        }
        TRelation relation = new TRelation();
        relation.setOpenid(openid);
        relation.setOpenidRelation(tUserinfo.getOpenid());
        int i = tRelationService.addRelationBinding(relation);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("绑定关系失败");
            return resp;
        }

        //给被绑定者推送绑定申请通知
        String bindingPush = bindingPush(tUserinfo.getOpenid(), userinfo.getPhonenumber());
        logger.info("=========bindingPush={}==========", bindingPush);

        resp.setRetCode(0);
        resp.setRetDesc("绑定关系成功");
        return resp;
    }


    /**
     * 解除绑定关系
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/relationBindingDel.do")
    @ResponseBody
    public CheckSmsCodeResp relationBindingDel(Integer id) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tRelationService.deleterelationBinding(id);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("解除绑定关系失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("解除绑定关系成功");
        return resp;
    }


    /**
     * 查询已绑定关系的用户
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/relationBindingQry.do")
    @ResponseBody
    public List<UserRelationDto> relationBindingQry(String openid) {
        List<UserRelationDto> tUserinfoList = new ArrayList<>();
        List<TUserinfo> tUserinfos = tUserinfoService.selectByRelationOpenId(openid);
        logger.info("=========tUserinfos={}=========", tUserinfos.size());
        if (CollectionUtils.isEmpty(tUserinfos)) {
            return tUserinfoList;
        }
        for (TUserinfo tUserinfo : tUserinfos) {
            TRelation relation = tRelationService.selectByOpenid(openid, tUserinfo.getOpenid());
            logger.info("========come in========");
            UserRelationDto userinfo = new UserRelationDto();
            userinfo.setId(relation.getId());
            userinfo.setOpenid(tUserinfo.getOpenid());
            userinfo.setNickName(tUserinfo.getNickname());
            userinfo.setPhone(tUserinfo.getPhonenumber());
            userinfo.setStatus(relation.getStatus());
            tUserinfoList.add(userinfo);
        }

        return tUserinfoList;
    }


    /**
     * 查询绑定自己的人的信息
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/bindingOwnUserQry.do")
    @ResponseBody
    public List<BindingOwnUser> bindingOwnUserQry(String openid) {
        List<BindingOwnUser> bindingOwnUsers = new ArrayList<>();
        List<TRelation> tRelationList = tRelationService.selectByRelationOpenid(openid);
        if (CollectionUtils.isEmpty(tRelationList)) {
            return bindingOwnUsers;
        }
        for (TRelation relation : tRelationList) {
            String openid1 = relation.getOpenid();
            TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid1);
            BindingOwnUser bindingOwnUser = new BindingOwnUser();
            bindingOwnUser.setOpenid(tUserinfo.getOpenid());
            bindingOwnUser.setCreateTime(relation.getCreateTime());
            bindingOwnUser.setNickName(tUserinfo.getNickname());
            bindingOwnUser.setPhone(tUserinfo.getPhonenumber());
            bindingOwnUser.setStatus(relation.getStatus());
            bindingOwnUser.setRelationId(relation.getId());
            bindingOwnUsers.add(bindingOwnUser);
        }
        return bindingOwnUsers;
    }


    /**
     * 通过绑定
     *
     * @param relationId
     * @return
     */
    @RequestMapping(value = "/passBindings.do")
    @ResponseBody
    public CheckSmsCodeResp passBindings(Integer relationId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tRelationService.updatePassByRelationId(relationId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("绑定失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("通过绑定");
        return resp;
    }


    /**
     * 拒绝绑定
     *
     * @param relationId
     * @return
     */
    @RequestMapping(value = "/refuseBindings.do")
    @ResponseBody
    public CheckSmsCodeResp refuseBindings(Integer relationId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tRelationService.updateRefuseByRelationId(relationId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("绑定失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("拒绝绑定");
        return resp;
    }


    /**
     * 设置应答的回答
     *
     * @param knowledgeId
     * @param openid
     * @param answerId
     * @return
     */
    @RequestMapping(value = "/userQaSet.do")
    @ResponseBody
    public CheckSmsCodeResp userQaSet(String knowledgeId, String openid, Long answerId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserQa tUserQa = new TUserQa();
        tUserQa.setKnowledgeId(knowledgeId);
        tUserQa.setOpenid(openid);
        tUserQa.setAnswerId(answerId);
        int i = tUserQaService.addCustomerresp(tUserQa);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("设置应答失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("设置应答成功");
        return resp;
    }


    /**
     * 新增号码类型
     *
     * @param openid
     * @param phone
     * @param type
     * @return
     */
    @RequestMapping(value = "/phoneTypeAdd.do")
    @ResponseBody
    public CheckSmsCodeResp phoneTypeAdd(String openid, String phone, Integer type) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TPhonetype tPhonetype = new TPhonetype();
        tPhonetype.setOpenid(openid);
        tPhonetype.setPhone(phone);
        tPhonetype.setType(type);
        int i = tPhonetypeService.addPhoneType(tPhonetype);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("新增号码类型失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("新增号码类型成功");
        return resp;
    }


    /**
     * 查询号码类型
     *
     * @param openid
     * @param type
     * @return
     */
    @RequestMapping(value = "/phoneTypeQry.do")
    @ResponseBody
    public List<TPhonetype> phoneTypeQry(String openid, String type) {
        List<TPhonetype> tPhonetypeList = new ArrayList<>();
        List<TPhonetype> phonetypeList = tPhonetypeService.selectByOpenidAndType(openid, type);
        if (CollectionUtils.isEmpty(phonetypeList)) {
            return tPhonetypeList;
        }
        for (TPhonetype tPhonetype : phonetypeList) {
            TPhonetype phonetype = new TPhonetype();
            phonetype.setId(tPhonetype.getId());
            phonetype.setType(tPhonetype.getType());
            phonetype.setPhone(tPhonetype.getPhone());
            phonetype.setOpenid(tPhonetype.getOpenid());
            phonetype.setCreateTime(tPhonetype.getCreateTime());
            tPhonetypeList.add(phonetype);
        }

        return tPhonetypeList;
    }


    /**
     * 删除号码类型
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/phoneTypeDel.do")
    @ResponseBody
    public CheckSmsCodeResp phoneTypeDel(Integer id) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TPhonetype tPhonetype = tPhonetypeService.selectById(id);
        if (StringUtils.isEmpty(tPhonetype)) {
            resp.setRetCode(1);
            resp.setRetDesc("没有此记录，删除失败");
            return resp;
        }

        int i = tPhonetypeService.delectById(id);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除号码类型失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除号码类型成功");
        return resp;
    }


    /**
     * 在线问答QA
     *
     * @param content
     * @return
     */
    @RequestMapping(value = "/onlineQaQry.do")
    @ResponseBody
    public String onlineQaQry(String content) {
        String[] contentArr = new String[1];
        contentArr[0] = content;
        logger.info("======contentArr[0]={}=======", contentArr[0]);

        /**
         * {"workflow_id":0,"company_id":2122,"resp_ids":[],"match_resp_ids":[],"business_id":134680591,"content":["怎么联系"]}
         */
        //拼接请求body
        JSONObject object = new JSONObject();
        object.put("workflow_id", 0);
        object.put("company_id", 2122);
        object.put("resp_ids", new String[0]);
        object.put("match_resp_ids", new String[0]);
        object.put("business_id", 134680591);
        object.put("content", contentArr);

        //调用 算法查询QA的url
        String url = "http://47.106.109.34:8001/get_response_id";
        String result = HttpUtil.postRequest(url, object.toJSONString());
        logger.info("======object.toJSONString()={}=======", object.toJSONString());
        logger.info("======result={}=======", result);
        /**
         * response
         *
         * {
         "rc": 2,
         "content": {
         "craft_id": "9b13cfde8d304de1844c1c606a26bfc0",
         "company_id": 2122,
         "business_id": 134680591,
         "name": "商务合作怎么联系",
         "match_key_word": "",
         "match_question": "怎么联系",
         "type": 1,
         "jump": 0,
         "action": 3,
         "score": 0,
         "focus": "",
         "msgtempl_id": 0,
         "workflow_id": 0,
         "vad_eos": -1,
         "req_content": "怎么联系"
         }
         }
         */

        JSONObject jsonObject = JSONObject.parseObject(result);
        String contentStr = jsonObject.getString("content");
        if (StringUtils.isEmpty(contentStr)) {
            //content为空，返回默认的回答
            return "您问的问题我还无法回答，但是我们会努力优化的";
        }
        JSONObject ob = JSONObject.parseObject(contentStr);
        String craftId = ob.getString("craft_id");
        List<KnowledgeAnswer> knowledgeAnswers = knowledgeAnswerService.selectByKnowledgeId(craftId);
        KnowledgeAnswer answer = knowledgeAnswers.get(0);
        return answer.getAnswer();
    }


    /**
     * 查询用户感兴趣的事项
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/userIntrestingQry.do")
    @ResponseBody
    public List<TMall> userIntrestingQry(String openid) {
        List<TMall> tMallList = new ArrayList<>();
        //新建一个去重的临时list
        List<TMall> del = new ArrayList<>();
        //查询user_id为0的标签
        List<TMall> defaultList = tMallService.selectAllByOpenid();
        //自己勾选的标签
        List<TMall> mallList = tMallService.selectByOpenid(openid);
        if (!CollectionUtils.isEmpty(mallList)) {
            for (TMall tMall : defaultList) {
                for (TMall mall : mallList) {
                    if (tMall.getId().equals(mall.getId())) {
                        del.add(tMall);
                    }
                }
            }
            //去重，留用户勾选的标签
            defaultList.removeAll(del);
            defaultList.addAll(mallList);
            //用比较器 根据id升序排序
            Collections.sort(defaultList, new ReverseSortTmall());


            for (TMall tMall : defaultList) {
                TMall mall = new TMall();
                mall.setId(tMall.getId());
                mall.setUserId(tMall.getUserId());
                mall.setName(tMall.getName());
                mall.setLevel(tMall.getLevel());
                mall.setImageUrl(tMall.getImageUrl());
                mall.setFatherId(tMall.getFatherId());
                tMallList.add(mall);
            }
            return tMallList;
        }
        return defaultList;
    }


    /**
     * 设置紧急联系人号码
     *
     * @param openid
     * @param sosPhone
     * @return
     */
    @RequestMapping(value = "/sosPhoneSet.do")
    @ResponseBody
    public CheckSmsCodeResp sosPhoneSet(String openid, String sosPhone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tUserinfoService.updateSosPhoneByOpenid(openid, sosPhone);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("设置紧急联系人失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("设置紧急联系人成功");
        return resp;
    }


    /**
     * 查询紧急联系人号码
     *
     * @param openid
     * @return
     */
    @RequestMapping(value = "/sosPhoneQry.do")
    @ResponseBody
    public List<String> sosPhoneQry(String openid) {
        List<String> list = new ArrayList<>();
        TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
        String sosPhone = tUserinfo.getSosPhone();
        if (StringUtils.isEmpty(sosPhone)) {
            return list;
        }
        if (sosPhone.contains(",")) {
            String[] split = sosPhone.split(",");
            for (String s : split) {
                list.add(s);
            }
            return list;
        }

        list.add(sosPhone);
        return list;
    }


    /**
     * 请求鼎鼎下达挂机命令
     *
     * @param callerPhone
     * @param calledPhone
     * @return
     */
    @RequestMapping(value = "/hangup.do")
    @ResponseBody
    public String hangup(String callerPhone, String calledPhone) {
        try {
            //根据被叫号码查询openid
            TUserinfo tUserinfo = tUserinfoService.selectByPhone(calledPhone.substring(4));
            if (StringUtils.isEmpty(tUserinfo)) {
                return "您拨打的用户还未关注小兵秘书公众号！！！";
            }

            TPhonetype tPhonetype = tPhonetypeService.selectByOpenidAndPhone(tUserinfo.getOpenid(), callerPhone);
            logger.info("=====tPhonetype.getType()={}===========", tPhonetype);

            if (StringUtils.isEmpty(tPhonetype)) {
                //查询是否是亲人
                TGroupTag tGroupTag = tGroupTagService.selectByOpenidAndPhone(tUserinfo.getOpenid(), callerPhone);
                if (!StringUtils.isEmpty(tGroupTag)){
                    logger.info("=====tGroupTag.getTagId()={}===========", tGroupTag.getTagId());
                    if (tGroupTag.getTagId() == 3) {
                        if (StringUtils.isEmpty(tUserinfo.getSosPhone())) {
                            return "normal";
                        }
                        return "sofia/gateway/zsdxjfdh2097/" + tUserinfo.getSosPhone();
                    }
                }
            }

            logger.info("=====@@type={}===@@calledPhone={}===@@callerPhone={}===========",
                    tPhonetype.getType(), calledPhone.substring(4), callerPhone);
            //拒接的号码
            if (tPhonetype.getType() == 2) {
                //推送被拦截的号码通知
                logger.info("========拒接拦截启动========");
                String interceptPush = interceptPush(callerPhone, calledPhone);
                logger.info("=========interceptPush===========", interceptPush);

                //把拦截的号码插入db记录下来
                InterceptSta interceptSta = new InterceptSta();
                interceptSta.setCalleephone(calledPhone.substring(4));
                interceptSta.setCallerphone(callerPhone);
                interceptSta.setCreateTime(new Date());
                interceptSta.setType(2);
                tPhonetypeService.insertInterceptPhone(interceptSta);

                return "hangup";
            }
            //重要的号码（有两种情况：1，没接通（可直接转紧急联系人）。2，接通（问答触发转紧急联系人））
            else if (tPhonetype.getType() == 1) {
                if (StringUtils.isEmpty(tUserinfo.getSosPhone())) {
                    return "normal";
                }
                //推送被拦截的号码通知
                logger.info("========重要号码拦截启动========");
                String interceptPush = interceptPush(callerPhone, calledPhone);
                logger.info("=========interceptPush===========", interceptPush);

                //把拦截的号码插入db记录下来
                InterceptSta interceptSta = new InterceptSta();
                interceptSta.setCalleephone(calledPhone.substring(4));
                interceptSta.setCallerphone(callerPhone);
                interceptSta.setCreateTime(new Date());
                interceptSta.setType(1);
                tPhonetypeService.insertInterceptPhone(interceptSta);

                return "sofia/gateway/zsdxjfdh2097/" + tUserinfo.getSosPhone();
            }
        } catch (Exception e) {
            logger.info("出现异常了", e);
        }
        return "normal";
    }

    /**
     * 推送拦截号码（重要号码和拒接号码）来电的通知
     *
     * @param callerPhone
     * @param calledPhone
     * @return
     */
    public String interceptPush(String callerPhone, String calledPhone) {
        //根据被叫号码查询openid
        logger.info("========推送拦截已启动========");
        TUserinfo tUserinfo = tUserinfoService.selectByPhone(calledPhone.substring(4));
        //查询主叫姓名
        String nameAndPhone = callerPhone;
        TBook tBook = tBookService.selectByPhoneAndOpenid(callerPhone, tUserinfo.getOpenid());
        if (!StringUtils.isEmpty(tBook)) {
            nameAndPhone = callerPhone + "（" + tBook.getFriendName() + "）";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        TemplateSendRequestDto templateSendRequestDto = new TemplateSendRequestDto();

        String title = "您好，小兵秘书为您拦截到一条未接来电";
        TemplateParamRequest templateParamRequest = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest1 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest2 = new TemplateParamRequest();
//        TemplateParamRequest templateParamRequest3 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest4 = new TemplateParamRequest();

        templateParamRequest.setValue(title);
        templateParamRequest1.setValue(nameAndPhone);
        templateParamRequest1.setColor("#1c4587");

        templateParamRequest2.setValue(formatter.format(new Date()));
        templateParamRequest2.setColor("#1c4587");

//        templateParamRequest3.setValue("感谢您的支持。");
//        templateParamRequest3.setColor("#1c4587");

        templateParamRequest4.setValue("小兵秘书将竭诚为您服务");
        templateParamRequest4.setColor("#1c4587");

        Map<String, TemplateParamRequest> data = new HashMap<>();
        data.put("first", templateParamRequest);
        data.put("keyword1", templateParamRequest1);
        data.put("keyword2", templateParamRequest2);
//        data.put("keyword3", templateParamRequest3);
        data.put("remark", templateParamRequest4);

//                String url = String.format("https://ai.yousayido.net/wxgzh/templates/calldetail.html?dialogId=" + tDialog.getId());
        templateSendRequestDto.setTouser(tUserinfo.getOpenid());
        templateSendRequestDto.setTemplate_id(XBMSConstant.XBMS_WX_INTERCEPTTEMPLATE_ID);
//                templateSendRequestDto.setUrl(url);
        templateSendRequestDto.setData(data);

        String accessToken = wxService.getAccessToken();
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

        String req = HttpUtil.postRequest(requestUrl, templateSendRequestDto.toJsonString());

        logger.info("========templateSendReq=========" + templateSendRequestDto.toJsonString());
        logger.info("========requestUrl=========" + requestUrl);
        logger.info("========req=========" + req);

        if ("ok".equals(JSONObject.parseObject(req).get("errmsg"))) {
            return "拦截号码通知推送成功~~";
        }

        return "拦截号码通知推送失败~~";
    }


    /**
     * @param openid 被绑定者的openid
     * @param phone  申请人的手机号
     * @return
     */
    public String bindingPush(String openid, String phone) {
        //传入的openid为被绑定者的openid，phone为申请人的手机号
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //根据openid查询用户基本信息
        logger.info("========推送号码绑定已启动========");
        //查询申请人的基本信息
        TUserinfo tUserinfo = tUserinfoService.selectByPhone(phone);
        //查询申请人在被绑定者通讯录中的备注名
        TBook tBook = tBookService.selectByPhoneAndOpenid(phone, openid);
        //控制只有数据库中没有建立过绑定关系的才会推送
//        TRelation relation = tRelationService.selectByOpenid(tUserinfo.getOpenid(), openid);

        TemplateSendRequestDto templateSendRequestDto = new TemplateSendRequestDto();

        String title = "您好，小兵秘书为您接收到一条绑定申请通知";
        TemplateParamRequest templateParamRequest = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest1 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest2 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest3 = new TemplateParamRequest();
        TemplateParamRequest templateParamRequest4 = new TemplateParamRequest();

        templateParamRequest.setValue(title);
        templateParamRequest1.setValue((StringUtils.isEmpty(tBook)) ? tUserinfo.getNickname() : "（姓名：" + tBook.getFriendName() + "）");
        templateParamRequest1.setColor("#1c4587");

        templateParamRequest2.setValue("（即为手机号）" + phone);
        templateParamRequest2.setColor("#1c4587");

        templateParamRequest3.setValue(formatter.format(new Date()));
        templateParamRequest3.setColor("#1c4587");

        templateParamRequest4.setValue("点击对该微信绑定申请进行审核!");
        templateParamRequest4.setColor("#1c4587");

        Map<String, TemplateParamRequest> data = new HashMap<>();
        data.put("first", templateParamRequest);
        data.put("keyword1", templateParamRequest1);
        data.put("keyword2", templateParamRequest2);
        data.put("keyword3", templateParamRequest3);
        data.put("remark", templateParamRequest4);

        String url = String.format("https://ai.yousayido.net/wxgzh/templates/n-monitor.html?openid=" + openid);
        templateSendRequestDto.setTouser(openid);
        templateSendRequestDto.setTemplate_id(XBMSConstant.XBMS_WX_BINDINGTEMPLATE_ID);
        templateSendRequestDto.setUrl(url);
        templateSendRequestDto.setData(data);

        String accessToken = wxService.getAccessToken();
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

        //控制只有数据库中没有建立过绑定关系的才会推送
        String req = HttpUtil.postRequest(requestUrl, templateSendRequestDto.toJsonString());

        logger.info("========req=========" + req);
        logger.info("========templateSendReq=========" + templateSendRequestDto.toJsonString());
        logger.info("========requestUrl=========" + requestUrl);

        if ("ok".equals(JSONObject.parseObject(req).get("errmsg"))) {
            return "绑定申请推送成功~~";
        }
        return "绑定申请推送失败~~";
    }

    /**
     * 通过code，点击链接获得用户的基本信息，并存到db
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/wxUserinfo")
    @ResponseBody
    public TUserinfo wxUserinfoDetail(String code, String parentId) {
        Integer parentid = 0;
        String sonId = "";
        String sonIds = "";

        if (!StringUtils.isEmpty(parentId)) {
            parentid = Integer.parseInt(parentId);
        }

        if (!StringUtils.isEmpty(parentId)) {
            //26
            int userId = Integer.parseInt(parentId);
            TUserinfo tUserinfo = tUserinfoService.selectOpenidByUserId(userId);
            //-0-21
            sonId = tUserinfo.getSonId();
            if (sonId == null) {
                sonIds = "-0-" + userId;
            } else {
                sonIds = sonId + "-" + userId;
            }
        }

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + XBMSConstant.XBMS_WX_APPID +
                "&secret=" + XBMSConstant.XBMS_WX_SECRET +
                "&code=" + code +
                "&grant_type=authorization_code";
        String postRequest = HttpUtil.getHttps(url);
        logger.info("=========postRequest=========" + postRequest);

        JSONObject object = JSONObject.parseObject(postRequest);
        String openid = object.getString("openid");
        String acctoken = object.getString("access_token");
        logger.info("=========openid=========" + openid);
        // 获取用户的信息
        String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + acctoken + "&openid=" + openid + "&lang=zh_CN";
        String info = HttpUtil.getHttps(userUrl);
        // 把信息保存到数据库
        JSONObject userInfo = JSONObject.parseObject(info);
        //  {"openid":"opNfewkh_P3y7ya0rlw7FXbwwzrQ","nickname":"橘子橙子大柚子","sex":1,"language":"zh_CN","city":"深圳","province":"广东","country":"中国","headimgurl":"http:\/\/thirdwx.qlogo.cn\/mmopen\/vi_32\/1USguvJx0N2v4gGpDu6Ypiajiae2AsL7D8ibwwicroN7JQRSbNJGWudx0qHZefg95deichu0DFeMgxicQmgDVDeSSkXg\/132","privilege":[]}

        TUserinfo user = new TUserinfo();

        user.setOpenid(openid);

        String nickname = userInfo.getString("nickname");
        user.setNickname(nickname);

        String sex = userInfo.getString("sex");
        user.setSex(sex);

        String language = userInfo.getString("language");
        user.setLanguage(language);

        String city = userInfo.getString("city");
        user.setCity(city);

        String province = userInfo.getString("province");
        user.setProvince(province);


        String country = userInfo.getString("country");
        user.setCountry(country);

        String headimgurl = userInfo.getString("headimgurl");
        user.setHeadimgurl(headimgurl);

        String unionid = userInfo.getString("unionid");
        user.setUnionid(unionid);

        user.setRecommenderId(parentid);
        user.setSonId(sonIds);

        TUserinfo dbUser = tUserinfoService.selectUserIdByOpenId(openid);
        if (dbUser == null) {
            tUserinfoService.insert(user);

            TUserinfo tUserinfo = tUserinfoService.selectUserIdByOpenId(openid);
            tMallService.insertBaseData(tUserinfo.getId());

            TUserinfo userinfo = new TUserinfo();
            userinfo.setOpenid(openid);
            userinfo.setNickname(nickname);
            userinfo.setHeadimgurl(headimgurl);
            return userinfo;
        }

        return dbUser;
    }


    /**
     * 根据unionid获取用户的openid
     *
     * @param unionid
     * @return
     */
    @RequestMapping(value = "/getOpenidByUnionid.do")
    @ResponseBody
    public String getOpenidByUnionid(String unionid) {
        TUserinfo userinfo = tUserinfoService.selectByUnionid(unionid);
        if (StringUtils.isEmpty(userinfo)) {
            return "unionid not exit";
        }
        return userinfo.getOpenid();
    }


    /**
     * 根据unionid获取用户信息
     *
     * @param unionid
     * @return
     */
    @RequestMapping(value = "/getUserByUnionid.do")
    @ResponseBody
    public TUserinfo getUserByUnionid(String unionid) {
        if (StringUtils.isEmpty(unionid)) {
            return new TUserinfo("unionid is null");
        }
        TUserinfo userinfo = tUserinfoService.selectByUnionid(unionid);
        if (StringUtils.isEmpty(userinfo)) {
            return new TUserinfo("unionid is fault or user not exit");
        }
        return userinfo;
    }


    /**
     * 有的手机本地缓存没效果，我要把存在手机上 的cookie存到数据库中
     *
     * @param key
     * @param value
     * @return
     */
    @RequestMapping(value = "/saveCookie.do")
    @ResponseBody
    public CheckSmsCodeResp saveCookie(String openid, String key, String value) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TKeyValue keyValue = new TKeyValue();
        keyValue.setKey(key);
        keyValue.setValue(value);
        keyValue.setOpenid(openid);
        int i = tKeyValueService.saveCookie(keyValue);

        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("save cookie fail!!!");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("save cookie success!!!");
        return resp;
    }


    /**
     * 有的手机本地缓存没效果，我要把存在手机上 的cookie存到数据库中
     *
     * @param key
     * @param openid
     * @return
     */
    @RequestMapping(value = "/getCookie.do")
    @ResponseBody
    public TKeyValue getCookie(String openid, String key) {
        TKeyValue tKeyValue = tKeyValueService.selectByKey(openid, key);
        if (StringUtils.isEmpty(tKeyValue)) {
            return new TKeyValue("the key is not exit!!!");
        }

        return tKeyValue;
    }


    /**
     * 生成二维码邀请好友
     *
     * @param userId
     * @param url
     * @param httpServletResponse
     */
    @RequestMapping(value = "/getQrcode.do", method = RequestMethod.GET)
    @ResponseBody
    public void getQrcode(String userId, String url, HttpServletResponse httpServletResponse) {
        InputStream imagein = null;
        try {
            httpServletResponse.setContentType("application/x-png");
            httpServletResponse.addHeader("Content-Disposition",
                    "attachment;filename="
                            + URLEncoder.encode("图片.png", "utf-8"));
            OutputStream outputStream = httpServletResponse.getOutputStream();
//            String text = "https://ai.yousayido.net/wxgzh/templates/n-paymeinvite.html?parentId="+ids; // 二维码内容PPNkkUNC4DNCQuDuOv
            String text = url + "?parentId=" + userId;
            String pressText = "";//向享图片中添加文字
            int width = 300; // 二维码图片宽度
            int height = 300; // 二维码图片高度
            String format = "png";// 二维码的图片格式
            int fontStyle = 0; //字体风格
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 内容所使用字符集编码
            BitMatrix bitMatrix = deleteWhite(new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints));
            //生成的二维码
            BufferedImage buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
            Graphics gs = buffImg.createGraphics();
            //设置画笔的颜色
            gs.setColor(Color.black);
            //设置字体
            Font font = new Font("宋体", fontStyle, 24);
            FontMetrics metrics = gs.getFontMetrics(font);
            //文字在图片中的坐标 这里设置在中间
            int startX = (width - metrics.stringWidth(pressText)) / 2;
            int startY = height - 400;
            gs.setFont(font);
            gs.drawString(pressText, startX, startY);
            //背景图片的路径
            imagein = this.getClass().getResourceAsStream("/2.png");
//            File logoFile = new File(WxController.class.getClassLoader().getResource("2.png").getFile());
//            logger.info("=======logoFile={}=========",logoFile.toString());
//            imagein = new FileInputStream(logoFile.toString());
            //读出背景图片
            BufferedImage image = ImageIO.read(imagein);
            Graphics g = image.getGraphics();
            //将二维码画到背景图片中
            g.drawImage(buffImg, 235, 365, buffImg.getWidth(), buffImg.getHeight(), null);
            // 生成二维码
            ImageIO.write(image, format, outputStream);
            ImageIO.write(image, format, new File("d:/NO." + userId + ".png"));

            outputStream.flush();
//            inputStream.close();
            outputStream.close();
            System.out.println("完成二维码生成");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("打包二维码异常");
        }
    }

    /**
     * 删除白边
     */
    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2];
        int resHeight = rec[3];
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }


    public static String iStreamToXML(InputStream inputStream) {
        String str = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line;

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            str = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    public void post(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        response.setContentType("text/html;charset=UTF-8");

        // 调用核心业务类接收消息、处理消息
        String respMessage = messageService.newMessageRequest(request);

        // 响应消息
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(respMessage);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            out.close();
            out = null;
        }
    }


    /**
     * 测试性别识别
     *
     * @param beginDate
     * @param phoneNumber
     * @return
     */
    @RequestMapping(value = "/testSex")
    @ResponseBody
    public String testSex(String beginDate, String phoneNumber) {
        String sex = "未知";
        String dir = "/mnt/tm/recording_sections/" + beginDate;
        File dirFile = new File(dir);
        List<File> fileList = new ArrayList<>();
        for (File f : dirFile.listFiles()) {
//            logger.info("=====f.getName()={}============", f.getName());
            if (f.getName().startsWith(phoneNumber)) {
                fileList.add(f);
            }
        }
        logger.info("======fileList={}===========", fileList.size());
        if (fileList.size() > 0) {

            File tempFile = new File("tmpFile.wav");
            File[] files = new File[fileList.size()];
            logger.info("=======files={}==============", files);

            for (int i = 0; i < fileList.size(); i++) {
                files[i] = fileList.get(i);
            }

            try {
                logger.info("=======files={}==============", files.length);
                WavMergeUtil.mergeWav(files, tempFile.getAbsolutePath());
                FileInputStream is = new FileInputStream(new File(tempFile.getAbsolutePath()));
                byte[] bytes = IOUtils.toByteArray(is);

                sex = voiceGenderService.genderRecognition(bytes);
                logger.info("==========sex=========="+sex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sex;
    }


    @RequestMapping(value = "/testSex2")
    @ResponseBody
    public String testSex2(String path) {
        String sex = "未知";
        File file = new File(path);
        List<File> fileList = new ArrayList<>();
        for (File f : file.listFiles()) {
//            logger.info("=====f.getName()={}============", f.getName());
            if (f.getName().endsWith(".wav")) {
                fileList.add(f);
            }
        }
        logger.info("======fileList={}===========", fileList.size());
        if (fileList.size() > 0) {

            File tempFile = new File("tmpFile.wav");
            File[] files = new File[fileList.size()];
            logger.info("=======files={}==============", files);

            for (int i = 0; i < fileList.size(); i++) {
                files[i] = fileList.get(i);
            }

            try {
                logger.info("=======files={}==============", files.length);
                WavMergeUtil.mergeWav(files, tempFile.getAbsolutePath());
                FileInputStream is = new FileInputStream(new File(tempFile.getAbsolutePath()));
                byte[] bytes = IOUtils.toByteArray(is);

                sex = voiceGenderService.genderRecognition(bytes);
                logger.info("==========sex=========="+sex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sex;
    }


    @RequestMapping("/wechatlogin")
    public String wechatlogin(String code) {
        System.out.println("code:" + code);
        // 小程序
        String AppID = "wxd9302bae6250fc49";
        String AppSecret = "6bb1c8d7230e398685732b57fa7249b1";//这两个都可以从微信公众平台中查找
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="
                + AppID + "&secret=" + AppSecret + "&js_code="
                + code + "&grant_type=authorization_code";
        URL reqURL = null;
        String result = "";
        try {
            reqURL = new URL(url);

            HttpsURLConnection openConnection = (HttpsURLConnection) reqURL
                    .openConnection();
            openConnection.setConnectTimeout(10000);
            //这里我感觉获取openid的时间比较长，不过也可能是我网络的问题，
            //所以设置的响应时间比较长
            openConnection.connect();
            InputStream in = openConnection.getInputStream();

            StringBuilder builder = new StringBuilder();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(in));
            for (String temp = bufreader.readLine(); temp != null; temp = bufreader
                    .readLine()) {
                builder.append(temp);
            }

            result = builder.toString();
            in.close();
            openConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("访问了wechatlogin接口：" + result);
        return result;
        //result就是包含openid的键值对，返回给小程序前端即可
    }

    @RequestMapping("/getUserinfo")
    public String getUserinfo(String encryptedData, String sessionKey, String iv) {
        System.out.println("访问了decrypt接口");
        System.out.println("encryptedData" + encryptedData);
        System.out.println("sessionKey" + sessionKey);
        System.out.println("iv" + iv);
        // 小程序
        String appId = "wxd9302bae6250fc49";
        String resut = WXCore.decrypt(appId, encryptedData, sessionKey, iv);
        wxService.smallerInsert(resut);
        return resut;
    }

    @RequestMapping("/setPhone")
    public String setPhone(String encryptedData, String sessionKey, String iv, String unionid) {
// 小程序
        String appId = "wxd9302bae6250fc49";
        String resut = WXCore.decrypt(appId, encryptedData, sessionKey, iv);
        JSONObject jsonObject = JSONObject.parseObject(resut);
        jsonObject.put("unionid", unionid);
        wxService.setPhoneByUnionid(jsonObject);
        return resut;
    }
}
