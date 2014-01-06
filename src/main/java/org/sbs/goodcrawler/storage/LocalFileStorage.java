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
package org.sbs.goodcrawler.storage;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.page.ExtractedPage;
import org.sbs.goodcrawler.storage.StoreResult.Status;


/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * 本地文件系统存储实现
 */
@SuppressWarnings("rawtypes")
public class LocalFileStorage extends Storage {
	
	private Log log = LogFactory.getLog(this.getClass());
	public String storeDir;
	String jobName;
	File storeFile;
	public LocalFileStorage(String storeDir,String jobName){
		this.storeDir = storeDir;
		this.jobName = jobName;
		File dir = new File(this.storeDir + File.separator + this.jobName);
		System.out.println(dir.getAbsolutePath());
		if(!dir.exists()){
			dir.mkdirs();
			storeFile = new File(dir.getAbsolutePath(), jobName+".txt");
		}
		if(null==storeFile){
			storeFile = new File(dir.getAbsolutePath(), jobName+".txt");
		}
	}
	
	@Override
	public StoreResult beforeStore() {
		return null;
	}

	@Override
	public StoreResult onStore(ExtractedPage page) {
		StoreResult storeResult = new StoreResult();
		JSONObject json = JSONObject.fromObject(page.getMessages(), new JsonConfig());
		try {
			FileUtils.writeStringToFile(storeFile, json.toString()+"\n\n","utf-8",true);
			storeResult.setStatus(Status.failed);
		} catch (IOException e) {
			 log.error(e.getMessage());
			 storeResult.setMessge(e.getMessage());
			 storeResult.setStatus(Status.failed);
		}
		return storeResult;
	}

	@Override
	public StoreResult afterStore(ExtractedPage page) {
		return null;
	}
	
}

