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
package org.sbs.goodcrawler.job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.storage.StorageType;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 任务加载管理类
 */
public class JobConfigurationManager {
	
	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * @param configFile
	 * @return
	 * @desc 从配置文件中加载任务配置，一个job对应一个jobConfiguration
	 */
	public List<JobConfiguration> loadJobConfigurations(File configFile) throws ConfigurationException{
		List<JobConfiguration> list = new ArrayList<>();
		try {
			Document doc = Jsoup.parse(configFile, "utf-8");
			Elements jobs = doc.select("job");
			String temp = "";
			for(Element e : jobs){
				System.out.println(e.select("name").text());
				System.out.println(e.select("storage type").text());
				System.out.println(e.select("urlFilters urlFileter").val());
				JobConfiguration conf = new JobConfiguration();
				
				conf.setJobName(e.select("name").text())
				.setStorageType(getStorageType(e.select("storage").text()))
				.setAgent(e.select("agent").text());
				
				temp = e.select("threadNum").text();
				if(StringUtils.isNotBlank(temp))
					conf.setThreadNum(Integer.parseInt(temp));
				temp = e.select("delayBetweenRequests").text();
				if(StringUtils.isNotBlank(temp))
					conf.setDelayBetweenRequests(Integer.parseInt(temp));
					;
//				.setAgent(agent)
//				.setst
//				.setAgent(e.select("storage type").text());
				list.add(conf);
			}
		} catch (IOException e) {
			 log.fatal(e.getMessage());
			 throw new ConfigurationException("配置文件中存在错误，详细错误请查看日志");
		}
		return list;
	}
	
	/**
	 * @param storageType
	 * @return
	 * @desc 返回一个存储类型
	 */
	public StorageType getStorageType(String storageType){
		if(StringUtils.isNotBlank(storageType)){
			if(StorageType.Hbase.name().equals(storageType)){
				return StorageType.Hbase;
			}else if (storageType.equals(StorageType.ElasticSearch.name())) {
				return StorageType.ElasticSearch;
			}else if (storageType.equals(StorageType.LocalFile.name())){
				return StorageType.LocalFile;
			}else if (storageType.equals(StorageType.Mongodb.name())) {
				return StorageType.Mongodb;
			}else if (storageType.equals(StorageType.Mysql.name())){
				return StorageType.Mysql;
			}
		}
		return StorageType.LocalFile;
	}
	
	/**
	 * @param args
	 * @throws ConfigurationException 
	 * @desc 
	 */
	public static void main(String[] args) throws ConfigurationException {
//		JobConfigurationManager manager = new JobConfigurationManager();
//		List<JobConfiguration> jobs =  manager.loadJobConfigurations(
//				new File("D:\\pioneer\\goodcrawler\\src\\main\\resources\\job_conf.xml"));
//		for(JobConfiguration job :jobs){
////			System.out.println(job.toString());
//		}
		System.out.println(StorageType.Hbase.name());
	}

}
