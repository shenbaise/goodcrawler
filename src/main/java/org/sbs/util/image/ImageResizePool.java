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
package org.sbs.util.image;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author whiteme
 * @date 2013年10月22日
 * @desc 一个简单的线程池，用于提交图片压缩任务的线程
 */
public class ImageResizePool {
	/**
	 * 线程池
	 */
	public ExecutorService pool;
	
	private static ImageResizePool instance;
	
	private ImageResizePool(){
		pool = Executors.newFixedThreadPool(10);
	}
	
	public static ImageResizePool getInstance(){
		if(instance == null){
			instance = new ImageResizePool();
		}
		return instance;
	}
	/**
	 * 提交线程
	 * @return 
	 */
	public Future<?> submit(Callable<?> call){
		return pool.submit(call);
	}
}
