package com.pl.indexserver.untils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsJg {
	private static final Logger logger = LoggerFactory.getLogger(SmsJg.class);

	/**
	 * curl --insecure -X POST -v https://api.sms.jpush.cn/v1/codes -H
	 * "Content-Type: application/json" \ -u
	 * "24286bbb7fde1ab272b71a6eMaster:5e987ac6d2e04d95a9d8f0d1" -d
	 * '{"mobile":"xxxxxxxxxxx","sign_id":*,"temp_id":*}'
	 * 
	 * @param url
	 * @param body
	 * @return
	 */
	public static String sendSms(String mobile) {
		

		
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {

			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, tm, new java.security.SecureRandom());

			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			String url = "https://api.sms.jpush.cn/v1/codes";
			String temp_id = "1";
			String sign_id = "24286bbb7fde1ab272b71a6e";
			String appKey = "4b9b1c29efd1109cfde08b84";
			String masterSecret = "Basic MjQyODZiYmI3ZmRlMWFiMjcyYjcxYTZlOjRiOWIxYzI5ZWZkMTEwOWNmZGUwOGI4NA==";

			String body = "{\"mobile\":\"" + mobile + "\",\"sign_id\":\"\",\"temp_id\":1}";
			 
			// 打开和URL之间的连接
			URL realUrl = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) realUrl
					.openConnection();
			conn.setSSLSocketFactory(ssf);

			// 设置通用的请求属性
			conn.setRequestProperty("Authorization", masterSecret);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("content-Type", "application/json");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数

			out.print(body);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			System.out.println("-----result-----" + result);
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	
	/**
	 * curl --insecure -X POST -v https://api.sms.jpush.cn/v1/codes/288193860302/valid -H "Content-Type: application/json" \
-u "7d431e42dfa6a6d693ac2d04:5e987ac6d2e04d95a9d8f0d1" -d '{"code":"123456"}'
	 * @param hsUrl
	 * @return
	 */
	
	public static boolean checkSms(String messageId,String code) {	
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {

			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, tm, new java.security.SecureRandom());

			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			
			//curl --insecure -X POST -v https://api.sms.jpush.cn/v1/codes/847660990814/valid  -H "Content-Type: application/json" -u "24286bbb7fde1ab272b71a6e:4b9b1c29efd1109cfde08b84" -d '{"code":"111111"}'
			
			String url = "https://api.sms.jpush.cn/v1/codesapi.sms.jpush.cn/v1/codes/"+messageId+"/valid";
			url = "https://api.sms.jpush.cn/v1/codes/847666665091/valid";
			String temp_id = "1";
			String sign_id = "24286bbb7fde1ab272b71a6e";
			String appKey = "4b9b1c29efd1109cfde08b84";
			String masterSecret = "Basic MjQyODZiYmI3ZmRlMWFiMjcyYjcxYTZlOjRiOWIxYzI5ZWZkMTEwOWNmZGUwOGI4NA==";

			String body = "{\"code\":\"" + code + "\"}";
			 
			// 打开和URL之间的连接
			URL realUrl = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) realUrl
					.openConnection();
			conn.setSSLSocketFactory(ssf);

			// 设置通用的请求属性
			conn.setRequestProperty("Authorization", masterSecret);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("content-Type", "application/json");
			conn.setRequestProperty("user-agent",
					"curl/7.29.0");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数

			out.print(body);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			System.out.println("-----result-----" + result);
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				
			}
		}
		return result.contains("true");
	}
	
	public static String getHttps(String hsUrl) {
		try {
			URL url;

			url = new URL(hsUrl);
			logger.info("getHttps url=" + hsUrl);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			X509TrustManager xtm = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					// TODO Auto-generated method stub

				}
			};

			TrustManager[] tm = { xtm };

			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tm, null);

			con.setSSLSocketFactory(ctx.getSocketFactory());
			con.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});

			InputStream inStream = con.getInputStream();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			byte[] b = outStream.toByteArray();// 网页的二进制数据
			outStream.close();
			inStream.close();
			String object = new String(b, "utf-8");

			logger.info("\ngetHttps url=" + hsUrl + "\nresponse\n" + object);

			return object;

		} catch (Exception e) {
			logger.warn("getHttps error.", e);
		}
		return null;
	}

	public static void main(String[] a) {
		//String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx93e9fc5be7062cc1&secret=57f7cfd23cf48b75e757470c67eeaa50";
		//String ret = sendSms("18750031121");
		Object ret = checkSms("847666665091","580633");
		System.out.println(ret);
	}

}

class MyX509TrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
