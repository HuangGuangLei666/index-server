package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.UploadFileService;
import com.pl.mapper.UploadFileMapper;
import com.pl.model.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    private UploadFileMapper uploadFileMapper;

    @Override
    public Boolean insertSelective(UploadFile uploadFile){
        int i = uploadFileMapper.insertSelective(uploadFile);
        return i > 0;
    }
}
