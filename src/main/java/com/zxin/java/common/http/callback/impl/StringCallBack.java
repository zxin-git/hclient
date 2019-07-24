/**
 * 
 */
package com.zxin.java.common.http.callback.impl;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxin.java.common.http.callback.IHttpResponseCallback;


/**
 * @author zxin
 *
 */
public class StringCallBack implements IHttpResponseCallback<String> {

	private static final Logger logger = LoggerFactory.getLogger(StringCallBack.class);
	
	private static final StringCallBack INSTANCE = new StringCallBack(); 
	
	private StringCallBack(){};
	
	public static StringCallBack getIntsance(){
		return INSTANCE;
	}

	@Override
	public String callback(HttpRequestBase request, CloseableHttpResponse response) throws ParseException, IOException {
		String responseContent = "";
		HttpEntity entity  = response.getEntity();
		if (entity != null) {
			responseContent = EntityUtils.toString(entity, Consts.UTF_8);
		}
		logger.debug("接收到响应：{}", responseContent);
		return responseContent;
	}

}
