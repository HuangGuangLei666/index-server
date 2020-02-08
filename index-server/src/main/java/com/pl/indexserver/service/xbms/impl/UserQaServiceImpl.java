package com.pl.indexserver.service.xbms.impl;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.indexserver.service.TUserQaService;
import com.pl.indexserver.service.xbms.UserQaService;
import com.pl.mapper.TUserQaMapper;
import com.pl.mapper.xbms.UserQaMapper;
import com.pl.model.wx.TUserQa;
import com.pl.model.xbms.UserQa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 17:42
 * @Description
 */
@Service
public class UserQaServiceImpl implements UserQaService {

    @Autowired
    private UserQaMapper userQaMapper;

    @Override
    public CheckSmsCodeResp addCustomerresp(UserQa tUserQa) {
        CheckSmsCodeResp resp = new CheckSmsCodeResp();
        int i = userQaMapper.addCustomerresp(tUserQa);
        if (i < 1) {
            resp.setRetCode(1);
            resp.setRetDesc("设置应答失败");
            return resp;
        }
        resp.setRetCode(0);
        resp.setRetDesc("设置应答成功");
        return resp;
    }
}
