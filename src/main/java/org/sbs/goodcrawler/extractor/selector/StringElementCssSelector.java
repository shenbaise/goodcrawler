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

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;
import org.sbs.goodcrawler.extractor.selector.action.StringSelectorAction;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年10月11日
 * @desc String 类型的选择器
 */
public class StringElementCssSelector extends AbstractElementCssSelector<String> {
	
	private String content;
	private List<StringSelectorAction> actions = Lists.newArrayList();
	
	public StringElementCssSelector() {
		super();
	}

	public StringElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}

	/**
	 * 提取内容，并根据Action对内容做加工
	 */
	@Override
	public String getContent() throws ExtractException{
		try {
			// 同一个文档的2+次调用不用重新计算。
			if(StringUtils.isNotBlank(this.content) && !newDoc){
				return content;
			}
			// 抽取document中对应的Selector
			if (super.document != null) {
				Elements elements = super.document.select(value);
				if(elements.isEmpty())
					return null;
				StringBuilder sb = new StringBuilder();
				switch ($Attr) {
				case text:
					for (Element e : elements) {
						sb.append(e.text()).append("\n");
					}
					break;
				default:
					for (Element e : elements) {
						sb.append(e.attr(attr));
					}
					break;
				}
				if(StringUtils.isNotBlank(sb)){
					if(null!=actions && actions.size()>0){
						String temp = sb.substring(0,sb.length()-1);
						for(StringSelectorAction action:actions){
							temp = action.doAction(temp);
						}
						this.content = temp;
					}else {
						this.content = sb.substring(0, sb.length()-1);
					}
					newDoc = false;
					return this.content;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExtractException(StringElementCssSelector.class.getSimpleName()+"信息提取错误:"+e.getMessage());
		}
		return "";
	}

	@Override
	public Map<String, String> getContentMap() throws ExtractException{
		if(newDoc){
			getContent();
		}
		if(StringUtils.isBlank(this.content))
			return null;
		Map<String, String> m = new HashMap<String, String>(1);
		m.put(name, this.content);
		return m;
	}

	public List<StringSelectorAction> getActions() {
		return actions;
	}

	public void setActions(List<StringSelectorAction> actions) {
		this.actions = actions;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void addAction(SelectorAction action) {
		this.actions.add((StringSelectorAction) action);
	}
	
}
