/**
 * 
 */
package com.zxin.java.common.http.request;

import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.zxin.java.common.http.ContentType;
import com.zxin.java.common.http.HttpMethod;

/**
 * 常见HTTP请求工厂类
 * 
 * @author zxin
 *
 */
public class HttpRequestFactory {

	/**
	 * 表单形式的post请求
	 * 
	 * @param uri 地址
	 * @param map 表单数据
	 * @return
	 */
	public static HttpEntityEnclosingRequestBase postForm(String uri, Map<String, String> map){
		return post(uri).params(map).build();
	}
	
	/**
	 * 自定义请求体的post请求；
	 * 例如： json, xml等
	 * 
	 * @param uri
	 * @param contentType
	 * @param requestContent
	 * @return
	 */
	public static HttpEntityEnclosingRequestBase post(String uri, ContentType contentType, String requestContent){
		return post(uri).contentType(contentType).content(requestContent).build();
	}
	
	/**
	 * 联动专用
	 * 默认为  text/plain 请求
	 * 
	 * @param uri
	 * @param requestContent
	 * @return
	 */
	public static HttpEntityEnclosingRequestBase post(String uri, String requestContent){
		return post(uri).contentType(ContentType.TEXT_PLAIN).content(requestContent).build();
	}
	
	/**
	 * get请求，自己封装好uri的参数；
	 * 注意请求参数需要urlencoded
	 * 
	 * @param uri
	 * @return
	 */
	public static HttpEntityEnclosingRequestBase get(String uri){
		return HttpRequestBuilder.custom(uri).method(HttpMethod.GET).build();
	}
	
	/**
	 * Thread safed, 每次调用都会新建一个request对象； 
	 * {@link HttpRequestBuilder#custom(String)}
	 * @param uri
	 * @return
	 */
	private static HttpRequestBuilder post(String uri){
		return HttpRequestBuilder.custom(uri).method(HttpMethod.POST);
	}
	
	
	/**
	 * 设置特殊请求的超时
	 * 
	 * @param httpRequestBase 
	 * @param connectTimeout
	 * @param requestTimeout
	 * @return
	 */
	public static HttpRequestBase timeout(HttpRequestBase httpRequestBase, int connectTimeout, int requestTimeout){
		RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout)
				.setSocketTimeout(requestTimeout).build();
		httpRequestBase.setConfig(config);
		return httpRequestBase;
	}
	
	@Deprecated
	public static HttpPost httpPost(String url, ContentType contentType, String content){
		HttpPost httpPost = new HttpPost();
		httpPost.addHeader(HTTP.CONTENT_TYPE, contentType.value());
		StringEntity reqEntity = new StringEntity(content, Consts.UTF_8);
		httpPost.setEntity(reqEntity);
		return httpPost;
	}
}
