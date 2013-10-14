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
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.extractor.selector.action.IntegerSelectorAction;

import com.google.common.base.CharMatcher;

/**
 * @author whiteme
 * @date 2013年10月13日
 * @desc 整型抽取器，如果抽取内容不正确则返回null
 */
public class IntegerElementCssSelector extends ElementCssSelector<Integer> {
	
	private Integer content;
	private List<IntegerSelectorAction> actions;
	
	public IntegerElementCssSelector() {
		super();
	}

	public IntegerElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}

	@Override
	protected Integer getContent() {
		// 如果content不为空且不是新文档，则表示是同一个document的2+次调用，不用重新计算
		if(null!=content && !newDoc){
			return content;
		}
		if(null!=document){
			Elements elements = super.document.select(value);
			if(elements.isEmpty())
				return null;
			String temp;
			switch ($Attr) {
			case text:
				temp = CharMatcher.DIGIT.retainFrom(elements.text());
				break;
			default:
				temp = CharMatcher.DIGIT.retainFrom(elements.attr(attr));
				break;
			}
			
			if(StringUtils.isNotBlank(temp)){
				Integer integer = Integer.parseInt(temp);
				if(null!=actions){
					for(IntegerSelectorAction action:actions){
						this.content = action.doAction(integer);
					}
				}else {
					this.content = integer;
				}
				return content;
			}
		}
		return null;
	}
	
	/**
	 * 如果content为空，且是新文档，则重新计算。
	 */
	@Override
	protected Map<String, Integer> getContentMap() {
		if(null==content && newDoc){
			getContent();
		}
		Map<String, Integer> m = new HashMap<String, Integer>(1);
		m.put(name, this.content);
		return m;
	}

	public List<IntegerSelectorAction> getActions() {
		return actions;
	}

	public void setActions(List<IntegerSelectorAction> actions) {
		this.actions = actions;
	}

	public void setContent(Integer content) {
		this.content = content;
	}
	
}
