package com.pl.mapper;

import com.pl.model.TUnknownRecord;
import org.apache.ibatis.annotations.Param;

public interface TUnknownRecordMapper extends BaseMapper<TUnknownRecord> {
    int deleteByPrimaryKey(Long id);

    int insert(TUnknownRecord record);

    int insertSelective(TUnknownRecord record);

    TUnknownRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TUnknownRecord record);

    int updateByPrimaryKey(TUnknownRecord record);

    /**
     * 根据业务id和文本内容忽略对应未识别语句（不做展示）
     *
     * @param taskIds 多任务id组成的字符串
     * @param content 文本内容
     */
    int neglectByBusinessIdAndContent(@Param("taskIds") String taskIds, @Param("content") String content);

}