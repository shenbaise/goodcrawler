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
package org.sbs.goodcrawler.conf.jobconf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.storage.StorageType;
import org.sbs.goodcrawler.urlmanager.PendingUrls;
import org.sbs.goodcrawler.urlmanager.WebURL;

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
	 * @throws ConfigurationException 
	 * @desc 从配置文件中加载任务配置，一个job对应一个jobConfiguration
	 */
	public List<JobConfiguration> loadJobConfigurations(File configFile) throws ConfigurationException{
		List<JobConfiguration> list = new ArrayList<>();
		try {
			Document doc = Jsoup.parse(configFile, "utf-8");
			Elements jobs = doc.select("job");
			String tem = "";
			String field = "";
			for(Element e : jobs){
				JobConfiguration conf = new JobConfiguration();
				Map<?, ?> m = BeanUtils.describe(conf);
				Iterator<?>  iterator = m.keySet().iterator();
				// 普通属性
				while (iterator.hasNext()) {
					field = (String) iterator.next();
					// seeds，urlFilterReg，selects单独处理
					if(field.equals("seeds") || "urlFilterReg".equals(field) 
							|| "selects".equals(field) || "storageType".equals(field))
						continue;
					if(StringUtils.isNotBlank((tem=e.select(field).text()))){
						BeanUtils.copyProperty(conf, field, ConvertUtils.convert(tem, PropertyUtils.getPropertyDescriptor(conf, field).getPropertyType()));
					}
				}
				// 种子&把种子插入队列
				Elements elements = e.select("seeds");
				List<String> seeds = new ArrayList<>();
				for(Element element:elements){
					String s = element.select("seed").text();
					seeds.add(s);
					WebURL seed = new WebURL();
					seed.setURL(s);
					try {
						PendingUrls.getInstance().addUrl(seed);
					} catch (QueueException e1) {
					}
				}
				conf.setSeeds(seeds);
				// Url正则
				elements = e.select("urlFilters urlFileter");
				List<String> urlFilters = new ArrayList<>();
				for(int i=0,n=elements.size();i<n;i++){
					urlFilters.add(elements.get(i).val());
				}
				conf.setUrlFilterReg(urlFilters);
				// 提取器
				elements = e.select("selects select");
				Map<String, String> selects = new HashMap<String, String>();
				for(int i=0,n=elements.size();i<n;i++){
					selects.put(elements.get(i).attr("name"), elements.get(i).attr("value"));
				}
				conf.setSelects(selects);
				// 存储类型
				conf.setStorageType(getStorageType(e.select("storageType").text()));
				list.add(conf);
			}
		} catch (IOException e) {
			 log.fatal(e.getMessage());
			 throw new ConfigurationException("配置文件中存在错误，详细错误请查看日志");
		} catch (IllegalAccessException e) {
			 log.error(e.getMessage());
		} catch (InvocationTargetException e) {
			 log.error(e.getMessage());
		} catch (NoSuchMethodException e) {
			 log.error(e.getMessage());
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
		JobConfigurationManager manager = new JobConfigurationManager();
		List<JobConfiguration> jobs =  manager.loadJobConfigurations(
				new File("D:\\pioneer\\goodcrawler\\src\\main\\resources\\job_conf.xml"));
		for(JobConfiguration job :jobs){
			System.out.println(job.toString());
		}
//		System.out.println(StorageType.Hbase.name());
	}

}
