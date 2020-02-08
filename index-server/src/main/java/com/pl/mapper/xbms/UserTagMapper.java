package com.pl.mapper.xbms;

import com.pl.model.xbms.UserTag;
import com.pl.model.xbms.UserTagDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2020/1/16 14:41
 * @Description
 */
public interface UserTagMapper {
    UserTag selectUsertagByPhone(@Param("userId") Integer userId, @Param("telephone")String telephone);

    int insertUserTag(UserTag tUserTag);

    List<UserTagDto> selectByUseridAndTagname(@Param("userId")Integer userId, @Param("tagName")String tagName);

    UserTag selectByPhone(@Param("userId")Integer userId, @Param("phone")String phone);

    int deleteByUseridAndPhone(@Param("userId")Integer userId, @Param("phone")String phone);
}
