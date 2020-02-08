package com.pl.mapper;

import com.pl.indexserver.model.TOrdDiskInfoDto;
import com.pl.model.TOrdDiskInfo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public interface TOrdDiskInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TOrdDiskInfo record);

    int insertSelective(TOrdDiskInfo record);

    TOrdDiskInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TOrdDiskInfo record);

    int updateByPrimaryKey(TOrdDiskInfo record);

    /**
     * 统计对应公司指定交易状态且和指定过期时间之后所能使用的磁盘空间
     *
     * @param companyId 公司id
     * @param status    订单状态
     * @param expireTtime   过期时间
     * @return
     */
    @Select("select sum(total_space) diskSpaceSize,min(expire_time) expireTtime from t_ord_disk_info " +
            "where company_id=#{companyId} and `status`=#{status} and expire_time>#{expireTtime}")
    TOrdDiskInfoDto selectByCompanyIdAndExpireTime(@Param("companyId") String companyId, @Param("status") String status, @Param("expireTtime") String expireTtime);
}