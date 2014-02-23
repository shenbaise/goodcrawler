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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.jobconf.FetchConfig;
import org.sbs.pendingqueue.PendingManager;
import org.sbs.url.WebURL;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-1
 * 默认的爬取工人
 */
public class DefaultFetchWorker extends FetchWorker {
	
	public DefaultFetchWorker(FetchConfig conf, PageFetcher fetcher) {
		super(conf, fetcher);
	}

	private Log log = LogFactory.getLog(this.getClass());
	
	

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
		try {
			while(!isStop()){
				while(null!=(url=pendingUrls.getElementT())){
					fetchPage(url);
					// 确保当前任务完成后跳出
					if(isStop())
						break;
					c++;
					if(c>10000){
						c=0;
						Thread.sleep(2000L);
					}
				}
			}
		} catch (QueueException e) {
			e.printStackTrace();
			 log.error(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			 log.error(e.getMessage());
		}
	}
	
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
	
	}
}
