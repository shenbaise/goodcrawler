goodcrawler(web crawler) 网络爬虫
===========
本版本将对爬虫爬去策略，通用抽取器做重构
---------------------------
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
```
pending.urls.queue.size=10000
pending.pages.queue.size=5000
failed.pages.queue.size=5000
pending.store.pages.queue.size=20000
failed.pages.backup.path=./failed-pages/
ignore.failed.pages=true
```
	这个文件主要配置全局信息，队列大小等
现在配置更加灵活
-------------
#### job_conf.xml文件
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!--全局配置 -->
<conf>
	<globalSettings>
		<threadPoolSize>50</threadPoolSize>
		<timeout>10000</timeout>
		<reportProcess>true</reportProcess>
	</globalSettings>

	<!--作业配置 -->
	<jobs>
		<job>
			<name>movie</name>
			<!-- 定义 Fetcher -->
			<fetch>
				<!-- 默认为Default，如果有其他实现，填写类的全路径 -->
				<type>default</type>
				<agent>Kindle</agent>
				<threadNum>3</threadNum>
				<delayBetweenRequests>100</delayBetweenRequests>
				<maxDepthOfCrawling>-1</maxDepthOfCrawling>
				<maxOutgoingLinksToFollow>1000</maxOutgoingLinksToFollow>
				<fetchBinaryContent>false</fetchBinaryContent>
				<fileSuffix>gif,jpg,png,zip,rar,aiv,mtk</fileSuffix>
				<https>true</https>
				<onlyDomain>true</onlyDomain>
				<connection>
					<socketTimeoutMilliseconds>20000</socketTimeoutMilliseconds>
					<connectionTimeout>2000</connectionTimeout>
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
				<seeds>
					<seed>http://www.youku.com/</seed>
				</seeds>
				<!-- 加入Url待处理队列的策略 -->
				<fetchUrlFilters>
					<!-- 
					<filter>http://.+[.]youku[.]com/.*[.]html$</filter>
					 -->
					<!-- 种子需要加入过滤策略 -->
					<filter>http://.*.youku.com/show_page/.*</filter>
					<filter>http://.*.youku.com/star_page/.*</filter>
					<filter>http://www.youku.com/</filter>
				</fetchUrlFilters>
				<!-- 加入待抽取信息页队列的策略 -->
				<extractUrlfilters>
					<filter>http://.*.youku.com/show_page/.*</filter>
					<filter>http://.*.youku.com/star_page/.*</filter>
				</extractUrlfilters>
			</fetch>

			<!-- 定义extract -->
			<extract>
				<type>default</type>
				<threadNum>6</threadNum>
				<template>
					<urls>
						<url>http://.*.youku.com/show_page/.*</url>
					</urls>
					<elements>
						<element name="title" type="string" value=".title .name" attr="text" required="true" />
						<element name="category" type="string" value=".title .type a" attr="text" required="true">
							
							<if test="category=电影" ref="category">
								<element1 name="year" type="int" value=".title .pub" attr="text"/>
								<element1 name="score" type="int" value=".ratingstar .num" attr="text"/>
								<element1 name="alias" type="string" value=".alias" attr="text">
									<action1 opration="split" separator=":" retain="1"></action1>
								</element1>
								<element1 name="area" type="string" value=".row2 .area a" attr="text"/>
								<element1 name="lang" type="string" value=".lang" attr="text">
									<action1 opration="split" separator=":" retain="1"></action1>
								</element1>
								<element1 name="type" type="set" value=".row2 .type a" attr="text" required="true"/>
								<element1 name="director" type="string" value=".row2 .director a" attr="text" />
								<element1 name="actors" type="set" value=".actor a" attr="text" />
								<element1 name="duration" type="int" value=".row2 .duration" attr="text" />
								<element1 name="hot" type="int" value=".row2 .vr .num" attr="text" />
								<element1 name="summary" type="string" value=".detail .long" attr="text" />
								<element1 name="thumbnail" type="string" value=".baseinfo .thumb img" attr="src" required="true"/>
								
								<element1 name="play1" type="url" value=".btnplayposi" attr="href">
									<element2 name="online1" type="set" value="#link2" attr="value" />
								</element1>
								<element1 name="play2" type="url" value=".btnplaytrailer" attr="value">
									<element2 name="online2" type="set" value="#link2" attr="value" />
								</element1>
							</if>
							<if test="category=电视剧">
								<element1 name="year" type="int" value=".title .pub" attr="text" required="true"/>
								<element1 name="score" type="int" value=".ratingstar .num" attr="text"/>
								<element1 name="alias" type="string" value=".alias" attr="text">
									<action1 opration="split" separator=":" retain="1"></action1>
								</element1>
								<element1 name="area" type="string" value=".row2 .area a" attr="text"/>
								<element1 name="lang" type="string" value=".lang" attr="text">
									<action1 opration="split" separator=":" retain="1"></action1>
								</element1>
								<element1 name="type" type="set" value=".row2 .type a" attr="text" required="true"/>
								<element1 name="director" type="string" value=".row2 .director a" attr="text" />
								<element1 name="actors" type="set" value=".actor a" attr="text"/>
								<element1 name="hot" type="int" value=".row2 .vr .num" attr="text" />
								<element1 name="summary" type="string" value="#show_info_short" attr="text" />
								<element1 name="thumbnail" type="string" value=".baseinfo .thumb img" attr="src" required="true"/>
								<!-- 剧集超过40需要模拟点击 #研究中 -->
								<element1 name="play1" type="url" value="#episode a" attr="href">
									<element2 name="online" type="set" value="#link2" attr="value" />
								</element1>
							</if>
							<if test="category=综艺">
								<element1 name="year" type="int" value=".title .pub" attr="text"/>
								<element1 name="score" type="int" value=".ratingstar .num" attr="text" />
								<element1 name="area" type="string" value=".row2 .area a" attr="text" />
								<element1 name="type" type="set" value=".row1 .type .a" attr="text" required="true"/>
								<element1 name="lang" type="string" value=".lang" attr="text">
									<action1 opration="split" separator=":" retain="1"></action1>
								</element1>
								<element1 name="host" type="string" value=".row1 .host a" attr="text"/>
								<element1 name="hot" type="int" value=".row2 .vr .num" attr="text" />
								<element1 name="summary" type="string" value="#show_info_short" attr="text" />
								<element1 name="thumbnail" type="string" value=".baseinfo .thumb img" attr="src" required="true"/>
								<!-- 剧集超过40需要模拟点击 #研究中 -->
								<element1 name="play1" type="url" value="#episode a" attr="href">
									<element2 name="online" type="set" value="#link2" attr="value" />
								</element1>
							</if>
							<if test="category=动漫">
								<element1 name="year" type="int" value=".title .pub" attr="text" required="true"/>
								<element1 name="score" type="int" value=".ratingstar .num" attr="text"/>
								<element1 name="alias" type="string" value=".alias" attr="text">
									<action1 opration="split" separator=":" retain="1"></action1>
								</element1>
								<element1 name="area" type="string" value=".row2 .area a" attr="text"/>
								<element1 name="type" type="set" value=".row2 .type a" attr="text" required="true" />
								<element1 name="hot" type="int" value=".row2 .vr .num" attr="text" />
								<element1 name="summary" type="string" value=".detail .long" attr="text" />
								<element1 name="thumbnail" type="string" value=".baseinfo .thumb img" attr="src" required="true"/>
								<element1 name="play1" type="url" value="#episode a" attr="href">
									<element2 name="online" type="set" value="#link2" attr="value" />
								</element1>
							</if>
						</element>
					</elements>
				</template>
			</extract>
			<!-- 存储配置 -->
			<store>
				<type>default</type>
				<threadNum>1</threadNum>
			</store>
			<!-- job运行时效，-1表示直到爬完全部，60表示一个小时后job自动结束 -->
			<timeout>-1</timeout>
		</job>
	</jobs>
</conf>
```
### 把任务配置文件放到conf目录，程序会自动扫描该目录并加载任务。

每一个job元素代表一个抓取工程，不同的job相互独立。

如何使用
----------------------
# 就这么简单！！！就一行代码
```java
BootStrap.start();
```
# 或者git代码解压后，进入目录运行 mvn jetty:run，打开浏览器输入http://localhost:8080/通过web界面控制程序
```java
mvn jetty:run
```

# 抓取结果为Json结构，很容易进行进一步加工处理
```json
{
_score: 1
_source: {
summary: 世宗（朴永奎 饰）因不满大儿子的沉迷酒色，荒淫无度，罢黜了他王世子的地位，改立三子李祹(朱智勋 饰)为王世子。然而终日读书体质羸弱的李祹对宫廷政治和权力并不感兴趣，甚至在父亲的一再施压下企图逃离这备受约束的生活。德七(朱智勋 饰)是一名满身蛮力的奴隶，长着一张和王世子一样的容貌。德七从小就和自家主人的女儿秀妍（李荷妮 饰）互为倾心。不料这家主人牵扯到一场政治阴谋中，全家以逆反的罪名被诬陷入狱。德七企图爬过宫墙救自己的心上人，阴错阳差的和正逃亡宫外的王世子调换了身份。一方面，德七心惊胆战的在宫中顶替着王世子，另一方面，来到民间游历的王世子历经磨难，尝尽苦头，看到了百姓的苦难，渐渐有了作为君主的一番见解。而此时，宰相沈奕发现了假世子的秘密…… 　　该片取自韩国历史上出名的英君贤主李祹成为世子前后的秘史。
alias:  I Am The King / 나는 왕이로소이다
score: 61
type: [
喜剧
]
director: 张圭成
online1: [
http://player.youku.com/player.php/sid/XNTcwODEyNjEy/v.swf
]
title: 我是王
category: 电影
duration: 120
area: 韩国
thumbnail: 2012\dy\27c48348795a209207757bd1ab4787ce.jpg
hot: 5930
actors: [
朱智勋
李荷妮
]
year: 2012
}
}
```

# 基础memcached后可进行分布式部署。

LICENSE
-------------------
Apache License, Version 2.0 
http://www.apache.org/licenses/LICENSE-2.0 ( TXT or HTML )

shenbaise1001@126.com