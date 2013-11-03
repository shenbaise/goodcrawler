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
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.extractor.ExtractResult;
import org.sbs.goodcrawler.urlmanager.WebURL;

import com.google.common.collect.Maps;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 */
public class PendingStore extends Pending implements Serializable {

	private static final long serialVersionUID = -1872886200443134215L;
	private Log log = LogFactory.getLog(this.getClass());
	@SuppressWarnings("rawtypes")
	private BlockingQueue<ExtractedPage> Queue = null;
	private String jobName = null;
	private int capacity = 100;
	
	private static ConcurrentMap<String, PendingStore> map = Maps.newConcurrentMap();
	
	/**
	 * 构造函数
	 * @param jobName
	 * @param capacity
	 */
	private PendingStore(String jobName,int capacity){
		this.jobName = jobName;
		this.capacity = capacity;
		init();
	}
	/**
	 * 默认大小（100）的构造方法
	 * @param jobName
	 */
	private PendingStore(String jobName){
		this.jobName = jobName;
		init();
	}
	/**
	 * 返回一个Pending队列。不存在则使用capacity进行构造。如存在则直接返回。
	 * @param jobName
	 * @param capacity
	 */
	public static PendingStore getPendingStore(String jobName,int capacity) {
		PendingStore pendingStore = map.get(jobName);
		if(null!=pendingStore)
			return pendingStore;
		map.put(jobName, new PendingStore(jobName,capacity));
		return map.get(jobName);
	}
	/**
	 * 返回实例，不存在则生成默认大小（100）构造
	 * @param jobName
	 * @return
	 */
	public static PendingStore getPendingStore(String jobName) {
		PendingStore pendingStore = map.get(jobName);
		if(null!=pendingStore)
			return pendingStore;
		map.put(jobName, new PendingStore(jobName));
		return map.get(jobName);
	}

	/**
	 * @desc 初始化队列
	 */
	private PendingStore init() {
		File file = new File(PropertyConfigurationHelper.getInstance()
				.getString("status.save.path", "status")
				+ File.separator
				+ this.jobName
				+ "/stores.good");
		if (file.exists()) {
			try {
				FileInputStream fisUrl = new FileInputStream(file);
				ObjectInputStream oisUrl = new ObjectInputStream(fisUrl);
				PendingStore instance = (PendingStore) oisUrl.readObject();
				oisUrl.close();
				fisUrl.close();
				Queue = instance.Queue;
				failure = instance.failure;
				success = instance.success;
				count = instance.count;
				ignored = instance.ignored;
				jobName = instance.jobName;
				capacity = instance.capacity;
				System.out.println("recovery store queue..." + Queue.size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} 
		if(null==Queue)
			Queue = new ArrayBlockingQueue<>(capacity);
		return this;
	}

	public void addExtracedPage(ExtractedPage<?, ?> store)
			throws QueueException {
		try {
			if (null != store && null!=store.getMessages() && store.getMessages().size()>0) {
				Queue.put(store);
				count.incrementAndGet();
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			throw new QueueException("待处理存储信息队列加入操作中断");
		}
	}
	
	/**
	 * @return
	 * @desc 返回一个将要处理的URL
	 */
	@SuppressWarnings("rawtypes")
	public ExtractedPage getExtractedPage() throws QueueException {
		try {
			return Queue.take();
		} catch (InterruptedException e) {
			log.info("take page error");
			log.error(e.getMessage());
			throw new QueueException("待处理存储信息队列取出操作中断");
		}
	}

	public boolean isEmpty() {
		return Queue.isEmpty();
	}

	public String pendingStatus() {
		StringBuilder sb = new StringBuilder(32);
		sb.append("队列中等待处理的Store有").append(Queue.size()).append("个，")
		.append("截至目前共收到").append(count).append("个页面。已成功处理")
		.append(success.get()).append("个，失败").append(failure.get())
		.append("个，忽略").append(ignored.get()).append("个")
		.append("队列容量[").append(Queue.size()).append("/").append(this.capacity).append("]");
		return sb.toString();
	}

	/**
	 * @author shenbaise(shenbaise@outlook.com)
	 * @date 2013-7-2 从Page中提取到信息
	 */
	public class ExtractedPage<V, T> implements Serializable {
		private static final long serialVersionUID = 3965488944964575676L;
		/**
		 * 保留字段
		 */
		String reserve;
		/**
		 * 附加的WebURL信息
		 */
		WebURL url;
		/**
		 * 提取到信息，与job配置中的select对应，内容可以在扩展接口中重新修改和定义。
		 */
		HashMap<V, T> messages = new HashMap<>();

		ExtractResult result;

		public ExtractResult getResult() {
			return result;
		}

		public ExtractedPage<V, T> setResult(ExtractResult result) {
			this.result = result;
			return this;
		}

		public ExtractedPage() {
		}

		public ExtractedPage(WebURL url) {
			super();
			this.url = url;
		}

		public WebURL getUrl() {
			return url;
		}

		public ExtractedPage<V, T> setUrl(WebURL url) {
			this.url = url;
			return this;
		}

		public String getReserve() {
			return reserve;
		}

		public ExtractedPage<V, T> setReserve(String reserve) {
			this.reserve = reserve;
			return this;
		}

		public HashMap<V, T> getMessages() {
			return messages;
		}

		public ExtractedPage<V, T> setMessages(HashMap<V, T> messages) {
			this.messages = messages;
			return this;
		}

		public ExtractedPage<V, T> addMessage(V key, T value) {
			messages.put(key, value);
			return this;
		}
	}
}
