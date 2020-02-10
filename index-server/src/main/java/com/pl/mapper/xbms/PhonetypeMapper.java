package com.pl.mapper.xbms;

import com.pl.model.xbms.Phonetype;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PhonetypeMapper {
    int addPhoneType(Phonetype tPhonetype);

    List<Phonetype> selectByUseridAndType(@Param("userId") Integer userId, @Param("type")String type);

    Phonetype selectById(Integer id);

    int delectById(Integer id);
}