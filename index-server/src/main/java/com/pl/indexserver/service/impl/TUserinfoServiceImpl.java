package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TUserinfoService;
import com.pl.mapper.TOrderMapper;
import com.pl.mapper.TQctivationcodeMapper;
import com.pl.mapper.TUserinfoMapper;
import com.pl.mapper.xbms.MechanismMapper;
import com.pl.model.wx.*;
import com.pl.model.xbms.Mechanism;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
@Service
public class TUserinfoServiceImpl implements TUserinfoService {

    private static final Logger logger = LoggerFactory.getLogger(TUserinfoServiceImpl.class);

    @Autowired
    private TUserinfoMapper tUserinfoMapper;
    @Autowired
    private MechanismMapper mechanismMapper;
    @Autowired
    private TQctivationcodeMapper tQctivationcodeMapper;
    @Autowired
    private TOrderMapper tOrderMapper;

    @Override
    public TUserinfo selectOpenidByUserId(Integer userId) {
        return tUserinfoMapper.selectOpenidByUserId(userId);
    }

    @Override
    public TUserinfo selectUserIdByOpenId(String openId) {
        return tUserinfoMapper.selectUserIdByOpenId(openId);
    }

    @Override
    public int updateMyStatus(Integer userId, Integer id) {
        return tUserinfoMapper.updateMyStatus(userId,id);
    }

    @Override
    public int cancelMyStatus(Integer userId, Integer id) {
        return tUserinfoMapper.cancelMyStatus(userId,id);
    }

    @Override
    public List<TUserinfo> selectByRecommenderId(Integer recommenderId) {
        return tUserinfoMapper.selectByRecommenderId(recommenderId);
    }

    @Override
    public List<TUserinfo> selectBySonId(Integer userId) {
        return tUserinfoMapper.selectBySonId(userId);
    }

    @Override
    public int updateMyVoice(Integer userId, Integer voiceId) {
        return tUserinfoMapper.updateMyVoice(userId,voiceId);
    }

    @Override
    public int cancelMyVoice(Integer userId, Integer voiceId) {
        return tUserinfoMapper.cancelMyVoice(userId,voiceId);
    }

    @Override
    public int cancelAndUpdateMyStatus(Integer userId, Integer id) {
        return tUserinfoMapper.cancelAndUpdateMyStatus(userId,id);
    }

    @Override
    public void insert(TUserinfo user) {
        tUserinfoMapper.insertSelective(user);
    }

    @Override
    public void updateMemberInfo(String openid,Integer useDays) {
        tUserinfoMapper.updateMemberInfo(openid,useDays);
    }

    @Override
    public void updateMemberinfo(String openid,Integer useDays) {
        tUserinfoMapper.updateMemberinfo(openid, useDays);
    }

    @Override
    public void updateEmpByOpenid(Integer recommenderId,String sonId,String empNum,String openid) {
        tUserinfoMapper.updateEmpByOpenid(recommenderId,sonId,empNum,openid);
    }

    @Override
    public TUserinfo selectByCode(String code) {
        return tUserinfoMapper.selectByCode(code);
    }

    @Override
    public void updateIdeByOpenid(String openId) {
        tUserinfoMapper.updateIdeByOpenid(openId);
    }

    @Override
    public List<TUserinfo> selectMembershipExpireTime() {
        return tUserinfoMapper.selectMembershipExpireTime();
    }

    @Override
    public void updateByExpireTime(Date expireTime) {
        tUserinfoMapper.updateByExpireTime(expireTime);
    }

    @Override
    public TUserinfo selectByPhone(String phone) {
        return tUserinfoMapper.selectByPhone(phone);
    }

    @Override
    public int updatePhoneByOpenid(String openid) {
        return tUserinfoMapper.updatePhoneByOpenid(openid);
    }

    @Override
    public List<TUserinfo> selectByRelationOpenId(String openid) {
        return tUserinfoMapper.selectByRelationOpenId(openid);
    }

    @Override
    public void updateMemberinfoAdd(String openid, Integer useDays) {
        tUserinfoMapper.updateMemberinfoAdd(openid,useDays);
    }

    @Override
    public void updateMemberInfoAdd(String openid, Integer useDays) {
        tUserinfoMapper.updateMemberInfoAdd(openid,useDays);
    }

    @Override
    public int updateSosPhoneByOpenid(String openid, String sosPhone) {
        return tUserinfoMapper.updateSosPhoneByOpenid(openid,sosPhone);
    }

    @Override
    public void addUser(TUserinfo user) {
        tUserinfoMapper.addUser(user);
    }

    @Override
    public List<TUserinfo> selectByRecommenderid(Integer id) {
        return tUserinfoMapper.selectByRecommenderid(id);
    }

    @Override
    public List<TUserinfo> selectByPhoneIsNull() {
        return tUserinfoMapper.selectByPhoneIsNull();
    }

    @Override
    public void updateRegisterStatus(String openid) {
        tUserinfoMapper.updateRegisterStatus(openid);
    }

    @Override
    public List<TUserinfo> selectByPhoneIsnotNull() {
        return tUserinfoMapper.selectByPhoneIsnotNull();
    }

    @Override
    public TUserinfo selectByUnionid(String unionid) {
        return tUserinfoMapper.selectByUnionid(unionid);
    }

    @Override
    public void updateMemberinfoByUnionid(String unionid, Integer useDays) {
        tUserinfoMapper.updateMemberinfoByUnionid(unionid,useDays);
    }

    @Override
    public void updateMemberInfoByUnionid(String unionid, Integer useDays) {
        tUserinfoMapper.updateMemberInfoByUnionid(unionid,useDays);
    }

    @Override
    public void updateMemberinfoAddByUnionid(String unionid, Integer useDays) {
        tUserinfoMapper.updateMemberinfoAddByUnionid(unionid,useDays);
    }

    @Override
    public void updateMemberInfoAddByUnionid(String unionid, Integer useDays) {
        tUserinfoMapper.updateMemberInfoAddByUnionid(unionid,useDays);
    }

    @Override
    public TUserinfo selectByUserId(int userid) {
        return tUserinfoMapper.selectByUserId(userid);
    }

    @Override
    public CheckSmsCodeResp agentAdd(String code, String empNum, Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(userId);
        if (!StringUtils.isEmpty(tUserinfo.getRecommenderId())) {
            resp.setRetCode(1);
            resp.setRetDesc("您已经是代理，无法申请");
            return resp;
        }

        TUserinfo userinfo = tUserinfoMapper.selectUserByCode(code);
        if (StringUtils.isEmpty(userinfo)) {
            resp.setRetCode(1);
            resp.setRetDesc("机构码不存在");
            return resp;
        }
        Integer id = userinfo.getId();
        String sonId = "-0-" + id;
        tUserinfoMapper.updateEmpnumByUserId(id, sonId, empNum, userId);

        resp.setRetCode(0);
        resp.setRetDesc("申请代理成功");
        return resp;
    }

    @Override
    public IsAgentResp isAgentQry(Integer userId) {
        IsAgentResp resp = new IsAgentResp();
        TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(userId);
        logger.info("=========tUserinfo==========" + tUserinfo);
        if (tUserinfo.getIdentity() == null) {
            resp.setOpenid(tUserinfo.getOpenid());
            resp.setNickname(tUserinfo.getNickname());
            resp.setIdentity(tUserinfo.getIdentity());
            return resp;
        }
        if (!"2".equals(tUserinfo.getIdentity().toString())) {
            logger.info("=======come in=========");
            resp.setOpenid(tUserinfo.getOpenid());
            resp.setNickname(tUserinfo.getNickname());
            resp.setIdentity(tUserinfo.getIdentity());
            return resp;
        }
        TUserinfo userinfo = tUserinfoMapper.selectByUserId(tUserinfo.getRecommenderId());

        Mechanism tMechanism = mechanismMapper.selectByUserId(userinfo.getId());

        resp.setOpenid(tUserinfo.getOpenid());
        resp.setNickname(tUserinfo.getNickname());
        resp.setIdentity(tUserinfo.getIdentity());
        resp.setOrgNum(tMechanism.getOrgNum());
        return resp;
    }

    @Override
    public CheckSmsCodeResp membershipListQry(Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(userId);
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

    @Override
    public CheckSmsCodeResp relieveUserPhone(Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tUserinfoMapper.updatePhoneByUserId(userId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("解除手机号失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("解除手机号成功");
        return resp;
    }

    @Override
    public List<AgentsDto> agentAllListQry(Integer userId) {
        int allPay = 0;
        int allAct = 0;
        //查询自己下级的数量
        List<AgentsDto> agentsDtos = new ArrayList<>();

        List<AgentsDto> agentsDtoList = new ArrayList<>();
        int payCount = 0;

        int sonPayCount = 0;
        //查询此用户信息
        TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(userId);
        if (!StringUtils.isEmpty(tUserinfo.getIsMembership())) {
            payCount = 1;
            allPay += payCount;
        }
        List<TQctivationcode> tQctivationcodes = tQctivationcodeMapper.selectByAgentIdAndStatus(tUserinfo.getId());
        allAct = tQctivationcodes.size();
//        int actCount = tQctivationcodes.size();
        //查询自己的下一级代理
        List<TUserinfo> tUserinfoList = tUserinfoMapper.selectAllAgentBySonId(userId);

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
            List<TQctivationcode> qctivationcodes = tQctivationcodeMapper.selectByAgentIdAndStatus(userinfo.getId());
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

    @Override
    public RewardDto getReward(Integer userId) {
        RewardDto rewardDto = new RewardDto();
        Integer money = 0;
        //查询自己的下一级代理
        List<TUserinfo> tUserinfoList = tUserinfoMapper.selectByRecommender(userId);
        if (CollectionUtils.isEmpty(tUserinfoList)) {
            rewardDto.setNumber(0);
            rewardDto.setMoney(0);
            return rewardDto;
        }
        for (TUserinfo userinfo : tUserinfoList) {
            Integer userid = userinfo.getId();
            //根据pay_money降序排列
            List<TOrder> tOrderList = tOrderMapper.selectByUseridAndStatus(userid);
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

    @Override
    public TUserinfo selectByPhoneNumber(String phone) {
        return tUserinfoMapper.selectByPhoneNumber(phone);
    }

    @Override
    public CheckSmsCodeResp sosPhoneSet(Integer userId, String sosPhone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = tUserinfoMapper.updateSosPhoneByUserid(userId, sosPhone);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("设置紧急联系人失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("设置紧急联系人成功");
        return resp;
    }

    @Override
    public List<String> sosPhoneQry(Integer userId) {
        List<String> list = new ArrayList<>();
        TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(userId);
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
}
