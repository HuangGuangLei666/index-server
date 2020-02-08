package com.pl.indexserver.untils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.pl.model.wx.XBMSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(HttpUtil.class);

	public static String get(String urlStr) {
		String ret = null;
		try {

			URL url = new URL(urlStr);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			// 返回结果-字节输入流转换成字符输入流，控制台输出字符
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			ret = sb.toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/* 机器人访问 POST 请求 */
	public static String postRequest(String url, String body) {
		String result = null;
		BufferedReader reader;
		try {
			URL apiUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) apiUrl
					.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			connection.connect();

			PrintWriter writer = new PrintWriter(connection.getOutputStream());
			writer.write(body);
			writer.flush();
			writer.close();

			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), StandardCharsets.UTF_8));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();
			result = buffer.toString();
			return result;
		} catch (Exception e) {
			logger.error(
					"Request url:{} error, request info:{}, error info:{}, response info:{}",
					url, body, e.getMessage(), result, e);
			return result;
		}
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

			logger.info("\ngetHttps url=" + hsUrl + "\nresponse\n"+object);
			
			return object;

		} catch (Exception e) {
			logger.warn("getHttps error." ,e);
		}
		return null;
	}

	public static void main(String[] a) {
		String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" +
				"&appid="+ XBMSConstant.XBMS_WX_APPID +
				"&secret="+ XBMSConstant.XBMS_WX_SECRET;
		String ret = getHttps(url);
		System.out.println(ret);
	}

}
