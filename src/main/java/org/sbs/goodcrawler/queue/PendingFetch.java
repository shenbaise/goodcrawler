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
package org.sbs.goodcrawler.queue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.urlmanager.WebURL;

import com.google.common.collect.Maps;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * @desc 待处理的Urls队列，允许有多个实例。多个实例由PendingUrlsManager管理
 */
public class PendingFetch extends Pending implements Serializable {

	private static final long serialVersionUID = 2260258894389085989L;
	private Log log = LogFactory.getLog(this.getClass());
	private BlockingQueue<WebURL> Queue = null;
	private String jobName = null;
	private int capacity = 1000000;
	
	
	private static ConcurrentMap<String, PendingFetch> map = Maps.newConcurrentMap();
	
	/**
	 * 构造函数
	 * @param jobName
	 * @param capacity
	 */
	private PendingFetch(String jobName,int capacity){
		this.jobName = jobName;
		this.capacity = capacity;
		init();
	}
	/**
	 * 构造函数，默认队列大小1000000
	 * @param jobName
	 */
	private PendingFetch(String jobName){
		this.jobName = jobName;
		init();
	}
	
	/**
	 * @return
	 * @desc 返回队列实例
	 */
	public static PendingFetch getPendingFetch(String jobName,int queueSize){
		PendingFetch pendingFetch = map.get(jobName);
		if(null!=pendingFetch)
			return pendingFetch;
		map.put(jobName, new PendingFetch(jobName, queueSize));
		return map.get(jobName);
	}
	/**
	 * 返回一个实例，不存在则使用默认大小（1000000）进行构造
	 * @param jobName
	 * @return
	 */
	public static PendingFetch getPendingFetch(String jobName){
		PendingFetch pendingFetch = map.get(jobName);
		if(null!=pendingFetch)
			return pendingFetch;
		map.put(jobName, new PendingFetch(jobName));
		return map.get(jobName);
	}
	
	/**
	 * @desc 初始化队列
	 */
	private PendingFetch init() {
		File file = new File(PropertyConfigurationHelper.getInstance()
				.getString("status.save.path", "status")
				+ File.separator
				+ this.jobName + "/urls.good");
		if (file.exists()) {
			try {
				FileInputStream fisUrl = new FileInputStream(file);
				ObjectInputStream oisUrl = new ObjectInputStream(fisUrl);
				PendingFetch instance = (PendingFetch) oisUrl.readObject();
				oisUrl.close();
				fisUrl.close();
				Queue = instance.Queue;
				failure = instance.failure;
				success = instance.success;
				count = instance.count;
				ignored = instance.ignored;
				this.jobName = instance.jobName;
				this.capacity = instance.capacity;
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
			Queue = new ArrayBlockingQueue<>(this.capacity);
		return 
			this;
	}

	/**
	 * @param url
	 * @desc 加入一个待处理的URL，Url总数+1
	 */
	public void addUrl(WebURL url) throws QueueException {
		try {
			if (null != url) {
				Queue.put(url);
				count.getAndIncrement();
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
	public boolean addUrl(WebURL url,int timeout) throws QueueException {
		if (url != null) {
			try {
				boolean b = Queue.offer(url, timeout, TimeUnit.MILLISECONDS);
				if(b){
					count.getAndIncrement();
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

	public String pendingStatus() {
		StringBuilder sb = new StringBuilder(32);
		sb.append("队列中等待处理的URL有").append(Queue.size()).append("个，")
				.append("截至目前共爬到").append(count.get()).append("个链接。已成功处理")
				.append(success.get()).append("个，失败").append(failure.get())
				.append("个，忽略").append(ignored()).append("个").append("。队列容量[")
				.append(Queue.size()).append("/").append(this.capacity).append("]");
		return sb.toString();
	}
}
