package com.pl.indexserver.service.xbms;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.FriendPhonesDto;
import com.pl.model.wx.TGroupTag;
import com.pl.model.wx.TGroupTagDto;
import com.pl.model.wx.TTagGroup;
import com.pl.model.xbms.GroupTag;
import com.pl.model.xbms.GroupTagDto;

import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
public interface GroupTagService {

    CheckSmsCodeResp groupTagAdd(GroupTag groupTag, String friendName);

    List<GroupTagDto> groupTagQry(Integer userId, String tagName);

    List<FriendPhonesDto> friendPhonesQry(Integer userId, String tagName, String friendName);

    CheckSmsCodeResp friendPhonesAdd(String friendName, String[] phones, Integer userId, Integer tagId, String type);

    CheckSmsCodeResp friendPhonesUpd(Integer userId, String friendName, String desName, String[] phones, Integer tagId, String type);

    CheckSmsCodeResp groupTagDel(Integer userId, String phone);
}
