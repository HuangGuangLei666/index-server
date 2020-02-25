package com.pl.indexserver.untils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class AsrBaidu {

	
	private static final Logger logger = LoggerFactory.getLogger(AsrBaidu.class);

	
	 private static final String serverURL = "http://vop.baidu.com/server_api";
	    private static String token = "";
	    private static final String testFileName = "F:/lsb/013613004847_d72e3645-7770-46cf-a079-24143f7a0504_2.wav"; // 百度语音提供技术支持
	    //put your own params here
	    // 下面3个值要填写自己申请的app对应的值
	    private static final String apiKey = "hG9yh4apYWSHIposBaloWscb";
	    private static final String secretKey = "sGYzokGt3mZXeU0aARFqjVsvUlw54lhT";
	    private static final String cuid = "15513669";
	 
	    /*public static void main(String[] args) throws Exception {
	       // getToken();
	       // method1();
	       long t = System.currentTimeMillis();
	       File pcmFile = new File(testFileName);
	       byte [] audioDatas = loadFile(pcmFile);
	       String ret = asr(audioDatas,true);


	       System.out.println("\n===ret= " + ret + "=====" +(System.currentTimeMillis()-t));
	    }*/

	public static void main(String[] args) throws Exception {
		// getToken();
		// method1();
		long t = System.currentTimeMillis();
		File pcmFile = new File("C:\\Users\\Pulan\\Desktop\\女声\\dfe7fa05bdde0753ccabd48a307a8496.wav");
		byte[] audioDatas = loadFile(pcmFile);
		String ret = asr(audioDatas, true);


		System.out.println("\n===ret= " + ret + "=====" +(System.currentTimeMillis()-t));
	}
	 
	    private static String  getToken() throws Exception {
	        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + 
	            "&client_id=" + apiKey + "&client_secret=" + secretKey;
	        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
	        token =   printResponse(conn).getString("access_token");
	        return token;
	    }
	 
	    public static String asr(byte[] audioDatas,boolean isReDo)  {
	        
	    	String ret = "";
	        try {
				HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();
 
				// construct params
				JSONObject params = new JSONObject();
				params.put("format", "pcm");
				params.put("rate", 8000);
				params.put("channel", "1");
				
				token = getToken() ;
				params.put("token", token);
				params.put("lan", "zh");
				params.put("cuid", cuid);
				params.put("len", audioDatas.length);
				params.put("speech", DatatypeConverter.printBase64Binary(audioDatas));
 
				// add request header
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
 
				conn.setDoInput(true);
				conn.setDoOutput(true);
 
				// send request
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				String res = params.toString();
				wr.writeBytes(res);
				wr.flush();
				wr.close();
 
				JSONObject jsonObject = printResponse(conn);
				
				if (jsonObject == null)
				{
					logger.info("========jsonObject=" +jsonObject);
					return "";
				}
				else
				{
					
					if (isReDo && "json param token error.".equals(jsonObject.getString("err_msg")))
					{
						 getToken();
						 ret =  asr( audioDatas,false);
						 if (ret.length() ==0)
							{
								logger.info("========ret=" +ret);
							 
							}
						 return ret;
					}
				}
				String errMsg = jsonObject.getString("err_msg");
				if ("success.".equals(errMsg))
				{
					ret = jsonObject.getString("result");
				}
				else
				{
					logger.warn("asrBaidu error. jsonObject=" +jsonObject);
				}
		 
			} catch (Exception e) {
				logger.warn("asrBaidu Exception. Exception=" + e.toString(), e);
			}
	        ret = ret.replaceAll("\\[\"", "");
	        ret = ret.replaceAll("\"\\]", "");
	        ret = ret.replaceAll("。", "");
	        return ret;
	    }
	 
//	    private static void method2() throws Exception {
//	        File pcmFile = new File(testFileName);
//	        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL
//	                + "?cuid=" + cuid + "&token=" + token).openConnection();
//	 
//	        // add request header
//	        conn.setRequestMethod("POST");
//	        conn.setRequestProperty("Content-Type", "audio/pcm; rate=8000");
//	 
//	        conn.setDoInput(true);
//	        conn.setDoOutput(true);
//	 
//	        // send request
//	        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
//	        wr.write(loadFile(pcmFile));
//	        wr.flush();
//	        wr.close();
//	 
//	     //   System.out.println( getUtf8String(printResponse(conn).toString()));
//	    }
	 
	    private static JSONObject printResponse(HttpURLConnection conn) throws Exception {
	        if (conn.getResponseCode() != 200) {
	            // request error
	        	logger.info("conn.getResponseCode() = " + conn.getResponseCode());
	            return null;
	        }
	        InputStream is = conn.getInputStream();
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	        String line;
	        StringBuffer response = new StringBuffer();
	        while ((line = rd.readLine()) != null) {
	            response.append(line);
	            response.append('\r');
	        }
	        rd.close();
	        JSONObject  json =  JSONObject.parseObject(response.toString());
	       // System.out.println(json );
	        return json;
	    }
	 
	    public static byte[] loadFile(File file)   {
	        byte[] bytes = null;
			try {
				InputStream is = new FileInputStream(file);
 
				long length = file.length();
				bytes = new byte[(int) length];
 
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length
				        && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				    offset += numRead;
				}
 
				if (offset < bytes.length) {
				    is.close();
				    throw new IOException("Could not completely read file " + file.getName());
				}
 
				is.close();
			 
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return bytes;
	    }
//	    
//	    // GBK编码转为UTF-8
//	    private static String getUtf8String(String s) throws UnsupportedEncodingException
//	    {
//	    	StringBuffer sb = new StringBuffer();
//	    	sb.append(s);
//	    	String xmlString = "";
//	    	String xmlUtf8 = "";
//			xmlString = new String(sb.toString().getBytes("GBK"));
//			xmlUtf8 = URLEncoder.encode(xmlString , "GBK");
//	    	
//	    	return URLDecoder.decode(xmlUtf8, "UTF-8");
//	    }
 

}
