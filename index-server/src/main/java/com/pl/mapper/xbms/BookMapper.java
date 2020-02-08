package com.pl.mapper.xbms;


import com.pl.model.xbms.Book;
import org.apache.ibatis.annotations.Param;

public interface BookMapper {

    Book selectByPhoneAndUserId(@Param("telephone") String telephone, @Param("userId")Integer userId);

    int insertNotFriendName(Book tBook);

    int insert(Book tBook);

    Book selectByPhoneAnduserId(@Param("phone")String phone, @Param("userId")Integer userId);

    int delByUserIdAndFriendName(@Param("userId")Integer userId, @Param("friendName")String friendName);

    void deletePhonesDto1(@Param("userId")Integer userId, @Param("friendName")String friendName);
}