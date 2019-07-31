/**
 * 
 */
package com.zxin.java.common.http.request;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxin.java.common.http.ContentType;
import com.zxin.java.common.http.Result;
import com.zxin.java.common.http.callback.impl.StringCallBack;
import com.zxin.java.common.http.client.HttpUtils;
import com.zxin.java.common.http.request.HttpRequestFactory;

/**
 * @author zxin
 *
 */
public class HttpRequestFactoryTest extends HttpRequestFactory {

	private static final Logger log = LoggerFactory.getLogger(HttpRequestFactoryTest.class);

	/**
	 * Test method for {@link com.zxin.java.common.http.request.HttpRequestFactory#postForm(java.lang.String, java.util.Map)}.
	 */
	@Test
	public void testPostForm() {
		
	}

	/**
	 * Test method for {@link com.zxin.java.common.http.request.HttpRequestFactory#post(java.lang.String, com.zxin.java.common.http.ContentType, java.lang.String)}.
	 */
	@Test
	public void testPost() {
		String uri = "https://www.cnblogs.com";
		Result<String> result =  HttpUtils.sendByPool(post(uri,ContentType.TEXT_PLAIN,""), (request, response)->{
			return StringCallBack.getIntsance().callback(request, response);
		}) ;
		
		System.out.println(result);
	}

	/**
	 * Test method for {@link com.zxin.java.common.http.request.HttpRequestFactory#get(java.lang.String)}.
	 */
	@Test
	public void testGet() {
		String uri = "http://sohu.com";
//		String uri = "https://program-think.blogspot.com";
//		String uri = "https://10.102.1.75/v2/_catalog";
		Result<String> result =  HttpUtils.sendByPool(get(uri), (request, response)->{
			return StringCallBack.getIntsance().callback(request, response);
		}) ;
		
		System.out.println(result);
		
	}

}
