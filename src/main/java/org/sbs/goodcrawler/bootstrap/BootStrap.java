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
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.bootstrap.foreman.ExtractForeman;
import org.sbs.goodcrawler.bootstrap.foreman.FetchForeman;
import org.sbs.goodcrawler.bootstrap.foreman.StoreForeman;
import org.sbs.goodcrawler.conf.JobConfigurationManager;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.jobconf.ExtractConfig;
import org.sbs.goodcrawler.jobconf.FetchConfig;
import org.sbs.goodcrawler.jobconf.Job;
import org.sbs.goodcrawler.jobconf.StoreConfig;
import org.sbs.goodcrawler.queue.PendingExtract;
import org.sbs.goodcrawler.queue.PendingFetch;
import org.sbs.goodcrawler.queue.PendingStore;
import org.sbs.goodcrawler.urlmanager.BloomfilterHelper;

import com.google.common.collect.Sets;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-3 bootstrap
 */
public class BootStrap {
	private static Log log = LogFactory.getLog("bootStrap");
	private static PropertyConfigurationHelper conHelper = PropertyConfigurationHelper
			.getInstance();
	private static Set<Job> jobs = Sets.newLinkedHashSet();

	/**
	 * @param args
	 * @desc
	 */
	public static void main(String[] args) {
		try {
			startAll();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动任务
	 * 
	 * @param jobConf
	 * @throws ConfigurationException
	 */
	public static void startAll() throws ConfigurationException {
		JobConfigurationManager.init();
		JobConfigurationManager manager = JobConfigurationManager.getInstance();
		List<Document> configDocs = manager.getConfigDoc();

		for (Document doc : configDocs) {
			final Job job = createJob(doc);
			if(null!= JobStops.isStop(job.getJobName()) && !JobStops.isStop(job.getJobName()))
				continue;
			// fetcher
			FetchForeman fetchForeman = new FetchForeman();
			fetchForeman.start(job.getFetchConfig());
			// extract
			ExtractForeman extractForeman = new ExtractForeman();
			extractForeman.start(job.getExtractConfig());
			// store
			StoreForeman storeForeman = new StoreForeman();
			storeForeman.start(job.getStoreConfig());
			// 设置标识
			JobStops.startJob(job.getJobName());
			// 添加job
			BootStrap.jobs.add(job);
			// 状态监听
			if (job.isMonitorJob()) {
				Runnable runnable = new Runnable() {
					PendingFetch pendingUrls = PendingFetch.getPendingFetch(job
							.getJobName());
					PendingExtract pendingPages = PendingExtract
							.getPendingExtract(job.getJobName());
					PendingStore pendingStore = PendingStore
							.getPendingStore(job.getJobName());

					@Override
					public void run() {
						while (!JobStops.isStop(job.getJobName())) {
							try {
								Thread.sleep(20000L);
							} catch (InterruptedException e) {
							}
							System.out.println(pendingUrls.pendingStatus());
							System.out.println(pendingPages.pendingStatus());
							System.out.println(pendingStore.pendingStatus());
						}
					}
				};

				Thread monitor = new Thread(runnable, "QueueMonitor["
						+ job.getJobName() + "]");
				monitor.start();
			}
		}

		CrawlerStatus.running = true;
	}
	
	/**
	 * 启动一个任务
	 * @param jobName
	 */
	public static void start(String jobName){
		for(final Job job:jobs){
			if(jobName.endsWith(job.getJobName())){
				// fetcher
				FetchForeman fetchForeman = new FetchForeman();
				fetchForeman.start(job.getFetchConfig());
				// extract
				ExtractForeman extractForeman = new ExtractForeman();
				extractForeman.start(job.getExtractConfig());
				// store
				StoreForeman storeForeman = new StoreForeman();
				storeForeman.start(job.getStoreConfig());
				// 设置标识
				JobStops.startJob(jobName);
				// 状态监听
				if (job.isMonitorJob()) {
					Runnable runnable = new Runnable() {
						PendingFetch pendingUrls = PendingFetch.getPendingFetch(job
								.getJobName());
						PendingExtract pendingPages = PendingExtract
								.getPendingExtract(job.getJobName());
						PendingStore pendingStore = PendingStore
								.getPendingStore(job.getJobName());

						@Override
						public void run() {
							while (!JobStops.isStop(job.getJobName())) {
								try {
									Thread.sleep(20000L);
								} catch (InterruptedException e) {
								}
								System.out.println(pendingUrls.pendingStatus());
								System.out.println(pendingPages.pendingStatus());
								System.out.println(pendingStore.pendingStatus());
							}
						}
					};

					Thread monitor = new Thread(runnable, "QueueMonitor["
							+ job.getJobName() + "]");
					monitor.start();
				}
			}
		}
	}
	
	/**
	 * 停止全部任务&保存状态
	 */
	public static void stopAll() {
		stopAndSaveAllJobsStatus();
		CrawlerStatus.running = false;
	}

	/**
	 * 停止任务并保存任务状态
	 * 
	 * @param jobName
	 */
	private static void stopAndSaveStatus(String jobName) {
		for (Job job : jobs) {
			if (jobName.equals(job.getJobName())) {
				JobStops.stopJob(jobName);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				saveJobStatus(jobName);
				break;
			}
		}
	}
	
	
	public static Set<Job> getJobs() {
		return jobs;
	}

	/**
	 * 保存job运行状态
	 * 
	 * @param jobName
	 */
	private static void saveJobStatus(String jobName) {
		PendingFetch urls = PendingFetch.getPendingFetch(jobName);
		PendingExtract pages = PendingExtract.getPendingExtract(jobName);
		PendingStore stores = PendingStore.getPendingStore(jobName);
		BloomfilterHelper bloomfilter = BloomfilterHelper.getInstance();

		File base = new File(conHelper.getString("status.save.path", "status")
				+ "/" + jobName + "/");
		if (!base.exists()) {
			base.mkdir();
		}
		File urlsFile = new File(base, "urls.good");
		File pagesFile = new File(base, "pages.good");
		File storesFile = new File(base, "stores.good");
		File filterFile = new File(base, "filter.good");

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

	/**
	 * 保存状态，下次启动时可恢复
	 */
	private static void stopAndSaveAllJobsStatus() {
		for (Job job : jobs) {
			stopAndSaveStatus(job.getJobName());
		}
	}

	/**
	 * 创建job配置对象
	 * 
	 * @param doc
	 * @return
	 */
	private static Job createJob(Document doc) {
		Job job = new Job();
		Elements e = doc.select("job");
		String temp = e.attr("name");
		if (StringUtils.isNotBlank(temp)) {
			job.setJobName(temp);
		}
		temp = e.select("urlNum").text();
		if (StringUtils.isNotBlank(temp)) {
			job.setUrlNum(Integer.parseInt(temp));
		}
		temp = e.select("monitorJob").text();
		if (StringUtils.isNotBlank(temp)) {
			Boolean b = Boolean.parseBoolean(temp);
			if (null != b)
				job.setMonitorJob(b);
		}
		temp = e.select("jobtime").text();
		if (StringUtils.isNotBlank(temp)) {
			job.setJobtime(Integer.parseInt(temp));
		}
		temp = e.select("cron").text();
		if (StringUtils.isNotBlank(temp)) {
			job.setCron(temp);
		}
		try {
			// fconf
			FetchConfig fConfig = new FetchConfig();
			job.setFetchConfig(fConfig.loadConfig(doc));
			PendingFetch.getPendingFetch(fConfig.jobName,
					fConfig.getQueueSize());
			log.info("正在加载任务:" + fConfig.jobName);
			log.info("Fetch加载完成");
			// extract
			ExtractConfig eConfig = new ExtractConfig();
			job.setExtractConfig(eConfig.loadConfig(doc));
			PendingExtract.getPendingExtract(eConfig.jobName,
					eConfig.getQueueSize());
			log.info("Extract加载完成");
			// store
			StoreConfig sConfig = new StoreConfig();
			job.setStoreConfig(sConfig.loadConfig(doc));
			PendingStore.getPendingStore(sConfig.jobName,
					sConfig.getQueueSize());
			log.info("Store加载完成");
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		}
		return job;
	}
}
