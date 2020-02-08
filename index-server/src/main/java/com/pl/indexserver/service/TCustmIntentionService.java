package com.pl.indexserver.service;

import com.pl.indexserver.model.TCustmIntentionDto;
import com.pl.indexserver.model.TUnknownRecordDto;
import com.pl.indexserver.query.BaseQuery;
import com.pl.indexserver.query.Page;
import com.pl.indexserver.query.TCustmIntentionQuery;
import com.pl.model.TCustmIntention;

import java.util.List;

public interface TCustmIntentionService {

    /**
     * 根据查询对象查询对应的参数
     *
     * @param baseQuery 查询对象
     * @return 返回查询到的结果集
     */
    List<TCustmIntention> selectByQuery(BaseQuery baseQuery) throws Exception;
    /**
     * 根据查询对象统计数量
     *
     * @param baseQuery 查询对象
     * @return 返回统计结果
     */
    Long countByQuery(BaseQuery baseQuery) throws Exception;

    /**
     * 根据任务id和文本内容查询对应任务下该未识别语句涉及到的通话信息
     *
     * @param tCustmIntentionQuery 查询条件
     * @return 返回查询结果
     * @throws Exception
     */
    Page<TCustmIntentionDto> selectByBusinessIdAndContent(TCustmIntentionQuery tCustmIntentionQuery) throws Exception;

}
