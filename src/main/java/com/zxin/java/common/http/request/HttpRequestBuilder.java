/**
 * 
 */
package com.zxin.java.common.http.request;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxin.java.common.http.ContentType;
import com.zxin.java.common.http.HttpMethod;

/**
 * HTTP请求构构建类，
 * 返回类型统一为 {@link HttpEntityEnclosingRequestBase}
 * 
 * @author zxin
 *
 */
public class HttpRequestBuilder {

	private static final Logger log = LoggerFactory.getLogger(HttpRequestBuilder.class);
	
	private Charset charset = Consts.UTF_8;
	
	private URI uri;
	
	private String method = HttpGet.METHOD_NAME;
	
	private List<Header> headers = new ArrayList<Header>();
	
	private HttpEntity entity;
	
	private int requestTimeout;
	
	private int connectTimeout;
	
	
	private HttpRequestBuilder(){};
	
	/**
	 * 新建HttpRequestBuilder对象，uri参数必传 
	 * @param uri http请求uri
	 * @return
	 */
	public static HttpRequestBuilder custom(String uri){
		return new HttpRequestBuilder().uri(uri);
	}
	
	public static HttpRequestBuilder custom(Charset charset){
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
		requestBuilder.charset = charset;
		return requestBuilder;
	}
	
	public HttpRequestBuilder copy(){
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
		requestBuilder.charset = this.charset;
		requestBuilder.uri = this.uri;
		requestBuilder.method = this.method;
		requestBuilder.entity = this.entity;
		requestBuilder.requestTimeout = this.requestTimeout;
		requestBuilder.connectTimeout = this.connectTimeout;
		requestBuilder.headers = new ArrayList<>();
		for (int i = 0; i < this.headers.size(); i++) {
			Header header = new BasicHeader(this.headers.get(i).getName(), this.headers.get(i).getValue());
			requestBuilder.headers.add(header);
		}
		return requestBuilder;
	}
	
	public HttpRequestBuilder uri(String uri){
		this.uri = uri != null ? URI.create(uri) : null;
		return this;
	}
	
	/**
	 * 设置超时时间
	 * @param connectTimeout
	 * @param requestTimeout
	 * @return
	 */
	public HttpRequestBuilder timeout(int connectTimeout, int requestTimeout){
		this.connectTimeout = connectTimeout;
		this.requestTimeout = requestTimeout;
		return this;
	}
	
	/**
	 * 填充请求表单参数
	 * 自动更改contentType为  {@link ContentType.APPLICATION_FORM_URLENCODED}
	 * @param map
	 * @return
	 */
	public HttpRequestBuilder params(Map<String, String> map){
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if(map!=null){
			for (Map.Entry<String, String> entry : map.entrySet()) {
				formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		this.entity = new UrlEncodedFormEntity(formparams, charset);
		this.contentType(ContentType.APPLICATION_FORM_URLENCODED);
		return this;
	}
	
	/**
	 * 填充请求体
	 * {@link StringEntity}
	 * @param content
	 * @return
	 */
	public HttpRequestBuilder content(String content){
		this.entity = new StringEntity(content, charset);
		return this;
	}
	
	/**
	 * 设置媒介类型，采用httpcore定义类型，防止字符输入错误。
	 * {@link ContentType}
	 * @param contentType
	 * @return
	 */
	public HttpRequestBuilder contentType(ContentType contentType){
		headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType.value()));
		return this;
	}
	
	/**
	 * 添加头信息
	 * 建议name用{@link HttpHeaders}命名，有效止输入错误。
	 * @param name
	 * @param value
	 * @return
	 */
	public HttpRequestBuilder addHeader(final String name, final String value){
		headers.add(new BasicHeader(name, value));
		return this;
	}
	
	/**
	 * 设置请求方法，默认为get。
	 * @param httpMethod
	 * @return
	 */
	public HttpRequestBuilder method(HttpMethod httpMethod){
		if(httpMethod != null){
			this.method = httpMethod.name();
		}
		return this;
	}
	
	/**
	 * 构建内部请求体请求
	 * 如果超时都不为0，则设置超时，否则默认忽略
	 * @return HttpEntityEnclosingRequestBase
	 */
	public HttpEntityEnclosingRequestBase build(){
		InternalEntityEclosingRequest request = new InternalEntityEclosingRequest(method);
		request.setURI(uri);
		request.setHeaders(headers.toArray(new Header[headers.size()]));
		request.setEntity(entity);
		
		if (connectTimeout !=0 && requestTimeout != 0) {
			RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout)
					.setSocketTimeout(requestTimeout).build();
			request.setConfig(config);
		}
		return request;
	}
	
	/**
	 * HttpEntityEnclosingRequestBase内部实现类
	 * @author zxin
	 *
	 */
	static class InternalEntityEclosingRequest extends HttpEntityEnclosingRequestBase {

        private final String method;

        InternalEntityEclosingRequest(final String method) {
            super();
            this.method = method;
        }

        @Override
        public String getMethod() {
            return this.method;
        }

    }

}
