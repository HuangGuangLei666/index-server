package com.pl.mapper;

import com.pl.model.TmUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TmUserMapper {
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
	List<Integer> selectPositionIdByUserId(String userid);

    /**
     * 根据公司id其下员工头像占用的空间
     *
     * @param companyId
     *            公司id
     * @return 返回统计结果
     */
    @Select("select sum(file_size) from tm_user where company_id=#{companyId}")
    Long countFileSizeByCompanyId(Long companyId);

    /**
     * 根据opendid查询用户
     * @param openid
     * @return
     */
    TmUser selectByOpenid(@Param("openid") String openid);

    /**
     * 根据用户名和密码设置opendid
     * @param openid
     * @param username
     * @param password
     * @return
     */
    int updateOpenidByNameAndPassword(@Param("openid") String openid, @Param("username") String username, @Param("password") String password);
}