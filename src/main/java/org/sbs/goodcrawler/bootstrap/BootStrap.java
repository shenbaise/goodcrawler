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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.sbs.goodcrawler.bootstrap.foreman.ExtractForeman;
import org.sbs.goodcrawler.bootstrap.foreman.FetchForeman;
import org.sbs.goodcrawler.bootstrap.foreman.StoreForeman;
import org.sbs.goodcrawler.conf.JobConfigurationManager;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.conf.Worker;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.jobconf.ExtractConfig;
import org.sbs.goodcrawler.jobconf.FetchConfig;
import org.sbs.goodcrawler.jobconf.StoreConfig;
import org.sbs.pendingqueue.PendingManager;
import org.sbs.pendingqueue.PendingPages;
import org.sbs.pendingqueue.PendingStore;
import org.sbs.pendingqueue.PendingUrls;
import org.sbs.util.BloomfilterHelper;
import org.sbs.util.MD5Utils;

import com.google.common.collect.Maps;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-3
 * bootstrap
 */
public class BootStrap {
	private static Log log = LogFactory.getLog("bootStrap");
	private static PropertyConfigurationHelper propertyHelper = PropertyConfigurationHelper.getInstance();
	private static Map<String, String> jobs = Maps.newConcurrentMap();
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
		try {
			start();
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 启动任务
	 * @param jobConf
	 * @throws ConfigurationException 
	 */
	public static void start() throws ConfigurationException{
		JobConfigurationManager.init();
		JobConfigurationManager manager = JobConfigurationManager.getInstance();
		List<Document> configDocs = manager.getConfigDoc();
		
		for(Document doc:configDocs){
			// fetcher
			FetchConfig fConfig = new FetchConfig();
			FetchForeman fetchForeman = new FetchForeman();
			fetchForeman.start(fConfig.loadConfig(doc));
			log.info("正在加载任务:"+fConfig.jobName);
			log.info("Fetch加载完成");
			
			// extract
			ExtractConfig eConfig = new ExtractConfig();
			ExtractForeman extractForeman = new ExtractForeman();
			extractForeman.start(eConfig.loadConfig(doc));
			log.info("Extract加载完成");
			// store
			StoreConfig sConfig = new StoreConfig();
			StoreForeman storeForeman = new StoreForeman();
			storeForeman.start(sConfig.loadConfig(doc));
			log.info("Store加载完成");
			jobs.put(MD5Utils.createMD5(fConfig.jobName),fConfig.jobName);
		}
		
		/**
		 * 状态监听
		 */
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while(!Worker.stop){
					try {
						Thread.sleep(20000L);
					} catch (InterruptedException e) {
					}
					Collection<String> js = jobs.values();
					for(String job:js){
						System.out.println("job="+job+"\n"+PendingManager.getPendingUlr(job).pendingStatus());
						System.out.println("job="+job+"\n"+PendingManager.getPendingPages(job).pendingStatus());
						System.out.println("job="+job+"\n"+PendingManager.getPendingStore(job).pendingStatus());
					}
				}
//				System.exit(0);
			}
		};
		
		Thread monitor = new Thread(runnable,"QueueMonitor");
		monitor.start();
		
		CrawlerStatus.running = true;
	}
	
	/**
	 * 停止抓取&保存状态
	 */
	public synchronized static void stop(String jobId){
		Worker.stop();
		saveStatus(jobId);
	}
	/**
	 * stop all
	 */
	public synchronized static void stopAll(){
		for(String job:jobs.keySet()){
			stop(job);
		}
		CrawlerStatus.running = false;
	}
	/**
	 * 保存状态，下次启动时可恢复
	 */
	private static void saveStatus(String jobId){
		PendingUrls urls = PendingManager.getPendingUlr(jobs.get(jobId));
		PendingPages pages = PendingManager.getPendingPages(jobs.get(jobId));
		PendingStore stores = PendingManager.getPendingStore(jobs.get(jobId));
		BloomfilterHelper bloomfilter = BloomfilterHelper.getInstance();
		
		File base = new File(propertyHelper.getString("status.save.path", "status"));
		if (!base.exists()) {
			base.mkdir();
		}
		File urlsFile = new File(base,jobs.get(jobId) + "/"+urls.getClass().getSimpleName()+".good");
		File pagesFile = new File(base,jobs.get(jobId) + "/"+urls.getClass().getSimpleName()+".good");
		File storesFile = new File(base,jobs.get(jobId) + "/"+urls.getClass().getSimpleName()+".good");
		File filterFile = new File(base,"filter.good");
		
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
			
			FileOutputStream fosFilter = new FileOutputStream(filterFile);
			ObjectOutputStream oosFilter = new ObjectOutputStream(fosFilter);
			oosFilter.writeObject(bloomfilter);
			oosFilter.close();
			fosFilter.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getJobsNames(){
		Collection<String> js = jobs.values();
		return StringUtils.join(js, ",");
	}
	
	public static String getJobids(){
		return StringUtils.join(jobs.keySet(),",");
	}
}
