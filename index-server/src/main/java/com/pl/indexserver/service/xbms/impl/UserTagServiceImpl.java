package com.pl.indexserver.service.xbms.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TUserTagService;
import com.pl.indexserver.service.xbms.UserTagService;
import com.pl.mapper.TUserTagMapper;
import com.pl.mapper.xbms.BookMapper;
import com.pl.mapper.xbms.UserTagMapper;
import com.pl.model.wx.TBook;
import com.pl.model.wx.TTag;
import com.pl.model.wx.TUserTag;
import com.pl.model.wx.TUserTagDto;
import com.pl.model.xbms.Book;
import com.pl.model.xbms.UserTag;
import com.pl.model.xbms.UserTagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/19
 */
@Service
public class UserTagServiceImpl implements UserTagService {

    private static final Logger logger = LoggerFactory.getLogger(UserTagServiceImpl.class);

    @Autowired
    private UserTagMapper userTagMapper;
    @Autowired
    private BookMapper bookMapper;

    @Override
    public CheckSmsCodeResp userTagAdd(UserTag tUserTag, String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        //添加数据到t_user_tag
        int i = userTagMapper.insertUserTag(tUserTag);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("用户标签添加失败");
            return resp;
        }
        //添加数据到t_book
        Book tBook = new Book();
        tBook.setPhone(tUserTag.getPhone());
        tBook.setFriendName(friendName);
        tBook.setUserId(tUserTag.getUserId());
        int insert = bookMapper.insertNotFriendName(tBook);
        resp.setRetCode(0);
        resp.setRetDesc("用户标签添加成功");
        return resp;
    }

    @Override
    public List<UserTagDto> userTagQry(Integer userId, String tagName) {
        List<UserTagDto> tUserTagList = new ArrayList<>();
        List<UserTagDto> tagList = userTagMapper.selectByUseridAndTagname(userId, tagName);
        logger.info("=======tagList={}==========", tagList.size());
        if (!CollectionUtils.isEmpty(tagList)) {
            for (UserTagDto tUserTag : tagList) {
                String friendName = "";
                String phone = tUserTag.getPhone();
                Book tBook = bookMapper.selectByPhoneAnduserId(phone, userId);
                if (!StringUtils.isEmpty(tBook)) {
                    friendName = tBook.getFriendName();
                }

                UserTagDto userTag = new UserTagDto();
                userTag.setUserId(tUserTag.getUserId());
                userTag.setPhone(tUserTag.getPhone());
                userTag.setTagId(tUserTag.getTagId());
                userTag.setTagName(tUserTag.getTagName());
                userTag.setType(tUserTag.getType());
                userTag.setId(tUserTag.getId());
                userTag.setCreateTime(tUserTag.getCreateTime());
                userTag.setFriendName(friendName);
                tUserTagList.add(userTag);
            }
        }
        return tUserTagList;
    }

    @Override
    public CheckSmsCodeResp userTagDel(Integer userId, String phone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        int i = userTagMapper.deleteByUseridAndPhone(userId, phone);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("用户标签删除失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("用户标签删除成功");
        return resp;
    }

    @Override
    public List<TTag> selectuserTagList() {
        return userTagMapper.selectuserTagList();
    }
}
