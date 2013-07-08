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

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-6 es cilent
 */
public class EsClient {

	static Settings settings = ImmutableSettings.settingsBuilder()
	// .put("cluster.name", "ES-index")
			.put("client.transport.sniff", true).build();

	private static Client client = null;

	static {
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						"127.0.0.1", 9300));
	}

	public static Client getClient() {
		if (null == client) {
			client = new TransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(
							"127.0.0.1", 9300));
		}
		return client;
	}

	public static void index(String index, String type, Map<String, String> data) {
		try {
			XContentBuilder xBuilder = jsonBuilder().startObject();
			Iterator it = data.keySet().iterator();
			while (it.hasNext()) {
				String key,value;
				key = (String) it.next();
				value = data.get(key);
				xBuilder.field(key)
				.value(value);
			}
			xBuilder.endObject();
			IndexResponse response = client.prepareIndex(index, type).setId(data.get("movie"))
					.setSource(xBuilder).execute().actionGet();
			// what does respose contains?
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SearchResponse search(String index,String id){
		SearchResponse response = client.prepareSearch(index)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(termQuery("_id", id))
		        .setFrom(0).setSize(10).setExplain(false)
		        .execute()
		        .actionGet();
		return response;
	}
	
	public static void distroy() {
		client.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SearchResponse response = EsClient.search("movie", "魔境仙踪[国语高清]");
		System.out.println(response.toString());
		response.getHits().getTotalHits();
	}

}
