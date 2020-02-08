package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TQctivationcodeService;
import com.pl.indexserver.web.XbmsController;
import com.pl.mapper.TMealMapper;
import com.pl.mapper.TQctivationcodeMapper;
import com.pl.mapper.TUserinfoMapper;
import com.pl.model.wx.TMeal;
import com.pl.model.wx.TQctivationcode;
import com.pl.model.wx.TUserinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/10
 */
@Service
public class TQctivationcodeServiceImpl implements TQctivationcodeService {

    private static final Logger logger = LoggerFactory.getLogger(TQctivationcodeServiceImpl.class);

    @Autowired
    private TQctivationcodeMapper tQctivationcodeMapper;
    @Autowired
    private TUserinfoMapper tUserinfoMapper;
    @Autowired
    private TMealMapper tMealMapper;


    @Override
    public void addTQctivationcode(TQctivationcode tQctivationcode) {
        tQctivationcodeMapper.addTQctivationcode(tQctivationcode);
    }

    @Override
    public TQctivationcode selectByCode(String activationCode) {
        return tQctivationcodeMapper.selectByCode(activationCode);
    }

    @Override
    public int openMembershipByCode(String code, Integer userId) {
        return tQctivationcodeMapper.openMembershipByCode(code,userId);
    }

    @Override
    public TQctivationcode selectByTradeNo(String ordersSn) {
        return tQctivationcodeMapper.selectByTradeNo(ordersSn);
    }

    @Override
    public List<TQctivationcode> selectByUserIdAndStatus(Integer userId) {
        return tQctivationcodeMapper.selectByUserIdAndStatus(userId);
    }

    @Override
    public CheckSmsCodeResp openMemberByCode(String code, Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(userId);
        TQctivationcode tQctivationcode = tQctivationcodeMapper.selectQctivationByCode(code);
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
        TMeal tMeal = tMealMapper.selectByCodeAndMealId(code);
        String tMealType = tMeal.getType();
        Integer useDays = tMeal.getUseDays();
        logger.info("========tMealName={}==========", tMealType);

        int i = tQctivationcodeMapper.updateMembershipByCode(code, tUserinfo.getId());
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("开通会员失败");
            return resp;
        }

        if (StringUtils.isEmpty(tUserinfo.getIsMembership())) {
            //之前不是会员
            if ("私人秘书".equals(tMeal.getType())) {
                tUserinfoMapper.updateMemberday(userId, useDays);
            }
            if ("私人定制".equals(tMeal.getType())) {
                tUserinfoMapper.updateMemberDay(userId, useDays);
            }
        } else {
            //之前是会员
            if ("私人秘书".equals(tMeal.getType())) {
                tUserinfoMapper.updateMemberdayAdd(userId, useDays);
            }
            if ("私人定制".equals(tMeal.getType())) {
                tUserinfoMapper.updateMemberDayAdd(userId, useDays);
            }
        }

        resp.setRetCode(0);
        resp.setRetDesc("开通会员成功");
        return resp;
    }
}
