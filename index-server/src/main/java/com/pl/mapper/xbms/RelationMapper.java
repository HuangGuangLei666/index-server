package com.pl.mapper.xbms;


import com.pl.model.xbms.Relation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RelationMapper {

    Relation selectByUseridAndPhone(@Param("userId") Integer userId, @Param("relationId")Integer relationId);

    int addRelationBinding(Relation relation);

    Relation selectByUserid(@Param("userId")Integer userId, @Param("relationId")Integer relationId);

    List<Relation> selectByRelationUserid(Integer userId);

    int deleterelationBinding(Integer id);

    int updatePassByRelationId(Integer relationId);

    int updateRefuseByRelationId(Integer relationId);
}