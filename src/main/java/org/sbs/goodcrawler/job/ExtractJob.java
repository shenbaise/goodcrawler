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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.jobconf.JobConfigurationX;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.fetcher.PendingPages;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 提取页面信息job
 */
public class ExtractJob extends Job {
	
	private Log log = LogFactory.getLog(this.getClass());
	private PendingPages pendingPages = PendingPages.getInstace();
	
	public ExtractJob(JobConfigurationX conf) {
		super(conf);
	}
	
	/**
	 * @param page
	 * @desc 抽取页面信息
	 */
	public void extract(Page page){
		
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Page page ;
		try {
			while(null!=(page=pendingPages.getPage())){
				extract(page);
			}
		} catch (QueueException e) {
			log.error(e.getMessage());
		}
	}
}
