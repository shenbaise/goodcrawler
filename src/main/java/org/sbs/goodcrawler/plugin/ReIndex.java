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
package org.sbs.goodcrawler.plugin;

import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.sbs.util.MD5Utils;

/**
 * @author Administrator
 * @date 2014年3月8日
 * desc: 整理文档，重建索引
 */
public class ReIndex extends IndexScanner {
	
	private String index;
	private String type;
	
	public ReIndex(String index, String type) {
		super();
		this.index = index;
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.plugin.IndexScanner#process(org.elasticsearch.search.SearchHit)
	 */
	@Override
	public void process(SearchHit hit) {
		Map<String, Object> m = hit.getSource();
		hit.getIndex();
		System.out.println(m);
		// TODO 处理文档，进一步加工。
//		EsClient.index(index, type, MD5Utils.createMD5((String) m.get("title")), m);
		EsClient.delete(index, type, hit.getId());
	}
	
	
	public void deleteOneByOne(String index,String type,String id){
		EsClient.delete(index, type, id);
	}
	
	public static void main(String[] args) {
		ReIndex reIndex = new ReIndex("movie", "0");
		reIndex.scanIndex("movie", 100, 60000);
	}
}
