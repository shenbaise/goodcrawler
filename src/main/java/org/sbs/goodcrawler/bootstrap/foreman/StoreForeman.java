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
package org.sbs.goodcrawler.bootstrap.foreman;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sbs.goodcrawler.jobconf.StoreConfig;
import org.sbs.goodcrawler.plugin.classloader.PluginClassLoader;
import org.sbs.goodcrawler.plugin.storage.ElasticSearchStorage;
import org.sbs.goodcrawler.plugin.storage.p.IESStoragePlugin;
import org.sbs.goodcrawler.storage.DefaultStoreWorker;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-4
 * 存储工头
 */
public class StoreForeman {

	public StoreForeman() {
	}
	
	@SuppressWarnings("rawtypes")
	public void start(StoreConfig conf){
		int threadNum = conf.getThreadNum();
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		for(int i=0;i<threadNum;i++){
			try {
				IESStoragePlugin p = (IESStoragePlugin) PluginClassLoader.loadClass(conf.getPluginClass()).newInstance();
				p.setConfig(conf);
				executor.submit(new DefaultStoreWorker(conf,new ElasticSearchStorage(conf,p)));
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}
}
