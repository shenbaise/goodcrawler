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
package org.sbs.goodcrawler.plugin;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * 
 * @author shenbaise
 * @date 2014年3月8日
 * desc: 遍历索引
 */
public abstract class IndexScanner {
	/**
	 * 处理索引文档
	 * @param hit
	 */
	public abstract void process(SearchHit hit);
	
	/**
	 * 扫描整个索引，每个文档需要process进行加工和进一步操作，例如重建索引等。
	 * @param index
	 * @param size
	 * @param keepAlive
	 */
	public void scanIndex(String index,int size,int keepAlive){
		Client client = EsClient.getClient();
		SearchResponse scrollResp = client.prepareSearch(index)
		        .setSearchType(SearchType.SCAN)
		        .setScroll(new TimeValue(keepAlive))
		        .setQuery(QueryBuilders.matchAllQuery())
		        .setSize(size).execute().actionGet(); 
		//100 hits per shard will be returned for each scroll
		//Scroll until no hits are returned
		while (true) {
		    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(keepAlive)).execute().actionGet();
		    for (SearchHit hit : scrollResp.getHits()) {
		       process(hit);
		    }
		    //Break condition: No hits are returned
		    if (scrollResp.getHits().getHits().length == 0) {
		        break;
		    }
		}
	}
}
