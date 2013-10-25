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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.action.IntegerSelectorAction;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年10月13日
 * @desc
 */
public class NumericaElementCssSelector extends ElementCssSelector<Number> {

	NumberFormat format;
	Number content;
	private List<IntegerSelectorAction> actions = Lists.newArrayList();

	public NumericaElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}
	
	public NumericaElementCssSelector(String name, String value, String attr,
			boolean isRequired,String parttern) {
		super(name, value, attr, isRequired);
		format = new DecimalFormat(parttern);
	}
	
	@Override
	public Number getContent() throws ExtractException{
		try {
			// 如果content不为空且不是新文档，则表示是同一个document的2+次调用，不用重新计算
			if (null != content && !newDoc) {
				return content;
			}
			if (null != document) {
				Elements elements = super.document.select(value);
				if (elements.isEmpty())
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

				if (StringUtils.isNotBlank(temp)) {
					content = NumberUtils.createNumber(temp);
					if(null!=actions){
						for(IntegerSelectorAction action:actions){
							this.content = action.doAction((Integer) content);
						}
					}
					newDoc = false;
					return content;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExtractException("信息提取错误:"+e.getMessage());
		}
		return null;
	}


	@Override
	public Map<String, Number> getContentMap() throws ExtractException{
		if(newDoc){
			getContent();
		}
		if(null==content)
			return null;
		Map<String, Number> m = new HashMap<String, Number>(1);
		m.put(name, this.content);
		return m;
	}
	
	/**
	 * 返回字符
	 * @return
	 */
	public String getContentString() throws ExtractException{
		if(null==content && newDoc){
			getContent();
		}
		return format.format(this.content);
	}

	public NumberFormat getFormat() {
		return format;
	}

	public void setFormat(NumberFormat format) {
		this.format = format;
	}

	public List<IntegerSelectorAction> getActions() {
		return actions;
	}

	public void setActions(List<IntegerSelectorAction> actions) {
		this.actions = actions;
	}
	
	public void addAction(IntegerSelectorAction action){
		this.actions.add(action);
	}
	
	public static void main(String[] args) {
		
	}

	@Override
	public void addAction(SelectorAction action) {
		this.actions.add((IntegerSelectorAction) action);
	}
}
