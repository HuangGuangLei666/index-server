package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TSuggestionService;
import com.pl.mapper.TSuggestionMapper;
import com.pl.mapper.xbms.SuggestionMapper;
import com.pl.model.wx.TSuggestion;
import com.pl.model.xbms.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author HuangGuangLei
 * @Date 2019/11/12
 */
@Service
public class TSuggestionServiceImpl implements TSuggestionService {

    @Autowired
    private TSuggestionMapper tSuggestionMapper;
    @Autowired
    private SuggestionMapper suggestionMapper;

    @Override
    public int insertSuggestion(TSuggestion suggestion) {
        return tSuggestionMapper.insertSuggestion(suggestion);
    }

    @Override
    public CheckSmsCodeResp suggestionAdd(Suggestion suggestion) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = suggestionMapper.insertSuggestion(suggestion);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("改进建议添加失败");
            return resp;
        }

        resp.setRetCode(0);
        resp.setRetDesc("改进建议添加成功");
        return resp;
    }
}
