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
package org.sbs.goodcrawler.extractor.selector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author whiteme
 * @date 2013年10月13日
 * @desc Url类别的选择器。该类别选择器抽取到Url后会进一步抓取该Url内容并根据配置进行再一次抽取。
 */
public class UrlElementCssSelector extends ElementCssSelector<HashMap<String, Object>> {
	/**
	 * 该Url选择器下的选择器
	 */
	List<ElementCssSelector<?>> selectors = Lists.newArrayList();
	/**
	 * 该Url选择器下提取到的内容
	 */
	HashMap<String, Object> content;
	/**
	 * 返回该Url选择器下子选择器提取到的内容
	 */
	public UrlElementCssSelector() {
		super();
	}

	public UrlElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}
	
	
	public List<ElementCssSelector<?>> getSelectors() {
		return selectors;
	}

	public void setSelectors(List<ElementCssSelector<?>> selectors) {
		this.selectors = selectors;
	}
	
	public void addSelector(ElementCssSelector<?> selector){
		this.selectors.add(selector);
	}
	@Override
	public HashMap<String, Object> getContent() throws ExtractException{
		if (null != content && !newDoc) {
			return content;
		}
		content = Maps.newHashMap();
		if(selectors!=null)
		for(ElementCssSelector<?> selector :selectors){
			selector.setDocument(document);
			content.put(selector.getName(), selector.setDocument(document).getContent());
		}
		newDoc = false;
		return content;
	}
	
	@Override
	public Map<String, HashMap<String, Object>> getContentMap() throws ExtractException{
		if(null==content){
			getContent();
		}
		if(content == null)
			return null;
		HashMap<String, HashMap<String, Object>> map = new HashMap<>(1);
		map.put(name, content);
		return map;
	}

	@Override
	public void addAction(SelectorAction action) {
		
	}
}
