package com.pl.model.wx;

import com.pl.indexserver.web.WxController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HuangGuangLei
 * @Date 2019/10/29
 */
public class XBMSConstant {

    private static final Logger logger = LoggerFactory.getLogger(XBMSConstant.class);
    //微信公众号appid
    public static final String XBMS_WX_APPID = "wxec01266416a98d18";

    //小程序appid
    public static final String XBMS_WX_APPLET_APPID = "wxd9302bae6250fc49";

    //微信公众号secret
    public static final String XBMS_WX_SECRET = "1a675765cc3766aa56b323a579bf28b3";

    //微信商户号id
    public static final String XBMS_WXPAY_MCH_ID = "1560224611";

    //微信商户号密钥
    public static final String xbms_wxpay_paternerkey = "TDxw713r5K1kqhEaOxkxIKCvmyQFCwZJ";

    //支付方式
    public static final String XBMS_WXPAY_TRADE_TYPE = "JSAPI";

    //模板id
    public static final String XBMS_WX_TEMPLATE_ID = "tfTD0HlAsHL4v40JBaGlSltl83tIMndWJNtKPpnw4kk";

    //小兵秘书拦截通知模板
    public static final String XBMS_WX_INTERCEPTTEMPLATE_ID = "hmN_tsODUruwWpgWA3L409BIk2duMpQk5CzDeGJtcFg";

    //申请监控号码推送
    public static final String XBMS_WX_BINDINGTEMPLATE_ID = "ufVvk6zFzWygO9-03Omgl_NJHDNN-EyluSvkNT7FPqQ";

    //未完成注册推送
    public static final String XBMS_WX_UNREGISTERTEMPLATE_ID = "jnrv7j2l7F0bw-00QYWVC8ma9OfNXTzBodaHbtOQUI4";

    //日报推送
    public static final String XBMS_WX_DAILYTEMPLATE_ID = "7Am0iPQVkJ6qDmgzwumcwEXj8Qkb1MhsUXGBA92joiA";


    /**
     * 获取用户的ip地址
     * @param request
     * @return
     */
    public static String getRequestIp(HttpServletRequest request) {
        //获取请求ip地址
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.indexOf(",") != -1) {
            String[] ips = ip.split(",");
            ip = ips[0].trim();
        }
        logger.info("=======ip={}=========", ip);

        return ip;
    }
}
