package com.pl.mapper;

import com.pl.model.UploadFile;

public interface UploadFileMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UploadFile record);

    int insertSelective(UploadFile record);

    UploadFile selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UploadFile record);

    int updateByPrimaryKey(UploadFile record);
}