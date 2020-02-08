package com.pl.indexserver.service.impl;
import com.pl.indexserver.service.UserService;
import com.pl.mapper.TmUserMapper;
import com.pl.model.TmUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TmUserMapper tmUserMapper;

    @Override
    public int deleteByPrimaryKey(String userid) {
        return tmUserMapper.deleteByPrimaryKey(userid);
    }

    @Override
    public int insert(TmUser record) {
        return tmUserMapper.insert(record);
    }

    @Override
    public int insertSelective(TmUser record) {
        return tmUserMapper.insertSelective(record);
    }

    @Override
    public TmUser selectByPrimaryKey(String userid) {
        return tmUserMapper.selectByPrimaryKey(userid);
    }

    @Override
    public int updateByPrimaryKeySelective(TmUser record) {
        return tmUserMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(TmUser record) {
        return tmUserMapper.updateByPrimaryKey(record);
    }

    @Override
    public TmUser getUserById(String userid) {
        return tmUserMapper.getUserById(userid);
    }

    @Override
    public TmUser getUserByName(String username) {
        return tmUserMapper.getUserByName(username);
    }

    @Override
    public int updatePassword(String userid, String password) {
        return tmUserMapper.updatePassword(userid, password);
    }

    @Override
    public TmUser getUser(String username, String password) {
        return tmUserMapper.getUser(username, password);
    }

    @Override
    public String getCompanyNameCompany_id(Long company_id) {
        return tmUserMapper.getCompanyNameCompany_id(company_id);
    }

	@Override
	public List<Integer> selectPositionIdByUserId(String userid) throws Exception {
		return tmUserMapper.selectPositionIdByUserId(userid);
	}

    @Override
    public long countFileSizeByCompanyId(Long companyId) throws Exception {
        Long size = tmUserMapper.countFileSizeByCompanyId(companyId);
        return size==null?0:size;
    }

    @Override
    public TmUser selectByOpenid(String openid) {
        return tmUserMapper.selectByOpenid(openid);
    }

    @Override
    public int updateOpenidByNameAndPassword(String openid, String username, String password) {
        return tmUserMapper.updateOpenidByNameAndPassword(openid, username, password);
    }
}
