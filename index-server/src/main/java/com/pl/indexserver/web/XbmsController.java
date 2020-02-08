package com.pl.indexserver.web;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.model.ReturnMsg;
import com.pl.indexserver.service.*;
import com.pl.indexserver.service.xbms.*;
import com.pl.indexserver.untils.MD5;
import com.pl.indexserver.untils.jiguang.ValidSMSResult;
import com.pl.indexserver.untils.jiguang.common.SMSClient;
import com.pl.mapper.xbms.BookMapper;
import com.pl.model.UserIsMembership;
import com.pl.model.wx.*;
import com.pl.model.xbms.*;
import com.pl.thirdparty.api.VoiceService;
import com.pl.thirdparty.dto.Result;
import com.pl.thirdparty.enums.TtsVoiceNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2020/1/16 10:40
 * @Description 全部之前通过openid查的接口改为userId
 */
@RestController
@RequestMapping(value = "/busiManagement/xbms")
public class XbmsController {

    private static final Logger logger = LoggerFactory.getLogger(XbmsController.class);

    @Reference(version = "${thirdparty.service.version}",
            application = "${dubbo.application.id}", timeout = 180000)
    private VoiceService voiceService;

    @Autowired
    private TUserinfoService tUserinfoService;
    @Autowired
    private WxService wxService;
    @Autowired
    private TSetService tSetService;
    @Autowired
    private TDialogService tDialogService;
    @Autowired
    private TMealService tMealService;
    @Autowired
    private TQctivationcodeService tQctivationcodeService;
    @Autowired
    private TOrderService tOrderService;
    @Autowired
    private TSuggestionService tSuggestionService;
    @Autowired
    private UserTagService userTagService;
    @Autowired
    private GroupTagService groupTagService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private CustomerrespService customerrespService;
    @Autowired
    private RelationService relationService;
    @Autowired
    private UserQaService userQaService;
    @Autowired
    private PhonetypeService phonetypeService;
    @Autowired
    private TMallService tMallService;


    //极光验证短信
    @Value("${jiguang.appkey}")
    private String appKey;
    @Value("${jiguang.mastersecret}")
    private String masterSecret;

    /**
     * 验证码校验接口
     *
     * @param verifyCode
     * @param msgId
     * @param userId
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
                                         Integer userId,
                                         String phoneNumber,
          /**/                           String recommenderId) {
        Integer recommenderid = 0;
        String sonId = "";
        String sonIds = "";

        if (!StringUtils.isEmpty(recommenderId)) {
            //26
            int userid = Integer.parseInt(recommenderId);
            TUserinfo tUserinfo = tUserinfoService.selectByUserId(userid);
            //-0-21
            sonId = tUserinfo.getSonId();
            if (sonId == null) {
                sonIds = "-0-" + userid;
            } else {
                sonIds = sonId + "-" + userid;
            }
        }

        if (!StringUtils.isEmpty(recommenderId)) {
            /**/
            recommenderid = Integer.parseInt(recommenderId);
        }

        CheckSmsCodeResp checkSmsCode = new CheckSmsCodeResp();
        TUserinfo userinfo = wxService.selectByPhoneNumber(phoneNumber);

        TUserinfo userinfoOp = wxService.selectByUserId(userId);
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
            int i = wxService.updateUser(userinfoOp.getId(), phoneNumber, recommenderid, sonIds);
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
     * 通过userId查询用户信息
     * @param userId
     * @return
     */
    @RequestMapping(value = "/wxUserDetailByUserId")
    @ResponseBody
    public TUserinfo getUserinfo(Integer userId) {
        return wxService.selectByUserId(userId);
    }


    /**
     * 查询用户自定义开场白
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/prologueQry.do")
    @ResponseBody
    public String prologueQry(Integer userId) {
        List<TSet> tSetList = tSetService.selectprologueByUserId(userId);
        logger.info("========tSetList={}===========", tSetList.size());

        if (CollectionUtils.isEmpty(tSetList)) {
            return "您暂时还没自定义开场白";
        }
        return tSetList.get(0).getContent();
    }


    /**
     * 接听清单分页查询（绑定的好友）
     *
     * @param userId
     * @param pageIndex
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/answerListBindingPageQry.do")
    @ResponseBody
    public ReturnMsg answerListBindingPageQry(Integer userId, int pageIndex, int pageNum) {
        Page<DialogDto> page = tDialogService.getBdingDialogListOfPage(pageIndex, pageNum, userId);

        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(0);
        returnMsg.setContent(page);
        return returnMsg;
    }


    /**
     * 套餐查询
     *
     * @return
     */
    @RequestMapping(value = "/mealListQry.do")
    @ResponseBody
    public List<TMeal> mealListQry(Integer userId) {
        List<TMeal> tMealList = tMealService.mealListQry(userId);
        return tMealList;
    }


    /**
     * 查询是否会员
     *
     * @return
     */
    @RequestMapping(value = "/isMembershipQry.do")
    @ResponseBody
    public UserIsMembership isMembershipQry(Integer userId) {
        UserIsMembership membership = new UserIsMembership();
        TUserinfo tUserinfo = tUserinfoService.selectByUserId(userId);
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
    public CheckSmsCodeResp openMembershipByCode(String code, Integer userId) {
        CheckSmsCodeResp resp = tQctivationcodeService.openMemberByCode(code,userId);
        return resp;
    }

    /**
     * 申请代理
     *
     * @param code
     * @param empNum
     * @param userId
     * @return
     */
    @RequestMapping(value = "/agentAdd.do")
    @ResponseBody
    public CheckSmsCodeResp agentAdd(String code, String empNum, Integer userId) {
        CheckSmsCodeResp resp = tUserinfoService.agentAdd(code,empNum,userId);
        return resp;
    }

    /**
     * 查询是否为合伙人和代理
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/isAgentQry.do")
    @ResponseBody
    public IsAgentResp isAgentQry(Integer userId) {
        IsAgentResp resp = tUserinfoService.isAgentQry(userId);
        return resp;
    }


    /**
     * 查询会员过期时间
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/membershipListQry.do")
    @ResponseBody
    public CheckSmsCodeResp membershipListQry(Integer userId) {
        CheckSmsCodeResp resp = tUserinfoService.membershipListQry(userId);
        return resp;
    }


    /**
     * 查询用户的历史订单
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orderHistoryQry.do")
    @ResponseBody
    public OrderHistoryResp orderHistoryQry(Integer userId) {
        //查询已支付的订单，包含会员续费的、送秘书的订单，即有无激活码的订单
        OrderHistoryResp resp = tOrderService.orderHistoryQry(userId);
        return resp;
    }


    /**
     * 添加用户改进建议
     *
     * @param userId
     * @param sugFuntion
     * @param sugCraft
     * @param sugOther
     * @return
     */
    @RequestMapping(value = "/suggestionAdd.do")
    @ResponseBody
    public CheckSmsCodeResp suggestionAdd(Integer userId,
                                          String sugFuntion,
                                          String sugCraft,
                                          String sugOther){
        Suggestion suggestion = new Suggestion();
        suggestion.setUserId(userId);
        suggestion.setSugFuntion(sugFuntion);
        suggestion.setSugCraft(sugCraft);
        suggestion.setSugOther(sugOther);
        CheckSmsCodeResp resp = tSuggestionService.suggestionAdd(suggestion);

        return resp;
    }


    /**
     * 添加用户标签
     *
     * @param userId
     * @param phone
     * @param tagId
     * @param tagName
     * @param type
     * @return
     */
    @RequestMapping(value = "/userTagAdd.do")
    @ResponseBody
    public CheckSmsCodeResp userTagAdd(Integer userId,
                                       String phone,
                                       Integer tagId,
                                       String tagName,
                                       String type,
                                       String friendName) {
        UserTag tUserTag = new UserTag();
        tUserTag.setUserId(userId);
        tUserTag.setPhone(phone);
        tUserTag.setTagId(tagId);
        tUserTag.setTagName(tagName);
        tUserTag.setType(type);
        CheckSmsCodeResp resp = userTagService.userTagAdd(tUserTag,friendName);
        return resp;
    }


    /**
     * 添加群标签
     *
     * @param userId
     * @param phones
     * @param tagId
     * @param tagName
     * @param type
     * @return
     */
    @RequestMapping(value = "/groupTagAdd.do")
    @ResponseBody
    public CheckSmsCodeResp groupTagAdd(Integer userId,
                                        String phones,
                                        Integer tagId,
                                        String tagName,
                                        String type,
                                        String friendName) {
        /*//初始化好友姓名（默认为电话号码）
        String name = phones;
        TBook book = tBookService.selectByPhoneAndOpenid(phones, openid);
        if (!StringUtils.isEmpty(book)) {
            //db有，则为原来的姓名
            name = book.getFriendName();
        }*/
        GroupTag groupTag = new GroupTag();
        groupTag.setUserId(userId);
        groupTag.setPhones(phones);
        groupTag.setTagId(tagId);
        groupTag.setTagName(tagName);
        groupTag.setType(type);

        return groupTagService.groupTagAdd(groupTag,friendName);
    }


    /**
     * 查询用户标签
     *
     * @param userId
     * @param tagName
     * @return
     */
    @RequestMapping(value = "/userTagQry.do")
    @ResponseBody
    public List<UserTagDto> userTagQry(Integer userId, String tagName) {
        return userTagService.userTagQry(userId,tagName);
    }


    /**
     * 查询群标签
     *
     * @param userId
     * @param tagName
     * @return
     */
    @RequestMapping(value = "/groupTagQry.do")
    @ResponseBody
    public List<GroupTagDto> groupTagQry(Integer userId, String tagName) {
        return groupTagService.groupTagQry(userId,tagName);
    }


    /**
     * 查询好友下的多个号码
     *
     * @param userId
     * @param tagName
     * @return
     */
    @RequestMapping(value = "/friendPhonesQry.do")
    @ResponseBody
    public List<FriendPhonesDto> friendPhonesQry(Integer userId, String tagName, String friendName) {
        return groupTagService.friendPhonesQry(userId,tagName,friendName);
    }


    /**
     * 删除好友的号码
     *
     * @param userId
     * @param friendName
     * @return
     */
    @RequestMapping(value = "/friendPhonesDel.do")
    @ResponseBody
    public CheckSmsCodeResp friendPhonesDel(Integer userId, String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = bookMapper.delByUserIdAndFriendName(userId, friendName);
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
     * @param userId
     * @param tagId
     * @param type
     * @return
     */
    @RequestMapping(value = "/friendPhonesAdd.do")
    @ResponseBody
    public CheckSmsCodeResp friendPhonesDel(String friendName,
//                                            String tagName,
                                            String[] phones,
                                            Integer userId,
                                            Integer tagId,
                                            String type) {
        return groupTagService.friendPhonesAdd(friendName,phones,userId,tagId,type);
    }


    /**
     * 批量编辑好友号码
     *
     * @param friendName 原好友姓名
     * @param phones
     * @param userId
     * @return
     */
    @RequestMapping(value = "/friendPhonesUpd.do")
    @ResponseBody
    public CheckSmsCodeResp friendPhonesUpd(Integer userId,
//                                            String tagName,
                                            String friendName,
                                            String desName,
                                            String[] phones,
                                            Integer tagId,
                                            String type) {
        return groupTagService.friendPhonesUpd(userId,friendName,desName,phones,tagId,type);
    }


    /**
     * 解除当前用户的手机号
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/relieveUserPhone.do")
    @ResponseBody
    public CheckSmsCodeResp relieveUserPhone(Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserinfo tUserinfo = tUserinfoService.selectByUserId(userId);
        if (StringUtils.isEmpty(tUserinfo)) {
            resp.setRetCode(1);
            resp.setRetDesc("此用户不存在");
            return resp;
        }
        return tUserinfoService.relieveUserPhone(userId);
    }


    /**
     * 删除群标签
     *
     * @param userId
     * @param phone
     * @return
     */
    @RequestMapping(value = "/groupTagDel.do")
    @ResponseBody
    public CheckSmsCodeResp groupTagDel(Integer userId, String phone) {
        return groupTagService.groupTagDel(userId,phone);
    }


    /**
     * 删除用户标签
     *
     * @param userId
     * @param phone
     * @return
     */
    @RequestMapping(value = "/userTagDel.do")
    @ResponseBody
    public CheckSmsCodeResp userTagDel(Integer userId, String phone) {
        return userTagService.userTagDel(userId,phone);
    }


    /**
     * 查询单个问题下的答案
     *
     * @param knowledgeId
     * @return
     */
    @RequestMapping(value = "/answersListQry.do")
    @ResponseBody
    public List<KnowledgeAnswerResp> answerListQry(String knowledgeId, Integer userId) {
        return customerrespService.answerListQry(knowledgeId,userId);
    }


    /**
     * 查询所有问题
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/questionListQry.do")
    @ResponseBody
    public List<QuestionDto> questionListQry(Integer userId) {
        return customerrespService.questionListQry(userId);
    }


    /**
     * 新增快捷自定义回复
     *
     * @param userId
     * @param knowledgeId
     * @param content
     * @return
     */
    @RequestMapping(value = "/customerRespAdd.do")
    @ResponseBody
    public CheckSmsCodeResp customerRespAdd(Integer userId, String knowledgeId, String content) {
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
        Customresp tCustomresp = new Customresp();
        tCustomresp.setUserId(userId);
        tCustomresp.setKnowledgeId(knowledgeId);
        tCustomresp.setAnsContent(content);
        tCustomresp.setFileName(fileName);

        return customerrespService.customerRespAdd(tCustomresp);
    }


    /**
     * 查询所有下级代理
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/agentAllListQry.do")
    @ResponseBody
    public List<AgentsDto> agentAllListQry(Integer userId) {
        return tUserinfoService.agentAllListQry(userId);
    }


    /**
     * 查询赚取奖励金
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/reward.do")
    @ResponseBody
    public RewardDto reward(Integer userId) {
        return tUserinfoService.getReward(userId);
    }


    /**
     * 绑定关系（老人防诈骗）
     *
     * @param userId
     * @param phone
     * @return
     */
    @RequestMapping(value = "/relationBinding.do")
    @ResponseBody
    public CheckSmsCodeResp relationBinding(Integer userId, String phone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        //查询被绑定人的基本信息
        TUserinfo tUserinfo = tUserinfoService.selectByPhoneNumber(phone);
        if (StringUtils.isEmpty(tUserinfo)) {
            resp.setRetCode(1);
            resp.setRetDesc("手机号码填写错误 或没关注小兵秘书公众号");
            return resp;
        }
        //判断是否已经绑定
        Relation tRelation = relationService.selectByUseridAndPhone(userId, tUserinfo.getId());
        if (!StringUtils.isEmpty(tRelation)) {
            resp.setRetCode(1);
            resp.setRetDesc("已经绑定过了，无法重复绑定");
            return resp;
        }

        return relationService.relationBinding(userId,tUserinfo.getId());
    }


    /**
     * 查询已绑定关系的用户
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/relationBindingQry.do")
    @ResponseBody
    public List<UserRelationDto> relationBindingQry(Integer userId) {

        return relationService.relationBindingQry(userId);
    }


    /**
     * 查询绑定自己的人的信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/bindingOwnUserQry.do")
    @ResponseBody
    public List<BindingOwnUser> bindingOwnUserQry(Integer userId) {

        return relationService.bindingOwnUserQry(userId);
    }

    /**
     * 设置应答的回答
     *
     * @param knowledgeId
     * @param userId
     * @param answerId
     * @return
     */
    @RequestMapping(value = "/userQaSet.do")
    @ResponseBody
    public CheckSmsCodeResp userQaSet(String knowledgeId, Integer userId, Long answerId) {
        UserQa tUserQa = new UserQa();
        tUserQa.setKnowledgeId(knowledgeId);
        tUserQa.setUserId(userId);
        tUserQa.setAnswerId(answerId);
        return userQaService.addCustomerresp(tUserQa);
    }

    /**
     * 新增号码类型
     *
     * @param userId
     * @param phone
     * @param type
     * @return
     */
    @RequestMapping(value = "/phoneTypeAdd.do")
    @ResponseBody
    public CheckSmsCodeResp phoneTypeAdd(Integer userId, String phone, Integer type) {
        Phonetype tPhonetype = new Phonetype();
        tPhonetype.setUserId(userId);
        tPhonetype.setPhone(phone);
        tPhonetype.setType(type);

        return phonetypeService.phoneTypeAdd(tPhonetype);
    }

    /**
     * 查询号码类型
     *
     * @param userId
     * @param type
     * @return
     */
    @RequestMapping(value = "/phoneTypeQry.do")
    @ResponseBody
    public List<Phonetype> phoneTypeQry(Integer userId, String type) {

        return phonetypeService.phoneTypeQry(userId,type);
    }

    /**
     * 查询用户感兴趣的事项
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userIntrestingQry.do")
    @ResponseBody
    public List<TMall> userIntrestingQry(Integer userId) {
        return tMallService.userIntrestingQry(userId);
    }

    /**
     * 设置紧急联系人号码
     *
     * @param userId
     * @param sosPhone
     * @return
     */
    @RequestMapping(value = "/sosPhoneSet.do")
    @ResponseBody
    public CheckSmsCodeResp sosPhoneSet(Integer userId, String sosPhone) {
        return tUserinfoService.sosPhoneSet(userId,sosPhone);
    }

    /**
     * 查询紧急联系人号码
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/sosPhoneQry.do")
    @ResponseBody
    public List<String> sosPhoneQry(Integer userId) {
        return tUserinfoService.sosPhoneQry(userId);
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
}
