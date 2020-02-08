package com.pl.mapper;

import com.pl.model.TDialogDetailExt;
import org.apache.ibatis.annotations.Param;

public interface TDialogDetailExtMapper {
    //int deleteByPrimaryKey(Long dialogId);

    int insert(TDialogDetailExt record);

    //int insertSelective(TDialogDetailExt record);

    TDialogDetailExt selectByPrimaryKey(@Param("dialogId") Long dialogId,@Param("postfix") String postfix);

    TDialogDetailExt selectCallInByPrimaryKey(Long dialogId);

    TDialogDetailExt getDialogRecordByDialogId(@Param("id") Long id,@Param("postfix") String postfix);

    TDialogDetailExt selectByDialoginId(Long id);

    void delDetailById(Long dialogId);
/*
    int updateByPrimaryKeySelective(TDialogDetailExt record);

    int updateByPrimaryKeyWithBLOBs(TDialogDetailExt record);

    int updateByPrimaryKey(TDialogDetailExt record);
*/
}