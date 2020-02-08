package com.pl.indexserver.service;


import com.pl.model.TmUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {
    int deleteByPrimaryKey(String userid);

    int insert(TmUser record);

    int insertSelective(TmUser record);

    TmUser selectByPrimaryKey(String userid);

    int updateByPrimaryKeySelective(TmUser record);

    int updateByPrimaryKey(TmUser record);


    TmUser getUserById(String userid);
    TmUser getUserByName(String username);
    int updatePassword(@Param("userid") String userid, @Param("password") String password);
    TmUser getUser(@Param("username") String username, @Param("password") String password);

    String getCompanyNameCompany_id(@Param("company_id") Long company_id);
    
    /**
	 * 根据用户id查询其岗位id
	 *
	 * @param userid
	 *            用户id
	 * @return 返回查询结果
	 */
	List<Integer> selectPositionIdByUserId(String userid) throws Exception;


    /**
     * 根据公司id其下员工头像占用的空间
     *
     * @param companyId
     *            公司id
     * @return 返回统计结果（单位：字节）
     * @throws Exception
     */
    long countFileSizeByCompanyId(Long companyId) throws Exception;

    TmUser selectByOpenid(String openid);

    int updateOpenidByNameAndPassword(String openid, String username, String password);
}
