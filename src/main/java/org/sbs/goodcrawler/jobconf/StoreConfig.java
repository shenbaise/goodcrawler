/**
 * ########################  SHENBAISE'S WORK  ##########################
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
package org.sbs.goodcrawler.jobconf;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.Configuration;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.util.EncryptUtils;
import org.sbs.util.MD5Utils;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 存储配置对象，默认使用es
 */
public class StoreConfig extends Configuration {
	private Log log = LogFactory.getLog(this.getClass());
	private String type = "default";
	private IDPolicy id = IDPolicy.auto;
	private String policyRef = "";
	private int threadNum = 2;
	private String pluginClass = null;
	
	public StoreConfig() {
	}
	
	public StoreConfig loadConfig(Document confDoc){
		Document doc = confDoc;
		jobName = doc.select("job").attr("name");
		indexName = doc.select("job").attr("indexName");
		Elements e = doc.select("store");
		this.type = e.select("type").text();
		if(StringUtils.isNotBlank(e.select("threadNum").text())){
			this.threadNum = Integer.parseInt(e.select("threadNum").text());
		}
		String className = e.select("plugin").text();
		if(StringUtils.isNotBlank(className)){
			this.pluginClass = className;
		}
		// id生成策略 
		String idPolicy = e.select("idPolicy").text();
		if(StringUtils.isNotBlank(idPolicy)){
			id = EnumUtils.getEnum(IDPolicy.class, idPolicy);
			if(!IDPolicy.auto .equals(id)){
				String pref = e.select("ref").text();
				if(StringUtils.isNotBlank(pref)){
					this.policyRef = pref;
				}
				if(StringUtils.isBlank(this.policyRef)){
					try {
						throw new ConfigurationException("指定了ID生成策略但未指定参考");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
		return this;
	}
	/**
	 * 如果不是自动生成ID则调用此方法生成ID
	 * @return
	 */
	public String genId(HashMap<String, Object> data){
		switch (id) {
		case auto:
			return null;
		case md5:
			return MD5Utils.createMD5((String) data.get(policyRef));
		case base64:
			return EncryptUtils.encodeBase64((String) data.get(policyRef));
		case urlencode:
			try {
				return URLEncoder.encode((String)data.get(policyRef), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		default:
			return null;
		}
	}
	
	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
	
	public String getPluginClass() {
		return pluginClass;
	}

	public void setPluginClass(String pluginClass) {
		this.pluginClass = pluginClass;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StoreConfig [log=").append(log).append(", type=")
				.append(type).append(", threadNum=").append(threadNum)
				.append(", pluginClass=").append(pluginClass).append("]");
		return builder.toString();
	}

	public static void main(String[] args) {
		StoreConfig extractConfig = new StoreConfig();
		Document document;
		try {
			document = Jsoup.parse(new File("conf/wasu_conf.xml"), "utf-8");
			System.out.println(extractConfig.loadConfig(document).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author shenbaise
	 * @date 2014年3月10日
	 * desc:文档id的生成策略
	 */
	public enum IDPolicy {
		auto,md5,base64,urlencode
	}
}
