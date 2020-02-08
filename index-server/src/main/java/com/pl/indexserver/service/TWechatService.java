package com.pl.indexserver.service;

import com.pl.indexserver.model.ReturnMsg;
import com.pl.model.TWechatRecord;
import com.pl.model.WechatRespon;

/**
 * @author HuangGuangLei
 * @Date 2019/7/25
 */
public interface TWechatService {
    /**
     * 微信机器人问答带流程
     * @param wechatRecord
     * @return
     */
    WechatRespon selectWechatRecordByWechatIds(TWechatRecord wechatRecord);
}
