/**
 * 
 */
package com.zxin.java.common.http;

/**
 * HTTP返回的封装类
 * @author zxin
 *
 */
//@JsonIgnoreProperties({"trace"})
//@JsonInclude(JsonInclude.Include.NON_NULL) 
//@JsonPropertyOrder({"timestamp", "uri", "status", "data", "error", "message", "exception"})
public class Result<T> {

//	仅作用Date类型上
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss SSS") 
	private long timestamp = System.currentTimeMillis();
	
	private final HttpStatus httpStatus;
	
	private final String uri;
	
	/**
	 * only {@link HttpStatus#OK}
	 */
	private final T data;

	/**
	 * only not {@link HttpStatus#OK}
	 */
	private final String message;
	
	private final Exception exception;
	
	private Result(String uri, HttpStatus httpStatus, T data, String message, Exception exception){
		this.uri = uri;
		this.httpStatus = httpStatus;
		this.data = data;
		this.message = message;
		this.exception = exception;
	}
	
	public static <T> Result<T> success(String uri, T data){
		return new Result<>(uri, HttpStatus.OK, data, null, null);
	}
	
	public static <T> Result<T> error(String uri, HttpStatus httpStatus, String msg, Exception exception){
		return new Result<>(uri, httpStatus, null, msg, exception);
	}
	
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getUri() {
		return uri;
	}

	public T getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return exception;
	}
	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Result [timestamp=").append(timestamp);
		buffer.append(", httpStatus=").append(httpStatus.value());
		
		if(HttpStatus.OK.equals(httpStatus)){
			buffer.append(", data=").append(data);
		}else{
			buffer.append(", message=").append(message);
			buffer.append(", exception=").append(exception);
		}
		
		buffer.append(", uri=").append(uri).append("]");
		return buffer.toString();
	}

//	public String toJsonString(){
//		try {
//			return JacksonMapper.JSON.toString(this);
//		} catch (JsonProcessingException e) {
//			return null;
//		}
//	}
	
}
