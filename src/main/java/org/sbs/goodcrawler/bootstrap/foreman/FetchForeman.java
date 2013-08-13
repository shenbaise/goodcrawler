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

import org.sbs.goodcrawler.conf.jobconf.FetchConfig;
import org.sbs.goodcrawler.fetcher.DefaultFetchWorker;
import org.sbs.goodcrawler.fetcher.PageFetcher;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2
 * 这是个工头
 */
public class FetchForeman extends Foreman{
	
	public static void start(FetchConfig conf){
		int threadNum = conf.getThreadNum();
		PageFetcher fetcher = new PageFetcher(conf);
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		for(int i=0;i<threadNum;i++){
			executor.submit(new DefaultFetchWorker(conf,fetcher));
		}
		executor.shutdown();
	}
	
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("////");
			}
		});
		executor.shutdown();
	}

}
