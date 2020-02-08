package com.pl.mapper.xbms;


import com.pl.model.wx.TUserQa;
import com.pl.model.xbms.UserQa;
import org.apache.ibatis.annotations.Param;

public interface UserQaMapper {
    UserQa selectAnswerId(@Param("knowledgeId") String knowledgeId, @Param("userId")Integer userId);

    int addCustomerresp(UserQa tUserQa);
}