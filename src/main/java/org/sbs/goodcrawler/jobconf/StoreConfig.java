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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.JobConfiguration;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 存储配置对象，默认使用es
 */
public class StoreConfig extends JobConfiguration {
	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * 默认存储插件（es）,否则应该填写相应插件的全路径
	 */
	private String type = "default";
	/**
	 * 线程数
	 */
	private int threadNum = 2;
	/**
	 * 队列大小
	 */
	private int queueSize = 100;
	/**
	 * 是否使用自动生成的id，默认是自动生成。
	 */
	private boolean autoId = true;
	/**
	 * 提取信息名，使用自定提取信息的md5值做id。
	 */
	private String md5Ref = null;
	/**
	 * 重复文档的处理策略
	 */
	private RepeatPolicy repeatPolicy;
	/**
	 * 单独提取保存的，提取的模板信息。空则不提取，直接保存。
	 */
	private List<String> templates = null;
	
	public StoreConfig() {
	}
	
	public StoreConfig loadConfig(Document confDoc){
		Document doc = confDoc;
		jobName = doc.select("job").attr("name");
		
		Elements e = doc.select("store");
		this.type = e.select("type").text();
		if(StringUtils.isNotBlank(e.select("threadNum").text())){
			this.threadNum = Integer.parseInt(e.select("threadNum").text());
		}
		
		if(StringUtils.isNotBlank(e.select("queueSize").text())){
			this.queueSize = Integer.parseInt(e.select("queueSize").text());
		}
		
		String temp = e.select("id").text();
		if(StringUtils.isNotBlank(temp)){
			if("auto".equals(temp.toLowerCase())){
				this.autoId = true;
			}else {
				this.autoId = false;
				temp = StringUtils.substringBetween(temp, "{", "}");
				if(temp.startsWith("md5(")){
					temp = StringUtils.substringBetween(temp, "(", ")");
					this.md5Ref = temp;
				}
			}
		}
		
		temp = e.select("repeatPolicy").text();
		if(StringUtils.isNotBlank(temp)){
			this.repeatPolicy = RepeatPolicy.valueOf(temp);
		}
		temp = e.select("templates").text();
		if(StringUtils.isNotBlank(temp)){
			templates = Lists.newArrayList(StringUtils.split(temp,","));
		}
		return this;
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
	
	public boolean isAutoId() {
		return autoId;
	}

	public void setAutoId(boolean autoId) {
		this.autoId = autoId;
	}

	public String getMd5Ref() {
		return md5Ref;
	}

	public void setMd5Ref(String md5Ref) {
		this.md5Ref = md5Ref;
	}
	
	public RepeatPolicy getRepeatPolicy() {
		return repeatPolicy;
	}

	public void setRepeatPolicy(RepeatPolicy repeatPolicy) {
		this.repeatPolicy = repeatPolicy;
	}
	
	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public List<String> getTemplates() {
		return templates;
	}

	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StoreConfig [log=").append(log).append(", type=")
				.append(type).append(", threadNum=").append(threadNum)
				.append(", jobName=").append(jobName).append("]");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		StoreConfig extractConfig = new StoreConfig();
		Document document;
		try {
			document = Jsoup.parse(new File("conf/youku_conf.xml"), "utf-8");
			System.out.println(extractConfig.loadConfig(document).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}