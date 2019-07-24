package com.zxin.java.common.http.callback;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Http响应回调接口
 * 
 * @author zxin
 * 
 */
public interface IHttpResponseCallback<T> {

	/**
	 * 当执行结果为正常时的处理方法
	 * @param response
	 * 
	 * @return 
	 * @throws CallbackException
	 */
	public T callback(HttpRequestBase request, CloseableHttpResponse response) throws Exception;

}