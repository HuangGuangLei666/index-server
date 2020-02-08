package com.pl.mapper;
import com.pl.model.wx.TBook;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TBookMapper {
    int deleteByPrimaryKey(String phone);

    int insert(TBook book);

    int insertSelective(TBook record);

    int updateByPrimaryKeySelective(TBook record);

    int updateByPrimaryKey(TBook record);

    int delFriendByOpenIdAndPhone(@Param("openId") String openId,@Param("phone") String phone);

    int updateFriendBookByPhone(@Param("phone")String phone, @Param("openId")String openId,
                                @Param("friendName")String friendName);

    List<TBook> qryFriendDetail(@Param("openId")String openId, @Param("phone")String phone);

    TBook qryFriendByOpenidAndPhone(@Param("openId")String openId, @Param("phone")String phone);

    List<TBook> selectFriendByOpenId(String openid);

    TBook selectFriendByPhone(String phone);

    TBook selectByPhoneAndOpenid(@Param("value")String value,
                                 @Param("openId")String openid);

    int insertNotFriendName(TBook tBook);

    TBook selectFriendByGroupTagId(Integer id);

    int updateByOpenidAndPhone(@Param("openid")String openid,
                               @Param("phones")String phones,
                               @Param("phone")String phone,
                               @Param("friendName")String friendName);

    int delByOpenIdAndFriendName(@Param("openid")String openid, @Param("friendName")String friendName);

    void updateByfriendNameAndPhone(@Param("openid")String openid,
                                    @Param("friendName")String friendName,
                                    @Param("phone")String phone,
                                    @Param("desName")String desName,
                                    @Param("desPhone")String desPhone);

    TBook selectByOpenidPhoneFriendName(@Param("openid")String openid,
                                        @Param("phone")String phone,
                                        @Param("desName")String desName);

    void deletePhonesDto(@Param("openid")String openid,
                         @Param("phone")String phone,
                         @Param("friendName")String friendName);

    TBook selectByOpenidFriendName(@Param("openid")String openid, @Param("desName")String desName);

    void deletePhonesDto1(@Param("openid")String openid, @Param("friendName")String friendName);

    void delByOpenIdFriendNamePh(@Param("openid")String openid,
                                 @Param("name")String name,
                                 @Param("ph")String ph);
}