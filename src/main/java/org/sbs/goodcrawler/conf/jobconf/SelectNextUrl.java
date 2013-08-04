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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sbs.util.MapUtils;

import com.google.common.collect.Maps;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 
 */
public class SelectNextUrl {
	private Selector refSelector;
	private Selector selector;
	
	public SelectNextUrl(){}
	
	public SelectNextUrl(Selector selector) {
		super();
		this.selector = selector;
	}

	/**
	 * 处理Ulr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> process(Document doc){
		HashMap<String,Object> result = Maps.newHashMap();
		try {
			Object object = refSelector.process(doc);
			if(object!=null){
				Set<String> urls =  (Set<String>) object;
				for(String url:urls){
					if(StringUtils.isNotBlank(url)){
						Document d = Jsoup.parse(new URL(url), 10000);
						HashMap<String, Object> x = selector.process2(d);
						result = MapUtils.mager(result, x);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Selector getRefSelector() {
		return refSelector;
	}

	public void setRefSelector(Selector refSelector) {
		this.refSelector = refSelector;
	}

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}
}
