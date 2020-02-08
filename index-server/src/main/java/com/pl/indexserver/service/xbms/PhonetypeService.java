package com.pl.indexserver.service.xbms;

import com.pl.indexserver.model.CheckSmsCodeResp;
import com.pl.model.wx.InterceptSta;
import com.pl.model.wx.TPhonetype;
import com.pl.model.xbms.Phonetype;

import java.util.List;

/**
 * @version 1.0
 * @Author Guanglei Huang
 * @Date 2019/12/3 10:43
 * @Description
 */
public interface PhonetypeService {
    CheckSmsCodeResp phoneTypeAdd(Phonetype tPhonetype);

    List<Phonetype> phoneTypeQry(Integer userId, String type);
}
