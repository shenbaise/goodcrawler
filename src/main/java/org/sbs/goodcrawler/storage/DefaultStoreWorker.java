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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.jobconf.StoreConfig;
import org.sbs.goodcrawler.page.ExtractedPage;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @param <T>
 * @param <V>
 * @date 2013-7-4
 * 默认存储工
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultStoreWorker<V, T> extends StoreWorker<V, T>{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public DefaultStoreWorker(StoreConfig conf, Storage storage) {
		super(conf, storage);
	}

	@Override
	public void run() {
		ExtractedPage page ;
		while(!isStop()) {
			try {
				while(!pendingStore.isEmpty() && !isStop()){
					page = pendingStore.getElementT();
					System.out.println("get one ");
					work(page);
					System.out.println("over");
				}
			} catch (QueueException e) {
				 log.error(e.getMessage());
			}
		}
		System.err.println("###### out");
	}

	@Override
	public void onSuccessed(ExtractedPage<V, T> page) {
		page = null;
		pendingStore.getSuccess().incrementAndGet();
	}

	@Override
	public void onFailed(ExtractedPage<V, T> page) {
		page = null;
		pendingStore.getFailure().incrementAndGet();
	}

	@Override
	public void onIgnored(ExtractedPage<V, T> page) {
		page = null;
		pendingStore.getIgnored().incrementAndGet();
	}

	@Override
	public StoreResult store(ExtractedPage<V, T> page) {
		return storage.onStore(page);
	}

}
