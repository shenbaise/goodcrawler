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

import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2
 * 提取器接口
 */
public abstract class Extractor {
	public JobConfiguration conf = null;
	PendingUrls pendingUrls = PendingUrls.getInstance();
	BloomfilterHelper bloomfilterHelper = BloomfilterHelper.getInstance();
	List<Pattern> patterns = new ArrayList<>();
	List<String> domains = new ArrayList<>();

	public Extractor(JobConfiguration conf){
		this.conf = conf;
		List<String> regs = conf.getUrlFilterReg();
		for(String reg:regs){
			Pattern p = Pattern.compile(reg);
			patterns.add(p);
		}
		List<String> list = conf.getSeeds();
		for(String seed:list){
			domains.add(getDomain(seed));
		}
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
