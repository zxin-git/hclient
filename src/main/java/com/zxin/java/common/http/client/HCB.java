/**
 * 
 */
package com.zxin.java.common.http.client;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.impl.client.HttpClientBuilder;

import com.zxin.java.common.http.exception.HCBException;

/**
 * @author zxin
 *
 */
public class HCB extends HttpClientBuilder{

	private HCB(){}
	
	public static HCB custom(){
		return new HCB();
	}
	
	/**
	 * 设置连接池（默认开启https）
	 * 
	 * @param maxTotal					最大连接数
	 * @param defaultMaxPerRoute	每个路由默认连接数
	 * @return	返回当前对象
	 * @throws HCBException	HCB异常
	 */
	public HCB pool(int maxTotal, int maxPerRoute) throws HCBException{
		try {
			return (HCB) this.setConnectionManager(HttpClientDefault.pool(maxTotal, maxPerRoute));
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new HCBException("创建pool异常", e);
		}
	}
	
	/**
	 * 设置超时时间
	 * @param connectTimeout
	 * @param requestTimeout
	 * @param poolTimeout
	 * @return
	 */
	public HCB timeout(int connectTimeout, int requestTimeout, int poolTimeout){
		return (HCB) this.setDefaultRequestConfig(HttpClientDefault.timeout(connectTimeout, requestTimeout, poolTimeout));
	}
	
	
	/**
	 * 设置ssl安全链接
	 * 
	 * @return	返回当前对象
	 * @throws HCBException	HCB异常
	 */
	public HCB ssl() throws HCBException{
		try {
			return (HCB) this.setSSLSocketFactory(HttpClientDefault.ssl());
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new HCBException("创建ssl异常", e);
		}
	}
	
	
	/**
	 * 设置代理
	 * 
	 * @param hostname		代理host或者ip
	 * @param port			代理端口
	 * @return	返回当前对象
	 */
	public HCB proxy(String hostname, int port){
		return (HCB) this.setRoutePlanner(HttpClientDefault.proxy(hostname, port));
	}
	
	
}
