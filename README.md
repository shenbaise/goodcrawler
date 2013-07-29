goodcrawler(web crawler) 网络爬虫
===========

0.0.2版本将提供web界面方便管理监控。
----------
0.0.1版本不再更新
------


配置文件
-----------------
#### conf.properties文件
```
pending.urls.queue.size=10000
pending.pages.queue.size=5000
failed.pages.queue.size=5000
pending.store.pages.queue.size=20000
failed.pages.backup.path=./failed-pages/
ignore.failed.pages=true
```
	这个文件主要配置全局信息，队列大小等
#### job_conf.xml文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!--全局配置 -->
<conf>
	<globalSettings>
		<threadPoolSize>30</threadPoolSize>
		<timeout>3000</timeout>
		<reportProcess>true</reportProcess>
	</globalSettings>

	<!--作业配置 -->
	<jobs>
		
		<job>
			<name>testJob</name>
			<storageType>localFile</storageType>
			<agent>Kindle</agent>
			<threadNum>2</threadNum>
			<delayBetweenRequests>100</delayBetweenRequests>
			<maxDepthOfCrawling>-1</maxDepthOfCrawling>
			<maxOutgoingLinksToFollow>1000</maxOutgoingLinksToFollow>
			<fetchBinaryContent>true</fetchBinaryContent>
			<fileSuffix>gif,jpg,png,zip,rar,aiv,mtk</fileSuffix>
			<https>true</https>
			<onlyDomain>true</onlyDomain>
			
			<connection>
				<socketTimeoutMilliseconds>10000</socketTimeoutMilliseconds>
				<connectionTimeout>10000</connectionTimeout>
				<maxTotalConnections>120</maxTotalConnections>
				<maxConnectionsPerHost>100</maxConnectionsPerHost>
				<maxDownloadSizePerPage>1048576</maxDownloadSizePerPage>
			</connection>
			
			<proxy>
				<proxyHost></proxyHost>
				<proxyPort></proxyPort>
				<proxyUsername></proxyUsername>
				<proxyPassword></proxyPassword>
			</proxy>
			
			<!-- job运行时效，-1表示直到爬完全部，60表示一个小时后job自动结束 -->
			<timeout>-1</timeout>
			<seeds>
				<seed>http://www.66e.cc</seed>
			</seeds>
			<urlFilters>
				<urlFileter value="http://www\.66e\.cc/\[a-zA-Z]+/\d{4}?/\d{2}/\w[^_]+\.html"/>
			</urlFilters>
			<selects>
				<!-- 选择抓取的网页类容 -->
				<select name="title" value="#title" />
				<select name="subTitle" value=".subTitle" />
			</selects>
		</job>
		
	</jobs>
</conf>
```

	每一个job元素代表一个抓取工程，不同的job相互独立。
如何使用
----------------------

# 就这么简单！！！
```java
public static void main(String[] args) {
	JobConfigurationManager manager = new JobConfigurationManager();
	List<JobConfiguration> jobs;
	try {
		jobs = manager.loadJobConfigurations(
				new File("D:\\pioneer\\goodcrawler\\src\\main\\resources\\job_conf.xml"));
		for(JobConfiguration conf:jobs){
			// fetch
			FetchForeman.start(conf);
			// extract
			ExtractForeman.start(conf);
			// store
			StoreForeman.start(conf);
		}
	} catch (ConfigurationException e) {
		 e.getMessage();
	}
}
```
### 没有任何编码负担。


LICENSE
-------------------
Apache License, Version 2.0 
http://www.apache.org/licenses/LICENSE-2.0 ( TXT or HTML )

shenbaise1001@126.com

