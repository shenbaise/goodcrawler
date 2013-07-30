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
package org.sbs.goodcrawler.fetcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.sbs.goodcrawler.conf.GlobalConstants;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.job.Page;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29 等待处理的页面
 */
public class PendingPages implements Serializable {

	private static final long serialVersionUID = -5671808882701246813L;
	private static PendingPages instance = null;

	/**
	 * 待处理页面（页面已下载）队列
	 */
	private BlockingQueue<Page> Queue = null;

	/**
	 * 配置文件助手
	 */
	private PropertyConfigurationHelper config = PropertyConfigurationHelper
			.getInstance();

	/**
	 * 总共获得到的页面数，每爬到一个+1
	 */
	private AtomicLong count = new AtomicLong(0L);

	/**
	 * 解析（抽取）失败的页面数
	 */
	private AtomicInteger failure = new AtomicInteger(0);

	private PendingPages() {
		init();
	}

	public static PendingPages getInstace() {
		if (null == instance) {
			instance = new PendingPages();
		}
		return instance;
	}

	private void init() {
		File file = new File(PropertyConfigurationHelper.getInstance()
				.getString("status.save.path", "status")
				+ File.separator
				+ "pages.good");
		if (file.exists()) {
			try {
				FileInputStream fisUrl = new FileInputStream(file);
				ObjectInputStream oisUrl = new ObjectInputStream(fisUrl);
				instance = (PendingPages) oisUrl.readObject();
				oisUrl.close();
				fisUrl.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} 
		if(null==Queue)
			Queue = new LinkedBlockingDeque<Page>(config.getInt(
					GlobalConstants.pendingPagesQueueSize, 2000));
	}

	/**
	 * 向队列中添加一个页面
	 * 
	 * @param page
	 * @desc
	 */
	public void addPage(Page page) throws QueueException {
		if (page != null) {
			try {
				Queue.put(page);
			} catch (InterruptedException e) {
				throw new QueueException("待处理页面加入操作中断");
			}
			count.incrementAndGet();
		}
	}

	/**
	 * 从队列中取走一个页面
	 * 
	 * @return
	 * @desc
	 */
	public Page getPage() throws QueueException {
		try {
			return Queue.take();
		} catch (InterruptedException e) {
			throw new QueueException("待处理页面队列取出操作中断");
		}
	}

	public boolean isEmpty() {
		return Queue.isEmpty();
	}

	/**
	 * @param page
	 * @desc 添加一个处理失败的页面
	 */
	public void addFailedPage(Page page) {
		try {
			if (null != page) {
				FailedPageBackup.getInstace().addPage(page);
				failure.incrementAndGet();
			}
		} catch (Exception | QueueException e) {
			// ..
		}
	}

	public String pendingStatus() {
		StringBuilder sb = new StringBuilder(32);
		sb.append("队列中等待处理的Page有").append(Queue.size()).append("个，失败")
				.append(failure.get()).append("个").append(Queue.isEmpty());
		return sb.toString();
	}

	/**
	 * @param args
	 * @desc
	 */
	public static void main(String[] args) {
		PendingPages pendingPages = PendingPages.getInstace();
		pendingPages.addFailedPage(null);
	}
}
