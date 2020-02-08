package com.pl.mapper;

import com.pl.indexserver.model.KnowledgeAnswerDto;
import com.pl.model.KnowledgeAnswer;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface KnowledgeAnswerMapper {
	int deleteByPrimaryKey(Long id);

	int insert(KnowledgeAnswer record);

	int insertSelective(KnowledgeAnswer record);

	int insertSimpleAnswer(KnowledgeAnswer record);

	KnowledgeAnswer selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(KnowledgeAnswer record);

	int updateByPrimaryKeyWithBLOBs(KnowledgeAnswer record);

	int updateByPrimaryKey(KnowledgeAnswer record);

	/**
	 * 根据知识标识查询相关的知识库答案数据
	 *
	 * @param knowledgeId
	 *            知识标识
	 * @return 返回查询到的结果集
	 */
	List<KnowledgeAnswerDto> selectByKnowledgeId(String knowledgeId);

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
	 * 根据公司id统计其知识库问答录音文件所占用的空间大小
	 *
	 * @param companyId 公司id
	 * @return  返回统计结果
	 */
	@Select("select sum(file_size) from knowledge_answerext where company_id=#{companyId}")
	Long countFileSizeByCompanyId(Long companyId);


	/**
	 * 根据知识标识修改相关回答的通用数据
	 * @param knowledgeAnswer	数据模型
	 * @return
	 */
	int updateByKnowledgeIdSelective(KnowledgeAnswer knowledgeAnswer);


	List<KnowledgeAnswer> selectKnowledgeAnswerByKnowledgeId(@Param("knowledgeId") String knowledgeId);

	/**
	 * 查询指定智库中包含指定标签的回答数量
	 *
	 * @param businessId 智库标识
	 * @param tag      标签
	 * @return
	 */
	@Select("select count(id) from knowledge_answerext where business_id=#{businessId} and answer regexp concat('.*\\\\{\\\\{',#{tag},'\\\\}\\\\}')")
	int countLabelData(@Param("businessId") Long businessId, @Param("tag") String tag);

	List<KnowledgeAnswer> selectByCompanyIdBusId(@Param("companyId")Long companyId,@Param("businessId")Long businessId);

	/**
	 * hgl
	 * 根据knowledgeId查询相关问答
	 * @param knowledgeId	知识库id
	 * @return
	 */
	KnowledgeAnswer getAnswerByKnowledgeId(String knowledgeId);

    List<KnowledgeAnswer> selectAnswerByKnowledgeId(String knowledgeId);

    KnowledgeAnswer selectByAnswerIdAndKnowledgeId(@Param("answerId")Long answerId, @Param("knowledgeId")String knowledgeId);
}