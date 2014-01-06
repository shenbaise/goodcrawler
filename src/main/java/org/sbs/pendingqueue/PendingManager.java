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
package org.sbs.pendingqueue;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc
 */
public class PendingManager {
	
	private static ConcurrentHashMap<String, AbsPendingQueue<?>> CM = new ConcurrentHashMap<>();
	
	/**
	 * 获取某任务的url等待队列
	 * @param jobName
	 * @return
	 */
	public static PendingUrls getPendingUlr(String jobName){
		Object p = CM.get(jobName);
		if(p!=null){
			return (PendingUrls)p;
		}else {
			PendingUrls pu = new PendingUrls(jobName);
			CM.put(jobName, pu);
			return (PendingUrls) CM.get(jobName);
		}
	}
	
	/**
	 * 返回某job对应的Page队列
	 * @param jobName
	 * @return
	 */
	public static PendingPages getPendingPages(String jobName){
		Object p = CM.get(jobName);
		if(p!=null){
			return (PendingPages)p;
		}else {
			PendingPages pu = new PendingPages(jobName);
			CM.put(jobName, pu);
			return (PendingPages) CM.get(jobName);
		}
	}
	
	/**
	 * 返回某job对应的Store队列
	 * @param jobName
	 * @return
	 */
	public static PendingStore getPendingStore(String jobName){
		Object p = CM.get(jobName);
		if(p!=null){
			return (PendingStore)p;
		}else {
			PendingStore pu = new PendingStore(jobName);
			CM.put(jobName, pu);
			return (PendingStore) CM.get(jobName);
		}
	}
	
}
