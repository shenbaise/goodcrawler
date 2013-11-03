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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.sbs.goodcrawler.jobconf.RepeatPolicy;
import org.sbs.goodcrawler.jobconf.StoreConfig;
import org.sbs.goodcrawler.plugin.EsClient;
import org.sbs.goodcrawler.queue.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.storage.Storage;
import org.sbs.goodcrawler.storage.StoreResult;
import org.sbs.goodcrawler.storage.StoreResult.Status;
import org.sbs.util.MD5Utils;
import org.sbs.util.MapUtils;

import com.google.common.collect.Lists;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 存储到es
 */
@SuppressWarnings("rawtypes")
public class ElasticSearchStorage extends Storage {
	private Log log = LogFactory.getLog(this.getClass());
	public String index = "";
	private List<String> templates = Lists.newArrayList();
	Client client = EsClient.getClient();
	/**
	 * 1自动生成。2md5某个信息
	 */
	short idPolicy = 1;
	String refFeild = null;
	/**
	 * 构造
	 * @param storeConfig
	 */
	public ElasticSearchStorage(StoreConfig storeConfig){
		this.index = storeConfig.jobName;
		this.templates = storeConfig.getTemplates();
		if(storeConfig.isAutoId()){
			this.idPolicy = 1;
		}else if(StringUtils.isNotBlank(storeConfig.getMd5Ref())){
			this.idPolicy = 2;
			this.refFeild = storeConfig.getMd5Ref();
		}else {
			this.idPolicy = 1;
		}
	}
	
	@Override
	public StoreResult beforeStore() {
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public StoreResult onStore(ExtractedPage page,RepeatPolicy policy) {
		StoreResult storeResult = null;
		try {
			storeResult = new StoreResult();
			// 处理Result
			HashMap<String, Object> data = page.getMessages();
			String url = page.getUrl().getURL();
			boolean b = true;
			if(this.templates!=null && templates.size()>0){
				for(String name:templates){
					HashMap<String, Object> m = (HashMap<String, Object>)data.get(name);
					m.put("url", url);
					b = store(m, policy);
				}
			}
			if(b){
				storeResult.setStatus(Status.success);
			}else {
				storeResult.setStatus(Status.failed);
			}
			return storeResult;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			storeResult.setStatus(Status.failed);
			return storeResult;
		}
	}
	/**
	 * Index data
	 * @param data
	 * @param policy
	 * @return
	 */
	public boolean store(HashMap<String, Object> data,RepeatPolicy policy){
		if(data==null)
			return false;
		String id = null;
		if(1==this.idPolicy){
			try {
				EsClient.index(index, "0", data);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else if (2==this.idPolicy) {
			try {
				id = MD5Utils.createMD5((String) data.get(this.refFeild));
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
			// 判断是否存在
			GetResponse get = null;
			try {
				get = client.prepareGet(index, "0",id)
						.execute()
						.actionGet();
			} catch (Exception e) {
				e.printStackTrace();
				// index不存在？？
				if(null!=data)
					try {
						EsClient.index(index, "0",id, data);
					} catch (IOException e1) {
						e1.printStackTrace();
						return false;
					}
			}
			
			try {
				if(null!=get && get.isExists()){
					if(RepeatPolicy.merge .equals(policy)){
						Map<String, Object> m = get.getSource();
						m = MapUtils.mager((HashMap<String, Object>) m, data);
						EsClient.index(index, "0",id, m);
					}else if (RepeatPolicy.overwrite.equals(policy)) {
						EsClient.index(index, "0",id, data);
					}else if (RepeatPolicy.ignore.equals(policy)) {
						return true;
					}
				}else{
					EsClient.index(index, "0",id, data);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		data.clear();
		return true;
	}
	
	@Override
	public StoreResult afterStore(ExtractedPage page) {
		return null;
	}
	
}
