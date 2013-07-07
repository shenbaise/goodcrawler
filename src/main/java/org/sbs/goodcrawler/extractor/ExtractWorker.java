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
package org.sbs.goodcrawler.extractor;

import org.sbs.crawler.Worker;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.fetcher.PendingPages;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.storage.PendingStore;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.PendingUrls;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2 信息提取工
 */
public abstract class ExtractWorker extends Worker {


	protected PendingUrls pendingUrls = PendingUrls.getInstance();
	protected PendingPages pendingPages = PendingPages.getInstace();
	protected PendingStore pendingStore = PendingStore.getInstance();
	protected JobConfiguration conf;
	protected Extractor extractor;

	public ExtractWorker(JobConfiguration conf, Extractor extractor) {
		this.conf = conf;
		this.extractor = extractor;
	}

	/**
	 * @desc 工作成功
	 */
	public abstract void onSuccessed(Page page);

	/**
	 * @desc 工作失败
	 */
	public abstract void onFailed(Page page);

	/**
	 * @desc 忽略工作
	 */
	public abstract void onIgnored(Page page);

	public abstract ExtractedPage<?, ?> doExtract(Page page);

	public void work(Page page) {
		// 提取信息
		ExtractedPage<?, ?> ep = doExtract(page);
		// 回馈结果
		if (null != ep && null != ep.getResult()){
			switch (ep.getResult()) {
			case ignored:
				onIgnored(page);
				break;
			case success:
				onSuccessed(page);
				break;
			case failed:
				onFailed(page);
				break;
			default:
				break;
			}
		}
	}
}
