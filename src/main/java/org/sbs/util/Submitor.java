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
package org.sbs.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.urlmanager.PendingUrls;
import org.sbs.goodcrawler.urlmanager.WebURL;

/**
 * @author whiteme
 * @date 2013年10月7日
 * @desc 
 */
public class Submitor {
	private static ExecutorService poolExecutor = null;
	private static PendingUrls pendingUrls = PendingUrls.getInstance();
	static {
		poolExecutor = Executors.newFixedThreadPool(10);
	}
	
	/**
	 * 添加一个Url到队列，超时2秒后丢弃。
	 * @param url
	 */
	public static void submitUrl(final WebURL url) {
		poolExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					pendingUrls.addUrl(url, 50);
				} catch (QueueException e) {
					try {
						throw new QueueException(e.getMessage());
					} catch (QueueException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
