package com.pl.indexserver.service;

import com.alibaba.fastjson.JSONObject;
import com.pl.model.wx.TQctivationcode;
import com.pl.model.wx.TUserinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * @author HGL
 * @Date 2018/12/28
 */
public interface WxService {
	void subscribe(Map<String,String> map);
	void unsubscribe(Map<String,String> map);
	
	String getAccessToken();

    TUserinfo selectUserByPhoneNumber(String phoneNumber);

	TUserinfo selectUserByOpenId(String openid);

    int updateByPrimaryKeySelective(int id);

	int updateByPrimaryKey(Integer id, String phoneNumber, Integer recommenderId, String sonIds);

    String getMediaId();

    void smallerInsert(String jsonString);

    void setPhoneByUnionid(JSONObject jsonObject);

    Map<String,Object> payOfAppletGiveSecretary(HttpServletRequest request, String openid, Integer goodsId, String unionid);

    TQctivationcode wxNotifyGiveSecretary(HttpServletRequest request, HttpServletResponse response);

    TUserinfo selectUserByUserId(Integer userId);

    TUserinfo selectByPhoneNumber(String phoneNumber);

    TUserinfo selectByUserId(Integer userId);

    int updateUser(Integer id, String phoneNumber, Integer recommenderid, String sonIds);
}
