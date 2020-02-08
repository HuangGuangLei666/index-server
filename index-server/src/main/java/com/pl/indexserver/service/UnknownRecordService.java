package com.pl.indexserver.service;

import com.pl.model.TUnknownRecord;

public interface UnknownRecordService extends BaseService<TUnknownRecord> {

    /**
     * 根据业务id和文本内容忽略对应未识别语句（不做展示）
     *
     * @param businessId 业务id
     * @param content 文本内容
     * @throws Exception
     */
    int neglectByBusinessIdAndContent(Long businessId,String content) throws Exception;

}
