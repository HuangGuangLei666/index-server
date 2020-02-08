package com.pl.indexserver.service;

import com.pl.model.UploadFile;

public interface UploadFileService {

    Boolean insertSelective(UploadFile uploadFile) throws Exception;
}
