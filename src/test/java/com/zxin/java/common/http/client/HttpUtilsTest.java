/**
 * 
 */
package com.zxin.java.common.http.client;

import static org.junit.Assert.*;

import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxin.java.common.http.ContentType;
import com.zxin.java.common.http.HttpStatus;
import com.zxin.java.common.http.Result;
import com.zxin.java.common.http.callback.impl.StringCallBack;
import com.zxin.java.common.http.client.HttpUtils;
import com.zxin.java.common.http.request.HttpRequestFactory;

/**
 * @author zxin
 *
 */
public class HttpUtilsTest {

	private static final Logger log = LoggerFactory.getLogger(HttpUtilsTest.class);

	@Test
	public void test() {
		HttpRequestBase request = HttpRequestFactory.get("http://10.102.1.75:8081/sss");
		Result<String> result = HttpUtils.sendByPool(request, StringCallBack.getIntsance());
		System.out.println(result);
	}
	
	@Test
	public void testSelfSign(){
		HttpRequestBase request = HttpRequestFactory.get("https://10.102.1.75/v2/_catalog");
//		Result<String> result = HttpUtils.send(HttpUtils.simpleHttpclient(), request, StringCallBack.getIntsance());
		Result<String> result = HttpUtils.sendByPool(request, StringCallBack.getIntsance());
		assertEquals(result.getHttpStatus(), HttpStatus.OK);
		System.out.println(result);
	}
	
	@Test
	public void testCommonSign(){
		String[] uris = {"https://www.cnblogs.com", "https://blog.csdn.net"};
		for (String uri : uris) {
			HttpRequestBase request = HttpRequestFactory.get(uri);
			Result<String> result = HttpUtils.send(HttpUtils.simpleHttpclient(), request, StringCallBack.getIntsance());
//			Result<String> result = HttpUtils.sendByPool(request, StringCallBack.getIntsance());
			assertEquals(result.getHttpStatus(), HttpStatus.OK);
			System.out.println(result);
		}
	}
	
	@Test
	public void testHttpsPost(){
		String uri = "https://opendevl.fbank.com/pm2/opencallback/callback/passthrough/bg4ice?apiCode=bigdata&apiMethod=iceBingLianFen";
		HttpRequestBase request = HttpRequestFactory.post(uri, "sdfasfd");
//		Result<String> result = HttpUtils.send(HttpUtils.simpleHttpclient(), request, StringCallBack.getIntsance());
		Result<String> result = HttpUtils.sendByPool(request, StringCallBack.getIntsance());
		assertEquals(result.getHttpStatus(), HttpStatus.OK);
		System.out.println(result);
	}
	
	@Test
	public void testLoop() {
		for (int i = 0; i < 5; i++) {
//			testSimpleHttpClient();
		}
	}

}
