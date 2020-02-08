package com.pl.indexserver.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HuangGuangLei
 * @Date 2019/11/1
 */
public interface MessageService {
    /**
     * 微信公众号处理
     * @param request
     * @return
     */
    String newMessageRequest(HttpServletRequest request);
}
