package com.pl.indexserver.service;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.TSuggestion;
import com.pl.model.xbms.Suggestion; /**
 * @author HuangGuangLei
 * @Date 2019/11/12
 */
public interface TSuggestionService {
    int insertSuggestion(TSuggestion suggestion);

    CheckSmsCodeResp suggestionAdd(Suggestion suggestion);
}
