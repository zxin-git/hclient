/**
 * 
 */
package com.zxin.java.common.http;

import java.util.HashMap;
import java.util.Map;

/**
 * 常见的ContentType工具类，仅作为字符通用；
 * 如有charset等因素，考虑直接输入头信息；
 * 类型范围，参见{@link org.apache.http.entity.ContentType}
 * 
 * @author zxin
 *
 */
public class ContentType {

	public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml");
	public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded");
	public static final ContentType APPLICATION_JSON = create("application/json");
	public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream");
	public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml");
	public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml");
	public static final ContentType APPLICATION_XML = create("application/xml");
	
	public static final ContentType IMAGE_BMP = create("image/bmp");
	public static final ContentType IMAGE_GIF = create("image/gif");
	public static final ContentType IMAGE_JPEG = create("image/jpeg");
	public static final ContentType IMAGE_PNG = create("image/png");
	public static final ContentType IMAGE_SVG = create("image/svg+xml");
	public static final ContentType IMAGE_TIFF = create("image/tiff");
	public static final ContentType IMAGE_WEBP = create("image/webp");
	
	public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data");
	public static final ContentType TEXT_HTML = create("text/html");
	public static final ContentType TEXT_PLAIN = create("text/plain");
	public static final ContentType TEXT_XML = create("text/xml");
	public static final ContentType WILDCARD = create("*/*");
	
	
	private final String mimeType;
	
	private ContentType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * 新建 媒介类型 的ContentType
	 * @param mimeType 媒介类型字符串
	 * @return
	 */
	public static ContentType create(String mimeType){
		if(Cache.CACHE.get(mimeType)==null){
			synchronized (ContentType.class) {
				if(Cache.CACHE.get(mimeType)==null){
					Cache.CACHE.put(mimeType, new ContentType(mimeType));
				}
			}
		}
		return Cache.CACHE.get(mimeType);
	}
	
	public String value() {
		return mimeType;
	}
	
	
	/**
	 * 使用静态内部类，提前加载缓存map
	 * @author zxin
	 *
	 */
	private static class Cache{
		static final Map<String, ContentType> CACHE = new HashMap<>(32);
	}
	
}
