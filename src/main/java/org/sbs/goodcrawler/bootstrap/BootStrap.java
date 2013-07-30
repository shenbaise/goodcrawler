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
package org.sbs.goodcrawler.bootstrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.sbs.crawler.Worker;
import org.sbs.goodcrawler.bootstrap.foreman.ExtractForeman;
import org.sbs.goodcrawler.bootstrap.foreman.FetchForeman;
import org.sbs.goodcrawler.bootstrap.foreman.StoreForeman;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.conf.jobconf.JobConfigurationManager;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.fetcher.PageFetcher;
import org.sbs.goodcrawler.fetcher.PendingPages;
import org.sbs.goodcrawler.plugin.extract.Extractor66ys;
import org.sbs.goodcrawler.plugin.storage.ElasticSearchStorage;
import org.sbs.goodcrawler.storage.PendingStore;
import org.sbs.goodcrawler.urlmanager.PendingUrls;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-3
 * bootstrap
 */
public class BootStrap {
	private static PropertyConfigurationHelper conHelper = PropertyConfigurationHelper.getInstance();
	private static String jobs = "";
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
//		start("D:\\pioneer\\goodcrawler\\src\\main\\resources\\job_conf.xml");
		stop();
	}
	/**
	 * 启动任务
	 * @param jobConf
	 */
	public static void start(String jobConf){
		JobConfigurationManager manager = new JobConfigurationManager();
		List<JobConfiguration> jobs = null;
		try {
			jobs = manager.loadJobConfigurations(
					new File(jobConf));
			for(JobConfiguration conf:jobs){
				// fetch
				FetchForeman.start(conf,new PageFetcher(conf));
				// extract
				// 66sy
				
//				Class<?> extractor66ys = ClassLoader.getSystemClassLoader().loadClass("org.sbs.goodcrawler.plugin.extract.Extractor66ys.java");
				
				ExtractForeman.start(conf,new Extractor66ys(conf));
				// dytt8
//				ExtractForeman.start(conf,new ExtractorDytt8(conf));
				// store
				StoreForeman.start(conf,new ElasticSearchStorage(conf.getName()));
				
				BootStrap.jobs += conf.getName() + ";";
			}
		} catch (ConfigurationException e) {
			 e.getMessage();
		}
		
		
		Runnable runnable = new Runnable() {
			PendingUrls pendingUrls = PendingUrls.getInstance();
			PendingPages pendingPages = PendingPages.getInstace();
			PendingStore pendingStore = PendingStore.getInstance();
			@Override
			public void run() {
				while(!Worker.stop){
					try {
						Thread.sleep(10000L);
					} catch (InterruptedException e) {
					}
					System.out.println(pendingUrls.pendingStatus());
					System.out.println(pendingPages.pendingStatus());
					System.out.println(pendingStore.pendingStatus());
				}
			}
		};
		
		Thread monitor = new Thread(runnable,"QueueMonitor");
		monitor.start();
		
		CrawlerStatus.running = true;
	}
	
	/**
	 * 停止抓取&保存状态
	 */
	public static void stop(){
		Worker.stop();
		saveStatus();
		CrawlerStatus.running = false;
	}
	/**
	 * 保存状态，下次启动时可恢复
	 */
	private static void saveStatus(){
		PendingUrls urls = PendingUrls.getInstance();
		PendingPages pages = PendingPages.getInstace();
		PendingStore stores = PendingStore.getInstance();
		
		File base = new File(conHelper.getString("status.save.path", "status"));
		if (!base.exists()) {
			base.mkdir();
		}
		File urlsFile = new File(base, "ulrs.good");
		File pagesFile = new File(base,"pages.good");
		File storesFile = new File(base,"stores.good");
		
		try {
			FileOutputStream fosUrl = new FileOutputStream(urlsFile);
			ObjectOutputStream oosUrl = new ObjectOutputStream(fosUrl);
			oosUrl.writeObject(urls);
			oosUrl.close();
			fosUrl.close();
			
			FileOutputStream fosPage = new FileOutputStream(pagesFile);
			ObjectOutputStream oosPage = new ObjectOutputStream(fosPage);
			oosPage.writeObject(pages);
			oosPage.close();
			fosPage.close();
			
			FileOutputStream fosStore = new FileOutputStream(storesFile);
			ObjectOutputStream oosStore = new ObjectOutputStream(fosStore);
			oosStore.writeObject(stores);
			oosStore.close();
			fosStore.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getJobsNames(){
		return jobs;
	}
}
