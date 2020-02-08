package com.pl.mapper;

import com.pl.model.TAccDiskspace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TAccDiskspaceMapper extends BaseMapper<TAccDiskspace>{
//    int deleteByPrimaryKey(Long companyId);

//    int insert(TAccDiskspace record);

//    int insertSelective(TAccDiskspace record);

//    TAccDiskspace selectByPrimaryKey(Long companyId);

//    int updateByPrimaryKeySelective(TAccDiskspace record);

//    int updateByPrimaryKey(TAccDiskspace record);

    List<TAccDiskspace> getDiskSpaceInfo(@Param("companyId") Long companyId, @Param("beginDate") String beginDate,
                                         @Param("endDate") String endDate);

    /**
     * 查询所有数据
     *
     * @return  返回查询到的结果集
     */
//    List<TAccDiskspace> selectAll();
}