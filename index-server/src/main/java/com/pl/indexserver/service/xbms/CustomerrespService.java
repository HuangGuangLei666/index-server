package com.pl.indexserver.service.xbms;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.KnowledgeAnswerResp;
import com.pl.model.wx.QuestionDto;
import com.pl.model.wx.TCustomresp;
import com.pl.model.xbms.Customresp;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/28 15:33
 * @Description
 */
public interface CustomerrespService {
    List<KnowledgeAnswerResp> answerListQry(String knowledgeId, Integer userId);

    List<QuestionDto> questionListQry(Integer userId);

    CheckSmsCodeResp customerRespAdd(Customresp tCustomresp);

    CheckSmsCodeResp customerRespDel(Integer id);
}
