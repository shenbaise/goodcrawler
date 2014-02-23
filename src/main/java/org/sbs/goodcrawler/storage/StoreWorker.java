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

import org.sbs.goodcrawler.conf.Worker;
import org.sbs.goodcrawler.jobconf.StoreConfig;
import org.sbs.goodcrawler.page.ExtractedPage;
import org.sbs.pendingqueue.PendingManager;
import org.sbs.pendingqueue.PendingStore;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @param <V>
 * @param <T>
 * @date 2013-7-4
 * 存储工
 */
@SuppressWarnings("rawtypes")
public abstract class StoreWorker<V, T> extends Worker{
	
	protected PendingStore pendingStore = null;
	protected StoreConfig conf;
	protected Storage storage;
	/**
	 * 构造函数
	 */
	public StoreWorker(StoreConfig conf,Storage storage) {
		this.conf = conf;
		this.storage = storage;
		this.pendingStore = PendingManager.getPendingStore(conf.jobName);
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
		System.out.println("end ...");
		if (null != result && null!=result.status){
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
		}else {
			onFailed(page);
		}
	}
	
	
	public PendingStore getPendingStore() {
		return pendingStore;
	}

	public void setPendingStore(PendingStore pendingStore) {
		this.pendingStore = pendingStore;
	}

	public StoreConfig getConf() {
		return conf;
	}

	public void setConf(StoreConfig conf) {
		this.conf = conf;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {

	}

}
