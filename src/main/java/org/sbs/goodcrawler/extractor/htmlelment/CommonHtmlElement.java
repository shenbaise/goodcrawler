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
package org.sbs.goodcrawler.extractor.htmlelment;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.sbs.goodcrawler.fetcher.AjaxCallFetcher;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc 通用的HtmlElement提取器
 */
public class CommonHtmlElement extends AbstractHtmlElement<Object> {
	
	private AjaxCallFetcher fetch = new AjaxCallFetcher();
	
	private Object content;
	
	@Override
	public Object getContent() {
		if(page!=null){
			if (null != content && !newPage) {
				return content;
			}
			
			if (type.equals(HtmlElementExtractType.xpath)) {
				Object o = fetch.getElement(page, value);
				this.content = o;
				return this.content;
			}else {
				try {
					new Exception("需要使用xpath");
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取异步调用的URL
	 * @return
	 */
	public URL getAjaxUrl(){
		if(null!=content && !newPage){
			this.content = fetch.getAjaxCallUrl(page, value, type, action);
			return (URL) this.content;
		}
		return null;
	}
	
	@Override
	public Map<String, Object> getContentMap() {
		if(newPage){
			getContent();
		}
		if(null==content)
			return null;
		Map<String, Object> m = new HashMap<String, Object>(1);
		m.put(name, this.content);
		return m;
	}
}
