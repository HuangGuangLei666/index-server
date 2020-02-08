package com.pl.indexserver.service;

import java.util.List;
import java.util.Map;

import com.pl.indexserver.model.MenuDto;

public interface AuthService {

	/**
	 * 处理用户授权
	 * 
	 * @param userid
	 *            用户id
	 * @return 返回一个包含用户授权信息的字符串
	 * @throws Exception
	 */
    String getUserAuth(String userid) throws Exception;
    
    /**
	 * 查询是否需要验证的菜单
	 * 
	 * @param isValidate
	 *            是否需要验证
	 * @return 返回查询到的结果集
	 * @throws Exception
	 */
	List<MenuDto> selectByIsValidate(int isValidate) throws Exception;
	
	/**
	 * 查询需要验证的菜单
	 * 
	 * @return 返回查询到的结果集
	 * @throws Exception
	 */
	Map<String, Integer> selectIsValidate() throws Exception;
	
}
