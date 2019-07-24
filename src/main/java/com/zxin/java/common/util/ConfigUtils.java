/**
 * 
 */
package com.zxin.java.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zxin
 *
 */
public class ConfigUtils {

	private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
	
	private static final ConcurrentMap<String, Configuration> CONCURRENT_MAP = new ConcurrentHashMap<>();

	public static final String CONFIG_PROPERTIES = "config.properties";
	
	
	/**
	 * 获取配置对象
	 * 非实时生效，防止文件误篡改，使项目崩溃。
	 * @param fileName 目前仅支持properties、xml、ini；
	 * @return
	 */
	public static Configuration getInstance(String fileName){
		if(CONCURRENT_MAP.get(fileName) == null){
			synchronized(CONCURRENT_MAP){
				if(CONCURRENT_MAP.get(fileName) == null){
					try {
						CONCURRENT_MAP.put(fileName, getConfiguration(fileName));
					} catch (ConfigurationException e) {
						logger.error("",e);
					}
				}
			}
		}
		return CONCURRENT_MAP.get(fileName);
	}
	
	/**
	 * 默认文件 config.properties
	 * @return
	 */
	public static Configuration getInstance(){
		return getInstance(CONFIG_PROPERTIES);
	}
	
	/**
	 * 重载缓存池中所有的配置
	 * @return	是否重载成功
	 */
	public static boolean reload(){
		logger.info("重载所有配置文件。。。。。。");
		try {
			synchronized(CONCURRENT_MAP){
				for (Map.Entry<String, Configuration> entry : CONCURRENT_MAP.entrySet()) {
					String fileName = entry.getKey();
					logger.info("重载配置文件：[{}]", fileName);
					entry.setValue(getConfiguration(fileName));
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 重载某一文件配置
	 * @param fileName
	 * @return
	 */
	public static boolean reload(String fileName){
		logger.info("重载配置文件：[{}]", fileName);
		try {
			synchronized(CONCURRENT_MAP){
				CONCURRENT_MAP.put(fileName, getConfiguration(fileName));
			}
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 实时获取配置方法
	 * 未走缓存, Configuration是实时生成的
	 * 
	 * @param fileName 目前仅支持properties、xml、ini；
	 * @return
	 * @throws ConfigurationException
	 */
	public static Configuration getConfiguration(String fileName) throws ConfigurationException{
		Configuration configuration = null;
		
		if(StringUtils.isNotEmpty(fileName)){
			if(fileName.endsWith(".properties")){
				configuration = new PropertiesConfiguration(fileName);
			}else if(fileName.endsWith(".xml")){
				configuration = new XMLConfiguration(fileName);
			}else if(fileName.endsWith(".ini")){
				configuration = new HierarchicalINIConfiguration(fileName);
			}else{
				throw new RuntimeException("配置文件目前仅支持properties、xml、ini后缀，不支持"+fileName);
			}
		}
		
		return configuration;
	}
	
	
	/**
	 * 加载 properties
	 * 
	 * Return the location of the specified resource by searching the user home
     * directory, the current classpath and the system classpath.
     * 
	 * @param fileName 文件名称
	 * @return
	 */
	public static Properties getProperties(String fileName){
		Properties prop = new Properties();     
		URL url = ConfigurationUtils.locate(fileName);
		try (InputStream in = url.openStream()) {
			prop.load(in);     ///加载属性列表
		} catch (IOException e) {
			logger.error("Properties IO error", e);
		}
		return prop;
	}
	
}
