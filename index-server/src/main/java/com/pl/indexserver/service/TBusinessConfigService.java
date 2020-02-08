package com.pl.indexserver.service;

import com.pl.indexserver.model.BusinessConfigDto;
import com.pl.indexserver.model.CraftConfigDto;
import com.pl.model.SysBusinessTemplate;
import com.pl.model.TmUser;

import java.util.List;

/**
 * 智库配置服务
 *
 * @Author bei.zhang
 * @Date 2018/8/31 20:14
 */
public interface TBusinessConfigService {

    /**
     * 根据智库id查询智库配置
     *
     * @param companyId  公司id
     * @param businessId 智库id
     * @return
     */
    CraftConfigDto getBusinessConfig(Long companyId, Long businessId);

    /**
     * 保存智库配置
     *
     * @param user
     * @param businessId
     * @param configType
     * @param businessConfigDto
     * @return
     */
    int saveBusinessConfig(TmUser user, Long businessId, String configType, BusinessConfigDto businessConfigDto);

    /**
     * 克隆智库下配置
     *
     * @param user
     * @param sourceCompanyId
     * @param sourceBusinessId
     * @param targetCompanyId
     * @param targetBusinessId
     * @return
     */
    int clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId);

    /**
     * 查询系统级智库类型。
     * @return
     */
    List<SysBusinessTemplate> getSysBusinessTemplates();

}
