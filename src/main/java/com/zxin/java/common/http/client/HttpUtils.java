/**
 * 
 */
package com.zxin.java.common.http.client;

import static com.zxin.java.common.http.util.HttpConstant.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxin.java.common.http.HttpStatus;
import com.zxin.java.common.http.Result;
import com.zxin.java.common.http.callback.IHttpResponseCallback;
import com.zxin.java.common.http.callback.impl.StringCallBack;
import com.zxin.java.common.http.exception.HCBException;
import com.zxin.java.common.http.request.HttpRequestBuilder;
import com.zxin.java.common.http.request.HttpRequestFactory;
import com.zxin.java.common.util.ConfigUtils;

/**
 * @author zxin
 *
 */
public class HttpUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	private static CloseableHttpClient simpleHttpClient = null;
	
	private static CloseableHttpClient poolHttpClient = null;
	
	private HttpUtils(){};
	
	/**
	 * 获取httpclient连接池实例
	 * 双重校验锁单例
	 * @return
	 */
	public static CloseableHttpClient simpleHttpclient(){
		if(simpleHttpClient == null){
			synchronized(HttpPoolClientUtils.class){
				if(simpleHttpClient == null){
					simpleHttpClient = HttpClients.createDefault();
				}
			}
		}
		return simpleHttpClient;
	}
	
	
	/**
	 * 获取httpclient连接池实例
	 * 双重校验锁单例
	 * @return
	 */
	public static CloseableHttpClient poolHttpclient(){
		if(poolHttpClient == null){
			synchronized(HttpPoolClientUtils.class){
				if(poolHttpClient == null){
					try {
						poolHttpClient = createPoolHttpClientByConfig();
					} catch (HCBException e) {
						logger.error("HCB初始化 httpclient时出现异常", e);
					}
				}
			}
		}
		return poolHttpClient;
	}
	
	
	/**
	 * 发送请求，回调处理接口响应
	 * 
	 * @param httpclient 请求客户端
	 * @param request	请求信息
	 * @param callback	响应回调函数
	 * @return	{@link Result}
	 */
	public static <T> Result<T> send(CloseableHttpClient httpclient, HttpRequestBase request, IHttpResponseCallback<T> callback){
		String uri = request.getURI().toString();
		
		try (CloseableHttpResponse response = httpclient.execute(request);){
			int status =  response.getStatusLine().getStatusCode();
			if(HttpStatus.OK.value() == status){
				T t = callback.callback(request, response);
				return Result.success(uri, t);
			}else{
				String msg = StringCallBack.getIntsance().callback(request, response);
				return Result.error(uri, HttpStatus.valueOf(status), msg, null);
			}
		}catch (Exception e) {
			return Result.error(uri, HttpStatus.INTERNAL_SERVER_ERROR, "", e);
		}
	}
	
	/**
	 * 使用默认的连接池请求
	 * 
	 * @param request 基础的 http请求
	 * @param callback 响应处理的回调函数
	 *  
	 * 基础请求方法构建， 参见 {@link HttpRequestFactory}
	 * 自定义构建，参见 {@link HttpRequestBuilder#build()}
	 * @return
	 */
	public static <T> Result<T> sendByPool(HttpRequestBase request, IHttpResponseCallback<T> callback){
		return send(poolHttpclient(), request, callback);
		
	}
	
	
	/**
	 * 通过HCB创建httpclient默认配置文件，自定义配置httpclient各参数
	 * 
	 * @return
	 * @throws HCBException 
	 */
	public static CloseableHttpClient createPoolHttpClientByConfig() throws HCBException{
		CloseableHttpClient httpClient = null;
		HCB hcb = HCB.custom();
		
		int requestTimeout = ConfigUtils.getInstance().getInt("http.client.timeout.request", DEFAULT_TIMEOUT_REQUEST);
		int connectTimeout = ConfigUtils.getInstance().getInt("http.client.timeout.connect", DEFAULT_TIMEOUT_CONNECT);
		int poolTimeout = ConfigUtils.getInstance().getInt("http.client.timeout.pool", DEFAULT_TIMEOUT_POOL);
		hcb = hcb.timeout(connectTimeout, requestTimeout, poolTimeout);
		
		boolean usePool = ConfigUtils.getInstance().getBoolean("http.client.pool.use", DEFAULT_POOL_USE);
		if(usePool){
			int maxTotal = ConfigUtils.getInstance().getInt("http.client.pool.maxTotal", DEFAULT_POOL_MAX_TOTAL);
			int maxPerRoute = ConfigUtils.getInstance().getInt("http.client.pool.maxPerRoute", DEFAULT_POOL_MAX_PER_ROUTE);
			hcb.pool(maxTotal, maxPerRoute);		
		}
		
		boolean useProxy  = ConfigUtils.getInstance().getBoolean("http.client.proxy.use", DEFAULT_PROXY_USE);
		if(useProxy){
			String proxyHostname = ConfigUtils.getInstance().getString("http.client.proxy.hostname");
			int proxyPort = ConfigUtils.getInstance().getInt("http.client.proxy.port");
			hcb.proxy(proxyHostname, proxyPort);
		}
		
		httpClient = hcb.build();
		
		return httpClient;
	}
	
}
