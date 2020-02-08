package com.pl.indexserver.untils.wechat;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;


/**
 * 封装对外访问方法
 * @author liuyazhuang
 *
 */
public class WXCore {

    private static final String WATERMARK = "watermark";
    private static final String APPID = "appid";
    /**
     * 解密数据
     * @return
     * @throws Exception
     */
    public static String decrypt(String appId, String encryptedData, String sessionKey, String iv){
        String result = "";
        try {
            AES aes = new AES();
            byte[] resultByte = aes.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
            if(null != resultByte && resultByte.length > 0){
                result = new String(WxPKCS7Encoder.decode(resultByte));
                JSONObject jsonObject = JSONObject.parseObject(result);
                String decryptAppid = jsonObject.getJSONObject(WATERMARK).getString(APPID);
                if(!appId.equals(decryptAppid)){
                    result = "";
                }
            }
        } catch (Exception e) {
            result = "";
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) throws Exception{
//        String appId = "wxd9302bae6250fc49";
//        String encryptedData = "WFhpj9pq5CZN1dWDiA2r1Jjag/kfAWSlIIGqst/sdJdrWV7dsKYjNa8Rp8Om0QypXDaBS5EIWG92HuMD/gl+QEaVQUcXSbKfefTnUnVEhpc+eVIVdSR23Tc/GvGGczMVOz+FpnoJJ5ZH/iWGNXLk1hO5tP0eeguALI8DYQfFKR4lhoKOa7Rovuf/vR9Bx0n9E32WdEKilGfHilpWCqAfYg==";
//        String sessionKey = "zYb5q5P7cb35ZcW05ovsFw==";
//        String iv = "qxJOXqMYocRGY2xH/u8Cww==";
//        System.out.println(decrypt(appId, encryptedData, sessionKey, iv));
        Long v1 = Long.valueOf(155);
        Long v2 = Long.valueOf(155);
        System.out.println(v1==v2);
    }
}
