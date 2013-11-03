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
package org.sbs.goodcrawler.jobconf;


/**
 * @author whiteme
 * @date 2013年11月3日
 * @desc job任务配置对象
 */
public class Job {
	private String jobName ;
	private int jobtime;
	private int urlNum;
	private boolean monitorJob = true;
	private FetchConfig fetchConfig;
	private ExtractConfig extractConfig;
	private StoreConfig storeConfig;
	private String cron;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public int getJobtime() {
		return jobtime;
	}
	public void setJobtime(int jobtime) {
		this.jobtime = jobtime;
	}
	public int getUrlNum() {
		return urlNum;
	}
	public void setUrlNum(int urlNum) {
		this.urlNum = urlNum;
	}
	public FetchConfig getFetchConfig() {
		return fetchConfig;
	}
	public void setFetchConfig(FetchConfig fetchConfig) {
		this.fetchConfig = fetchConfig;
	}
	public ExtractConfig getExtractConfig() {
		return extractConfig;
	}
	public void setExtractConfig(ExtractConfig extractConfig) {
		this.extractConfig = extractConfig;
	}
	public StoreConfig getStoreConfig() {
		return storeConfig;
	}
	public void setStoreConfig(StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
	public boolean isMonitorJob() {
		return monitorJob;
	}
	public void setMonitorJob(boolean monitorJob) {
		this.monitorJob = monitorJob;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
}
