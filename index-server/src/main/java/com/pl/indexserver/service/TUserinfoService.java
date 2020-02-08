package com.pl.indexserver.service;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.AgentsDto;
import com.pl.model.wx.IsAgentResp;
import com.pl.model.wx.RewardDto;
import com.pl.model.wx.TUserinfo;

import java.util.Date;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/9/25
 */
public interface TUserinfoService {
    TUserinfo selectOpenidByUserId(Integer userId);

    TUserinfo selectUserIdByOpenId(String openId);

    int updateMyStatus(Integer userId, Integer id);

    int cancelMyStatus(Integer userId, Integer id);

    List<TUserinfo> selectByRecommenderId(Integer recommenderId);

    List<TUserinfo> selectBySonId(Integer userId);

    int updateMyVoice(Integer userId, Integer voiceId);

    int cancelMyVoice(Integer userId, Integer voiceId);

    int cancelAndUpdateMyStatus(Integer userId, Integer id);

    void insert(TUserinfo user);

    void updateMemberInfo(String openid,Integer useDays);

    void updateMemberinfo(String openid,Integer useDays);

    void updateEmpByOpenid(Integer recommenderId,String sonId,String empNum,String openid);

    TUserinfo selectByCode(String code);

    void updateIdeByOpenid(String openId);

    List<TUserinfo> selectMembershipExpireTime();

    void updateByExpireTime(Date expireTime);

    TUserinfo selectByPhone(String taskId);

    int updatePhoneByOpenid(String openid);

    List<TUserinfo> selectByRelationOpenId(String openid);

    void updateMemberinfoAdd(String openid, Integer useDays);

    void updateMemberInfoAdd(String openid, Integer useDays);

    int updateSosPhoneByOpenid(String openid, String sosPhone);

    void addUser(TUserinfo user);

    List<TUserinfo> selectByRecommenderid(Integer id);

    List<TUserinfo> selectByPhoneIsNull();

    void updateRegisterStatus(String openid);

    List<TUserinfo> selectByPhoneIsnotNull();

    TUserinfo selectByUnionid(String unionid);

    void updateMemberinfoByUnionid(String unionid, Integer useDays);

    void updateMemberInfoByUnionid(String unionid, Integer useDays);

    void updateMemberinfoAddByUnionid(String unionid, Integer useDays);

    void updateMemberInfoAddByUnionid(String unionid, Integer useDays);

    TUserinfo selectByUserId(int userid);

    CheckSmsCodeResp agentAdd(String code, String empNum, Integer userId);

    IsAgentResp isAgentQry(Integer userId);

    CheckSmsCodeResp membershipListQry(Integer userId);

    CheckSmsCodeResp relieveUserPhone(Integer userId);

    List<AgentsDto> agentAllListQry(Integer userId);

    RewardDto getReward(Integer userId);

    TUserinfo selectByPhoneNumber(String phone);

    CheckSmsCodeResp sosPhoneSet(Integer userId, String sosPhone);

    List<String> sosPhoneQry(Integer userId);
}
