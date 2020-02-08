package com.pl.indexserver.service;

import com.pl.indexserver.model.ACommonCraftDto;
import com.pl.model.ACommonCraft;
import com.pl.model.TmUser;

import java.util.List;

public interface ACommonCraftService {

    ACommonCraft selectByPrimaryKey(Long id);

    /**
     * 将传入的数据转换成内部对象并修改数据
     * @param aCommonCraftDtos
     * @throws Exception
     */
    void updateByPrimaryKeySelective(List<ACommonCraftDto> aCommonCraftDtos) throws Exception;

    /**
     * 根据公司id统计其通用话术录音文件所占用的空间大小
     * @param companyId 公司id
     * @return 返回统计结果
     * @throws Exception
     */
    long countFileSizeByCompanyId(Long companyId) throws Exception;

    /**
     * 新增一条通用话术回答数据
     * @param aCommonCraftDto   数据模型
     * @return  返回操作结果
     * @throws Exception
     */
    boolean insertAndRecardLog(ACommonCraftDto aCommonCraftDto,TmUser user)throws Exception;
    /**
     * 修改一条通用话术回答数据
     * @param aCommonCraftDto   数据模型
     * @return  返回操作结果
     * @throws Exception
     */
    boolean updateAndRecardLogByPrimaryKey(ACommonCraftDto aCommonCraftDto,TmUser user)throws Exception;
    /**
     * 删除一条通用话术回答数据
     * @param id   主键id
     * @param companyId   公司id
     * @return  返回操作结果
     * @throws Exception
     */
    boolean deleteByPrimaryKey(Long id,Long companyId)throws Exception;

}
