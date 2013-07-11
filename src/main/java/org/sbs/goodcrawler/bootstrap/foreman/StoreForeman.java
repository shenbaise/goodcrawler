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
package org.sbs.goodcrawler.bootstrap.foreman;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sbs.goodcrawler.conf.GlobalConstants;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.fetcher.DefaultFetchWorker;
import org.sbs.goodcrawler.fetcher.PageFetcher;
import org.sbs.goodcrawler.storage.DefaultStoreWorker;
import org.sbs.goodcrawler.storage.LocalFileStorage;
import org.sbs.goodcrawler.storage.Storage;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-4
 * 存储工头
 */
public class StoreForeman {

	public StoreForeman() {
	}
	
	public static void start(JobConfiguration conf,Storage storage){
		int threadNum = (int) (conf.getThreadNum() * 0.3);
		threadNum = 1;
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
//		Storage storage = new LocalFileStorage(PropertyConfigurationHelper.getInstance().getString(GlobalConstants.failedPagesBackupPath, ""), conf.getName());
		for(int i=0;i<threadNum;i++){
			executor.submit(new DefaultStoreWorker(conf,storage));
		}
	}

	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {

	}

}
