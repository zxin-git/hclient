/**
 * 
 */
package com.zxin.java.common.http.util;

import java.nio.charset.Charset;

/**
 * 
 * Http常量参数
 * @author zxin
 *
 */
public class HttpConstant {
	
	//=====================charset===================================
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    
    public static final Charset ASCII = Charset.forName("US-ASCII");
    
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    
    
	public static final int DEFAULT_TIMEOUT_REQUEST = 5 * 1000;

	public static final int DEFAULT_TIMEOUT_CONNECT = 1 * 1000;
	
	/**
	 * 从连接池中获取连接 超时 默认 1s
	 */
	public static final int DEFAULT_TIMEOUT_POOL = 500;
	
	/**
	 * 默认是否使用http连接池
	 */
	public static final boolean DEFAULT_POOL_USE = true;
	
	/**
	 * 最大池连接数
	 */
	public static final int DEFAULT_POOL_MAX_TOTAL = 2000;
	
	/**
	 * 单路由最大连接数
	 */
	public static final int DEFAULT_POOL_MAX_PER_ROUTE = 500;
	
	/**
	 * 默认是否使用代理
	 */
	public static final boolean DEFAULT_PROXY_USE = false;
	
	

}
