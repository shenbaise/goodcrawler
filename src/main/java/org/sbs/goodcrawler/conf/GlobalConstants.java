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
package org.sbs.goodcrawler.conf;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * 描述：系统全局静态常量
 */
public class GlobalConstants {
	/**
	 * conf配置文件路径
	 */
	public static String propertiyFilePaht = "conf.properties";
	/**
	 * 待处理URL队列大小
	 */
	public static String pendingUrlsQueueSize = "pending.urls.queue.size";
	/**
	 * 待处理页面队列大小
	 */
	public static String pendingPagesQueueSize = "pending.pages.queue.size";
	/**
	 * 失败页面队列大小
	 */
	public static String failedPagesQueueSize = "failed.pages.queue.size";
	/**
	 * （解析或者收取）失败页面备份文件路径
	 */
	public static String failedPagesBackupPath = "failed.pages.backup.path";
	/**
	 * 是否忽略错误的或者处理失败的页面
	 */
	public static String ignoreFailedPages = "ignore.failed.pages";
	
}
