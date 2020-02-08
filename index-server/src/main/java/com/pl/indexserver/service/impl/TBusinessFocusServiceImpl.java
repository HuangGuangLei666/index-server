package com.pl.indexserver.service.impl;

import com.pl.indexserver.model.BusinessFocusDto;
import com.pl.indexserver.service.TBusinessFocusService;
import com.pl.mapper.*;
import com.pl.model.SpeechcraftTag;
import com.pl.model.TBusinessFocus;
import com.pl.model.TmUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 智库关注点业务实现
 *
 * @Author bei.zhang
 * @Date 2018/9/1 14:36
 */
@Service
public class TBusinessFocusServiceImpl implements TBusinessFocusService {

    @Autowired
    private TBusinessFocusMapper tBusinessFocusMapper;

    @Override
    public List<BusinessFocusDto> getBusinessFocusList(Long companyId, Long businessId, String name) {
        return tBusinessFocusMapper.selectBusinessFocusDtoByName(companyId, businessId, name);
    }

    @Override
    public String convertFocusNamesToFocusIds(TmUser user, Long businessId, String focusNames) {
        if (!StringUtils.isEmpty(focusNames)) {
            String[] nameArr = focusNames.split("&");
            List<BusinessFocusDto> focusDtos = tBusinessFocusMapper.selectBusinessFocusDtoByNames(user.getCompanyId(), businessId, nameArr);
            Map<Integer, Integer> nameMap = new HashMap<>();
            for (BusinessFocusDto focusDto : focusDtos) {
                nameMap.put(focusDto.getName().hashCode(), focusDto.getId());
            }
            StringBuilder ids = new StringBuilder();
            for (String name : nameArr) {
                if (StringUtils.isEmpty(name)) {
                    continue;
                }
                if (nameMap.containsKey(name.hashCode())) {
                    ids.append(nameMap.get(name.hashCode())).append(",");
                } else {
                    TBusinessFocus businessFocus = new TBusinessFocus();
                    businessFocus.setCompanyId(user.getCompanyId());
                    businessFocus.setBusinessId(businessId);
                    businessFocus.setName(name);
                    businessFocus.setCreateBy(user.getUsername());
                    businessFocus.setCreateDate(new Date());
                    tBusinessFocusMapper.insertSelective(businessFocus);
                    ids.append(businessFocus.getId()).append(",");
                }
            }
            return ids.toString();
        }
        return "";
    }

    @Override
    public String convertFocusIdsToFocusNames(Long companyId, Long businessId, String focusIds) {
        if (!StringUtils.isEmpty(focusIds)) {
            return tBusinessFocusMapper.selectBusinessFocusNamesDtoByIds(companyId, businessId, focusIds);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
    public int clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId) {
        List<TBusinessFocus> tBusinessFocusList = tBusinessFocusMapper.selectByCompanyIdAndBusinessId(sourceCompanyId, sourceBusinessId);
        List<TBusinessFocus> targetBusinessFocusList = tBusinessFocusMapper.selectByCompanyIdAndBusinessId(targetCompanyId, targetCompanyId);
        Map<Integer, Integer> nameMap = new HashMap<>();
        Date date = new Date();
        for (TBusinessFocus businessFocus : targetBusinessFocusList) {
            nameMap.put(businessFocus.getName().hashCode(), businessFocus.getId());
        }
        for (TBusinessFocus businessFocus : tBusinessFocusList) {
            if (nameMap.containsKey(businessFocus.getName().hashCode())) {
                continue;
            }
            TBusinessFocus targetBusinessFocus = new TBusinessFocus();
            BeanUtils.copyProperties(businessFocus, targetBusinessFocus, "id");
            businessFocus.setCompanyId(targetCompanyId);
            businessFocus.setBusinessId(targetBusinessId);
            businessFocus.setCreateBy(user.getUsername());
            businessFocus.setCreateDate(date);
            tBusinessFocusMapper.insertSelective(targetBusinessFocus);
        }
        return 1;
    }


}
