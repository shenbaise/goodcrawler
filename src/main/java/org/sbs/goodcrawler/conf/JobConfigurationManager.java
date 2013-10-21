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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sbs.goodcrawler.exception.ConfigurationException;

import com.google.common.collect.Lists;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 任务加载管理类
 */
public class JobConfigurationManager {
	
	private static Log log = LogFactory.getLog(JobConfigurationManager.class);
	private static List<File> confFiles = null;
	private static List<Document> configDocuments = Lists.newArrayList();
	
	private static JobConfigurationManager manager = null;
	private static boolean initial = false;
	private JobConfigurationManager(){}
	/**
	 * 初始化配置
	 * @param confFile
	 */
	public static void init(){
		if(initial){
			try {
				throw new ConfigurationException("配置已经初始化，不能再次初始化！");
			} catch (Exception e) {
			}
		}else {
			File confPath = new File("conf");
			File[] files = confPath.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith("_conf.xml")){
						return true;
					}
					else
					return false;
				}
			});
			confFiles = Lists.newArrayList(files);
			load();
			manager = new JobConfigurationManager();
			initial = true;
		}
	}
	/**
	 * 返回配置实例
	 * @return
	 */
	public static JobConfigurationManager getInstance(){
		if(initial){
			return manager;
		}else {
			try {
				throw new ConfigurationException("请先进行初始化操作！");
			} catch (Exception e) {
			}
			return null;
		}
	}
	/**
	 * 加载配置
	 */
	private static void load(){
		for(File f:confFiles){
			try {
				Document doc = Jsoup.parse(f, "utf-8");
				configDocuments.add(doc);
			} catch (IOException e) {
				log.fatal("job配置加载失败");
			}
		}
	}
	
	public List<Document> getConfigDoc(){
		return configDocuments;
	}
	/**
	 * @param configFile
	 * @return
	 * @throws ConfigurationException 
	 * @desc 从配置文件中加载任务配置，一个job对应一个jobConfiguration
	 */
	/*
	public List<JobConfiguration> loadJobConfigurations(File configFile) throws ConfigurationException{
		List<JobConfiguration> list = Lists.newArrayList();
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
				List<String> seeds = Lists.newArrayList();
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
				List<String> urlFilters = Lists.newArrayList();
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
	*/
	
	
	/**
	 * @param args
	 * @throws ConfigurationException 
	 * @desc 
	 */
	public static void main(String[] args) throws ConfigurationException {
		/*
		JobConfigurationManager manager = new JobConfigurationManager();
		List<JobConfiguration> jobs =  manager.loadJobConfigurations(
				new File("D:\\pioneer\\goodcrawler\\src\\main\\resources\\job_conf.xml"));
		for(JobConfiguration job :jobs){
			System.out.println(job.toString());
		}
//		System.out.println(StorageType.Hbase.name());
		 */
		init();
	}

}
