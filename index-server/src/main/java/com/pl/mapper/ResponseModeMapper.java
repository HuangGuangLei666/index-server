package com.pl.mapper;

import com.pl.indexserver.model.ResponseModeDto;
import com.pl.model.ResponseMode;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ResponseModeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ResponseMode record);

    int insertSelective(ResponseMode record);

    ResponseMode selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ResponseMode record);

    int updateByPrimaryKeyWithBLOBs(ResponseMode record);

    int updateByPrimaryKey(ResponseMode record);

    List<ResponseModeDto> selectByCompanyId(String companyId);

    /**
     * 根据公司id 智库id查询响应方式列表
     *
     * @param companyId
     * @param businessId
     * @return
     */
    List<ResponseMode> selectByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    /**
     * 查询指定智库下包含指定标签的回答数量
     *
     * @param companyId 智库标识
     * @param tag      标签
     * @return
     */
    @Select("select count(id) from response_mode where company_id=#{companyId} and content regexp concat('.\\\\{\\\\{',#{tag},'\\\\}\\\\}')")
    int countLabelData(@Param("companyId") Long companyId, @Param("tag") String tag);

    /**
     * 根据公司id 智库id查询响应方式列表
     *
     * @param companyId
     * @param businessId
     * @return
     */
    List<ResponseMode> selectByWorkFlow(@Param("companyId") Long companyId, @Param("businessId") Long businessId);


    /**
     * 统计是否已经存储三种系统响应方式
     * @param companyId 公司Id
     * @return
     */
    int getSysResponseModeSize(@Param("companyId") Long companyId);

    /**
     * 创建智库时候插入系统响应方式。
     * @param companyId 公司Id
     * @return
     */
    int insertSysResponseMode(@Param("companyId") Long companyId);
}