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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.XMLProperties;


/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * 任务配置文件解析类
 */
public class JobConfiguration {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static XMLProperties conf;
	
	public static XMLProperties getSysConfig(){
		if(conf==null){
			try {
				conf = new XMLProperties(ClassLoader.getSystemResourceAsStream("job_conf.xml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return conf;
	}
	
	public static void main(String[] args) {
		System.out.println(JobConfiguration.getSysConfig().getProperty("jobs.job.selects.select"));
		System.out.println(conf.getAttribute("jobs.job.selects.select", "name"));
		
		String[]  ss = conf.getChildrenProperties("jobs.job.selects");
		for(String s:ss){
			System.out.println(conf.getAttribute("jobs.job.selects."+s, "name"));
		}
		System.out.println(ss[0]);
	}

}
