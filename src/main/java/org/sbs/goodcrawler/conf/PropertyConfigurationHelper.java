/**
 * ##########################  GoodCrawler  ############################
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sbs.goodcrawler.conf;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * @desc properties配置文件帮助类
 */
public class PropertyConfigurationHelper implements Serializable{
	
	private static final long serialVersionUID = 7599466817406649028L;
	private Log log = LogFactory.getLog(this.getClass());
	private static PropertyConfigurationHelper instance = null;
	private Properties properties;
	
	/**
	 * 构造函数
	 */
	private PropertyConfigurationHelper(){
		properties = new Properties();
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(GlobalConstants.propertiyFilePaht));
		} catch (IOException e) {
			log.fatal("配置文件缺失 [conf.properties]");
			e.printStackTrace();
		}
	}
	
	/**
	 * @desc 获取帮助类实例对象
	 */
	public static PropertyConfigurationHelper getInstance(){
		if(instance==null){
			instance = new PropertyConfigurationHelper();
		}
		return instance;
	}
	
	public String getString(String propertyName,String defaultValue){
		return properties.getProperty(propertyName, defaultValue);
	}
	
	public int getInt(String propertyName,int defaultValue){
		return Integer.parseInt(properties.getProperty(propertyName, String.valueOf(defaultValue)));
	}
	
	public Object getObject(String propertyName){
		return properties.get(propertyName);
	}
	
	public void destroy(){
		instance = null;
		properties.clear();
		properties = null;
	}
}
