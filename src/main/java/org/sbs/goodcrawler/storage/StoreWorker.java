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
package org.sbs.goodcrawler.storage;

import org.sbs.crawler.Worker;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @param <V>
 * @param <T>
 * @date 2013-7-4
 * 存储工
 */
public abstract class StoreWorker<V, T> extends Worker{
	
	protected PendingStore pendingStore = PendingStore.getInstance();
	protected JobConfiguration conf;
	protected Storage storage;
	/**
	 * 构造函数
	 */
	public StoreWorker(JobConfiguration jobConfiguration,Storage storage) {
		this.conf = jobConfiguration;
		this.storage = storage;
	}
	
	/**
	 * @desc 工作成功
	 */
	public abstract void onSuccessed(ExtractedPage<V, T> page);

	/**
	 * @desc 工作失败
	 */
	public abstract void onFailed(ExtractedPage<V, T> page);

	/**
	 * @desc 忽略工作
	 */
	public abstract void onIgnored(ExtractedPage<V, T> page);
	
	/**
	 * @param page
	 * @desc 存储
	 */
	public abstract StoreResult store(ExtractedPage<V, T> page);
	
	
	public void work(ExtractedPage<V, T> page) {
		StoreResult result = store(page);
		if (null != result){
			switch (result.status) {
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
	
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {

	}

}
