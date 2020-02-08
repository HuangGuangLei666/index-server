package com.pl.mapper;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.pl.model.TManual;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TManualMapper {

    List<TManual> getManualList(Pagination pagination, @Param("companyId") Long companyId , @Param("uid") String uid,
                                @Param("status") Integer status, @Param("phone")String phone,
                                @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    TManual getManualByPhone(@Param("taskId") Integer taskId, @Param("companyId") Long companyId, @Param("phone") String phone);

    int deleteByPrimaryKey(Integer id);

    int insertManual(TManual manual);

    int insertSelective(TManual manual);

    int updateByPrimaryKeySelective(TManual manual);

    TManual getManualByPrimaryKey(Integer id);
}
