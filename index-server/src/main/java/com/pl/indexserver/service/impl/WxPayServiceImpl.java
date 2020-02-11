package com.pl.indexserver.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.pl.indexserver.service.WxPayService;
import com.pl.indexserver.untils.BigIntegerUtil;
import com.pl.indexserver.untils.PayUtil;
import com.pl.indexserver.untils.RandomUtils;
import com.pl.mapper.TMealMapper;
import com.pl.mapper.TOrderMapper;
import com.pl.mapper.TQctivationcodeMapper;
import com.pl.mapper.TUserinfoMapper;
import com.pl.model.wx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayServiceImpl implements WxPayService{

    private static final Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Autowired
    private TUserinfoMapper tUserinfoMapper;
    @Autowired
    private TOrderMapper tOrderMapper;
    @Autowired
    private TMealMapper tMealMapper;
    @Autowired
    private TQctivationcodeMapper tQctivationcodeMapper;

    @Override
    public Map<String, Object> openMembershipPay(HttpServletRequest request, String totalFee, Integer goodsId, String unionid) {
        //根据unionid查询用户信息
        TUserinfo tUserinfo = tUserinfoMapper.selectByUnionId(unionid);
//        String openid = tUserinfo.getOpenid();
        try {
            String openid = "ozlCq5eVaVC4EGRBXo_7NzDX_oCo";
            String traNo = WXPayUtil.generateNonceStr();
            String requestIp = XBMSConstant.getRequestIp(request);
            //生成的随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
            //商品名称
            String body = "小兵秘书开通会员支付";

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", XBMSConstant.XBMS_WX_APPLET_APPID);
            packageParams.put("mch_id", XBMSConstant.XBMS_WXPAY_MCH_ID);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", traNo);//商户订单号
            packageParams.put("total_fee", BigIntegerUtil.changeFee(totalFee).toString());//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", requestIp);
            packageParams.put("notify_url", "https://ai.yousayido.net/busiManagement/xbms/openMembershipPayCallback.do");//支付成功后的回调地址
            packageParams.put("trade_type", XBMSConstant.XBMS_WXPAY_TRADE_TYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = PayUtil.sign(prestr, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + XBMSConstant.XBMS_WX_APPLET_APPID + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + XBMSConstant.XBMS_WXPAY_MCH_ID + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + "https://ai.yousayido.net/busiManagement/xbms/openMembershipPayCallback.do" + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + traNo + "</out_trade_no>"
                    + "<spbill_create_ip>" + requestIp + "</spbill_create_ip>"
                    + "<total_fee>" + BigIntegerUtil.changeFee(totalFee).toString() + "</total_fee>"
                    + "<trade_type>" + XBMSConstant.XBMS_WXPAY_TRADE_TYPE + "</trade_type>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            logger.info("调试模式_统一下单接口 请求XML数据：" + xml);

            //生成预支付订单
            String goodsName = "";
            TOrder tOrder = new TOrder();
            if ("0.1".equals(totalFee)) {
                goodsName = "首月体验";
            } else if ("4.9".equals(totalFee)) {
                goodsName = "包月VIP";
            } else if ("12.9".equals(totalFee)) {
                goodsName = "包季VIP";
            } else if ("49".equals(totalFee)) {
                goodsName = "包年VIP";
            }
            tOrder.setUserId(tUserinfo.getId());
            tOrder.setOpenid(tUserinfo.getOpenid());
            tOrder.setGoodsId(goodsId);
            tOrder.setPrice(totalFee);
            tOrder.setNumber(1);
            tOrder.setPayMoney(totalFee);
            tOrder.setStatus(1);
            tOrder.setTradeNo(traNo);
            tOrder.setType("RECHARGE-XBMS");
            //套餐名
            tOrder.setGoodsName(goodsName);
            int i = tOrderMapper.insertOrder(tOrder);
            logger.info("=======是否生成与支付订单i={}========", i);

            //调用统一下单接口，并接受返回的结果
            String result = PayUtil.httpRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", xml);

            logger.info("调试模式_统一下单接口 返回XML数据：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtil.doXMLParse(result);
            logger.info("=========map={}==========", map.toString());

            String return_code = (String) map.get("return_code");//返回状态码

            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if (return_code.equals("SUCCESS")) {
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                //拼接签名需要的参数
                String stringSignTemp = "appId=" + XBMSConstant.XBMS_WX_APPLET_APPID + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id + "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtil.sign(stringSignTemp, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();

                response.put("paySign", paySign);
            }

            response.put("appid", XBMSConstant.XBMS_WX_APPLET_APPID);
            response.put("unionid", unionid);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void openMembershipPayCallback(HttpServletRequest request, HttpServletResponse response) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            String resXml = "";
            logger.info("接收到的报文：" + notityXml);

            Map<String, String> map = null;
            try {
                map = PayUtil.doXMLParse(notityXml);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("=======返回的map={}============",map.toString());
            //商户订单号
            String ordersSn = map.get("out_trade_no");
            //unionid
            String unionid = map.get("unionid");

            String returnCode = (String) map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                //验证签名是否正确
                Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
                String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
                String sign = PayUtil.sign(validStr, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
                //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
                if (sign.equals(map.get("sign"))) {
                    /**此处添加自己的业务逻辑代码start**/

                    //支付成功，更新支付状态
                    tOrderMapper.updOrderStatus(ordersSn);
                    //先查询是否会员，是会员的话过期时间在原来的基础上累加，不是就在当前时间上累加
                    TUserinfo tUserinfo = tUserinfoMapper.selectByUnionId(unionid);
                    //查询套餐信息
                    TMeal tMeal = tMealMapper.selectByTradeno(ordersSn);
                    logger.info("=========ordersSn={}=======",ordersSn);
                    Integer useDays = tMeal.getUseDays();
                    if (StringUtils.isEmpty(tUserinfo.getIsMembership())) {
                        //插入会员信息（之前不是会员）
                        tUserinfoMapper.updateMembershipByUnionid(unionid, useDays);
                    } else {
                        //插入会员信息（之前是会员）
                        tUserinfoMapper.updateMembershipAddByUnionid(unionid, useDays);
                    }

                    /**此处添加自己的业务逻辑代码end**/
                    //通知微信服务器已经支付成功
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                            + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
            } else {
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
            logger.info(resXml);
            logger.info("微信支付回调数据结束");


            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> giveSecretaryPay(HttpServletRequest request, Integer userId, Integer goodsId, String unionid) {
        //根据unionid查询用户信息
        TUserinfo tUserinfo = tUserinfoMapper.selectByUnionId(unionid);
//        String openid = tUserinfo.getOpenid();
        String openid = "ozlCq5eVaVC4EGRBXo_7NzDX_oCo";
        //套餐信息
        TMeal tMeal = tMealMapper.selectBygoodsid(goodsId);
        try {
            String traNo = WXPayUtil.generateNonceStr();
            String requestIp = XBMSConstant.getRequestIp(request);
            //生成的随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
            //商品名称
            String body = "小兵秘书送秘书支付";

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", XBMSConstant.XBMS_WX_APPLET_APPID);
            packageParams.put("mch_id", XBMSConstant.XBMS_WXPAY_MCH_ID);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", traNo);//商户订单号
            packageParams.put("total_fee", BigIntegerUtil.changeFee(tMeal.getPrice()).toString());//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", requestIp);
            packageParams.put("notify_url", "https://ai.yousayido.net/busiManagement/xbms/giveSecretaryPayCallback.do");//支付成功后的回调地址
            packageParams.put("trade_type", XBMSConstant.XBMS_WXPAY_TRADE_TYPE);//支付方式
            packageParams.put("openid", openid);

            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String mysign = PayUtil.sign(prestr, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + XBMSConstant.XBMS_WX_APPLET_APPID + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + XBMSConstant.XBMS_WXPAY_MCH_ID + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + "https://ai.yousayido.net/busiManagement/xbms/giveSecretaryPayCallback.do" + "</notify_url>"
                    + "<openid>" + openid + "</openid>"
                    + "<out_trade_no>" + traNo + "</out_trade_no>"
                    + "<spbill_create_ip>" + requestIp + "</spbill_create_ip>"
                    + "<total_fee>" + BigIntegerUtil.changeFee(tMeal.getPrice()).toString() + "</total_fee>"
                    + "<trade_type>" + XBMSConstant.XBMS_WXPAY_TRADE_TYPE + "</trade_type>"
                    + "<sign>" + mysign + "</sign>"
                    + "</xml>";

            logger.info("调试模式_统一下单接口 请求XML数据：" + xml);

            //生成预支付订单
            String goodsName = "";
            TOrder tOrder = new TOrder();
            if (!StringUtils.isEmpty(goodsId)) {
                switch (goodsId) {
                    case 1:
                        goodsName = "包月VIP";
                    case 2:
                        goodsName = "包季VIP";
                    case 3:
                        goodsName = "包年VIP";
                }
            }
            tOrder.setUserId(tUserinfo.getId());
            tOrder.setOpenid(tUserinfo.getOpenid());
            tOrder.setGoodsId(goodsId);
            tOrder.setPrice(tMeal.getPrice());
            tOrder.setNumber(1);
            tOrder.setPayMoney(tMeal.getPrice());
            tOrder.setStatus(1);
            tOrder.setTradeNo(traNo);
            tOrder.setType("SEND-XBMS");
            //套餐名
            tOrder.setGoodsName(goodsName);
            int i = tOrderMapper.insertOrder(tOrder);
            logger.info("=======是否生成与支付订单i={}========", i);

            //调用统一下单接口，并接受返回的结果
            String result = PayUtil.httpRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", xml);

            logger.info("调试模式_统一下单接口 返回XML数据：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtil.doXMLParse(result);
            logger.info("=========map={}==========", map.toString());

            String return_code = (String) map.get("return_code");//返回状态码

            Map<String, Object> response = new HashMap<String, Object>();//返回给小程序端需要的参数
            if (return_code.equals("SUCCESS")) {
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                //拼接签名需要的参数
                String stringSignTemp = "appId=" + XBMSConstant.XBMS_WX_APPLET_APPID + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id + "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtil.sign(stringSignTemp, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();

                response.put("paySign", paySign);
            }

            response.put("appid", XBMSConstant.XBMS_WX_APPLET_APPID);
            response.put("unionid", unionid);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TQctivationcode giveSecretaryPayCallback(HttpServletRequest request, HttpServletResponse response) {
        String resXml = "";
        Map<String, String> map = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            String notityXml = sb.toString();
            logger.info("接收到的报文：" + notityXml);
            map = PayUtil.doXMLParse(notityXml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //商户订单号
        String ordersSn = map.get("out_trade_no");
        String returnCode = (String) map.get("return_code");
        if ("SUCCESS".equals(returnCode)) {
            //验证签名是否正确
            Map<String, String> validParams = PayUtil.paraFilter(map);  //回调验签时需要去除sign和空值参数
            String validStr = PayUtil.createLinkString(validParams);//把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            String sign = PayUtil.sign(validStr, XBMSConstant.xbms_wxpay_paternerkey, "utf-8").toUpperCase();//拼装生成服务器端验证的签名
            //根据微信官网的介绍，此处不仅对回调的参数进行验签，还需要对返回的金额与系统订单的金额进行比对等
            if (sign.equals(map.get("sign"))) {
                /**此处添加自己的业务逻辑代码start**/

                //更新支付状态
                tOrderMapper.updOrderStatus(ordersSn);
                //生成激活码，插入db
                TOrder order = tOrderMapper.selectByTradeno(ordersSn);
                TQctivationcode qctivationcode = tQctivationcodeMapper.selectByTradeno(ordersSn);
                if (!StringUtils.isEmpty(qctivationcode)) {
                    return new TQctivationcode("此订单已存在，无法二次生成激活码");
                }
                TQctivationcode code = new TQctivationcode();
                String activationCode = RandomUtils.genActivationCode();
                TQctivationcode tQctivationcode = tQctivationcodeMapper.selectByactCode(activationCode);
                if (!StringUtils.isEmpty(tQctivationcode)) {
                    activationCode = RandomUtils.genActivationCode();
                }
                code.setCodeMealId(order.getGoodsId() + "");
                code.setAgentId(order.getUserId());
                code.setStatus(1);
                code.setCodeType("USER_BUY");
                code.setTradeNo(ordersSn);
                code.setCode(activationCode);
                tQctivationcodeMapper.insertTQctivationcode(code);

                logger.info("=======code={}=======", code);

                /**此处添加自己的业务逻辑代码end**/
                //通知微信服务器已经支付成功
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                TQctivationcode qctCode = tQctivationcodeMapper.selectByTradeno(ordersSn);
                logger.info("=========qctCode={}============", qctCode);
                return qctCode;
            }
        } else {
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            return new TQctivationcode("小程序送秘书支付回调失败~~");
        }
        logger.info(resXml);
        logger.info("微信支付回调数据结束");


        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TQctivationcode("小程序送秘书支付回调失败~~");
    }
}
