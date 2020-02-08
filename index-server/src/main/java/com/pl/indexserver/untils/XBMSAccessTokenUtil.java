package com.pl.indexserver.untils;

import com.alibaba.fastjson.JSONObject;
import com.pl.model.wx.XBMSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author HuangGuangLei
 * @Date 2019/10/31
 */
@Component
public class XBMSAccessTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(XBMSAccessTokenUtil.class);

    @Autowired
    private RedisClient redisClient;


    private String generateAccessToken() {

        try {
            String url = "https://api.weixin.qq.com/cgi-bin/token?" +
                         "grant_type=client_credential" +
                         "&appid=" + XBMSConstant.XBMS_WX_APPID +
                         "&secret=" + XBMSConstant.XBMS_WX_SECRET;
            String ret = HttpUtil.getHttps(url);
            //{"access_token":"25_SMumCkkqWQTjrcH4hFa-lkq7b6SCb9eE5dye9dGp3-ytOsIQ_u8jxk598dGtuCXn7UdV2a8Pfa_3u1xMTY0o4m-RsodVmWScXhvfGIZxQP4n89VuE1kzmRZkKt_yqsKm7DgrKdJz8NWCpKIdJDFfADAYSU","expires_in":7200}
            JSONObject obj = JSONObject.parseObject(ret);
            String accessToken = obj.getString("access_token");

            redisClient.set("accessToken", accessToken);
            redisClient.set("accessTokenDate", System.currentTimeMillis() + "");
            logger.info("====get new accesstoken=" + accessToken);
            return accessToken;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.warn("generateAccessToken error.", e);
        }
        return null;
    }

    public String getAccessToken() {
        try {
            String accessToken = redisClient.get("accessToken");
            String accessTokenDate = redisClient.get("accessTokenDate");

            if (accessToken == null || accessToken.length() < 5 || accessTokenDate == null || accessTokenDate.length() < 5) {
                logger.info("accessToken or accessTokenDate is null.reflushAccessToken() ");
                accessToken = generateAccessToken();
                return accessToken;
            }
            long accessTokenDateLong = Long.parseLong(accessTokenDate);
            if (System.currentTimeMillis() - accessTokenDateLong > 3600000) {
                accessToken = generateAccessToken();
            }
            logger.debug("getAccessToken accessToken=" + accessToken);
            return accessToken;
        } catch (Exception e) {
            logger.warn("getAccessToken error.", e);
        }
        return null;
    }

}
