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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.fetcher.FetchWorker;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.storage.PendingStore;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.BloomfilterHelper;
import org.sbs.goodcrawler.urlmanager.PendingUrls;
import org.sbs.goodcrawler.urlmanager.WebURL;
import org.sbs.robotstxt.RobotstxtServer;
import org.sbs.util.UrlUtils;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2
 * 提取器接口
 */
public abstract class Extractor {
	public JobConfiguration conf = null;
	PendingUrls pendingUrls = PendingUrls.getInstance();
	PendingStore pendingStore = PendingStore.getInstance();
	BloomfilterHelper bloomfilterHelper = BloomfilterHelper.getInstance();
	List<Pattern> urlFilters = new ArrayList<>();
	List<String> domains = new ArrayList<>();
	UrlUtils urlUtils = new UrlUtils();
	protected RobotstxtServer robotstxtServer;

	/**
	 * @param conf
	 * 构造函数
	 */
	public Extractor(JobConfiguration conf){
		this.conf = conf;
		// url 过滤器
		List<String> regs = conf.getUrlFilterReg();
		for(String reg:regs){
			Pattern p = Pattern.compile(reg);
			urlFilters.add(p);
		}
		// job爬取的域名
		List<String> list = conf.getSeeds();
		for(String seed:list){
			domains.add(urlUtils.getDomain(seed));
		}
		robotstxtServer = FetchWorker.robotstxtServer;
	}
	/**
	 * @param url
	 * @return
	 * @desc 配置的正则
	 */
	private boolean regFilter(String url){
		for(Pattern p : urlFilters){
			if(p.matcher(url).matches())
				return true;
		}
		return false;
	}
	/**
	 * @param url
	 * @return
	 * @desc 根据域名
	 */
	private boolean domainFilter(String url){
		if(conf.isOnlyDomain()){
			for(String domain:domains){
				if(url.contains(domain))
					return true;
			}
			return false;
		}else {
			return true;
		}
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
	 * @desc robotstxt 过滤
	 */
	private boolean robotsFilter(String url){
		if(conf.isRobots()){
			WebURL webURL = new WebURL();
			webURL.setURL(url);
			return robotstxtServer.allows(webURL);
		}else {
			return true;
		}
	}
	/**
	 * @param url
	 * @return
	 * @desc 根据配置过滤Url
	 */
	public boolean filterUrls(String url){
		return regFilter(url) && domainFilter(url) && robotsFilter(url) && bloomFilter(url);
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
