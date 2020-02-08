package com.pl.indexserver.untils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * 调用rest服务客户端服务辅助类
 *
 */
public final class RestClientUtil {

	/**
	 * POST
	 */
	private final static String METHOD_POST = "POST";

	/**
	 * GET
	 */
	private final static String METHOD_GET = "GET";

	/**
	 * DELETE
	 */
	private final static String METHOD_DELETE = "DELETE";
	/**
	 * PUT
	 */
	private final static String METHOD_PUT = "PUT";

	private RestClientUtil() {
	}

	/**
	 * 用POST方式传数据
	 * 
	 * @param serviceUrl
	 * @param jsonParameter
	 * @return
	 * @throws IOException
	 */
	public static String postData(String serviceUrl, String jsonParameter) throws IOException {
		return sendData(serviceUrl, jsonParameter, METHOD_POST);
	}

	/**
	 * GET方式传数据
	 * 
	 * @param serviceUrl
	 * @param jsonParameter
	 * @return
	 * @throws IOException
	 */
	public static String getData(String serviceUrl, String jsonParameter) throws IOException {
		if (!(null == jsonParameter || 0 == jsonParameter.length())) {
			serviceUrl += "?" + jsonParameter;
		}
		return sendData(serviceUrl, jsonParameter, METHOD_GET);
	}

	/**
	 * DELETE方式传数据
	 * 
	 * @param serviceUrl
	 * @param jsonParameter
	 * @return
	 * @throws IOException
	 */
	public static String deleteData(String serviceUrl, String jsonParameter) throws IOException {
		if (!(null == jsonParameter || 0 == jsonParameter.length())) {
			serviceUrl += "?" + jsonParameter;
		}
		return sendData(serviceUrl, jsonParameter, METHOD_DELETE);
	}

	/**
	 * PUT方式传数据
	 * 
	 * @param serviceUrl
	 * @param jsonParameter
	 * @return
	 * @throws IOException
	 */
	public static String putData(String serviceUrl, String jsonParameter) throws IOException {
		return sendData(serviceUrl, jsonParameter, METHOD_PUT);
	}

	/**
	 * 发送数据
	 * 
	 * @param serviceUrl
	 * @param jsonParameter
	 * @param restMethod
	 * @return
	 */
	private static String sendData(String serviceUrl, String jsonParameter, String restMethod) throws IOException {
		HttpURLConnection conn = null;
		OutputStream outPutStream = null;
		Writer writer = null;
		BufferedReader br = null;
		try {
			conn = getURLConnection(serviceUrl, restMethod);
			if (METHOD_POST.equals(restMethod) || METHOD_PUT.equals(restMethod)) {
				outPutStream = conn.getOutputStream();
				writer = new OutputStreamWriter(outPutStream, "UTF-8");
				writer.write(jsonParameter);
				writer.flush();
			}
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = getMessageFromReader(br);
			return sb.toString();
		} finally {
			closeStreams(writer, outPutStream, br);
			if (null != conn) {
				conn.disconnect();
			}
		}
	}

	/**
	 * 从返回流里读取字符流
	 * 
	 * @param br
	 * @return
	 */
	private static StringBuilder getMessageFromReader(BufferedReader br) throws IOException {
		StringBuilder sb = new StringBuilder();
		String returnStrItem = null;
		while ((returnStrItem = br.readLine()) != null) {
			sb.append(returnStrItem);
		}
		return sb;
	}

	/**
	 * 取得URL连接对象
	 * 
	 * @param serviceUrl
	 * @param restMethod
	 * @return
	 */
	private static HttpURLConnection getURLConnection(String serviceUrl, String restMethod)
			throws MalformedURLException, IOException, ProtocolException {
		HttpURLConnection conn;
		URL url = new URL(serviceUrl);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(restMethod);
		if (METHOD_POST.equals(restMethod) || METHOD_PUT.equals(restMethod)) {
			conn.setDoOutput(true);
			conn.setDoInput(true);
		}
		// 只有get请求开始缓存，其他请求不行
		if (!METHOD_GET.equals(restMethod)) {
			conn.setUseCaches(false);
		}
		conn.setRequestProperty("Content-Type", "application/json");
		return conn;
	}

	/**
	 * 关闭存在的流
	 * 
	 * @param streams
	 */
	private static void closeStreams(Closeable... streams) {
		for (Closeable stream : streams) {
			try {
				stream.close();
			} catch (IOException ex) {
				// log.trace(ex.toString());
			}
		}
	}

}