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
package org.sbs.goodcrawler.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * job
 */
public class JobBean {
	
	private String jobName;
	private String storageType;
	private int threadNum;
	private int timeout;
	
	private List<String> seeds = new ArrayList<>();
	private List<String> urlFilterReg = new ArrayList<>();
	private Map<String, String> selects = new HashMap<>();
	
	public JobBean() {
	}

	public String getJobName() {
		return jobName;
	}

	public JobBean setJobName(String jobName) {
		this.jobName = jobName;
		return this;
	}

	public String getStorageType() {
		return storageType;
	}

	public JobBean setStorageType(String storageType) {
		this.storageType = storageType;
		return this;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public JobBean setThreadNum(int threadNum) {
		this.threadNum = threadNum;
		return this;
	}

	public List<String> getSeeds() {
		return seeds;
	}

	public JobBean setSeeds(List<String> seeds) {
		this.seeds = seeds;
		return this;
	}

	public List<String> getUrlFilterReg() {
		return urlFilterReg;
	}

	public JobBean setUrlFilterReg(List<String> urlFilterReg) {
		this.urlFilterReg = urlFilterReg;
		return this;
	}

	public int getTimeout() {
		return timeout;
	}

	public JobBean setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public Map<String, String> getSelects() {
		return selects;
	}

	public JobBean setSelects(Map<String, String> selects) {
		this.selects = selects;
		return this;
	}
	
}
