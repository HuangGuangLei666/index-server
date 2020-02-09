package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TMallService;
import com.pl.mapper.TMallMapper;
import com.pl.mapper.TUserinfoMapper;
import com.pl.model.wx.ReverseSortTmall;
import com.pl.model.wx.TMall;
import com.pl.model.wx.TUserinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/4
 */
@Service
public class TMallServiceImpl implements TMallService {

    private static final Logger logger = LoggerFactory.getLogger(TMallServiceImpl.class);

    @Autowired
    private TMallMapper tMallMapper;
    @Autowired
    private TUserinfoMapper tUserinfoMapper;

    @Override
    public List<TMall> selectAllData(Integer userId) {
        return tMallMapper.selectAllData(userId);
    }

    @Override
    public int insertlabel(TMall tMall) {
        return tMallMapper.insertlabel(tMall);
    }

    @Override
    public TMall selectById(Integer id,Integer userId) {
        return tMallMapper.selectByPrimaryKey(id,userId);
    }

    @Override
    public TMall selectByIdAndDefault(Integer id) {
        return tMallMapper.selectByIdAndDefault(id);
    }

    @Override
    public int deleteLabel(Integer id, Integer userId) {
        return tMallMapper.deleteLabel(id,userId);
    }

    @Override
    public TMall selectByIdAndUserid(Integer fatherId, Integer userId) {
        return tMallMapper.selectByIdAndUserid(fatherId,userId);
    }

    @Override
    public List<TMall> selectAllDataDefaul() {
        return tMallMapper.selectAllDataDefaul();
    }

    @Override
    public List<TMall> selectByFatherId(Integer id) {
        return tMallMapper.selectByFatherId(id);
    }

    @Override
    public void insertBaseData(Integer userId) {
        tMallMapper.insertBaseData(userId);
    }

    @Override
    public List<TMall> selectByOpenid(String openid) {
        return tMallMapper.selectByOpenid(openid);
    }

    @Override
    public List<TMall> selectAllByOpenid() {
        return tMallMapper.selectAllByOpenid();
    }

    @Override
    public List<TMall> userIntrestingQry(Integer userId) {
        List<TMall> tMallList = new ArrayList<>();
        //新建一个去重的临时list
        List<TMall> del = new ArrayList<>();
        //查询user_id为0的标签
        List<TMall> defaultList = tMallMapper.selectAllByUserid();
        //自己勾选的标签
        List<TMall> mallList = tMallMapper.selectByUserid(userId);
        if (!CollectionUtils.isEmpty(mallList)) {
            for (TMall tMall : defaultList) {
                for (TMall mall : mallList) {
                    if (tMall.getId().equals(mall.getId())) {
                        del.add(tMall);
                    }
                }
            }
            //去重，留用户勾选的标签
            defaultList.removeAll(del);
            defaultList.addAll(mallList);
            //用比较器 根据id升序排序
            Collections.sort(defaultList, new ReverseSortTmall());


            for (TMall tMall : defaultList) {
                TMall mall = new TMall();
                mall.setId(tMall.getId());
                mall.setUserId(tMall.getUserId());
                mall.setName(tMall.getName());
                mall.setLevel(tMall.getLevel());
                mall.setImageUrl(tMall.getImageUrl());
                mall.setFatherId(tMall.getFatherId());
                tMallList.add(mall);
            }
            return tMallList;
        }
        return defaultList;
    }

    @Override
    public CheckSmsCodeResp insertlabels(Integer id, Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        TMall tMall1 = tMallMapper.selectByLabelIdAndDefault(id);
        Integer fatherId = tMall1.getFatherId();
        TMall tMall2 = tMallMapper.selectByLabelIdAndDefault(fatherId);

        TMall tMall = new TMall();
        tMall.setId(id);
        tMall.setUserId(userId);
        tMall.setName(tMall1.getName());
        tMall.setLevel(tMall1.getLevel());
        tMall.setFatherId(tMall1.getFatherId());
        tMall.setImageUrl(tMall1.getImageUrl());

        TMall tMal2 = new TMall();
        tMal2.setId(fatherId);
        tMal2.setUserId(userId);
        tMal2.setName(tMall2.getName());
        tMal2.setLevel(tMall2.getLevel());
        tMal2.setFatherId(tMall2.getFatherId());
        tMal2.setImageUrl(tMall2.getImageUrl());

        TMall tMall3 = tMallMapper.selectByLabelIdAndUserid(id, userId);
        if (!StringUtils.isEmpty(tMall3)) {
            resp.setRetCode(1);
            resp.setRetDesc("已勾选，不能重复添加");
            return resp;
        }
        int i = tMallMapper.insertlabels(tMall);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("添加失败");
            return resp;
        }

        TMall tMall4 = tMallMapper.selectByLabelIdAndUserid(fatherId, userId);
        if (StringUtils.isEmpty(tMall4)) {
            int j = tMallMapper.insertlabels(tMal2);
        }
        resp.setRetCode(0);
        resp.setRetDesc("添加成功");
        return resp;
    }

    @Override
    public CheckSmsCodeResp deleteLabels(Integer id, Integer userId) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();

        int i = tMallMapper.deleteLabels(id, userId);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除成功");
        return resp;
    }

    @Override
    public TMall myStatusQry(Integer userId) {
        TUserinfo tUserinfo = tUserinfoMapper.selectByUserId(userId);
        TMall tMall1 = new TMall();
        if (!StringUtils.isEmpty(tUserinfo)) {
            Integer status = tUserinfo.getStatus();
            if (!StringUtils.isEmpty(status)) {
                TMall tMall = tMallMapper.selectByIdAndUserId(status, userId);
                logger.info("=========tMall={}=========", tMall);
                if (!StringUtils.isEmpty(tMall)) {
                    tMall1.setId(tMall.getId());
                    tMall1.setFatherId(tMall.getFatherId());
                    tMall1.setImageUrl(tMall.getImageUrl());
                    tMall1.setLevel(tMall.getLevel());
                    tMall1.setName(tMall.getName());
                    tMall1.setUserId(tMall.getUserId());
                    return tMall1;
                }
            }
        }
        return new TMall(-1, "不好意思，你没有设置情景状态~~");
    }

}
