package com.pl.indexserver.service;

import com.pl.indexserver.model.DialogInDetail;

import java.util.List;

/**
 * @author HGL
 * @Date 2018/12/27
 */
public interface DialogInDetailService {
    List<DialogInDetail> queryCallInRecordDetail(long callInId);
}
