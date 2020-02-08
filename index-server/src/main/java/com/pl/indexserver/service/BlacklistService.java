package com.pl.indexserver.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pl.indexserver.model.BlacklistDto;
import com.pl.model.TManual;

import java.util.Date;
import java.util.List;

/**
 * @author HGL
 * @Date 2018/12/28
 */
public interface BlacklistService {
    Page<BlacklistDto> queryBlacklistPage(int pageIndex, int pageSize ,Long companyId, String ctPhone);

    int deleteById(Long id);

    BlacklistDto selectByPrimaryKey(Long id);

    void addBlacklist(List<BlacklistDto> blacklistDtoList);

    List<String> getCompanyBlackPhoneList(Long companyId);

    List<BlacklistDto> queryBlacklistListByCompanyId(Long companyId);
}
