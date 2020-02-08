package com.pl.mapper;

import java.util.List;
import java.util.Map;

import com.pl.indexserver.model.SpeechcraftModelDto;
import com.pl.model.KnowledgeQuestion;
import com.pl.model.TDialogSelect;
import org.apache.ibatis.annotations.Param;

import com.pl.indexserver.model.SpecialtyTalkDto;
import com.pl.model.Speechcraft;
import org.apache.ibatis.annotations.Select;

public interface SpeechcraftMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Speechcraft record);

	int insertSelective(Speechcraft record);

	Speechcraft selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Speechcraft record);

	int updateByPrimaryKey(Speechcraft record);

	int selectSpecialtyTalkCount(Long id);

	int selectSpecialtyTalkCountByIsRecord(@Param("id")Long id);

	int selectTDialogCountByIStatus(@Param("callTaskIds") String callTaskIds);

	int selectTDialogCountByIsIntention(@Param("callTaskIds") String callTaskIds);

	List<SpecialtyTalkDto> selectSpeechcraftAllForName(Map<String, Object> map);

	List<Speechcraft> selectByCraftId(String craftId);

	List<Speechcraft> selectOfCheckBusiness(@Param("craftId") String craftId, @Param("businessId") Long businessId);

	int deleteByCraftId(String id);

	/**
	 * 查询该录音是否多处使用
	 * 
	 * @param companyId
	 *            公司id
	 * @param businessId
	 *            业务id
	 * @param fileName
	 *            录音文件名
	 * @return 返回查询结果
	 */
	int selectRecordIsUse(@Param("companyId") String companyId, @Param("businessId") String businessId,
			@Param("fileName") String fileName);

	/**
	 * 根据公司id统计其专用话术录音文件所占用的空间大小
	 *
	 * @param companyId 公司id
	 * @return  返回统计结果
	 */
	@Select("select sum(file_size) from speechcraft where company_id=#{companyId}")
	Long countFileSizeByCompanyId(Long companyId);

	/**
	 * 根据话术表示查询部分基础信息
	 * @param craftId
	 * @return
	 */
	@Select("select id, name ,business_id businessId,company_id companyId,content from speechcraft where craft_id = #{craftId} limit 0,1")
	SpeechcraftModelDto selectOneByCraftId(String craftId);

	/**
	 * 根据话术表示修改相关话术的数据
	 * @param speechcraft	专用话术模型对象
	 * @return
	 */
	int updateByCraftIdSelective(Speechcraft speechcraft);

	/**
	 * 根据公司id 智库id查询话术列表
	 *
	 * @param companyId
	 * @param businessId
	 * @return
	 */
	List<Speechcraft> selectByCompanyIdAndBusinessId(@Param("companyId") Long companyId, @Param("businessId") Long businessId);

	/**
	 * 根据公司标识和话术标识获取通用话术
	 * @param companyId 公司标识
	 * @param craftId   话术标识
	 * @return
	 */
	Speechcraft selectByCompanyIdAndCraftId(@Param("companyId")Long companyId, @Param("craftId")String craftId);

	/**
	 * 查询指定智库下包含指定标签的回答数量
	 *
	 * @param businessId 智库标识
	 * @param tag      标签
	 * @return
	 */
	@Select("select count(id) from speechcraft where business_id=#{businessId} and content regexp concat('.*\\\\{\\\\{',#{tag},'\\\\}\\\\}')")
	int countLabelData(@Param("businessId") Long businessId, @Param("tag") String tag);

	/**
	 * 导出节点话术
	 * @param map
	 * @param
	 * @return
	 */
	List<Speechcraft> node(Map<String, Object> map);

	/**
	 * hgl
	 * 获取开场白节点的话术
	 * @param craftId
	 * @return
	 */
    Speechcraft getSpeechcraftByCraftId(String craftId);
}