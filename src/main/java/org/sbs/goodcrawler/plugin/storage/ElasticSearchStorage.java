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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.sbs.goodcrawler.plugin.EsClient;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.storage.Storage;
import org.sbs.goodcrawler.storage.StoreResult;
import org.sbs.util.MapUtils;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 存储到es
 */
@SuppressWarnings("rawtypes")
public class ElasticSearchStorage extends Storage {
	private Log log = LogFactory.getLog(this.getClass());
	public String index = "";
	Client client = EsClient.getClient();
	public ElasticSearchStorage(String index){
		this.index = index;
	}
	
	@Override
	public StoreResult beforeStore() {
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public StoreResult onStore(ExtractedPage page) {
		try {
			StoreResult storeResult = new StoreResult();
			// 处理Result
			HashMap<String, Object> data = page.getMessages();
			HashMap<String, Object> content = (HashMap<String, Object>) data.get(index);
			// 判断是否已存在
			GetResponse get = client.prepareGet(index, "0",(String)content.get("title") )
					.execute()
					.actionGet();
			
			if(get.isExists()){
				Map<String, Object> m = get.getSource();
				m = MapUtils.mager((HashMap<String, Object>) m, data);
				EsClient.index(index, "0", m);
			}else{
				EsClient.index(index, "0", data);
			}
			data.clear();
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
