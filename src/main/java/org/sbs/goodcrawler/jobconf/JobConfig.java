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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sbs.goodcrawler.conf.Configuration;

/**
 * @author whiteme
 * @date 2013年10月13日
 * @desc 
 */
public class JobConfig extends Configuration {
	
	Log log = LogFactory.getLog(JobConfig.class);
	/**
	 * job运行一定时长后自动结束
	 */
	private int jobTime;
	/**
	 * job抓取一定数量url后自动结束
	 */
	private int urlNum;
	
	private FetchConfig fetchConfig;
	
	private ExtractConfig extractConfig;
	
	private StoreConfig storeConfig;
	
	private Document confDoc;
	
	public void loadConfig(String configFiles){
		try {
			this.confDoc = Jsoup.parse(new File("conf/youku_conf.xml"), "utf-8");
			super.jobName = confDoc.select("job name").text();
			super.indexName = confDoc.select("job").attr("indexName");
			this.jobTime = Integer.parseInt(confDoc.select("jobtime").text());
			this.urlNum = Integer.parseInt(confDoc.select("urlNum").text());
//			this.fetchConfig = FetchConfig
//			this.extractConfig = ExtractConfig.load(confDoc)
//			this.storeConfig = StoreConfig.load(confDoc)
			// TODO load fetch 、extract、store config
		} catch (IOException e) {
			log.fatal(e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
