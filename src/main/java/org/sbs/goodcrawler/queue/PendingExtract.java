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
package org.sbs.goodcrawler.queue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.job.Page;

import com.google.common.collect.Maps;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29 等待处理的页面
 */
public class PendingExtract extends Pending implements Serializable {

	private static final long serialVersionUID = -5671808882701246813L;
	private String jobName = null;
	private int capacity = 2000;
	/**
	 * 待处理页面（页面已下载）队列
	 */
	private BlockingQueue<Page> Queue = null;
	
	private static ConcurrentMap<String ,PendingExtract> map = Maps.newConcurrentMap();
	
	/**
	 * 构造方法
	 * @param jobName
	 * @param capacity
	 */
	private PendingExtract(String jobName,int capacity) {
		this.jobName = jobName;
		this.capacity = capacity;
		init();
	}
	
	/**
	 * 默认构造，队列大小2000
	 * @param jobName
	 */
	private PendingExtract(String jobName){
		this.jobName = jobName;
		init();
	}
	/**
	 * 获取一个实例
	 * @param jobName
	 * @param capacity
	 * @return
	 */
	public static PendingExtract getPendingExtract(String jobName,int capacity) {
		PendingExtract pendingExtract = map.get(jobName);
		if(null!=pendingExtract)
			return pendingExtract;
		map.put(jobName, new PendingExtract(jobName, capacity));
		return map.get(jobName);
	}
	/**
	 * 返回一个实例，不存在则返回默认大小（2000）构造
	 * @param jobName
	 * @return
	 */
	public static PendingExtract getPendingExtract(String jobName) {
		PendingExtract pendingExtract = map.get(jobName);
		if(null!=pendingExtract)
			return pendingExtract;
		map.put(jobName, new PendingExtract(jobName, 10000));
		return map.get(jobName);
	}
	
	private PendingExtract init() {
		File file = new File(PropertyConfigurationHelper.getInstance()
				.getString("status.save.path", "status")
				+ File.separator
				+ this.jobName
				+ "/pages.good");
		if (file.exists()) {
			try {
				FileInputStream fisUrl = new FileInputStream(file);
				ObjectInputStream oisUrl = new ObjectInputStream(fisUrl);
				PendingExtract instance = (PendingExtract) oisUrl.readObject();
				oisUrl.close();
				fisUrl.close();
				Queue = instance.Queue;
				failure = instance.failure;
				success = instance.success;
				count = instance.count;
				ignored = instance.ignored;
				jobName = instance.jobName;
				capacity = instance.capacity;
				System.out.println("recovery page queue..." + Queue.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} 
		if(null==Queue){
			Queue = new LinkedBlockingDeque<Page>(this.capacity);
		}
		return this;
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
				count.incrementAndGet();
			} catch (InterruptedException e) {
				ignored.incrementAndGet();
				throw new QueueException("待处理页面加入操作中断");
			}
		}
	}
	/**
	 * 向队列中添加一个页面。超时后被丢弃 <b>不推荐使用
	 * @param page
	 * @param timeout
	 * @throws QueueException
	 */
	@Deprecated
	public boolean addPage(Page page,int timeout) throws QueueException {
		if (page != null) {
			try {
				boolean b = Queue.offer(page, timeout, TimeUnit.MILLISECONDS);
				if(b){
					count.incrementAndGet();
				}
				return b;
			} catch (InterruptedException e) {
				throw new QueueException("待处理页面加入操作中断");
			}
		}
		return false;
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
				FailedPageBackup.getInstace(page.getWebURL().getJobName()).addPage(page);
				failure.incrementAndGet();
			}
		} catch (Exception | QueueException e) {
			// ..
		}
	}

	public String pendingStatus() {
		StringBuilder sb = new StringBuilder(64);
		sb.append("队列中等待处理的Page有").append(Queue.size()).append("个，")
		.append("截至目前共收到").append(count).append("个页面。已成功处理")
		.append(success.get()).append("个，失败").append(failure.get())
		.append("个，忽略").append(ignored.get()).append("个。")
		.append("队列容量[").append(Queue.size()).append("/").append(this.capacity).append("]");
		return sb.toString();
	}
}
