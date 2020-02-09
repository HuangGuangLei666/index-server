package com.pl.indexserver.service.xbms.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TGroupTagService;
import com.pl.indexserver.service.xbms.GroupTagService;
import com.pl.mapper.TGroupTagMapper;
import com.pl.mapper.xbms.BookMapper;
import com.pl.mapper.xbms.GroupTagMapper;
import com.pl.mapper.xbms.UserTagMapper;
import com.pl.model.wx.*;
import com.pl.model.xbms.Book;
import com.pl.model.xbms.GroupTag;
import com.pl.model.xbms.GroupTagDto;
import com.pl.model.xbms.UserTag;
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
public class GroupTagServiceImpl implements GroupTagService {

    private static final Logger logger = LoggerFactory.getLogger(GroupTagServiceImpl.class);

    @Autowired
    private GroupTagMapper groupTagMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserTagMapper userTagMapper;

    @Override
    public CheckSmsCodeResp groupTagAdd(GroupTag groupTag, String friendName) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = groupTagMapper.insertGroupTag(groupTag);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("群标签添加失败");
            return resp;
        }
        Book tBook = new Book();
        tBook.setPhone(groupTag.getPhones());
        tBook.setFriendName(friendName);
        tBook.setUserId(groupTag.getUserId());
        int insert = bookMapper.insert(tBook);
        logger.info("=======insert========", insert);
        resp.setRetCode(0);
        resp.setRetDesc("群标签添加成功");
        return resp;
    }

    @Override
    public List<GroupTagDto> groupTagQry(Integer userId, String tagName) {
        List<GroupTagDto> groupTagDtos = new ArrayList<>();
        //opNfewsLHoAkgEj9dpx6ej2zIt0M	1	领导	SYSTEM	领导	 小蒋蒋
        //根据openid和tagName联表查询：t_group_tag, t_tag_group, t_book
        List<GroupTagDto> tagList = groupTagMapper.selectByUseridAndTagname(userId, tagName);
        logger.info("=======tagList={}========", tagList.size());
        if (!CollectionUtils.isEmpty(tagList)) {
            for (GroupTagDto tagDto : tagList) {
                logger.info("======tagDto={}======", tagDto);

                GroupTagDto groupTagDto = new GroupTagDto();
                groupTagDto.setUserId(tagDto.getUserId());
                groupTagDto.setTagId(tagDto.getTagId());
                groupTagDto.setTagName(tagDto.getTagName());
                groupTagDto.setType(tagDto.getType());
                groupTagDto.setGroupName(tagDto.getGroupName());
                groupTagDto.setFriendName(tagDto.getFriendName());
                logger.info("======groupTagDto={}======", groupTagDto.toString());
                groupTagDtos.add(groupTagDto);
            }
            logger.info("======groupTagDtos={}======", groupTagDtos.size());
        }
        return groupTagDtos;
    }

    @Override
    public List<FriendPhonesDto> friendPhonesQry(Integer userId, String tagName, String friendName) {
        List<FriendPhonesDto> phonesDtos = new ArrayList<>();
        //根据分类，好友名字查询好友下的多个号码
        List<FriendPhonesDto> friendPhonesDtoList = groupTagMapper.selectPhoneList(userId, tagName, friendName);
        if (CollectionUtils.isEmpty(friendPhonesDtoList)) {
            return phonesDtos;
        }
        for (FriendPhonesDto phonesDto : friendPhonesDtoList) {
            String tag = "";
            UserTag tUserTag = userTagMapper.selectByPhone(userId, phonesDto.getPhone());
            if (!StringUtils.isEmpty(tUserTag)) {
                tag = tUserTag.getTagName();
            }
            FriendPhonesDto friendPhonesDto = new FriendPhonesDto();
            friendPhonesDto.setId(phonesDto.getId());
            friendPhonesDto.setFriendName(phonesDto.getFriendName());
            friendPhonesDto.setPhone(phonesDto.getPhone());
            friendPhonesDto.setTagName(tag);
            phonesDtos.add(friendPhonesDto);
        }
        return phonesDtos;
    }

    @Override
    public CheckSmsCodeResp friendPhonesAdd(String friendName, String[] phones, Integer userId, Integer tagId, String type) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        //根据tagId查询tagName
        TTagGroup tag = groupTagMapper.selectTagNameByTagId(tagId);
        for (String phone : phones) {
            GroupTag tGroupTag = new GroupTag();
            tGroupTag.setType(type);
            tGroupTag.setTagId(tagId);
            tGroupTag.setPhones(phone);
            tGroupTag.setUserId(userId);
            tGroupTag.setTagName(tag.getName());
            groupTagMapper.insertGroupTag(tGroupTag);

            Book tBook = new Book();
            tBook.setUserId(userId);
            tBook.setFriendName(friendName);
            tBook.setPhone(phone);
            bookMapper.insert(tBook);
        }

        resp.setRetCode(0);
        resp.setRetDesc("添加好友成功");
        return resp;
    }

    @Override
    public CheckSmsCodeResp friendPhonesUpd(Integer userId, String friendName, String desName, String[] phones, Integer tagId, String type) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        List<String> phList = new ArrayList<>();
        //根据tagId查询tagName
        TTagGroup tag = groupTagMapper.selectTagNameByTagId(tagId);
        //联表查询t_book,t_group_tag
        List<FriendPhonesDto> phonesDtos = groupTagMapper.selectPhoneList(userId, tag.getName(), friendName);
        /**
         *      小滢子	12356321254	82	12356321254	opNfewsLHoAkgEj9dpx6ej2zIt0M	领导
                小滢子	15566669999	83	15566669999	opNfewsLHoAkgEj9dpx6ej2zIt0M	领导
                小滢子	17788885555	84	17788885555	opNfewsLHoAkgEj9dpx6ej2zIt0M	领导
         */

        //實現，先删除全部数据，在进行添加

        //删除t_book
        bookMapper.deletePhonesDto1(userId, friendName);

        for (FriendPhonesDto phonesDto : phonesDtos) {
            phList.add(phonesDto.getPhones());
        }
        for (String s : phList) {
            //删除t_group_tag
            groupTagMapper.deletePhonesDto(userId, s, tag.getName());
        }

        //再添加新的数据
        for (String phone : phones) {
            GroupTag tGroupTag = new GroupTag();
            tGroupTag.setType(type);
            tGroupTag.setTagId(tagId);
            tGroupTag.setPhones(phone);
            tGroupTag.setUserId(userId);
            tGroupTag.setTagName(tag.getName());
            groupTagMapper.insertGroupTag(tGroupTag);

            Book tBook = new Book();
            tBook.setUserId(userId);
            tBook.setFriendName(desName);
            tBook.setPhone(phone);
            bookMapper.insert(tBook);
        }
        resp.setRetCode(0);
        resp.setRetDesc("编辑好友成功");
        return resp;
    }

    @Override
    public CheckSmsCodeResp groupTagDel(Integer userId, String phone) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        int i = groupTagMapper.deleteByOpenidAndPhone(userId, phone);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("群标签删除失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("群标签删除成功");
        return resp;
    }

    @Override
    public List<TTagGroup> selectGroupClassList() {
        return groupTagMapper.selectGroupClassList();
    }
}
