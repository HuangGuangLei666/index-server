package com.pl.indexserver.service;

import java.util.Map;

import com.pl.indexserver.model.ReturnMsg;

public interface LoginService {

    //用户登录
    ReturnMsg login(String username, String password);

    //修改密码
    ReturnMsg updateUser(String token, String new_password, String old_password);


}
