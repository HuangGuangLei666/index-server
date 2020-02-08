package com.pl.mapper;


import com.pl.model.wx.TQctivationcode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TQctivationcodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TQctivationcode record);

    int insertSelective(TQctivationcode record);

    TQctivationcode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TQctivationcode record);

    int updateByPrimaryKey(TQctivationcode record);

    void addTQctivationcode(TQctivationcode tQctivationcode);

    TQctivationcode selectByCode(String activationCode);

    int openMembershipByCode(@Param("code") String code, @Param("userId")Integer userId);

    TQctivationcode selectByTradeNo(String ordersSn);

    List<TQctivationcode> selectByUserIdAndStatus(Integer userId);

    TQctivationcode selectQctivationByCode(String code);

    int updateMembershipByCode(@Param("code") String code, @Param("userId")Integer userId);

    List<TQctivationcode> selectByAgentIdAndStatus(@Param("id")Integer id);
}