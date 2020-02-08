package com.pl.mapper;

import com.pl.indexserver.model.TCustmIntentionDto;
import com.pl.indexserver.query.BaseQuery;
import com.pl.indexserver.query.TCustmIntentionQuery;
import com.pl.model.TCustmIntention;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TCustmIntentionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TCustmIntention record);

    int insertSelective(TCustmIntention record);

    TCustmIntention selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TCustmIntention record);

    int updateByPrimaryKey(TCustmIntention record);

    /**
     * 根据查询对象查询对应数据
     *
     * @param baseQuery 查询对象
     * @return 返回查询到的结果集
     */
    List<TCustmIntention> selectByQuery(BaseQuery baseQuery);

    /**
     * 根据查询对象统计数量
     *
     * @param baseQuery 查询对象
     * @return 返回统计结果
     */
    Long countByQuery(BaseQuery baseQuery);

    /**
     * 根据任务id和未识别文本关联查询查询该未识别语句涉及到的通话信息
     *
     * @param tCustmIntentionQuery   查询对象
     * @return 返回查询到的结果集
     */
    List<TCustmIntentionDto> selectByTaskIdAndContent(TCustmIntentionQuery tCustmIntentionQuery);
    /**
     * 根据任务id和未识别文本关联查询统计该未识别语句涉及到的通话信息数量
     *
     * @param tCustmIntentionQuery   查询对象
     * @return 返回统计结果
     */
    int countByTaskIdAndContent(TCustmIntentionQuery tCustmIntentionQuery);
}