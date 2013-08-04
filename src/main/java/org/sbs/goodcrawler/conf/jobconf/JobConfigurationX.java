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
public class JobConfigurationX extends Configuration {

	public JobConfigurationX() {
	}

	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * job名称
	 */
	private String name;
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
	private int delayBetweenRequests = 200;
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
	 * 是否遵循robots协议
	 */
	private boolean robots = true;
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

	

	public Log getLog() {
		return log;
	}

	public JobConfigurationX setLog(Log log) {
		this.log = log;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getSocketTimeoutMilliseconds() {
		return socketTimeoutMilliseconds;
	}

	public void setSocketTimeoutMilliseconds(
			int socketTimeoutMilliseconds) {
		this.socketTimeoutMilliseconds = socketTimeoutMilliseconds;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public boolean isHttps() {
		return https;
	}

	public void setHttps(boolean https) {
		this.https = https;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	public int getMaxDownloadSizePerPage() {
		return maxDownloadSizePerPage;
	}

	public void setMaxDownloadSizePerPage(int maxDownloadSizePerPage) {
		this.maxDownloadSizePerPage = maxDownloadSizePerPage;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public List<String> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<String> seeds) {
		this.seeds = seeds;
	}

	public List<String> getUrlFilterReg() {
		return urlFilterReg;
	}

	public void setUrlFilterReg(List<String> urlFilterReg) {
		this.urlFilterReg = urlFilterReg;
	}

	public Map<String, String> getSelects() {
		return selects;
	}

	public void setSelects(Map<String, String> selects) {
		this.selects = selects;
	}

	public int getDelayBetweenRequests() {
		return delayBetweenRequests;
	}

	public void setDelayBetweenRequests(int delayBetweenRequests) {
		this.delayBetweenRequests = delayBetweenRequests;
	}

	public int getMaxDepthOfCrawling() {
		return maxDepthOfCrawling;
	}

	public void setMaxDepthOfCrawling(int maxDepthOfCrawling) {
		this.maxDepthOfCrawling = maxDepthOfCrawling;
	}

	public boolean isFetchBinaryContent() {
		return fetchBinaryContent;
	}

	public void setFetchBinaryContent(boolean fetchBinaryContent) {
		this.fetchBinaryContent = fetchBinaryContent;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
	
	public int getMaxOutgoingLinksToFollow() {
		return maxOutgoingLinksToFollow;
	}

	public void setMaxOutgoingLinksToFollow(int maxOutgoingLinksToFollow) {
		this.maxOutgoingLinksToFollow = maxOutgoingLinksToFollow;
	}
	
	public boolean isOnlyDomain() {
		return onlyDomain;
	}

	public void setOnlyDomain(boolean onlyDomain) {
		this.onlyDomain = onlyDomain;
	}
	
	public boolean isRobots() {
		return robots;
	}

	public void setRobots(boolean robots) {
		this.robots = robots;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobConfiguration [log=");
		builder.append(log);
		builder.append(", name=");
		builder.append(name);
		builder.append(", storageType=");
		builder.append(storageType);
		builder.append(", threadNum=");
		builder.append(threadNum);
		builder.append(", socketTimeoutMilliseconds=");
		builder.append(socketTimeoutMilliseconds);
		builder.append(", connectionTimeout=");
		builder.append(connectionTimeout);
		builder.append(", delayBetweenRequests=");
		builder.append(delayBetweenRequests);
		builder.append(", maxDepthOfCrawling=");
		builder.append(maxDepthOfCrawling);
		builder.append(", maxOutgoingLinksToFollow=");
		builder.append(maxOutgoingLinksToFollow);
		builder.append(", fetchBinaryContent=");
		builder.append(fetchBinaryContent);
		builder.append(", fileSuffix=");
		builder.append(fileSuffix);
		builder.append(", agent=");
		builder.append(agent);
		builder.append(", https=");
		builder.append(https);
		builder.append(", onlyDomain=");
		builder.append(onlyDomain);
		builder.append(", robots=");
		builder.append(robots);
		builder.append(", maxTotalConnections=");
		builder.append(maxTotalConnections);
		builder.append(", maxConnectionsPerHost=");
		builder.append(maxConnectionsPerHost);
		builder.append(", maxDownloadSizePerPage=");
		builder.append(maxDownloadSizePerPage);
		builder.append(", proxyHost=");
		builder.append(proxyHost);
		builder.append(", proxyPort=");
		builder.append(proxyPort);
		builder.append(", proxyUsername=");
		builder.append(proxyUsername);
		builder.append(", proxyPassword=");
		builder.append(proxyPassword);
		builder.append(", seeds=");
		builder.append(seeds);
		builder.append(", urlFilterReg=");
		builder.append(urlFilterReg);
		builder.append(", selects=");
		builder.append(selects);
		builder.append("]");
		return builder.toString();
	}
}
