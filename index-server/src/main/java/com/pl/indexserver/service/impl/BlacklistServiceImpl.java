package com.pl.indexserver.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.mapper.BlacklistMapper;
import com.pl.indexserver.model.BlacklistDto;
import com.pl.indexserver.service.BlacklistService;
import com.pl.model.TManual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HGL
 * @Date 2018/12/28
 */
@Service
public class BlacklistServiceImpl implements BlacklistService {

    @Autowired
    private BlacklistMapper blacklistMapper;


    @Override
    public Page<BlacklistDto> queryBlacklistPage(int pageIndex, int pageNum, Long companyId, String ctPhone) {
        Page<BlacklistDto> page = new Page<>(pageIndex, pageNum);
        List<BlacklistDto> list = blacklistMapper.queryBlacklistPage(page, companyId, ctPhone);
        if (CollectionUtils.isEmpty(list)){
            page.setRecords(new ArrayList<>());
        } else {
            page.setRecords(list);
        }
        return page;
    }

    @Override
    public int deleteById(Long id) {
        return blacklistMapper.deleteById(id);
    }

    @Override
    public BlacklistDto selectByPrimaryKey(Long id) {
        return blacklistMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addBlacklist(List<BlacklistDto> blacklistDtoList) {
        blacklistMapper.addBlacklist(blacklistDtoList);
    }

    @Override
    public List<String> getCompanyBlackPhoneList(Long companyId) {
        return blacklistMapper.selectCompanyBlackPhoneList(companyId);
    }

    @Override
    public List<BlacklistDto> queryBlacklistListByCompanyId(Long companyId) {
        return blacklistMapper.queryBlacklistListByCompanyId(companyId);
    }
}
