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
package org.sbs.goodcrawler.schedule;

import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.fetcher.FetchWorker;
import org.sbs.goodcrawler.fetcher.PageFetcher;
import org.sbs.goodcrawler.jobconf.FetchConfig;
import org.sbs.pendingqueue.PendRecraw;
import org.sbs.pendingqueue.PendingManager;
import org.sbs.url.WebURL;

public class RecrawFetherWorkor extends FetchWorker {
	
	private PendRecraw pendRecraw = null;
	public RecrawFetherWorkor(FetchConfig conf, PageFetcher fetcher) {
		super(conf, fetcher);
		pendRecraw = PendingManager.getUrlsToRecraw(conf.jobName);
	}

	@Override
	public void run() {
		WebURL url ;
		try {
			while(!isStop()){
				while(null!=(url=pendRecraw.getElementT())){
					fetchPageWhitoutExtractUrl(url);
					// 确保当前任务完成后跳出
					if(isStop())
						break;
				}
			}
		} catch (QueueException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSuccessed() {
		pendRecraw.processedSuccess();
	}

	@Override
	public void onFailed(WebURL url) {
		pendingPages.processedFailure();
	}

	@Override
	public void onIgnored(WebURL url) {
		pendingPages.processedIgnored();
	}

}
