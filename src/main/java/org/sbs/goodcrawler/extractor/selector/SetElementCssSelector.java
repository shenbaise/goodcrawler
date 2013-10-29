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
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;
import org.sbs.goodcrawler.extractor.selector.action.StringSelectorAction;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author whiteme
 * @param <T>
 * @date 2013年10月13日
 * @desc 将以Set的方式返回提取的信息内容
 */
public class SetElementCssSelector extends ElementCssSelector<Set<String>> {
	
	private Set<String> content;
	private List<StringSelectorAction> actions = Lists.newArrayList();
	public SetElementCssSelector() {
		super();
	}

	public SetElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}

	@Override
	public Set<String> getContent() throws ExtractException{
		try {
			if(null!=content && !newDoc){
				return content;
			}
			content = Sets.newHashSet();
			if(document!=null){
				Elements elements = super.document.select(value);
				if(elements.isEmpty())
					return null;
				switch ($Attr) {
				case text:
					for (Element e : elements) {
						content.add(e.text());
					}
					break;
				default:
					for (Element e : elements) {
						content.add(e.attr(attr));
					}
					break;
				}
				if(null!=actions && actions.size()>0){
					Set<String> newSet = Sets.newTreeSet();
					for(String string : content){
						String temp = "";
						for(StringSelectorAction action:actions){
							temp = action.doAction(temp);
						}
						this.content = temp;
					}
				}
				newDoc = false;
				return content;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExtractException(SetElementCssSelector.class.getSimpleName()+"信息提取错误:"+e.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, Set<String>> getContentMap() throws ExtractException{
		if(newDoc)
			getContent();
		if(content == null || content.size()==0)
			return null;
		Map<String, Set<String>> map = new HashMap<>(1);
		map.put(name, this.content);
		return map;
	}

	@Override
	public void addAction(SelectorAction action) {
		this.actions.add((StringSelectorAction) action);
	}
	
	public List<StringSelectorAction> getActions() {
		return actions;
	}

	public void setActions(List<StringSelectorAction> actions) {
		this.actions = actions;
	}
}
