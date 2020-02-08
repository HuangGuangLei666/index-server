package com.pl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.pl.indexserver.model.MenuDto;
import com.pl.indexserver.model.PermissionDto;

public interface TAuhPermissionMapper {

	/**
	 * 根据用户id或者岗位id查询对应权限(二选一)
	 * 
	 * @param userId
	 *            用户id
	 * @param positionId
	 *            岗位id
	 * @return 返回查询到的结果集
	 */
	List<PermissionDto> selectByUserIdOrPositionId(@Param("userId") String userId,
			@Param("positionId") String positionId);

	/**
	 * 查询是否需要验证的菜单
	 * 
	 * @param isValidate
	 *            是否需要验证
	 * @return 返回查询到的结果集
	 */
	List<MenuDto> selectByIsValidate(int isValidate);
	
}