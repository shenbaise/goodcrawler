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

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.extractor.selector.action.ListSelectorAction;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @param <T>
 * @date 2013年10月13日
 * @desc 将以List<?>的方式返回提取的信息内容
 */
public class ListElementCssSelector extends ElementCssSelector<List<String>> {
	
	private List<String> contenList;
	
	private List<ListSelectorAction> actions;
	
	public ListElementCssSelector(){}
	
	public ListElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<String> getContent() {
		if(null!=contenList && !newDoc){
			return contenList;
		}
		contenList = Lists.newArrayList();
		if(document!=null){
			Elements elements = super.document.select(value);
			if(elements.isEmpty())
				return null;
			switch ($Attr) {
			case text:
				for (Element e : elements) {
					contenList.add(e.text());
				}
				break;
			default:
				for (Element e : elements) {
					contenList.add(e.attr(attr));
				}
				break;
			}
			if(actions!=null&&actions.size()>0){
				for(ListSelectorAction action:actions){
					contenList = (List<String>) action.doAction(contenList);
				}
			}
			return contenList;
		}
		return null;
	}

	@Override
	protected Map<String, List<String>> getContentMap() {
		if(contenList==null && newDoc)
			getContent();
		Map<String, List<String>> map = new HashMap<>(1);
		map.put(name, this.contenList);
		return null;
	}
	
}
