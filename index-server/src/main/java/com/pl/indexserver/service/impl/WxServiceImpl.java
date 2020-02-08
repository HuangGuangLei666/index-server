package com.pl.indexserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import com.pl.indexserver.service.WxService;
import com.pl.indexserver.untils.*;
import com.pl.mapper.*;
import com.pl.model.wx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author HGL
 * @Date 2018/12/28
 */
@Service
public class WxServiceImpl implements WxService {
    private static final Logger logger = LoggerFactory.getLogger(WxServiceImpl.class);


    @Value("${xiaobingsecretary.AppId}")
    private String appId;
    @Value("${xiaobingsecretary.AppSecret}")
    private String appSecret;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TUserinfoMapper tUserinfoMapper;
    @Autowired
    private TMallMapper tMallMapper;
    @Autowired
    private TMealMapper tMealMapper;
    @Autowired
    private TOrderMapper tOrderMapper;
    @Autowired
    private TQctivationcodeMapper tQctivationcodeMapper;

    @Override
    public TUserinfo selectUserByPhoneNumber(String phoneNumber) {
        return tUserinfoMapper.selectUserByPhoneNumber(phoneNumber);
    }

    @Override
    public TUserinfo selectUserByOpenId(String openid) {
        /*try {
            String media_id = upload();
            logger.info("=========media_id={}============",media_id);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }*/
        return tUserinfoMapper.selectUserByOpenId(openid);
    }

    @Override
    public int updateByPrimaryKeySelective(int id) {
        return tUserinfoMapper.updateByPrimaryKeySelective(id);
    }

    @Override
    public int updateByPrimaryKey(Integer id, String phoneNumber, Integer recommenderId, String sonIds) {
        return tUserinfoMapper.updateByPrimaryKey(id, phoneNumber,recommenderId,sonIds);
    }

    @Override
    public String getMediaId() {
        String mediaId = "";
        try {
            mediaId = upload();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return mediaId;
    }

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


    /**
     * {
     "subscribe": 1,
     "openid": "oPHkiwj9faf3D3o0mtuwgR9lGryo",
     "nickname": "老滢子.avi",
     "sex": 1,
     "language": "zh_CN",
     "city": "广州",
     "province": "广东",
     "country": "中国",
     "headimgurl": "http://thirdwx.qlogo.cn/mmopen/ZVWXzDLhdGbWxhOOCZDPuMMB3GzYGlvRvXnhgrT4aR4qvP6hsKt3s8ysoGh4VYn9Wib8ydlrJn5LOfHx168TAFeMbPglgh7E5/132",
     "subscribe_time": 1568601747,
     "remark": "",
     "groupid": 0,
     "tagid_list": [ ],
     "subscribe_scene": "ADD_SCENE_QR_CODE",
     "qr_scene": 0,
     "qr_scene_str": ""
     }
     * @param map
     */
    public void subscribe(Map<String, String> map) {
        String openid = map.get("FromUserName");
        String accessToken = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?" +
                "access_token=" + accessToken +
                "&openid=" + openid;

        String ret = HttpUtil.getHttps(url);
        //{"access_token":"25_SMumCkkqWQTjrcH4hFa-lkq7b6SCb9eE5dye9dGp3-ytOsIQ_u8jxk598dGtuCXn7UdV2a8Pfa_3u1xMTY0o4m-RsodVmWScXhvfGIZxQP4n89VuE1kzmRZkKt_yqsKm7DgrKdJz8NWCpKIdJDFfADAYSU","expires_in":7200}
        JSONObject obj = JSONObject.parseObject(ret);

        TUserinfo user = new TUserinfo();

        user.setOpenid(openid);
        String subscribe = obj.getString("subscribe");
        user.setSubscribe(subscribe);

        String nickname = obj.getString("nickname");
        user.setNickname(nickname);

        String sex = obj.getString("sex");
        user.setSex(sex);

        String language = obj.getString("language");
        user.setLanguage(language);

        String city = obj.getString("city");
        user.setCity(city);

        String province = obj.getString("province");
        user.setProvince(province);


        String country = obj.getString("country");
        user.setCountry(country);

        String headimgurl = obj.getString("headimgurl");
        user.setHeadimgurl(headimgurl);

        String subscribeTime = obj.getString("subscribe_time");

        user.setSubscribeTime(new Date(1000 * Long.parseLong(subscribeTime)));

        String remark = obj.getString("remark");
        user.setRemark(remark);

        int groupid = obj.getInteger("groupid");
        user.setGroupid(groupid);

        String tagidList = obj.getString("tagid_list");
        user.setTagidList(tagidList);

        String subscribeScene = obj.getString("subscribe_scene");
        user.setSubscribeScene(subscribeScene);

        String qrScene = obj.getString("qr_scene");
        user.setQrScene(qrScene);

        String qrSceneStr = obj.getString("qr_scene_str");
        user.setQrSceneStr(qrSceneStr);
        //unionid
        String unionid = obj.getString("unionid");
        user.setUnionid(unionid);

        user.setCreateTime(new Date());

        TUserinfo dbUser = tUserinfoMapper.selectByPrimaryKey(openid);
        if (dbUser == null) {
            tUserinfoMapper.insert(user);
            logger.info("insert user success. obj=" + obj);

            TUserinfo tUserinfo = tUserinfoMapper.selectUserIdByOpenId(openid);
            tMallMapper.insertBaseData(tUserinfo.getId());
        } else {
            //TUserinfo updUser = new TUserinfo();
            logger.info("user alread exists. obj=" + obj);
        }
    }


    public void unsubscribe(Map<String, String> map) {
        logger.info("unsubscribe. map=" + map);
    }


    /**
     * 获取 media_id
     //     * @param filePath
     //     * @param accessToken
     //     * @param type
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws KeyManagementException
     */
    public String upload() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        String filePath = "C:\\Users\\Pulan\\Desktop\\假笑男孩.jpg";
        String accessToken = getAccessToken();
        String type = "image";

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
        String uploadUrl = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

        String url = uploadUrl.replace("ACCESS_TOKEN", accessToken).replace("TYPE",type);

        URL urlObj = new URL(url);
        //连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        try {
            //定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        JSONObject jsonObj = JSONObject.parseObject(result);
        System.out.println(jsonObj);
        String typeName = "media_id";
        if(!"image".equals(type)){
            typeName = type + "_media_id";
        }
        String mediaId = jsonObj.getString(typeName);
        return mediaId;
    }

    @Override
    public void smallerInsert(String jsonString) {
        JSONObject userinfo = JSONObject.parseObject(jsonString);
        TUserinfo tUserinfo = new TUserinfo();
        if (!StringUtils.isEmpty(userinfo.getString("avatarUrl"))) {
            tUserinfo.setHeadimgurl(userinfo.getString("avatarUrl"));
        }
        if (!StringUtils.isEmpty(userinfo.getString("city"))) {
            tUserinfo.setCity(userinfo.getString("city"));
        }
        if (!StringUtils.isEmpty(userinfo.getString("country"))) {
            tUserinfo.setCountry(userinfo.getString("country"));
        }
        if (!StringUtils.isEmpty(userinfo.getString("gender"))) {
            tUserinfo.setSex(userinfo.getString("gender"));
        }
        if (!StringUtils.isEmpty(userinfo.getString("language"))) {
            tUserinfo.setLanguage(userinfo.getString("language"));
        }
        if (!StringUtils.isEmpty(userinfo.getString("nickName"))) {
            tUserinfo.setNickname(userinfo.getString("nickName"));
        }
        String openid = userinfo.getString("openId");
        if (!StringUtils.isEmpty(openid)) {
            tUserinfo.setOpenid(openid);
        }
        if (!StringUtils.isEmpty(userinfo.getString("province"))) {
            tUserinfo.setProvince(userinfo.getString("province"));
        }
        String unionid = userinfo.getString("unionId");
        if (!StringUtils.isEmpty(unionid)) {
            tUserinfo.setUnionid(unionid);
        }
        tUserinfo.setCreateTime(new Date());
        TUserinfo dbUser = tUserinfoMapper.selectByUnionid(unionid);
        if (dbUser == null) {
            tUserinfoMapper.insert(tUserinfo);

            TUserinfo tUserinfo2 = tUserinfoMapper.selectByUnionid(unionid);
            tMallMapper.insertBaseData(tUserinfo2.getId());
        } else {
            //TUserinfo updUser = new TUserinfo();
            logger.info("user alread exists. obj=" + jsonString);
        }
    }

    @Override
    public void setPhoneByUnionid(JSONObject json) {
        String unionid = json.getString("unionid");
        String phone = json.getString("phoneNumber");
        tUserinfoMapper.setPhoneByUnionid(phone, unionid);
    }

    @Override
    public Map<String, Object> payOfAppletGiveSecretary(HttpServletRequest request, String openid, Integer goodsId, String unionid) {
        //根据unionid查询用户信息
        TUserinfo tUserinfo = tUserinfoMapper.selectByUnionid(unionid);
        //套餐信息
        TMeal tMeal = tMealMapper.selectBygoodsId(goodsId);
        try {
            String traNo = WXPayUtil.generateNonceStr();
            String requestIp = XBMSConstant.getRequestIp(request);
            //生成的随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
            //商品名称
            String body = "送秘书小程序支付";

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", XBMSConstant.XBMS_WX_APPLET_APPID);
            packageParams.put("mch_id", XBMSConstant.XBMS_WXPAY_MCH_ID);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", traNo);//商户订单号
            packageParams.put("total_fee", BigIntegerUtil.changeFee(tMeal.getPrice()).toString());//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", requestIp);
            packageParams.put("notify_url", "https://ai.yousayido.net/busiManagement/wxgzh/wxNotifyGiveSecretary");//支付成功后的回调地址
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
                    + "<notify_url>" + "https://ai.yousayido.net/busiManagement/wxgzh/wxNotifyGiveSecretary" + "</notify_url>"
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
            tOrder.setType("SEND-APPLET");
            //套餐名
            tOrder.setGoodsName(goodsName);
            int i = tOrderMapper.addOrder(tOrder);
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
    public TQctivationcode wxNotifyGiveSecretary(HttpServletRequest request, HttpServletResponse response) {
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
                tOrderMapper.updateOrderStatus(ordersSn);
                //生成激活码，插入db
                TOrder order = tOrderMapper.selectByTradeNo(ordersSn);
                TQctivationcode qctivationcode = tQctivationcodeMapper.selectByTradeNo(ordersSn);
                if (!StringUtils.isEmpty(qctivationcode)) {
                    return new TQctivationcode("此订单已存在，无法二次生成激活码");
                }
                TQctivationcode code = new TQctivationcode();
                String activationCode = RandomUtils.genActivationCode();
                TQctivationcode tQctivationcode = tQctivationcodeMapper.selectByCode(activationCode);
                if (!StringUtils.isEmpty(tQctivationcode)) {
                    activationCode = RandomUtils.genActivationCode();
                }
                code.setCodeMealId(order.getGoodsId() + "");
                code.setAgentId(order.getUserId());
                code.setStatus(1);
                code.setCodeType("USER_BUY");
                code.setTradeNo(ordersSn);
                code.setCode(activationCode);
                tQctivationcodeMapper.addTQctivationcode(code);

                logger.info("=======code={}=======", code);

                /**此处添加自己的业务逻辑代码end**/
                //通知微信服务器已经支付成功
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                TQctivationcode qctCode = tQctivationcodeMapper.selectByTradeNo(ordersSn);
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

    @Override
    public TUserinfo selectUserByUserId(Integer userId) {
        return tUserinfoMapper.selectOpenidByUserId(userId);
    }

    @Override
    public TUserinfo selectByPhoneNumber(String phoneNumber) {
        return tUserinfoMapper.selectByPhoneNumber(phoneNumber);
    }

    @Override
    public TUserinfo selectByUserId(Integer userId) {
        return tUserinfoMapper.selectByUserId(userId);
    }

    @Override
    public int updateUser(Integer id, String phoneNumber, Integer recommenderid, String sonIds) {
        return tUserinfoMapper.updateUser(id, phoneNumber, recommenderid, sonIds);
    }

}
