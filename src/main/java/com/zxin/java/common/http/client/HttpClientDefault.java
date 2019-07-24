/**
 * 
 */
package com.zxin.java.common.http.client;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;


/**
 * @author zxin
 *
 */
public class HttpClientDefault {
	
	public static HttpClientConnectionManager pool(int maxTotal, int maxPerRoute) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		SSLConnectionSocketFactory sslsf = ssl();
		
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslsf).build();
		
		//----pool-------
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(maxTotal);
		connectionManager.setDefaultMaxPerRoute(maxPerRoute);
		
		return connectionManager;
	}
	
	/**
	 * 
	 * @param connectTimeout
	 * @param requestTimeout
	 * @param poolTimeout
	 * @return
	 */
	public static RequestConfig timeout(int connectTimeout, int requestTimeout, int poolTimeout){
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(poolTimeout)
				.setConnectTimeout(connectTimeout)
				.setSocketTimeout(requestTimeout).build();
		return config;
	}
	
	/**
	 * 设置代理访问
	 * @param hostname 代理服务器主机名
	 * @param port	代理服务器端口
	 * @return
	 */
	public static HttpRoutePlanner proxy(String hostname, int port){
		HttpHost proxy = new HttpHost(hostname, port);
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		return routePlanner;
	}
	
	
    static SSLConnectionSocketFactory ssl() throws KeyManagementException, NoSuchAlgorithmException{
		SSLContext sc = SSLContext.getInstance("TLS");
    	sc.init(null, new TrustManager[] {  SkipX509TrustManager.INSTANCE }, new SecureRandom());
    	SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc, SkipHostnameVerifier.INSTANCE);
		
		return sslsf;
    }
	
    /**
     * 历史版本，不健全，已弃用 
     * Use {@link HttpClientDefault#ssl()}
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    @Deprecated
	static SSLConnectionSocketFactory sslmine() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		//------ssl-----------信任所有主机，信任自签名证书；
		SSLContext sslcontext = new SSLContextBuilder().loadTrustMaterial(null, TrustSelfSignedStrategy.INSTANCE).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);
		return sslsf;
	}
	
    /**
     * 绕过主机名验证，{@link NoopHostnameVerifier#INSTANCE}
     * @author zxin
     *
     */
	private static class SkipHostnameVerifier implements HostnameVerifier {
		
		public static final SkipHostnameVerifier INSTANCE = new SkipHostnameVerifier();

		@Override
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}

	}

	/**
	 * 绕过X509证书验证 
	 * @author zxin
	 *
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
