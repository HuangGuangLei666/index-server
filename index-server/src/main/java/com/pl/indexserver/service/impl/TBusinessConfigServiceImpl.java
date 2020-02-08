package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.BusinessConfigDto;
import com.pl.indexserver.model.CraftConfigDto;
import com.pl.indexserver.service.TBusinessConfigService;
import com.pl.indexserver.service.TBusinessFocusService;
import com.pl.mapper.TBusinessConfigMapper;
import com.pl.model.SysBusinessTemplate;
import com.pl.model.TBusinessConfig;
import com.pl.model.TmUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 智库配置业务实现
 *
 * @Author bei.zhang
 * @Date 2018/8/31 20:15
 */
@Service
public class TBusinessConfigServiceImpl implements TBusinessConfigService {

    @Autowired
    private TBusinessConfigMapper tBusinessConfigMapper;
    @Autowired
    private TBusinessFocusService tBusinessFocusService;

    @Override
    public CraftConfigDto getBusinessConfig(Long companyId, Long businessId) {
        CraftConfigDto craftConfigDto = new CraftConfigDto();
        List<TBusinessConfig> businessConfigs = tBusinessConfigMapper.selectByCompanyIdBusinessId(companyId, businessId);
        if (!CollectionUtils.isEmpty(businessConfigs)) {
            for (TBusinessConfig businessConfig : businessConfigs) {
                if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(businessConfig.getConfigType())) {
                    BusinessConfigDto detailDto = new BusinessConfigDto();
                    BeanUtils.copyProperties(businessConfig, detailDto);
                    detailDto.setIntentA(tBusinessFocusService.convertFocusIdsToFocusNames(companyId, businessId, detailDto.getIntentA()));
                    detailDto.setIntentB(tBusinessFocusService.convertFocusIdsToFocusNames(companyId, businessId, detailDto.getIntentB()));
                    detailDto.setIntentC(tBusinessFocusService.convertFocusIdsToFocusNames(companyId, businessId, detailDto.getIntentC()));
                    detailDto.setIntentD(tBusinessFocusService.convertFocusIdsToFocusNames(companyId, businessId, detailDto.getIntentD()));
                    craftConfigDto.setFocusConfig(detailDto);
                } else if (TBusinessConfig.ConfigType.SCORE.getCode().equals(businessConfig.getConfigType())) {
                    BusinessConfigDto detailDto = new BusinessConfigDto();
                    BeanUtils.copyProperties(businessConfig, detailDto);
                    craftConfigDto.setScoreConfig(detailDto);
                } else if (TBusinessConfig.ConfigType.INTENT.getCode().equals(businessConfig.getConfigType())) {
                    BusinessConfigDto detailDto = new BusinessConfigDto();
                    BeanUtils.copyProperties(businessConfig, detailDto);
                    craftConfigDto.setIntentConfig(detailDto);
                } else if (TBusinessConfig.ConfigType.PUSH.getCode().equals(businessConfig.getConfigType())) {
                    BusinessConfigDto detailDto = new BusinessConfigDto();
                    BeanUtils.copyProperties(businessConfig, detailDto);
                    craftConfigDto.setPushConfig(detailDto);
                }
            }
        }
        return craftConfigDto;
    }

    @Override
    public int saveBusinessConfig(TmUser user, Long businessId, String configType, BusinessConfigDto businessConfigDto) {
        TBusinessConfig oldConfig = tBusinessConfigMapper.selectByBusinessIdAndConfigType(businessId, configType);
        if (oldConfig != null) {
            TBusinessConfig businessConfig = new TBusinessConfig();
            if (TBusinessConfig.Status.USED.getCode().equals(businessConfigDto.getStatus())) {
                BeanUtils.copyProperties(businessConfigDto, businessConfig);
                businessConfig = rebuildBusinessConfig(user, businessId, configType, businessConfig);
            } else {
                businessConfig.setStatus(businessConfigDto.getStatus());
            }
            businessConfig.setId(oldConfig.getId());
            businessConfig.setUpdateBy(user.getUsername());
            businessConfig.setUpdateDate(new Date());
            return tBusinessConfigMapper.updateByPrimaryKeySelective(businessConfig);
        } else {
            TBusinessConfig businessConfig = new TBusinessConfig();
            if (businessConfigDto != null) {
                BeanUtils.copyProperties(businessConfigDto, businessConfig);
            }
            businessConfig = rebuildBusinessConfig(user, businessId, configType, businessConfig);
            businessConfig.setCompanyId(user.getCompanyId());
            businessConfig.setBusinessId(businessId);
            businessConfig.setConfigType(configType);
            if (StringUtils.isEmpty(businessConfig.getStatus())) {
                businessConfig.setStatus(TBusinessConfig.Status.UNUSED.getCode());
            }
            businessConfig.setCreateBy(user.getUsername());
            businessConfig.setCreateDate(new Date());
            return tBusinessConfigMapper.insertSelective(businessConfig);
        }
    }

    @Override
    public int clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId) {
        List<TBusinessConfig> businessConfigs = tBusinessConfigMapper.selectByCompanyIdBusinessId(sourceCompanyId, sourceBusinessId);
        Date date = new Date();
        for (TBusinessConfig businessConfig : businessConfigs) {
            TBusinessConfig targetConfig = new TBusinessConfig();
            BeanUtils.copyProperties(businessConfig, targetConfig, "id", "updateBy", "updateDate");
            if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(businessConfig.getConfigType())) {
                String focusANames = tBusinessFocusService.convertFocusIdsToFocusNames(sourceCompanyId, sourceBusinessId, businessConfig.getIntentA());
                String foucsAIds = tBusinessFocusService.convertFocusNamesToFocusIds(user, targetBusinessId, focusANames);
                targetConfig.setIntentA(foucsAIds);
                String focusBNames = tBusinessFocusService.convertFocusIdsToFocusNames(sourceCompanyId, sourceBusinessId, businessConfig.getIntentB());
                String foucsBIds = tBusinessFocusService.convertFocusNamesToFocusIds(user, targetBusinessId, focusBNames);
                targetConfig.setIntentB(foucsBIds);
                String focusCNames = tBusinessFocusService.convertFocusIdsToFocusNames(sourceCompanyId, sourceBusinessId, businessConfig.getIntentC());
                String foucsCIds = tBusinessFocusService.convertFocusNamesToFocusIds(user, targetBusinessId, focusCNames);
                targetConfig.setIntentC(foucsCIds);
                String focusDNames = tBusinessFocusService.convertFocusIdsToFocusNames(sourceCompanyId, sourceBusinessId, businessConfig.getIntentD());
                String foucsDIds = tBusinessFocusService.convertFocusNamesToFocusIds(user, targetBusinessId, focusDNames);
                targetConfig.setIntentD(foucsDIds);
            }
            targetConfig.setCompanyId(targetCompanyId);
            targetConfig.setBusinessId(targetBusinessId);
            targetConfig.setCreateBy(user.getUsername());
            targetConfig.setCreateDate(date);
            targetConfig.setUpdateDate(date);
            tBusinessConfigMapper.insertSelective(targetConfig);
        }
        return 1;
    }

    @Override
    public List<SysBusinessTemplate> getSysBusinessTemplates() {

        return tBusinessConfigMapper.getSysBuinessTemplates();
    }
    /**
     * 重新构建处理配置信息
     *
     * @param user
     * @param businessId
     * @param configType
     * @param businessConfig
     * @return
     */
    private TBusinessConfig rebuildBusinessConfig(TmUser user, Long businessId, String configType, TBusinessConfig businessConfig) {
        if (TBusinessConfig.ConfigType.FOCUS.getCode().equals(configType)) {
            businessConfig.setIntentA(tBusinessFocusService.convertFocusNamesToFocusIds(user, businessId, businessConfig.getIntentA()));
            businessConfig.setIntentB(tBusinessFocusService.convertFocusNamesToFocusIds(user, businessId, businessConfig.getIntentB()));
            businessConfig.setIntentC(tBusinessFocusService.convertFocusNamesToFocusIds(user, businessId, businessConfig.getIntentC()));
            businessConfig.setIntentD(tBusinessFocusService.convertFocusNamesToFocusIds(user, businessId, businessConfig.getIntentD()));
        }
        return businessConfig;
    }



}
