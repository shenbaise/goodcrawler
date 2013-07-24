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
package org.sbs.goodcrawler.plugin.storage;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
import org.sbs.goodcrawler.plugin.EsClient;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.storage.Storage;
import org.sbs.goodcrawler.storage.StoreResult;
import org.sbs.util.ImgUtil;

import com.alibaba.fastjson.JSON;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 存储到es
 */
public class ElasticSearchStorage extends Storage {
	private Log log = LogFactory.getLog(this.getClass());
//	ExBulk bulk = new ExBulk();	
	public String index = "";
	Client client = EsClient.getClient();
	String file = "d:\\eFile.txt";
	File f = new File(file);
	
	public ElasticSearchStorage(String index){
		this.index = index;
	}
	
	@Override
	public StoreResult beforeStore() {
		return null;
	}

	@Override
	public StoreResult onStore(ExtractedPage page) {
		try {
			if(!(page.getUrl().getURL().endsWith(".html") 
					|| !page.getUrl().getURL().endsWith(".htm")
					|| !page.getUrl().getURL().endsWith(".xhtml"))){
				return null;
			}
			StoreResult storeResult = new StoreResult();
			// 处理Result
			
			HashMap<String, Object> data = page.getMessages();
			
			// 处理缩略图
			
			List<String> imageList = (List<String>) data.get("img");
			
			String type = (String)data.get("t");
			if(StringUtils.isBlank(type)){
				type = "电影";
				log.error("#### 没有t字段 ：" + data.get("url"));
			}
			String lb = (String) data.get("lb");
			if(StringUtils.isBlank(lb)){
				lb = "其他";
			}
			lb  = lb.replace("/", "-");
			for(String s:imageList){
				if(ImgUtil.downAndResize(s, type + File.separator + lb)){
					break;
				}
			}
			
			// 判断是否已存在
			if(client.prepareGet(index, "0",(String)data.get("n") ).execute().actionGet().isExists()){
				FileUtils.write(f, JSON.toJSONString(data, true), true);
				FileUtils.write(f, "#######################", true);
				
			}else{
				EsClient.index(index, "0", data);
			}
			
			return storeResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public StoreResult afterStore(ExtractedPage page) {
		return null;
	}

	
}
