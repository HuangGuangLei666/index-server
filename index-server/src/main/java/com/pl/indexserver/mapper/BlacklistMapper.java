package com.pl.indexserver.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.pl.indexserver.model.BlacklistDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author HGL
 * @Date 2018/12/28
 */
public interface BlacklistMapper {

    int deleteById(Long id);

    BlacklistDto selectByPrimaryKey(Long id);

    void addBlacklist(@Param("blacklistDtoList") List<BlacklistDto> blacklistDtoList);

    List<String> selectCompanyBlackPhoneList(Long companyId);

    List<BlacklistDto> queryBlacklistPage(Pagination pagination,
                                          @Param("companyId")Long companyId,
                                          @Param("ctPhone")String ctPhone);

    List<BlacklistDto> queryBlacklistListByCompanyId(Long companyId);
}
