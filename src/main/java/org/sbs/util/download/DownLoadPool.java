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
package org.sbs.util.download;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author whiteme
 * @date 2013年10月20日
 * @desc 用于提交下载任务的线程池
 */
public class DownLoadPool {
	/**
	 * 线程池
	 */
	public ExecutorService pool;
	
	private static DownLoadPool instance;
	
	private DownLoadPool(){
		pool = Executors.newFixedThreadPool(10);
	}
	
	public static DownLoadPool getInstance(){
		if(instance == null){
			instance = new DownLoadPool();
		}
		return instance;
	}
	/**
	 * 提交线程
	 * @return 
	 */
	public Future<DownLoadBean> submit(Callable<DownLoadBean> call){
		return pool.submit(call);
	}
}
