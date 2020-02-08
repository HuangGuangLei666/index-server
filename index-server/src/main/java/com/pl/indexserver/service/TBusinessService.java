package com.pl.indexserver.service;

import com.pl.indexserver.model.TBusinessModelDto;
import com.pl.model.SpeechcraftTag;
import com.pl.model.TBusiness;
import com.pl.model.TmUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TBusinessService {
    int deleteByPrimaryKey(Long id);

    int insert(TBusiness record);

    int insertSelective(TBusiness record);

    TBusiness selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TBusiness record);

    int updateByPrimaryKey(TBusiness record);

    List<TBusiness> getTBusinessListByCompany_id(@Param("company_id") Long company_id) throws Exception;

    /**
     * 根据公司id获取对应的智库方案
     *
     * @param companyId 公司id
     * @return  返回查询到的结果集
     */
    List<TBusinessModelDto> getTBusinessDescribeListByCompanyId(String companyId) throws Exception;

    /**
     *根据业务数据对象保存模型数据
     * @param tBusinessModelDto 业务对象
     * @return
     */
    Boolean insertByTBusinessModelDto(TBusinessModelDto tBusinessModelDto) throws Exception;

    /**
     * 根据idc查询具体智库方案数据
     * @param id    主键id
     * @return  返回查询到的结果
     */
    TBusinessModelDto selectById(Long id);


    /**
     * 复制智库
     *
     * @param user
     * @param companyId
     * @param businessId
     * @param targetCompanyId
     * @return
     */
    int clone(TmUser user, Long companyId, Long businessId, Long targetCompanyId) throws Exception;

    /**
     * 业务中是否有发短信节点
     */
    int countSmsProbably(Long bussiness);

}
