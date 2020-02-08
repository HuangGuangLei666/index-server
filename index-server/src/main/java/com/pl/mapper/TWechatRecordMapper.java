package com.pl.mapper;

import com.pl.model.TWechatRecord;
import org.apache.ibatis.annotations.Param;

public interface TWechatRecordMapper {
    int deleteByPrimaryKey(TWechatRecord wechatRecord);

    int insert(TWechatRecord wechatRecord);

    /**
     * hgl
     * 没有此记录，新增一条
     * @param wechatRecord
     * @return
     */
    int insertSelective(TWechatRecord wechatRecord);

    TWechatRecord selectByPrimaryKey(TWechatRecord wechatRecord);

    int updateByPrimaryKeySelective(TWechatRecord wechatRecord);

    int updateByPrimaryKey(TWechatRecord wechatRecord);

    /**
     * hgl
     * 通过robotWechatId userWechatId businessId是否由此记录
     * @param robotWechatId
     * @param userWechatId
     * @param businessId
     * @return
     */
    TWechatRecord selectOneWorkNodeByWechatIds(@Param("robotWechatid")String robotWechatId,
                                               @Param("userWechatid")String userWechatId,
                                               @Param("businessId")Long businessId);

    void updateWorkNodeIdByRecord(@Param("id")Long id, @Param("nextId")Long nextId);

    /**
     * hgl
     * 跳到下一节点，更新当前的节点id
     * @param id
     * @param paramter
     */
    void updateByRecord(@Param("id")Long id, @Param("paramter")String paramter);
}