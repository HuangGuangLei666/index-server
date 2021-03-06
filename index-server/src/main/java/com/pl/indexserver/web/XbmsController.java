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
import com.pl.indexserver.untils.AsrBaidu;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private static int seq = (int) (System.currentTimeMillis() % 100000000);

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
    @Autowired
    private TVoiceService tVoiceService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private FileTransferService fileTransferService;


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
     * 查询用户自定义开场白类型
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/prologueTypeQry.do")
    @ResponseBody
    public PrologueDto prologueTypeQry(Integer userId) {
        return tSetService.prologueTypeQry(userId);
    }


    /**
     * 删除用户自己录音的开场白
     *
     * @param id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/prologueDel.do")
    @ResponseBody
    public CheckSmsCodeResp prologueDel(Integer id,Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        //查询是否此用户的开场白
        TSet set = tSetService.selectById(id);
        if (!userId.equals(set.getUserId())){
            resp.setRetCode(1);
            resp.setRetDesc("此录音不属于你，无法删除~~~");
            return resp;
        }
        //录音文件名
        String fileName = set.getFileName();
        return tSetService.prologueDel(id,userId,fileName);
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
        Page<DialogDto> page = tDialogService.getAnswerListPageQry(pageIndex, pageNum, userId);

        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setCode(0);
        returnMsg.setContent(page);
        return returnMsg;
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
     * @param phones
     * @param userId
     * @param tagId
     * @param type
     * @return
     */
    @RequestMapping(value = "/friendPhonesAdd.do")
    @ResponseBody
    public CheckSmsCodeResp friendPhonesDel(String friendName,
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
     * 根据号码获取用户基本信息
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/getUserInfo.do")
    @ResponseBody
    public TUserinfo getUserInfo(String phone) {
        TUserinfo tUserinfo = tUserinfoService.selectByPhoneNumber(phone);
        if (StringUtils.isEmpty(tUserinfo)) {
            return new TUserinfo();
        }
        return tUserinfo;
    }

    /**
     * 勾选我感兴趣的标签
     *
     * @param id 标签id
     * @param userId 用户id
     * @return
     */
    @RequestMapping(value = "/insertLabel.do")
    @ResponseBody
    public CheckSmsCodeResp insertlabel(Integer id, Integer userId) {

        return tMallService.insertlabels(id,userId);
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
        return tMallService.deleteLabels(id,userId);
    }

    /**
     * 设置自定义开场白
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/set.do")
    @ResponseBody
    public CheckSmsCodeResp set(HttpServletRequest request) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        String userId = request.getParameter("userId");
        String content = request.getParameter("content");
        if (!StringUtils.isEmpty(content)) {
            try {
                content = java.net.URLDecoder.decode(content, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

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

        TSet set = new TSet();
        set.setUserId(Integer.parseInt(userId));
        //type为2是tts合成的开场白
        set.setType("2");
        set.setValue("99990");
        set.setVoiceId(666666);
        set.setOperationId(temp);
        set.setContent(content);
        set.setFileName(fileName);
        return tSetService.insertSet(set);
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
     * 语音识别开场白录音
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/baiduAsrMyRecordings.do")
    @ResponseBody
    public String baiduAsrMyRecordings(@RequestParam("file") MultipartFile file) {
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            try {
                ins = file.getInputStream();
                toFile = new File(file.getOriginalFilename());
                //MultipartFile转化为File
                inputStreamToFile(ins, toFile);
                byte[] audioDatas = AsrBaidu.loadFile(toFile);
                String asr = AsrBaidu.asr(audioDatas, true);
                logger.info("========asr={}============",asr);
                ins.close();
                return asr;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "无效录音";
    }

    //MultipartFile转化为File
    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 上传开场白录音文件到服务器
     * @param file
     * @param userId
     */
    @RequestMapping(value = "/uploadMyRecording.do")
    @ResponseBody
    public Integer uploadMyRecording(@RequestParam("file") MultipartFile file,Integer userId) {
        //用百度asr识别出录音的内容
        String recordContent = baiduAsrMyRecordings(file);
        //上传到服务器的路径/mnt/tm/2122/BUSINESS-134680578/
        String filePath = "2122/BUSINESS-134680578/own_recordings/" + userId;
        logger.info("=======filePath={}===========",filePath);
        //存到db和服务器的录音文件名
        String fileName = "";
        InputStream is = null;
        //时间戳
        Integer temp = seq++;
        //上传录音文件
        try {
            fileName = MD5.MD5_32bit(recordContent) + ".wav";
            is = file.getInputStream();
            boolean flag = fileTransferService.uploadFileToFTP(filePath, fileName, is);

            TSet set = new TSet();
            set.setUserId(userId);
            //type为1是自己录音的开场白
            set.setType("1");
            set.setValue("99990");
            set.setVoiceId(666666);
            set.setOperationId(temp);
            set.setContent(recordContent);
            set.setFileName(fileName);
            tSetService.insertSet(set);
            return tSetService.prologueIdQry(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        return tUserinfoService.myStatusChange(userId,id,isCheck);
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
        return tUserinfoService.myVoiceChange(userId,voiceId,isCheck);
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
        return tMallService.myStatusQry(userId);
    }

    /**
     * 声音列表查询
     *
     * @return
     */
    @RequestMapping(value = "/voicesListQry.do")
    @ResponseBody
    public List<Voices> voicesListQry() {
        List<Voices> voicesList = tVoiceService.selectVoices();
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
    public Voices myIscheckedVoiceQry(Integer userId) {
        return tVoiceService.selectMyVoiceByUserid(userId);
    }

    /**
     * 送秘书支付套餐ui
     * @return
     */
    @RequestMapping(value = "/sendMealListQry.do")
    @ResponseBody
    public List<TMeal> sendMealListQry() {
        return tMealService.selectMealsList();
    }

    /**
     * 查询群分类列表
     *
     * @return
     */
    @RequestMapping(value = "/groupClassQry.do")
    @ResponseBody
    public List<TTagGroup> groupClassQry() {
        List<TTagGroup> tTagGroupList = groupTagService.selectGroupClassList();
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
        List<TTag> tTagList = userTagService.selectuserTagList();
        if (CollectionUtils.isEmpty(tTagList)) {
            return new ArrayList<>();
        }
        return tTagList;
    }

    /**
     * 删除快捷自定义回复
     *
     * @return
     */
    @RequestMapping(value = "/customerRespDel.do")
    @ResponseBody
    public CheckSmsCodeResp customerRespDel(Integer id) {
        return customerrespService.customerRespDel(id);
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
        return relationService.relationBindingDel(id);
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
        return relationService.passBindings(relationId);
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
        return relationService.refuseBindings(relationId);
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
        return phonetypeService.phoneTypeDel(id);
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
        return tUserinfoService.getUserByUnionid(unionid);
    }

    /**
     * 小兵秘书支付接口（开通会员通用版）
     * @param request
     * @param userId 用户id
     * @param totalFee 支付金额
     * @param goodsId 商品id
     * @param unionid 用户unionid
     * @return
     */
    @RequestMapping(value = "/openMembershipPay.do", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> openMembershipPay(HttpServletRequest request, Integer userId, String totalFee, Integer goodsId, String unionid) {
        //传进来的totalFee查数据库判断一下，不匹配就付款失败
        //查询此用户是否是新用户
        List<TOrder> tOrderList = tOrderService.selectByUseridAndStatus(userId);
        TMeal tMeal = tMealService.selectBygoodsId(goodsId);
        if (!totalFee.equals(tMeal.getPrice()) && !"0.1".equals(totalFee)) {
            return new HashMap<>();
        }
        if ("0.1".equals(totalFee)) {
            if (!CollectionUtils.isEmpty(tOrderList)) {
                return new HashMap<>();
            }
        }

        return wxPayService.openMembershipPay(request,totalFee,goodsId,unionid);
    }

    /**
     * 小兵秘书回调接口（开通会员通用版）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/openMembershipPayCallback.do")
    @ResponseBody
    public void openMembershipPayCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        wxPayService.openMembershipPayCallback(request,response);
    }

    /**
     * 小兵秘书支付接口（送秘书通用版）
     * @param request
     * @param userId 用户的id
     * @param goodsId
     * @param unionid
     * @return
     */
    @RequestMapping(value = "/giveSecretaryPay.do", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> giveSecretaryPay(HttpServletRequest request,Integer userId,Integer goodsId,String unionid) {
        return wxPayService.giveSecretaryPay(request,userId,goodsId,unionid);
    }

    /**
     * 小兵秘书回调接口（送秘书通用版）
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/giveSecretaryPayCallback.do")
    @ResponseBody
    public TQctivationcode giveSecretaryPayCallback(HttpServletRequest request, HttpServletResponse response) {
        return wxPayService.giveSecretaryPayCallback(request,response);
    }

}
