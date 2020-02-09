package com.pl.mapper;
import com.pl.model.wx.TUserinfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TUserinfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TUserinfo record);

    int insertSelective(TUserinfo record);

    TUserinfo selectByPrimaryKey(String openid);

    int updateByPrimaryKeySelective(int id);

    int updateByPrimaryKey(@Param("id") Integer id,
                           @Param("phoneNumber")String phoneNumber,
                           @Param("recommenderId") Integer recommenderId,
                           @Param("sonIds") String sonIds);

    TUserinfo selectUserByPhoneNumber(String phoneNumber);

    TUserinfo selectUserByOpenId(String openid);

    TUserinfo selectOpenidByUserId(Integer userId);

    TUserinfo selectUserIdByOpenId(String openId);

    int updateMyStatus(@Param("userId")Integer userId, @Param("id")Integer id);

    int cancelMyStatus(@Param("userId")Integer userId,@Param("id")Integer id);

    List<TUserinfo> selectByRecommenderId(Integer recommenderId);

    List<TUserinfo> selectBySonId(Integer userId);

    int updateMyVoice(@Param("userId")Integer userId, @Param("voiceId")Integer voiceId);

    int cancelMyVoice(@Param("userId")Integer userId, @Param("voiceId")Integer voiceId);

    int cancelAndUpdateMyStatus(@Param("userId")Integer userId, @Param("id")Integer id);

    void updateMemberInfo(@Param("openid")String openid,@Param("useDays")Integer useDays);

    void updateMemberinfo(@Param("openid")String openid,@Param("useDays")Integer useDays);

    void updateEmpByOpenid(@Param("recommenderId")Integer recommenderId,
                           @Param("sonId")String sonId,
                           @Param("empNum")String empNum,
                           @Param("openid")String openid);

    TUserinfo selectByCode(String code);

    void updateIdeByOpenid(String openId);

    List<TUserinfo> selectMembershipExpireTime();

    void updateByExpireTime(Date expireTime);

    TUserinfo selectByPhone(String phone);

    int updatePhoneByOpenid(String openid);

    List<TUserinfo> selectByRelationOpenId(String openid);

    void updateMemberinfoAdd(@Param("openid")String openid, @Param("useDays")Integer useDays);

    void updateMemberInfoAdd(String openid, Integer useDays);

    int updateSosPhoneByOpenid(@Param("openid")String openid, @Param("sosPhone")String sosPhone);

    void addUser(TUserinfo user);

    List<TUserinfo> selectByRecommenderid(Integer id);

    List<TUserinfo> selectByPhoneIsNull();

    void updateRegisterStatus(String openid);

    List<TUserinfo> selectByPhoneIsnotNull();

    TUserinfo selectByUnionid(String unionid);

    void setPhoneByUnionid(@Param("phone")String phone, @Param("unionid")String unionid);

    void updateMemberinfoByUnionid(@Param("unionid")String unionid, @Param("useDays")Integer useDays);

    void updateMemberInfoByUnionid(@Param("unionid")String unionid, @Param("useDays")Integer useDays);

    void updateMemberinfoAddByUnionid(@Param("unionid")String unionid, @Param("useDays")Integer useDays);

    void updateMemberInfoAddByUnionid(@Param("unionid")String unionid, @Param("useDays")Integer useDays);

    TUserinfo selectByUserId(int userid);

    TUserinfo selectByPhoneNumber(String phoneNumber);

    int updateUser(@Param("id") Integer id,
                   @Param("phoneNumber")String phoneNumber,
                   @Param("recommenderId") Integer recommenderId,
                   @Param("sonIds") String sonIds);

    List<TUserinfo> selectByRelationUserId(Integer userId);

    void updateMemberday(@Param("userId")Integer userId, @Param("useDays")Integer useDays);

    void updateMemberDay(@Param("userId")Integer userId, @Param("useDays")Integer useDays);

    void updateMemberdayAdd(@Param("userId")Integer userId, @Param("useDays")Integer useDays);

    void updateMemberDayAdd(@Param("userId")Integer userId, @Param("useDays")Integer useDays);

    TUserinfo selectUserByCode(String code);

    void updateEmpnumByUserId(@Param("id")Integer id,
                              @Param("sonId")String sonId,
                              @Param("empNum")String empNum,
                              @Param("userId")Integer userId);

    int updatePhoneByUserId(Integer userId);

    List<TUserinfo> selectAllAgentBySonId(Integer userId);

    List<TUserinfo> selectByRecommender(Integer userId);

    int updateSosPhoneByUserid(@Param("userId")Integer userId, @Param("sosPhone")String sosPhone);

    int updateStatus(@Param("userId")Integer userId, @Param("id")Integer id);

    int cancelStatus(@Param("userId")Integer userId,@Param("id")Integer id);

    int cancelAndUpdateStatus(@Param("userId")Integer userId,@Param("id")Integer id);

    int updateVoice(@Param("userId")Integer userId, @Param("voiceId")Integer voiceId);

    int cancelVoice(@Param("userId")Integer userId, @Param("voiceId")Integer voiceId);
}