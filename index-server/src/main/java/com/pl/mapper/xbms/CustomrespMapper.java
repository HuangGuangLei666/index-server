package com.pl.mapper.xbms;


import com.pl.model.xbms.Customresp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomrespMapper {
    List<Customresp> selectByUseridAndKnowledgeId(@Param("userId") Integer userId, @Param("knowledgeId")String knowledgeId);

    Customresp selectByUserIdAndKnowId(@Param("answerId")int i,
                                       @Param("userId")Integer userId,
                                       @Param("knowledgeId")String knowledgeId);

    int addCustomerresp(Customresp tCustomresp);

    int deleteCustomerresp(Integer id);
}