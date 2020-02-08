package com.pl.indexserver.service.xbms;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.TUserQa;
import com.pl.model.xbms.UserQa;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/11/29 17:42
 * @Description
 */
public interface UserQaService {

    CheckSmsCodeResp addCustomerresp(UserQa tUserQa);
}
