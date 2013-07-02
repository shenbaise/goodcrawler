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
package org.sbs.goodcrawler.job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.conf.jobconf.JobConfigurationManager;
import org.sbs.goodcrawler.exception.ConfigurationException;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-1
 * 任务容器
 */
public class JobContainer {
	
	private Log log = LogFactory.getLog(this.getClass());
	private static List<JobConfiguration> holder = new ArrayList<>();
	private JobConfigurationManager jobConfigurationManager = new JobConfigurationManager();
	List<String> jobConfigFile = new ArrayList<>();
	private static boolean init = false;
	
	public JobContainer(String jobConfigFile){
		this.jobConfigFile.add(jobConfigFile);
	}
	public void init(){
		if(init)
			return;
		holder.clear();
		List<JobConfiguration> jobConfigurations = new ArrayList<>();
		for(String file:jobConfigFile){
			try {
				jobConfigurations = jobConfigurationManager.loadJobConfigurations(new File(file));
				if(jobConfigurations!=null)
					holder.addAll(jobConfigurations);
			} catch (ConfigurationException e) {
				 log.error(e.getMessage());
			}
		}
		init = true;
	}
	
	public static List<JobConfiguration> getJobConfs(){
		return holder;
	}
	
}
