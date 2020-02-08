package com.pl.mapper;

import com.pl.indexserver.model.ACommonCraftDto;
import com.pl.model.ACommonCraft;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ACommonCraftMapper {

    int deleteByPrimaryKey(@Param("id")Long id,@Param("companyId") Long companyId);

    int insert(ACommonCraft record);

    int insertSelective(ACommonCraft record);

    ACommonCraft selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ACommonCraft record);

    int updateByPrimaryKey(ACommonCraft record);

    /**
     * 查询该录音是否多处使用
     *
     * @param companyId 公司id
     * @param fileName  录音文件名
     * @return 返回查询结果
     */
    int selectRecordIsUse(@Param("companyId") String companyId, @Param("fileName") String fileName);

    /**
     * 根据公司id统计其通用话术录音文件所占用的空间大小
     *
     * @param companyId 公司id
     * @return  返回统计结果
     */
    @Select("select sum(file_size) from a_common_craft where company_id=#{companyId}")
    Long countFileSizeByCompanyId(Long companyId);
    /**
     * 根据话术标识修改通用数据
     *
     * @param aCommonCraft 模型数据
     * @return
     */
    int updateByCraft(ACommonCraft aCommonCraft);

    /**
     * 查询指定智库下包含指定标签的回答数量
     *
     * @param businessId 智库标识
     * @param tag      标签
     * @return
     */
    @Select("select count(a.id) from a_common_craft a left join q_common_craft q " +
            "on q.craft_id = a.craft_id and q.company_id = a.company_id and q.business_id = a.business_id " +
            "where a.business_id=#{businessId} and q.`status`=0 and a.content regexp concat('.*\\\\{\\\\{',#{tag},'\\\\}\\\\}')")
    int countLabelData(@Param("businessId") Long businessId, @Param("tag") String tag);

    List<ACommonCraft> selectByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

    ACommonCraft getContentBycfidAndBsidAndCpid(@Param("craftId")String craftId,
                                                @Param("businessId")Long businessId,
                                                @Param("companyId")Long companyId);

    List<ACommonCraft> selectByCraftIdAndCompanyIdAndBusinessId(@Param("craftId")String craftId,
                                                                @Param("companyId")Long companyId,
                                                                @Param("businessId")Long businessId);

    /**
     * hgl
     * 查询系统通用的话术
     * @param craftId
     * @param companyId
     * @param businessId
     * @return
     */
    ACommonCraft selectContentByCraftIdAndCompanyIdAndBusinessId(@Param("craftId")String craftId,
                                                                 @Param("companyId")Long companyId,
                                                                 @Param("businessId")Long businessId);
}
