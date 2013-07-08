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
package org.sbs.goodcrawler.fetcher;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.conf.jobconf.JobConfigurationManager;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.urlmanager.WebURL;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-1
 * 默认的爬取工人
 */
public class DefaultFetchWorker extends FetchWorker {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public DefaultFetchWorker(JobConfiguration conf,PageFetcher fetcher) {
		super(conf);
		this.fetcher = fetcher;
	}

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.fetcher.FetchWorker#onSuccessed()
	 */
	@Override
	public void onSuccessed() {
		pendingUrls.processedSuccess();
	}

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.fetcher.FetchWorker#onFailed(org.sbs.goodcrawler.urlmanager.WebURL)
	 */
	@Override
	public void onFailed(WebURL url) {
//		log.info("一个页面抓取失败:"+url.getURL());
		pendingUrls.processedFailure();
	}

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.fetcher.FetchWorker#onIgnored(org.sbs.goodcrawler.urlmanager.WebURL)
	 */
	@Override
	public void onIgnored(WebURL url) {
//		log.info("一个页面大小超过限制，已被忽略："+url.getURL());
	}
	
	@Override
	public void run() {
		int c = 0;
		WebURL url ;
		String s = "";
		try {
			while(true){
				while(null!=(url=pendingUrls.getUrl())){
					s = url.getURL();
					if(s.contains(".rmvb")
							||s.contains("ftp")
							||s.contains(".aiv")
							||s.contains(".mtk")
							||s.contains(".rm")){
						pendingUrls.processedIgnored();
						
						continue;
					}
					fetchPage(url);
					c++;
					if(c>10000){
						c=0;
						Thread.sleep(2000L);
					}
				}
			}
		} catch (QueueException e) {
			 log.error(e.getMessage());
		} catch (InterruptedException e) {
			 log.error(e.getMessage());
		}
	}
	
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
		JobConfigurationManager manager = new JobConfigurationManager();
		List<JobConfiguration> jobs;
		try {
			jobs = manager.loadJobConfigurations(
					new File("D:\\pioneer\\goodcrawler\\src\\main\\resources\\job_conf.xml"));
			// 创建一个工人
			FetchWorker fetchWorker = new DefaultFetchWorker(jobs.get(1),new PageFetcher(jobs.get(1)));
			// 设置fetcher
//			fetchWorker.setFetcher(new PageFetcher(jobs.get(1)));
			// 开始工作
			(new Thread(fetchWorker)).start();
		} catch (ConfigurationException e) {
			// log.error(e.getMessage());
		}
	}
}
