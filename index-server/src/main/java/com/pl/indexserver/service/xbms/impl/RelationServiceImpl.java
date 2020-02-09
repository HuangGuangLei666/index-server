package com.pl.indexserver.service.xbms.impl;

import com.alibaba.fastjson.JSONObject;
import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.xbms.RelationService;
import com.pl.indexserver.untils.HttpUtil;
import com.pl.mapper.TUserinfoMapper;
import com.pl.mapper.xbms.BookMapper;
import com.pl.mapper.xbms.RelationMapper;
import com.pl.model.wx.*;
import com.pl.model.xbms.Book;
import com.pl.model.xbms.Relation;
import com.pl.thirdparty.dto.request.TemplateParamRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 14:25
 * @Description
 */
@Service
public class RelationServiceImpl implements RelationService {

    private static final Logger logger = LoggerFactory.getLogger(RelationServiceImpl.class);

    @Autowired
    private RelationMapper relationMapper;
    @Autowired
    private TUserinfoMapper tUserinfoMapper;

    @Override
    public CheckSmsCodeResp relationBinding(Integer userId, Integer relationId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        Relation relation = new Relation();
        relation.setUserId(userId);
        relation.setUserIdRelation(relationId);
        int i = relationMapper.addRelationBinding(relation);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("绑定关系失败");
            return resp;
        }

        //TODO给被绑定者推送绑定申请通知
        /*//给被绑定者推送绑定申请通知
        String bindingPush = bindingPush(tUserinfo.getOpenid(), userinfo.getPhonenumber());
        logger.info("=========bindingPush={}==========", bindingPush);*/

        resp.setRetCode(0);
        resp.setRetDesc("绑定关系成功");
        return resp;
    }

    @Override
    public Relation selectByUseridAndPhone(Integer userId, Integer relationId) {
        return relationMapper.selectByUseridAndPhone(userId,relationId);
    }

    @Override
    public List<UserRelationDto> relationBindingQry(Integer userId) {
        List<UserRelationDto> tUserinfoList = new ArrayList<>();
        List<TUserinfo> tUserinfos = tUserinfoMapper.selectByRelationUserId(userId);
        logger.info("=========tUserinfos={}=========", tUserinfos.size());
        if (CollectionUtils.isEmpty(tUserinfos)) {
            return tUserinfoList;
        }
        for (TUserinfo tUserinfo : tUserinfos) {
            Relation relation = relationMapper.selectByUserid(userId, tUserinfo.getId());
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

    @Override
    public List<BindingOwnUser> bindingOwnUserQry(Integer userId) {
        List<BindingOwnUser> bindingOwnUsers = new ArrayList<>();
        List<Relation> tRelationList = relationMapper.selectByRelationUserid(userId);
        if (CollectionUtils.isEmpty(tRelationList)) {
            return bindingOwnUsers;
        }
        for (Relation relation : tRelationList) {
            Integer id = relation.getUserId();
            TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(id);
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

    @Override
    public CheckSmsCodeResp relationBindingDel(Integer id) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = relationMapper.deleterelationBinding(id);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("解除绑定关系失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("解除绑定关系成功");
        return resp;
    }

    @Override
    public CheckSmsCodeResp passBindings(Integer relationId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = relationMapper.updatePassByRelationId(relationId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("绑定失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("通过绑定");
        return resp;
    }

    @Override
    public CheckSmsCodeResp refuseBindings(Integer relationId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = relationMapper.updateRefuseByRelationId(relationId);
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
     * @param openid 被绑定者的openid
     * @param phone  申请人的手机号
     * @return
     */
    /*public String bindingPush(String openid, String phone) {
        //传入的openid为被绑定者的openid，phone为申请人的手机号
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //根据openid查询用户基本信息
        logger.info("========推送号码绑定已启动========");
        //查询申请人的基本信息
        TUserinfo tUserinfo = tUserinfoMapper.selectByPhoneNumber(phone);
        //查询申请人在被绑定者通讯录中的备注名
        Book tBook = bookMapper.selectByPhoneAndOpenid(phone, openid);
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
    }*/
}
