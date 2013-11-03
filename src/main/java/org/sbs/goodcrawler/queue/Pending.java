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
package org.sbs.goodcrawler.queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author whiteme
 * @date 2013年11月3日
 * @desc 等待队列
 */
public abstract class Pending {
	/**
	 * 接收到一个 +1
	 */
	protected AtomicLong count = new AtomicLong(0L);
	/**
	 * 已经成功处理的个数
	 */
	protected AtomicLong success = new AtomicLong(0L);
	/**
	 * 处理失败的个数
	 */
	protected AtomicInteger failure = new AtomicInteger(0);
	/**
	 * 被忽略的个数
	 */
	protected AtomicLong ignored = new AtomicLong(0);
	
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
	public long Count() {
		return count.get();
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
}
