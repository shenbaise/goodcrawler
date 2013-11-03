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
package org.sbs.goodcrawler.bootstrap;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

/**
 * @author whiteme
 * @date 2013年11月3日
 * @desc 任务启动停止的控制标志，也是任务运行状态的标志。
 */
public class JobStops{
	/**
	 * 控制开关<job名,开启状态>
	 */
	private static ConcurrentMap<String, Boolean> stops = Maps.newConcurrentMap();
	
	/**
	 * 停止任务
	 * @param jobName
	 */
	public static synchronized void stopJob(String jobName){
		stops.put(jobName, true);
	}
	/**
	 * 标志任务已经启动，正在运行
	 * @param jobName
	 */
	public static synchronized void startJob(String jobName){
		stops.put(jobName, false);
	}
	/**
	 * job是否被停止
	 * @param jobName
	 * @return
	 */
	public static synchronized Boolean isStop(String jobName){
		return stops.get(jobName);
	}
}
