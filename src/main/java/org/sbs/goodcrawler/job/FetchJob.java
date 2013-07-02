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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.fetcher.CustomFetchStatus;
import org.sbs.goodcrawler.fetcher.PageFetchResult;
import org.sbs.goodcrawler.fetcher.PageFetcher;
import org.sbs.goodcrawler.processor.PendingPages;
import org.sbs.goodcrawler.urlmanager.PendingUrls;
import org.sbs.goodcrawler.urlmanager.WebURL;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @param <T>
 * @date 2013-6-30
 * 爬取页面的job
 */
public class FetchJob extends Job{
	
	private Log log = LogFactory.getLog(this.getClass());
	private PendingUrls pendingUrls = PendingUrls.getInstance();
	private PendingPages pendingPages = PendingPages.getInstace();
	private PageFetcher pageFetcher;
	private Parser parser;
	
	/**
	 * 构造函数
	 * @param conf
	 */
	public FetchJob(JobConfiguration conf) {
		super(conf);
		pageFetcher = new PageFetcher(conf);
		parser = new Parser(conf);
	}
	/**
	 * @param url
	 * @desc 抓取一个链接
	 */
	public void fetchPage(WebURL url){
		PageFetchResult result = null;
		if(null!=null && StringUtils.isNotBlank(url.getURL())){
			result = pageFetcher.fetchHeader(url);
			// 获取状态
			int statusCode = result.getStatusCode();
			if (statusCode == CustomFetchStatus.PageTooBig) {
				log.warn("页面超过大小限制，自动忽略: " + url.getURL());
				pendingUrls.processedIgnored();
				return ;
			}
			if (statusCode != HttpStatus.SC_OK){
				pendingUrls.processedFailure();
			}else {
				Page page = new Page(url);
				pendingUrls.processedSuccess();
				if (!result.fetchContent(page)) {
					pendingUrls.processedFailure();
				}
				if (!parser.parse(page, url.getURL())) {
					pendingUrls.processedFailure();
				}
				// 
				try {
					pendingPages.addPage(page);
				} catch (QueueException e) {
				}
			}
		}
	}
	
	@Override
	public void run() {
		WebURL url ;
		try {
			while(null!=(url=pendingUrls.getUrl())){
				fetchPage(url);
			}
		} catch (QueueException e) {
			log.fatal("待处理链接队列操作中断");
		}
	}

}
