package com.pl.indexserver.service.xbms;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.TTag;
import com.pl.model.wx.TUserTag;
import com.pl.model.wx.TUserTagDto;
import com.pl.model.xbms.UserTag;
import com.pl.model.xbms.UserTagDto;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
public interface UserTagService {

    CheckSmsCodeResp userTagAdd(UserTag tUserTag, String friendName);

    List<UserTagDto> userTagQry(Integer userId, String tagName);

    CheckSmsCodeResp userTagDel(Integer userId, String phone);
}
