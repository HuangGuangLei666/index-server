package com.pl.indexserver.service.xbms;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.BindingOwnUser;
import com.pl.model.wx.TRelation;
import com.pl.model.wx.UserRelationDto;
import com.pl.model.xbms.Relation;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 14:24
 * @Description
 */
public interface RelationService {

    CheckSmsCodeResp relationBinding(Integer userId, Integer id);

    Relation selectByUseridAndPhone(Integer userId, Integer id);

    List<UserRelationDto> relationBindingQry(Integer userId);

    List<BindingOwnUser> bindingOwnUserQry(Integer userId);
}
