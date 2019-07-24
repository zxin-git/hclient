package com.zxin.java.common.http.client;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 联动请求工具类
 * 依赖 jdk 与 httpclient、httpclient、slf4j
 *
 * @author zxin
 * @since jdk1.7 httpclient4.4
 *  
 * maven依赖:
 <dependency>
 	<groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.4</version> 4.4以上，不支持httpclient5.0
 </dependency>
 <dependency>  
 	<groupId>org.slf4j</groupId> 
    <artifactId>slf4j-api</artifactId>  
    <version>1.7.2</version>  
 </dependency> 
 */
public class HttpPoolClientUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpPoolClientUtils.class);
	
	public static final int DEFAULT_REQUEST_TIMEOUT = 5 * 1000;

	public static final int DEFAULT_CONNECT_TIMEOUT = 1 * 1000;
	
	/**
	 * 从连接池中获取连接 超时 默认 1s
	 */
	public static final int DEFAULT_CONNECT_REQUEST_TIMEOUT = 500;
	
	/**
	 * 最大池连接数
	 */
	public static final int DEFAULT_MAX_TOTAL = 2000;
	
	/**
	 * 单路由最大连接数
	 */
	public static final int DEFAULT_MAX_PER_ROUTE = 1000;
	
	private static CloseableHttpClient poolHttpClient = null;
	
	/**
	 * 构造器私有化，只作为静态工具类
	 */
	private HttpPoolClientUtils() {
		
	}
	
	/**
	 * 获取httpclient实例
	 * 双重校验锁单例
	 * @return
	 */
	public static CloseableHttpClient getHttpclient(){
		if(poolHttpClient == null){
			synchronized(HttpPoolClientUtils.class){
				if(poolHttpClient == null){
					poolHttpClient = createPoolHttpClient();
//					poolHttpClient = HttpPoolClientBuilder.createPoolHttpClientByConfig();
				}
			}
		}
		return poolHttpClient;
	}
	
	
	/**
	 * 创建http连接池
	 * 信任自签名证书，无主机验证 
	 * @return
	 */
	static CloseableHttpClient createPoolHttpClient(){
		CloseableHttpClient httpClient = null;
		try {
			//------ssl-----------信任所有主机，信任自签名证书；
			SSLContext sc = SSLContext.getInstance("TLS");
	    	sc.init(null, new TrustManager[] {  SkipX509TrustManager.INSTANCE }, new SecureRandom());
	    	SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
			
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslsf).build();
			
			//----pool-------
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL);
			connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
			
			//----timeout---
			RequestConfig config = RequestConfig.custom()
					.setConnectionRequestTimeout(DEFAULT_CONNECT_REQUEST_TIMEOUT)
					.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
					.setSocketTimeout(DEFAULT_REQUEST_TIMEOUT).build();
			
			httpClient = HttpClients.custom()
					.setConnectionManager(connectionManager)
					.setDefaultRequestConfig(config)
					.setRedirectStrategy(new LaxRedirectStrategy())
					.build();
			
//			HttpHost proxy = new HttpHost("10.102.1.75", 8890);  
//			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//			httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(config).setRoutePlanner(routePlanner).build();
			
		} catch (Exception e) {
			logger.error("初始化 httpclient时出现异常", e);
			throw new RuntimeException(e);
		}
		
		return httpClient;
	}
	
	
	/**
	 * 发送get请求
	 * @param url 请求地址与参数，参数由外部用户自己处理url编码
	 * @return 
	 * @throws Exception
	 */
	public static String get(String url) throws Exception{
		logger.info("发送的get请求路径：{}", url);
		HttpGet httpGet = new HttpGet(url);
		String response = send(httpGet);
		return response;
	}
	
	/**
	 * post请求
	 * Content-type：text/plain
	 * 仅限调用联动接口
	 * @param url 请求地址
	 * @param requestContent 请求体
	 * @return
	 * @throws Exception 
	 */
	public static String post(String url, String requestContent) throws Exception{
		logger.info("发送的pos请求路径："+url+"发送的报文："+requestContent);
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "text/plain");
		
		StringEntity reqEntity = new StringEntity(requestContent, Consts.UTF_8);
		httpPost.setEntity(reqEntity);
		
		String responseContent = send(httpPost);
		logger.info("收到的响应报文："+responseContent);
		return responseContent;
	}
	
	
	/**
	 * post请求
	 * @param url 请求地址
	 * @param contentType post类型
	 * @param requestContent 请求体
	 * @return
	 * @throws Exception 
	 */
	public static String post(String url, String contentType, String requestContent) throws Exception{
		logger.info("发送的pos请求路径："+url+"发送的报文："+requestContent);
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(HTTP.CONTENT_TYPE, contentType);
		
		StringEntity reqEntity = new StringEntity(requestContent, Consts.UTF_8);
		httpPost.setEntity(reqEntity);
		
		String responseContent = send(httpPost);
		logger.info("收到的响应报文："+responseContent);
		return responseContent;
	}
	
	/**
	 * post json
	 * @param url
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String url, String json) throws Exception{
		return post(url, "application/json", json);
	}
	
	/**
	 * 传输post表单数据
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, String> map) throws Exception{
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if(map!=null){
			for (Map.Entry<String, String> entry : map.entrySet()) {
				formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		StringEntity requestEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
		httpPost.setEntity(requestEntity);
		
		String responseContent = send(httpPost);
		logger.info("收到的响应报文："+responseContent);
		return responseContent;
	}
	
	
	/**
	 * 发送http请求
	 * 逻辑处理较简略，仅限工具类内部使用
	 * @param httpRequestBase
	 * @return
	 * @throws Exception
	 */
	static String send(HttpRequestBase httpRequestBase) throws Exception{
		String responseContent = null;
		CloseableHttpResponse resp = null;
		try {
			resp = getHttpclient().execute(httpRequestBase);
			HttpEntity entity  = resp.getEntity();
			if (entity != null) {
				responseContent = EntityUtils.toString(entity, Consts.UTF_8);
			}
			
			if(resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
				logger.info("响应的异常信息：{}", responseContent);
				httpRequestBase.abort();
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (resp != null) {
					resp.close();
				}
			} catch (Exception e) {
				logger.error("关闭请求连接失败", e);
			}
		}
		return responseContent;
	}
	
	
	public static void main(String[] args) {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request>  <identityNo>150104198211200513</identityNo>  <license>4vnvm648tz1inki2qxa2</license>  <datetime>20181029165601</datetime>  <funcode>Gup107822</funcode>  <transid>postTest</transid>  <name>%E6%BB%A1%E9%B8%BF%E5%BF%97</name>  <sign>97c3fe82b5ece5a77420bf04692a84b5</sign>  <merid>10001234</merid>  <childmerid>YLZHMYTK</childmerid></request>";
		try {
//			String response = post("http://10.102.5.53:9005//umpaydc/dataQuery/", content);
			String response = post("https://103.235.230.237:8444//umpaydc/dataQuery/", content);
//			String response = get("https://10.102.1.75/v2/dps-registry/tags/list");
			System.out.println(response);
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	
	/**
	 * 绕过证书验证
	 * @author zxin
	 */
	private static class SkipX509TrustManager implements X509TrustManager {
		
		/**
		 * 创建单例，防止每次new消耗资源
		 */
		public static final SkipX509TrustManager INSTANCE = new SkipX509TrustManager();

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

	}
	
}


