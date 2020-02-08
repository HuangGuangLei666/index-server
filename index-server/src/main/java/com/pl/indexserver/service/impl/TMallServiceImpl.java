package com.pl.indexserver.service.impl;

import com.pl.indexserver.service.TMallService;
import com.pl.mapper.TMallMapper;
import com.pl.model.wx.ReverseSortTmall;
import com.pl.model.wx.TMall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HuangGuangLei
 * @Date 2019/11/4
 */
@Service
public class TMallServiceImpl implements TMallService {

    @Autowired
    private TMallMapper tMallMapper;

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
}
