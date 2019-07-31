/**
 * 
 */
package com.zxin.java.common.http.client;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxin.java.common.http.client.HttpPoolClientUtils;

/**
 * @author zxin
 *
 */
public class HttpPoolClientUtilsTest {

	private static final Logger log = LoggerFactory.getLogger(HttpPoolClientUtilsTest.class);

	/**
	 * Test method for {@link com.zxin.java.common.http.client.HttpPoolClientUtils#post(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPostStringString() {
		try {
			
			String response = HttpPoolClientUtils.post("https://opendevl.fbank.com/pm2/opencallback/callback/passthrough/bg4ice?apiCode=bigdata&apiMethod=iceBingLianFen", "s");
			System.out.println(response);
		} catch (Exception e) {
			log.warn("",e);
		}
	}

}
