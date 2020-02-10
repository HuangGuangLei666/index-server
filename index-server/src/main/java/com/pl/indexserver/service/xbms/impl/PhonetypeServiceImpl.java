package com.pl.indexserver.service.xbms.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TPhonetypeService;
import com.pl.indexserver.service.xbms.PhonetypeService;
import com.pl.mapper.TPhonetypeMapper;
import com.pl.mapper.xbms.PhonetypeMapper;
import com.pl.model.wx.InterceptSta;
import com.pl.model.wx.TPhonetype;
import com.pl.model.xbms.Phonetype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/3 10:43
 * @Description
 */
@Service
public class PhonetypeServiceImpl implements PhonetypeService {

    @Autowired
    private PhonetypeMapper phonetypeMapper;

    @Override
    public CheckSmsCodeResp phoneTypeAdd(Phonetype tPhonetype) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = phonetypeMapper.addPhoneType(tPhonetype);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("新增号码类型失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("新增号码类型成功");
        return resp;
    }

    @Override
    public List<Phonetype> phoneTypeQry(Integer userId, String type) {
        List<Phonetype> tPhonetypeList = new ArrayList<>();
        List<Phonetype> phonetypeList = phonetypeMapper.selectByUseridAndType(userId, type);
        if (CollectionUtils.isEmpty(phonetypeList)) {
            return tPhonetypeList;
        }
        for (Phonetype tPhonetype : phonetypeList) {
            Phonetype phonetype = new Phonetype();
            phonetype.setId(tPhonetype.getId());
            phonetype.setType(tPhonetype.getType());
            phonetype.setPhone(tPhonetype.getPhone());
            phonetype.setUserId(tPhonetype.getUserId());
            phonetype.setCreateTime(tPhonetype.getCreateTime());
            tPhonetypeList.add(phonetype);
        }

        return tPhonetypeList;
    }

    @Override
    public CheckSmsCodeResp phoneTypeDel(Integer id) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        Phonetype tPhonetype = phonetypeMapper.selectById(id);
        if (StringUtils.isEmpty(tPhonetype)) {
            resp.setRetCode(1);
            resp.setRetDesc("没有此记录，删除失败");
            return resp;
        }

        int i = phonetypeMapper.delectById(id);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("删除号码类型失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("删除号码类型成功");
        return resp;
    }
}
