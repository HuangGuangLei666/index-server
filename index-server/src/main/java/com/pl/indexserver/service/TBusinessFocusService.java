package com.pl.indexserver.service;

import com.pl.indexserver.model.BusinessFocusDto;
import com.pl.model.SpeechcraftTag;
import com.pl.model.TmUser;

import java.util.List;

/**
 * 智库关注点服务服务
 *
 * @Author bei.zhang
 * @Date 2018/8/31 20:14
 */
public interface TBusinessFocusService {

    /**
     * 根据名称获取智库关注点
     *
     * @param companyId
     * @param businessId
     * @param name
     * @return
     */
    List<BusinessFocusDto> getBusinessFocusList(Long companyId, Long businessId, String name);

    /**
     * 转换关注点名称为ids
     *
     * @param user
     * @param businessId
     * @param focusNames
     * @return
     */
    String convertFocusNamesToFocusIds(TmUser user, Long businessId, String focusNames);

    /**
     * 转换关注点ids为names
     *
     * @param companyId
     * @param businessId
     * @param focusIds
     * @return
     */
    String convertFocusIdsToFocusNames(Long companyId, Long businessId, String focusIds);

    /**
     * 克隆智库下关注点
     *
     * @param user
     * @param sourceCompanyId
     * @param sourceBusinessId
     * @param targetCompanyId
     * @param targetBusinessId
     * @return
     */
    int clone(TmUser user, Long sourceCompanyId, Long sourceBusinessId, Long targetCompanyId, Long targetBusinessId);

}
