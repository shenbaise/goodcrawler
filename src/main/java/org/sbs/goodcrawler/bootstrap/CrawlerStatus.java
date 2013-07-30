/**
 * ########################  SHENBAISE'S WORK  ##########################
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

import org.sbs.goodcrawler.fetcher.PendingPages;
import org.sbs.goodcrawler.storage.PendingStore;
import org.sbs.goodcrawler.urlmanager.PendingUrls;

/**
 * @author whiteme
 * @date 2013年7月31日
 * @desc 爬虫运行状态
 */
public class CrawlerStatus {
	
	public static boolean running = false;
	
	public static String getStatus(){
		StringBuilder sb = new StringBuilder();
		sb.append(PendingUrls.getInstance().pendingStatus()).append("<br></br></br>")
		.append(PendingPages.getInstace().pendingStatus()).append("<br></br></br>")
		.append(PendingStore.getInstance().pendingStatus()).append("<br></br></br>");
		return sb.toString();
	}
	
	public static String getJobsNames(){
		return BootStrap.getJobsNames();
	}
}
