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
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.sbs.util.MD5Utils;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-6 es cilent
 */
public class EsClient {

	static Settings settings = ImmutableSettings.settingsBuilder()
	// 		.put("cluster.name", "ES-index")
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

	/**
	 * index data
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param data
	 * @throws IOException
	 */
	public static void index(String index, String type, String id,
			Map<String, Object> data) throws IOException {
		client.prepareIndex(index, type).setId(id).setSource(data).execute()
				.actionGet();
	}

	/**
	 * index data
	 * 
	 * @param index
	 * @param type
	 * @param data
	 * @throws IOException
	 */
	public static void index(String index, String type, Map<String, Object> data)
			throws IOException {
		client.prepareIndex(index, type).setSource(data).execute().actionGet();
	}

	/**
	 * Index
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param json
	 */
	public static void index(String index, String type, String id, String json) {
		try {
			client.prepareIndex(index, type).setId(id).setSource(json)
					.execute().actionGet();
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		}
	}

	public static SearchResponse search(String index, String id) {
		SearchResponse response = client.prepareSearch(index)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(termQuery("_id", id)).setFrom(0).setSize(10)
				.setExplain(false).execute().actionGet();
		return response;
	}

	public static void distroy() {
		client.close();
	}

	/**
	 * mapping
	 * 
	 * @return
	 */
	public XContentBuilder getMapping() {
		try {
			XContentBuilder mapping = jsonBuilder()
					.startObject()
					.startObject("movie")
					.startObject("_source")
					.field("enabled", "true")
					.field("compress", true)
					// .field("compress_threshold", "200b")
					.endObject().startObject("_all").field("enabled", "true")
					.endObject()

					.startObject("_index").field("enabled", "false")
					.endObject()

					.startObject("_type").field("index", "yes")
					.field("store", "yes").field("index", "not_analyzed")
					.endObject()

					.startObject("_id").field("index", "yes")
					.field("store", "yes").field("index", "not_analyzed")
					.endObject()

					.startObject("_analyzer").field("path", "field_analyzer")
					.endObject()

					.startObject("properties").startObject("pm")
					.field("type", "string").endObject()

					.startObject("nd").field("type", "integer")
					.field("omit_norms", "yes")
					.field("omit_term_freq_and_positions", "yes")
					.field("index", "not_analyzed").endObject()

					.startObject("ym").field("type", "string").endObject()

					.startObject("url").field("type", "string")
					.field("index", "no").field("store", "yes").endObject()

					.endObject().endObject().endObject();

			return mapping;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 设置mapping
	 */
	public void putMapping(String mapping,String index,String type) {
		try {
			client.admin().indices().preparePutMapping(index).setType(type)
					.setSource(mapping).execute().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	/**
	 * put Template
	 * @param templateName
	 * @param type
	 * @param mapping
	 */
	public void putTemplate(String templateName,String type,String mapping){
		try {
			client.admin().indices().preparePutTemplate(templateName)
			.addMapping(type, mapping).execute().actionGet();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 创建索引并设置mapping
	 * 
	 * @param index
	 * @param type
	 * @param mapping
	 */
	public static void createIndexAndMapping(String index, String type,
			String mapping) {
		client.prepareIndex(index, type).execute().actionGet();
		client.admin().indices().preparePutMapping("movie").setSource(mapping)
				.setType(type).execute().actionGet();

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SearchResponse response = EsClient.search("movie",
				MD5Utils.createMD5("魔境仙踪[国语高清]"));
		System.out.println(response.toString());
		// response.getHits().getTotalHits();
		// createIndexAndMapping("movie", "0", mapping);
		GetResponse get = client.prepareGet("movie", "0", "魔境仙踪[国语高清]")
				.execute().actionGet();

		System.out.println(get.toString());
	}

}
