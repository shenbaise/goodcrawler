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
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.sbs.goodcrawler.plugin.EsClient;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.storage.Storage;
import org.sbs.goodcrawler.storage.StoreResult;
import org.sbs.util.ImgUtil;
import org.sbs.util.MapUtils;
import org.sbs.util.PinyinUtil;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 存储到es
 */
@SuppressWarnings("rawtypes")
public class ElasticSearchStorage extends Storage {
	private Log log = LogFactory.getLog(this.getClass());
//	ExBulk bulk = new ExBulk();	
	public String index = "";
	String file = "d:\\eFile.txt";
	String imagePath = "d:\\images\\";
	File f = new File(file);
	int i = 0 ;
	
//	private ObjectMapper objectMapper = new ObjectMapper();
	
	
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
		Client client = EsClient.getClient();
		
		try {
			StoreResult storeResult = new StoreResult();
			// 处理Result
			HashMap<String, Object> data = page.getMessages();
			// 处理缩略图
			String type = (String)data.get("category");
			
			Object objectYear = data.get("year");
			int year = 1800;
			if(null!=objectYear){
				year = (int)objectYear;
			}
			
			Set<String> lb = (Set<String>) data.get("type");
			if(null!=lb&&lb.size()>0){
				// nothing to do 
			}else {
				lb = (Sets.newHashSet("其他"));
				data.put("type", lb);
			}
			
			Object temObject = data.get("thumbnail");
			if(null!=temObject){
				String thumbnails =  (String) temObject;
				type = PinyinUtil.getFirstSpell(type);
				Iterable<String> thums = Splitter.on(';').omitEmptyStrings().split(thumbnails);
				for (String img : thums) {
					if(null!=(img = ImgUtil.downThenResize(img, imagePath+year + File.separator + type))){
						if(img.lastIndexOf('.')>0){
							data.put("thumbnail", year + File.separator + type+File.separator + img);
						}else {
							data.put("thumbnail", year + File.separator + type+File.separator + img + ".jpg");
						}
						break;
					}
				}
			}
			// 是否需要更新
//			if(null==data.get("online")||((Set)data.get("online")).size()==0){
//				
//			}
			// 判断是否已存在
			GetResponse get = client.prepareGet(index, "0",(String)data.get("title") ).execute().actionGet();
//			System.out.println(i);
			i++;
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
