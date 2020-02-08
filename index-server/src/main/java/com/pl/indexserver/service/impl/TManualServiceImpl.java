package com.pl.indexserver.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.service.TManualService;
import com.pl.mapper.TManualMapper;
import com.pl.model.TManual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TManualServiceImpl implements TManualService {

    @Autowired
    private TManualMapper tManualMapper;

    @Override
    public Page<TManual> getTManualListOfPage(int pageIndex, int pageSize, String phone, Long companyId, String uid, Integer status, Date beginDate, Date endDate) {
        Page<TManual> page = new Page<>(pageIndex, pageSize);
        List<TManual> list = tManualMapper.getManualList(page, companyId, uid, status, phone, beginDate, endDate);
        if (CollectionUtils.isEmpty(list)){
            page.setRecords(new ArrayList<>());
        } else {
            page.setRecords(list);
        }
        return page;
    }

    @Override
    public TManual getTmanualByPhone(Integer taskId, Long companyId, String phone) {
        return tManualMapper.getManualByPhone(taskId, companyId, phone);
    }

    @Override
    public TManual getTmanualByPriKey(Integer id) {
        return tManualMapper.getManualByPrimaryKey(id);
    }

    @Override
    public int updateTmanual(TManual manual) {
        return tManualMapper.updateByPrimaryKeySelective(manual);
    }

    @Override
    public int insertTmanual(TManual manual) {
        int rest;
        TManual selManual = tManualMapper.getManualByPhone(manual.getTaskId(), manual.getCompanyId() ,manual.getPhoneNum());
        if (null == selManual){
            rest = tManualMapper.insertSelective(manual);
        }else {
            selManual.setRemark(manual.getRemark());
            rest = tManualMapper.updateByPrimaryKeySelective(manual);
        }
        if (rest > 0){
            return manual.getId();
        }
        return 0;
    }

    @Override
    public int deleteTmanual(Integer id) {
        return tManualMapper.deleteByPrimaryKey(id);
    }

}
