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

import org.sbs.goodcrawler.fetcher.FetchWorker;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.jobconf.ExtractConfig;
import org.sbs.goodcrawler.queue.PendingFetch;
import org.sbs.goodcrawler.queue.PendingStore;
import org.sbs.goodcrawler.queue.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.BloomfilterHelper;
import org.sbs.robotstxt.RobotstxtServer;
import org.sbs.util.UrlUtils;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2
 * 提取器接口
 */
public abstract class Extractor {
	
	public ExtractConfig conf = null;
	public PendingFetch pendingUrls = null;
	public PendingStore pendingStore = null;
	public BloomfilterHelper bloomfilterHelper = BloomfilterHelper.getInstance();
	public UrlUtils urlUtils = new UrlUtils();
	protected RobotstxtServer robotstxtServer;

	/**
	 * @param conf
	 * 构造函数
	 */
	public Extractor(ExtractConfig conf){
		this.conf = conf;
		robotstxtServer = FetchWorker.robotstxtServer;
		pendingUrls = PendingFetch.getPendingFetch(conf.jobName);
		pendingStore = PendingStore.getPendingStore(conf.jobName);
	}
	
	
	/**
	 * @param url
	 * @return
	 * @desc bloomFilter 过滤
	 */
	private boolean bloomFilter(String url){
		return !bloomfilterHelper.exist(url);
	}
	
	
	/**
	 * @param url
	 * @return
	 * @desc bloomFilter Url
	 */
	public boolean filterUrls(String url){
		return  bloomFilter(url);
	}
	/**
	 * @param page
	 * @return
	 * @desc 信息抽取方法
	 */
	public abstract ExtractedPage<?, ?> onExtract(Page page);

	/**
	 * @param page
	 * @return
	 * @desc 前（需要搜集本页的url，将符合要求的url加入等待队列）
	 */
	public abstract ExtractedPage<?, ?> beforeExtract(Page page);
	
	/**
	 * @param page
	 * @return
	 * @desc 后（对抽取的信息做进一步加工？或者别的操作）
	 */
	public abstract ExtractedPage<?, ?> afterExtract(Page page);
	
}
