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
package org.sbs.goodcrawler.conf.jobconf;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.sbs.util.MapUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 
 */
public class Template {
	private List<String> urls = Lists.newArrayList();
	private List<Selector> selectors ;
	private List<Pattern> extractFilter = Lists.newArrayList();
	private boolean giveup = false;
	
	public Template(){}
	
	public Template(List<String> urls, List<Selector> selectors) {
		super();
		this.urls = urls;
		this.selectors = selectors;
		for(String p:urls){
			extractFilter.add(Pattern.compile(p));
		}
	}
	/**
	 * 模板是否匹配
	 * @param url
	 * @return
	 */
	public boolean matches(String url){
		if(null==extractFilter||extractFilter.size()==0){
			return true;
		}
		for(Pattern p:extractFilter){
			if(p.matcher(url).matches()){
				giveup = true;
				return true;
			}
		}
		return false;
	}
	/**
	 * 执行抽取
	 * @param doc
	 * @return
	 */
	public HashMap<String, Object> extract(Document doc){
		giveup = false;
		HashMap<String, Object> result = Maps.newHashMap();
		for(Selector selector :selectors){
			HashMap<String, Object> m = selector.processAll(doc);
			if(selector.isGiveup()){
				giveup = true;
				return null;
			}
			result = MapUtils.mager(result, m);
		}
		return result;
	}
	
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	public List<Selector> getSelectors() {
		return selectors;
	}
	public void setSelectors(List<Selector> selectors) {
		this.selectors = selectors;
	}

	public boolean isGiveup() {
		return giveup;
	}

	public void setGiveup(boolean giveup) {
		this.giveup = giveup;
	}
	
}
