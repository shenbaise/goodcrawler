goodcrawler(web crawler) 网络爬虫
===========
为什么叫goodcrawler?
-----------
		首先它是个crawler，其次它不bad。我觉得不bad那就是good了，所以起名goodcrawler。
它是如何工作的？
---------------
		goodcrawler借用了crawler4j的一点点代码（只保留了Page等相关结构，删除了sleepycat的所有东西），当然依然使用
		httpclient。
		首先种子会被放入url队列中，然后爬取工人（fetchWorker）会从url队列中取url并爬取。
		然后爬到的页面在在page队列中，由抽取工人（extractWorker）从队列中取出并抽取信息。
		接着，抽取或者提炼出来的信息会被放入store队列中再由存储工人（storeWorker）负责存储。
		当然这些都是可扩展的，目前实现了66影视的提取器。本地文件系统的存储器、elasticsearch的存储器。。。
		这一切的工作都是多线程的，所以效率很高。
### 后期还会实现hdfs的存储器、mongodb的存储器等等。
如何抽取内容？
-----------------
		goodcrawler的默认抽取器使用Jsoup对页面进行解析，对于页面结构设计良好的网页，只需要在配置文件中填上需要抽取的元素的
		Xpath路径就可以自动实现抽取，而无需使用者自己去编写抽取器。
配置文件
-----------------
#### conf.properties文件
....
pending.urls.queue.size=10000
pending.pages.queue.size=5000
failed.pages.queue.size=5000
pending.store.pages.queue.size=20000
failed.pages.backup.path=./failed-pages/
ignore.failed.pages=true

	这个文件主要配置全局信息，队列大小等
#### job_conf.xml文件
...xml
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


每一个job元素代表一个抓取工程，不同的job相互独立。
如何使用
----------------------

# 就这么简单！！！
....java
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
...
		没有任何编码负担。

待解决的问题和以后的规划
------------------------
这个项目是一时兴起的小玩物，目前的几乎是完善和调整软件结构，实现更多特性（session、form提交等等）。后期打算将url队列单独分离出来
作为一个种子库放在server Center上，爬取器和存储器分散开来以实现分布式爬取。

shenbaise1001@126.com
