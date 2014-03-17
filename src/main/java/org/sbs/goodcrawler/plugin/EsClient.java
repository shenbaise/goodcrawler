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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.delete.DeleteResponse;
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
	// .put("cluster.name", "ES-index")
			.put("client.transport.sniff", true).build();

	private static Client client = null;
	
	private static String mapping = null;

	static {
		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						"127.0.0.1", 9300));
		// 获取mapping
		try {
			File m = new File("src/main/resources/mapping.json");
			mapping = FileUtils.readFileToString(m, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * index data,md5(url)作为id
	 * @param index
	 * @param type
	 * @param data
	 */
	public static void index(String index, String type, Map<String, Object> data) {
		try {
			XContentBuilder xBuilder = jsonBuilder().startObject();
			Set<Entry<String, Object>> sets = data.entrySet();
			for(Entry<String, Object> entry:sets){
				xBuilder.field(entry.getKey()).value(entry.getValue());
			}
			xBuilder.endObject();
					client.prepareIndex(index, type)
					.setId(MD5Utils.createMD5((String)data.get("url")))
					.setSource(xBuilder).execute();
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 使用指定的id建立索引
	 * @param index
	 * @param type
	 * @param id
	 * @param data
	 */
	public static void index(String index, String type, String id, Map<String, Object> data) {
		try {
			XContentBuilder xBuilder = jsonBuilder().startObject();
			Set<Entry<String, Object>> sets = data.entrySet();
			for(Entry<String, Object> entry:sets){
				xBuilder.field(entry.getKey()).value(entry.getValue());
			}
			xBuilder.endObject();
					client.prepareIndex(index, type)
					.setId(id)
					.setSource(xBuilder).execute().get();
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 使用自动生成的id建立索引
	 * @param index
	 * @param type
	 * @param data
	 */
	public static void indexAutoId(String index, String type, Map<String, Object> data) {
		try {
			XContentBuilder xBuilder = jsonBuilder().startObject();
			Set<Entry<String, Object>> sets = data.entrySet();
			for(Entry<String, Object> entry:sets){
				xBuilder.field(entry.getKey()).value(entry.getValue());
			}
			xBuilder.endObject();
					client.prepareIndex(index, type)
					.setSource(xBuilder).execute();
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Index
	 * @param index
	 * @param type
	 * @param id
	 * @param json
	 */
	public static void index(String index, String type, String id,String json) {
		try {
			client.prepareIndex(index, type)
			.setId(id)
			.setSource(json).execute().actionGet();
		} catch (ElasticSearchException e) {
			e.printStackTrace();
		} 
	}
	/**
	 * 删除索引中对应id的文档
	 * @param index
	 * @param id
	 */
	public static void delete(String index,String type,String id){
		try {
			DeleteResponse r = client.prepareDelete(index, type, id).execute()
			.get();
			System.out.println(r.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查找指定id的文档
	 * @param index
	 * @param id
	 * @return
	 */
	public static SearchResponse search(String index,String id){
		SearchResponse response = client.prepareSearch(index)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(termQuery("_id", id))
		        .setFrom(0).setSize(10).setExplain(false)
		        .execute()
		        .actionGet();
		return response;
	}
	
	/**
	 * 关闭客户端连接
	 */
	public static void distroy() {
		client.close();
	}
	
	/**
	 * mapping
	 * @return
	 */
	public XContentBuilder getMapping(){
		try {
			XContentBuilder mapping = jsonBuilder().startObject()
	        		.startObject("movie")
	        				.startObject("_source")
    						.field("enabled" , "true")
    						.field("compress", true)
//		        						.field("compress_threshold", "200b")
	        				.endObject()
	        				.startObject("_all")
	        						.field("enabled" , "true")
	        				.endObject()
	        				
	        				.startObject("_index")
	        						.field("enabled" , "false")
	        				.endObject()
	        				
	        				.startObject("_type")
	        					.field("index", "yes")
	        					.field("store", "yes")
	        					.field("index", "not_analyzed")
	        				.endObject()
	        				
	        				.startObject("_id")
	        					.field("index", "yes")
	        					.field("store", "yes")
	        					.field("index", "not_analyzed")
	        				.endObject()
	        				
	        				 .startObject("_analyzer").field("path", "field_analyzer").endObject()
	        				
	                        .startObject("properties") 
	                                .startObject("pm") 
	                                        .field("type", "string") 
	                                .endObject() 
	                                
	                                .startObject("nd") 
	                                        .field("type", "integer") 
	                                        .field("omit_norms","yes")
	                                        .field("omit_term_freq_and_positions","yes")
	                                        .field("index", "not_analyzed")
	                                .endObject()
	                                
	                                .startObject("ym") 
	                                        .field("type", "string")
	                                .endObject()
	                                
	                                .startObject("url")
	                                	.field("type","string")
	                                	.field("index", "no")
	                                	.field("store", "yes")
	                                .endObject()
	                                
	                        .endObject() 
	                .endObject()
	                .endObject();
			
			return mapping;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 设置mapping
	 */
	public void putMapping(){
		try {
			client.admin().indices().preparePutMapping("movie")
			.setType("0").setSource(mapping).execute().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置mapping
	 * @param index
	 * @param type
	 * @param file
	 */
	public void putMappingFile(String index,String type,File file){
		try {
			String m = FileUtils.readFileToString(file, "utf-8");
			client.admin().indices().preparePutMapping("moive").setType("0")
			.setSource(m).execute().get();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建索引并设置mapping
	 * @param index
	 * @param type
	 * @param mapping
	 */
	public static void createIndexAndMapping(String index,String type,String mapping){
//		mapping = "{  \"0\": {    \"_all\": {      \"enabled\": true    },    \"index_analyzer\": \"ik\",    \"search_analyzer\": \"ik\",    \"_timestamp\": {      \"enabled\": true,      \"format\": \"YYYY-MM-dd\"    },    \"dynamic_templates\": [      {        \"string_template\": {          \"match\": \"*\",          \"mapping\": {            \"type\": \"string\",            \"index\": \"not_analyzed\"          },          \"match_mapping_type\": \"string\"        }      }    ],    \"properties\": {      \"title\": {        \"type\": \"string\",        \"include_in_all\": true,        \"index\": \"analyzed\"      },      \"actors\": {        \"type\": \"string\",        \"include_in_all\": true,        \"index\": \"analyzed\"      },      \"director\": {        \"type\": \"string\",        \"include_in_all\": true,        \"index\": \"analyzed\"      },      \"summary\": {        \"type\": \"string\",        \"include_in_all\": false,        \"index\": \"not_analyzed\"      },      \"type\": {        \"type\": \"string\",        \"include_in_all\": true,        \"index\": \"analyzed\"      },      \"category\": {        \"type\": \"string\",        \"include_in_all\": true,        \"index\": \"analyzed\"      }    }  }}";
//			client.prepareIndex(index, type).execute().actionGet();
			client.admin().indices().preparePutMapping("movie").setType(type)
//			.setSource(mapping)
			.setSource(mapping)
			.execute().actionGet();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		SearchResponse response = EsClient.search("movie", "魔境仙踪[国语高清]");
//		System.out.println(response.toString());
//		response.getHits().getTotalHits();
		try {
			client.admin().indices().prepareDeleteMapping("movie").setType("0").execute().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		createIndexAndMapping("movie", "0", mapping);
//		GetResponse get =client.prepareGet("movie", "0","魔境仙踪[国语高清]" )
//		.execute()
//		.actionGet();
		
//		System.out.println(get.toString());
//		delete("update", "0", "72947ecbd3c8125df908dc9340b10551");
		
		
	}

}
