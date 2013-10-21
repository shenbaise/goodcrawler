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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.Configuration;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.urlmanager.BloomfilterHelper;
import org.sbs.goodcrawler.urlmanager.PendingUrls;
import org.sbs.goodcrawler.urlmanager.WebURL;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 
 */
public class FetchConfig extends Configuration{
	private Log log = LogFactory.getLog(this.getClass());
	
	public FetchConfig() {
		
	}
	
	private String type;
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
	private List<String> seeds = Lists.newArrayList();
	/**
	 * 收集url的策略
	 */
	private List<String> fetchUrlFilters = Lists.newArrayList();
	/**
	 * 推入下个处理环节的Url处理策略
	 */
	private List<String> extractUrlfilters = Lists.newArrayList();
	
	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getSocketTimeoutMilliseconds() {
		return socketTimeoutMilliseconds;
	}

	public void setSocketTimeoutMilliseconds(int socketTimeoutMilliseconds) {
		this.socketTimeoutMilliseconds = socketTimeoutMilliseconds;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
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

	public int getMaxOutgoingLinksToFollow() {
		return maxOutgoingLinksToFollow;
	}

	public void setMaxOutgoingLinksToFollow(int maxOutgoingLinksToFollow) {
		this.maxOutgoingLinksToFollow = maxOutgoingLinksToFollow;
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

	
	
	public List<String> getFetchUrlFilters() {
		return fetchUrlFilters;
	}

	public void setFetchUrlFilters(List<String> fetchUrlFilters) {
		this.fetchUrlFilters = fetchUrlFilters;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> getExtractUrlfilters() {
		return extractUrlfilters;
	}

	public void setExtractUrlfilters(List<String> extractUrlfilters) {
		this.extractUrlfilters = extractUrlfilters;
	}

	/**
	 * 从配置文件中加载配置信息
	 * @param confFile
	 * @return
	 */
	public FetchConfig loadConfig(Document confDoc) throws ConfigurationException{
		try {
			Document doc = confDoc;
			super.jobName = doc.select("job").attr("name");
			Elements e = doc.select("fetch");
			this.type = e.select("type").text();
			this.agent = e.select("agent").text();
			String temp = e.select("threadNum").text();
			if(StringUtils.isNotBlank(temp)){
				this.threadNum = Integer.parseInt(temp);
			}
			
			temp = e.select("delayBetweenRequests").text();
			if(StringUtils.isNotBlank(temp)){
				this.delayBetweenRequests = Integer.parseInt(temp);
			}
			
			temp = e.select("maxDepthOfCrawling").text();
			if(StringUtils.isNotBlank(temp)){
				this.maxDepthOfCrawling = Integer.parseInt(temp);
			}
			
			temp = e.select("fetchBinaryContent").text();
			if(StringUtils.isNotBlank(temp)){
				this.fetchBinaryContent = Boolean.parseBoolean(temp);
			}
			
			if(StringUtils.isNotBlank(e.select("maxOutgoingLinksToFollow").text())){
				this.maxOutgoingLinksToFollow = Integer.parseInt(e.select("maxOutgoingLinksToFollow").text());
			}
			
			temp = e.select("fileSuffix").text();
			if(StringUtils.isNotBlank(temp)){
				this.fileSuffix = temp;
			}
			
			temp = e.select("maxDownloadSizePerPage").text();
			if(StringUtils.isNotBlank(temp)){
				this.maxDownloadSizePerPage = Integer.parseInt(temp);
			}
			
			temp = e.select("https").text();
			if(StringUtils.isNotBlank(temp)){
				this.https = Boolean.parseBoolean(temp);
			}
			
			temp = e.select("onlyDomain").text();
			if(StringUtils.isNotBlank(temp)){
				this.onlyDomain = Boolean.parseBoolean(temp);
			}
			
			temp = e.select("socketTimeoutMilliseconds").text();
			if(StringUtils.isNotBlank(temp)){
				this.socketTimeoutMilliseconds = Integer.parseInt(temp);
			}
			
			temp = e.select("connectionTimeout").text();
			if(StringUtils.isNotBlank(temp)){
				this.connectionTimeout = Integer.parseInt(temp);
			}
			
			temp = e.select("maxTotalConnections").text();
			if(StringUtils.isNotBlank(temp)){
				this.maxTotalConnections = Integer.parseInt(temp);
			}
			
			temp = e.select("maxConnectionsPerHost").text();
			if(StringUtils.isNotBlank(temp)){
				this.maxConnectionsPerHost = Integer.parseInt(e.select("maxConnectionsPerHost").text());
			}
			
			temp = e.select("maxConnectionsPerHost").text();
			if(StringUtils.isNotBlank(temp)){
				this.maxConnectionsPerHost = Integer.parseInt(temp);
			}
			
			if(StringUtils.isNotBlank(e.select("proxyHost").text())){
				this.proxyHost = e.select("proxyHost").text();
			}
			if(StringUtils.isNotBlank(e.select("proxyPort").text())){
				this.proxyPort = Integer.parseInt(e.select("proxyPort").text());
			}
			if(StringUtils.isNotBlank(e.select("proxyUsername").text())){
				this.proxyUsername = e.select("proxyUsername").text();
			}
			if(StringUtils.isNotBlank(e.select("proxyPassword").text())){
				this.proxyPassword = e.select("proxyPassword").text();
			}
			if(StringUtils.isNotBlank(e.select("proxyHost").text())){
				this.proxyHost = e.select("proxyHost").text();
			}
			
			// 加入seed
			Elements seeds = doc.select("fetch seeds seed");
			for(Element element:seeds){
				WebURL url = new WebURL();
				String seed = element.text();
				this.seeds.add(seed);
				url.setURL(seed);
				try {
					PendingUrls.getInstance().addUrl(url);
					BloomfilterHelper.getInstance().add(url.getURL());
				} catch (QueueException e1) {
					e1.printStackTrace();
				}
			}
			
			/*
			 * 加载插入待爬取Url队列的过滤规则
			 */
			Elements fetchUrlFilters = doc.select("fetchUrlFilters filter");
			for(Element element:fetchUrlFilters){
				this.fetchUrlFilters.add(element.text());
			}
			
			/*
			 * 加载插入待提取页面Url的过滤规则
			 */
			Elements extractUrlfilters = doc.select("extractUrlfilters filter");
			for(Element element:extractUrlfilters){
				this.extractUrlfilters.add(element.text());
			}
		} catch (NumberFormatException e) {
			throw new ConfigurationException("配置文件加载错误："+e.getMessage());
		}
		
		return this;
	}


	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("FetchConfig [log=")
				.append(log)
				.append(", type=")
				.append(type)
				.append(", threadNum=")
				.append(threadNum)
				.append(", socketTimeoutMilliseconds=")
				.append(socketTimeoutMilliseconds)
				.append(", connectionTimeout=")
				.append(connectionTimeout)
				.append(", delayBetweenRequests=")
				.append(delayBetweenRequests)
				.append(", maxDepthOfCrawling=")
				.append(maxDepthOfCrawling)
				.append(", maxOutgoingLinksToFollow=")
				.append(maxOutgoingLinksToFollow)
				.append(", fetchBinaryContent=")
				.append(fetchBinaryContent)
				.append(", fileSuffix=")
				.append(fileSuffix)
				.append(", agent=")
				.append(agent)
				.append(", https=")
				.append(https)
				.append(", onlyDomain=")
				.append(onlyDomain)
				.append(", robots=")
				.append(robots)
				.append(", maxTotalConnections=")
				.append(maxTotalConnections)
				.append(", maxConnectionsPerHost=")
				.append(maxConnectionsPerHost)
				.append(", maxDownloadSizePerPage=")
				.append(maxDownloadSizePerPage)
				.append(", proxyHost=")
				.append(proxyHost)
				.append(", proxyPort=")
				.append(proxyPort)
				.append(", proxyUsername=")
				.append(proxyUsername)
				.append(", proxyPassword=")
				.append(proxyPassword)
				.append(", seeds=")
				.append(seeds != null ? seeds.subList(0,
						Math.min(seeds.size(), maxLen)) : null)
				.append(", fetchUrlFilters=")
				.append(fetchUrlFilters != null ? fetchUrlFilters.subList(0,
						Math.min(fetchUrlFilters.size(), maxLen)) : null)
				.append(", extractUrlfilters=")
				.append(extractUrlfilters != null ? extractUrlfilters.subList(
						0, Math.min(extractUrlfilters.size(), maxLen)) : null)
				.append("]");
		return builder.toString();
	}

	// test
	public static void main(String[] args) {
		FetchConfig fetchConfig = new FetchConfig();
		Document document;
		try {
			document = Jsoup.parse(new File("conf/youku_conf.xml"), "utf-8");
			System.out.println(fetchConfig.loadConfig(document).toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
