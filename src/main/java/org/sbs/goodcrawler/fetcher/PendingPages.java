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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.GlobalConstants;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.job.Page;
import org.sbs.util.DateTimeUtil;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * 等待处理的页面
 */
public class PendingPages {
	
	private Log log = LogFactory.getLog(this.getClass());
	private static PendingPages instance = null;
	/**
	 * 待处理页面（页面已下载）队列
	 */
	private BlockingQueue<Page> Queue = null;
	/**
	 * 处理失败的页面队列
	 */
	private BlockingQueue<Page> failedQueue = null;
	/**
	 * 配置文件助手
	 */
	private PropertyConfigurationHelper config = PropertyConfigurationHelper.getInstance();
	/**
	 * 是否忽略处理失败的页面（如果页面解析出错则放掉该也，不加入失败页面队列）
	 */
	private boolean ignoreFailedPage = true;
	
	/**
	 * 总共获得到的页面数，每爬到一个+1
	 */
	private AtomicLong count = new AtomicLong(0L);
	
	/**
	 * 解析（抽取）失败的页面数
	 */
	private AtomicInteger failure = new AtomicInteger(0);
	
	private PendingPages(){
		init();
	}
	
	public static PendingPages getInstace(){
		if(null==instance){
			instance = new PendingPages();
		}
		return instance;
	}
	
	private void init(){
		ignoreFailedPage = Boolean.getBoolean(config.getString(GlobalConstants.ignoreFailedPages, "true"));
		Queue = new LinkedBlockingDeque<Page>(config.getInt(GlobalConstants.pendingPagesQueueSize, 2000));
		if(!ignoreFailedPage){
			failedQueue = new LinkedBlockingDeque<Page>(config.getInt(GlobalConstants.failedPagesQueueSize, 2000));
			// 执行备份
			BackupFailedPages backup = new BackupFailedPages();
			Thread failedPagesBackupThread = new Thread(backup, "failed-pages-backup-thread");
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(failedPagesBackupThread, 60, 60, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * 向队列中添加一个页面
	 * @param page
	 * @desc
	 */
	public void addPage(Page page) throws QueueException{
		if(page!=null){
			try {
				Queue.put(page);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// log.erro(e.getMessage());
				throw new QueueException("待处理页面加入操作中断");
			}
			count.incrementAndGet();
		}
	}
	/**
	 * 从队列中取走一个页面
	 * @return
	 * @desc
	 */
	public Page getPage() throws QueueException{
		try {
			return Queue.take();
		} catch (InterruptedException e) {
			throw new QueueException("待处理页面队列取出操作中断");
		}
	}
	public boolean isEmpty(){
		return Queue.isEmpty();
	}
	/**
	 * @param page
	 * @desc 添加一个处理失败的页面
	 */
	public void addFailedPage(Page page){
		try {
			if(null!=page){
				failedQueue.put(page);
				failure.incrementAndGet();
			}
		} catch (Exception e) {
			// ..
		}
	}
	/**
	 * @author shenbaise(shenbaise@outlook.com)
	 * @date 2013-6-30
	 * 备份失败页面
	 */
	private class BackupFailedPages implements Runnable{
		@Override
		public void run() {
			Page page ;
			boolean flag = true;
			File backFile = null;
			FileChannel fc = null;
			byte[] b = new byte[]{(byte)1,(byte)1};
			if(!ignoreFailedPage){
				backFile = new File(config.getString(GlobalConstants.failedPagesBackupPath, "") + File.pathSeparator + DateTimeUtil.getDate());
				try {
					if(flag){
						while(null!=(page=failedQueue.poll())){
							fc = new FileOutputStream(backFile,true).getChannel();
							fc.write(ByteBuffer.wrap(page.getContentData()));
							fc.write(ByteBuffer.wrap(b));
						}
						fc.close();
					}
				}  catch (IOException e) {
					e.printStackTrace();
					log.warn(e.getMessage());
				}
			}
		}
	}
	
	
	public String pendingStatus(){
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
