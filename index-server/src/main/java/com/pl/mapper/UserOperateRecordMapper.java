package com.pl.mapper;

import com.pl.indexserver.model.CallTaskReturnDto;
import com.pl.indexserver.model.UserOperateRecordDto;
import com.pl.model.UserOperateRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserOperateRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserOperateRecord record);

    int insertSelective(UserOperateRecord record);

    UserOperateRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserOperateRecord record);

    int updateByPrimaryKey(UserOperateRecord record);

    List<UserOperateRecordDto> getLogger(@Param("callTask")CallTaskReturnDto callTask,@Param("type") int type);

    List<UserOperateRecordDto> selectByObject(@Param("userId") String userId, @Param("objectType") Integer objectType,
                                              @Param("object") String object);
}