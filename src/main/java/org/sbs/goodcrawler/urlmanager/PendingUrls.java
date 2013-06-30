/**
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
package org.sbs.goodcrawler.urlmanager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.GlobalConstants;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * @desc 待处理的Urls队列
 */
public class PendingUrls {
	
	private Log log = LogFactory.getLog(this.getClass());
	private static BlockingQueue<WebURL> Queue = null;
	private static PendingUrls instance = null;
	
	/**
	 * 总URL个数，每爬到一个+1
	 */
	private AtomicLong urlCount = new AtomicLong(0L);
	/**
	 * 已经成功处理的URL个数
	 */
	private AtomicLong success = new AtomicLong(0L);
	/**
	 * 处理失败的URL个数
	 */
	private AtomicInteger failure = new AtomicInteger(0);
	/**
	 * 依据job配置被忽略的链接个数
	 */
	private AtomicLong ignored = new AtomicLong(0);
	
	private PendingUrls(){
		init();
	}
	
	public static PendingUrls getInstance(){
		if(null==instance){
			instance = new PendingUrls();
		}
		return instance;
	}
	
	/**
	 * @desc 初始化队列
	 */
	private void init(){
		Queue = new ArrayBlockingQueue<>(PropertyConfigurationHelper.getInstance().getInt(GlobalConstants.pendingUrlsQueueSize, 2000));
	}
	
	/**
	 * @param url
	 * @desc 加入一个待处理的URL，Url总数+1
	 */
	public void addUrl(WebURL url){
		try {
			if(null != url){
				Queue.put(url);
				urlCount.getAndIncrement();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 * @desc 返回一个将要处理的URL
	 */
	public WebURL getUrl(){
		try {
			return Queue.take();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public long processedSuccess(long c){
		return success.addAndGet(c);
	}
	
	public long processedSuccess(){
		return success.incrementAndGet();
	}
	
	public int processedFailure(int c){
		return failure.addAndGet(c);
	}
	
	public int processedFailure(){
		return failure.incrementAndGet();
	}
	
	public long urlCount(){
		return urlCount.get();
	}
	
	public long success(){
		return success.get();
	}
	
	public int failure(){
		return failure.get();
	}
	
	public long processedIgnored(long c){
		return ignored.addAndGet(c);
	}
	
	public long processedIgnored(){
		return ignored.incrementAndGet();
	}
	
	public long ignored(){
		return ignored.get();
	}
	
	public String pendingStatus(){
		StringBuilder sb = new StringBuilder(32);
		sb.append("队列中等待处理的URL有").append(Queue.size()).append("个，")
		.append("截至目前共爬到").append(urlCount).append("个链接。\n已成功处理")
		.append(success.get()).append("个，失败").append(failure.get()).append("个")
		.append("忽略").append(ignored()).append("个");
		return sb.toString();
	}
}
