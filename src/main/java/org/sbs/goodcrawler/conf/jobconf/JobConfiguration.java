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
package org.sbs.goodcrawler.conf.jobconf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.Configuration;
import org.sbs.goodcrawler.storage.StorageType;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29 任务配置文件解析类
 */
public class JobConfiguration extends Configuration {

	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * job名称
	 */
	private String jobName;
	/**
	 * 存储类型
	 */
	private StorageType storageType;
	/**
	 * job需要启动的线程数
	 */
	private int threadNum;
	/**
	 * Socket超时，单位毫秒
	 */
	private int socketTimeoutMilliseconds = 10000;
	/**
	 * connection超时，单位毫秒
	 */
	private int connectionTimeout = 20000;
	/**
	 * 两次请求之间的等待（延迟）时间
	 */
	private int delayBetweenRequests = 100;
	/**
	 * 爬取链接的深度，-1表示无限制
	 */
	private int maxDepthOfCrawling = -1;
	/**
	 * 单个页面处理的最大外链数
	 */
	private int maxOutgoingLinksToFollow = 5000;
	/**
	 * 是否下载二进制文件
	 */
	private boolean fetchBinaryContent = false;
	/**
	 * 可下载文件的后缀
	 */
	private String fileSuffix = "jpg,gif,png,avi,mtk";
	/**
	 * agent
	 */
	private String agent;
	/**
	 * 是否爬https链接
	 */
	private boolean https = true;
	/**
	 * 是否仅抓取当前域名下内容
	 */
	private boolean onlyDomain = true;
	/**
	 * 最大连接数
	 */
	private int maxTotalConnections = 100;
	/**
	 * 每个远程主机的最大连接数
	 */
	private int maxConnectionsPerHost = 100;
	/**
	 * 页面大小限制，大于该值则不抓取
	 */
	private int maxDownloadSizePerPage = 1048576;
	/**
	 * 代理主机
	 */
	private String proxyHost = null;
	/**
	 * 代理端口
	 */
	private int proxyPort = 80;

	/**
	 * 代理用户名
	 */
	private String proxyUsername = null;

	/**
	 * 代理密码
	 */
	private String proxyPassword = null;
	/**
	 * 种子地址
	 */
	private List<String> seeds = new ArrayList<>();
	/**
	 * Url过滤正则表达式
	 */
	private List<String> urlFilterReg = new ArrayList<>();
	/**
	 * 要提取的页面信息
	 */
	private Map<String, String> selects = new HashMap<>();

	public JobConfiguration() {
	};

	public Log getLog() {
		return log;
	}

	public JobConfiguration setLog(Log log) {
		this.log = log;
		return this;
	}

	public String getJobName() {
		return jobName;
	}

	public JobConfiguration setJobName(String jobName) {
		this.jobName = jobName;
		return this;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public JobConfiguration setStorageType(StorageType storageType) {
		this.storageType = storageType;
		return this;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public JobConfiguration setThreadNum(int threadNum) {
		this.threadNum = threadNum;
		return this;
	}

	public int getSocketTimeoutMilliseconds() {
		return socketTimeoutMilliseconds;
	}

	public JobConfiguration setSocketTimeoutMilliseconds(
			int socketTimeoutMilliseconds) {
		this.socketTimeoutMilliseconds = socketTimeoutMilliseconds;
		return this;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public JobConfiguration setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	public String getAgent() {
		return agent;
	}

	public JobConfiguration setAgent(String agent) {
		this.agent = agent;
		return this;
	}

	public boolean isHttps() {
		return https;
	}

	public JobConfiguration setHttps(boolean https) {
		this.https = https;
		return this;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public JobConfiguration setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
		return this;
	}

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	public JobConfiguration setMaxConnectionsPerHost(int maxConnectionsPerHost) {
		this.maxConnectionsPerHost = maxConnectionsPerHost;
		return this;
	}

	public int getMaxDownloadSizePerPage() {
		return maxDownloadSizePerPage;
	}

	public JobConfiguration setMaxDownloadSizePerPage(int maxDownloadSizePerPage) {
		this.maxDownloadSizePerPage = maxDownloadSizePerPage;
		return this;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public JobConfiguration setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
		return this;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public JobConfiguration setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public JobConfiguration setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
		return this;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public JobConfiguration setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
		return this;
	}

	public List<String> getSeeds() {
		return seeds;
	}

	public JobConfiguration setSeeds(List<String> seeds) {
		this.seeds = seeds;
		return this;
	}

	public List<String> getUrlFilterReg() {
		return urlFilterReg;
	}

	public JobConfiguration setUrlFilterReg(List<String> urlFilterReg) {
		this.urlFilterReg = urlFilterReg;
		return this;
	}

	public Map<String, String> getSelects() {
		return selects;
	}

	public JobConfiguration setSelects(Map<String, String> selects) {
		this.selects = selects;
		return this;
	}

	public int getDelayBetweenRequests() {
		return delayBetweenRequests;
	}

	public JobConfiguration setDelayBetweenRequests(int delayBetweenRequests) {
		this.delayBetweenRequests = delayBetweenRequests;
		return this;
	}

	public int getMaxDepthOfCrawling() {
		return maxDepthOfCrawling;
	}

	public JobConfiguration setMaxDepthOfCrawling(int maxDepthOfCrawling) {
		this.maxDepthOfCrawling = maxDepthOfCrawling;
		return this;
	}

	public boolean isFetchBinaryContent() {
		return fetchBinaryContent;
	}

	public JobConfiguration setFetchBinaryContent(boolean fetchBinaryContent) {
		this.fetchBinaryContent = fetchBinaryContent;
		return this;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public JobConfiguration setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
		return this;
	}
	
	public int getMaxOutgoingLinksToFollow() {
		return maxOutgoingLinksToFollow;
	}

	public JobConfiguration setMaxOutgoingLinksToFollow(int maxOutgoingLinksToFollow) {
		this.maxOutgoingLinksToFollow = maxOutgoingLinksToFollow;
		return this;
	}
	
	public boolean isOnlyDomain() {
		return onlyDomain;
	}

	public JobConfiguration setOnlyDomain(boolean onlyDomain) {
		this.onlyDomain = onlyDomain;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobConfiguration [log=");
		builder.append(log);
		builder.append("\n jobName=");
		builder.append(jobName);
		builder.append("\n storageType=");
		builder.append(storageType);
		builder.append("\n threadNum=");
		builder.append(threadNum);
		builder.append("\n socketTimeoutMilliseconds=");
		builder.append(socketTimeoutMilliseconds);
		builder.append("\n connectionTimeout=");
		builder.append(connectionTimeout);
		builder.append("\n delayBetweenRequests=");
		builder.append(delayBetweenRequests);
		builder.append("\n maxDepthOfCrawling=");
		builder.append(maxDepthOfCrawling);
		builder.append("\n fetchBinaryContent=");
		builder.append(fetchBinaryContent);
		builder.append("\n fileSuffix=");
		builder.append(fileSuffix);
		builder.append("\n agent=");
		builder.append(agent);
		builder.append("\n https=");
		builder.append(https);
		builder.append("\n maxTotalConnections=");
		builder.append(maxTotalConnections);
		builder.append("\n maxConnectionsPerHost=");
		builder.append(maxConnectionsPerHost);
		builder.append("\n maxDownloadSizePerPage=");
		builder.append(maxDownloadSizePerPage);
		builder.append("\n proxyHost=");
		builder.append(proxyHost);
		builder.append("\n proxyPort=");
		builder.append(proxyPort);
		builder.append("\n proxyUsername=");
		builder.append(proxyUsername);
		builder.append("\n proxyPassword=");
		builder.append(proxyPassword);
		builder.append("\n seeds=");
		builder.append(seeds);
		builder.append("\n urlFilterReg=");
		builder.append(urlFilterReg);
		builder.append("\n selects=");
		builder.append(selects);
		builder.append("]");
		return builder.toString();
	}
	
}
