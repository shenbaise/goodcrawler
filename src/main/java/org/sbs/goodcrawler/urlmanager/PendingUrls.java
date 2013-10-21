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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.GlobalConstants;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.QueueException;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * @desc 待处理的Urls队列
 */
public class PendingUrls implements Serializable {

	private static final long serialVersionUID = 2260258894389085989L;
	private Log log = LogFactory.getLog(this.getClass());
	private BlockingQueue<WebURL> Queue = null;
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

	/**
	 * 构造函数
	 */
	private PendingUrls() {
		init();
	}

	/**
	 * @return
	 * @desc 返回队列实例
	 */
	public static PendingUrls getInstance() {
		if (null == instance) {
			instance = new PendingUrls();
		}
		return instance;
	}

	/**
	 * @desc 初始化队列
	 */
	private void init() {
		File file = new File(PropertyConfigurationHelper.getInstance()
				.getString("status.save.path", "status")
				+ File.separator
				+ "urls.good");
		if (file.exists()) {
			try {
				FileInputStream fisUrl = new FileInputStream(file);
				ObjectInputStream oisUrl = new ObjectInputStream(fisUrl);
				instance = (PendingUrls) oisUrl.readObject();
				oisUrl.close();
				fisUrl.close();
				Queue = instance.Queue;
				failure = instance.failure;
				success = instance.success;
				urlCount = instance.urlCount;
				ignored = instance.ignored;
				System.out.println("recovery url queue..." + Queue.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} 
		if(null==Queue)
			Queue = new ArrayBlockingQueue<>(PropertyConfigurationHelper
					.getInstance().getInt(GlobalConstants.pendingUrlsQueueSize,
							1000000));
	}

	/**
	 * @param url
	 * @desc 加入一个待处理的URL，Url总数+1
	 */
	public void addUrl(WebURL url) throws QueueException {
		try {
			if (null != url) {
				Queue.put(url);
				urlCount.getAndIncrement();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			throw new QueueException("待处理链接队列加入操作中断");
		}
	}
	/**
	 * 加入一个待处理的URL，Url总数+1。超时将导致Url被丢弃。
	 * @param url
	 * @param timeout (MILLISECONDS)
	 * @return
	 * @throws QueueException
	 */
	@Deprecated
	public boolean addUrl(WebURL url,int timeout) throws QueueException {
		if (url != null) {
			try {
				boolean b = Queue.offer(url, timeout, TimeUnit.MILLISECONDS);
				if(b){
					urlCount.getAndIncrement();
				}
				return b;
			} catch (InterruptedException e) {
				throw new QueueException("待处理页面加入操作中断");
			}
		}
		return false;
	}
	
	/**
	 * @return
	 * @desc 返回一个将要处理的URL
	 */
	public WebURL getUrl() throws QueueException {
		try {
			return Queue.take();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			throw new QueueException("待处理链接队列取出操作中断");
		}
	}

	public boolean isEmpty() {
		return Queue.isEmpty();
	}

	/**
	 * @param c
	 * @return
	 * @desc 抓取成功连接数+c
	 */
	public long processedSuccess(long c) {
		return success.addAndGet(c);
	}

	/**
	 * @return
	 * @desc 抓取成功连接数+1
	 */
	public long processedSuccess() {
		return success.incrementAndGet();
	}

	/**
	 * @param c
	 * @return
	 * @desc 抓取失败连接数+c
	 */
	public int processedFailure(int c) {
		return failure.addAndGet(c);
	}

	/**
	 * @return
	 * @desc 抓取失败链接数+1
	 */
	public int processedFailure() {
		return failure.incrementAndGet();
	}

	/**
	 * @return
	 * @desc 返回总链接数
	 */
	public long urlCount() {
		return urlCount.get();
	}

	/**
	 * @return
	 * @desc 返回成功抓取链接数
	 */
	public long success() {
		return success.get();
	}

	/**
	 * @return
	 * @desc 返回抓取失败链接数
	 */
	public int failure() {
		return failure.get();
	}

	/**
	 * @param c
	 * @return
	 * @desc 被忽略链接数+c
	 */
	public long processedIgnored(long c) {
		return ignored.addAndGet(c);
	}

	/**
	 * @return
	 * @desc 被忽略链接数+1
	 */
	public long processedIgnored() {
		return ignored.incrementAndGet();
	}

	/**
	 * @return
	 * @desc 被忽略链接个数
	 */
	public long ignored() {
		return ignored.get();
	}

	public String pendingStatus() {
		StringBuilder sb = new StringBuilder(32);
		sb.append("队列中等待处理的URL有").append(Queue.size()).append("个，")
				.append("截至目前共爬到").append(urlCount).append("个链接。已成功处理")
				.append(success.get()).append("个，失败").append(failure.get())
				.append("个，忽略").append(ignored()).append("个");
		return sb.toString();
	}
}
