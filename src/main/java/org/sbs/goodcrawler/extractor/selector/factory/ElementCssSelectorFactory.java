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
package org.sbs.goodcrawler.extractor.selector.factory;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.extractor.selector.DateElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.AbstractElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.FileElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.IntegerElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.ListElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.NumericaElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.SelectorType;
import org.sbs.goodcrawler.extractor.selector.SetElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.StringElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.PageElementSelector;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;
import org.sbs.goodcrawler.extractor.selector.action.string.ActionFactory;

/**
 * @author whiteme
 * @date 2013年10月17日
 * @desc css选择器工厂类
 */
public class ElementCssSelectorFactory {
	
	/**
	 * 创建各种选择器 
	 * @param name
	 * @param type
	 * @param value
	 * @param attr
	 * @param isRequired
	 * @param regex
	 * @return
	 */
	private static AbstractElementCssSelector<?> create(String name, String type,String value, String attr,
			boolean isRequired,int index,String regex,String pattenr){
		SelectorType $type = SelectorType.valueOf("$"+type);
		switch ($type) {
		case $int:
			return new IntegerElementCssSelector(name, value, attr, isRequired, index,regex);
		case $string:
			return new StringElementCssSelector(name, value, attr, isRequired, index,regex);
		case $list:
			return new ListElementCssSelector(name, value, attr, isRequired, index,regex);
		case $set:
			return new SetElementCssSelector(name, value, attr, isRequired, index,regex);
		case $url:
			return new PageElementSelector(name, value, attr, isRequired, index,regex);
		case $numerica:
			return new NumericaElementCssSelector(name, value, attr, isRequired, index,regex);
		case $date:
			return new DateElementCssSelector(name, value, attr, isRequired, index,regex,pattenr);
		case $file:
			return new FileElementCssSelector(name, value, attr, isRequired, index,regex);
		case $ajax:
//			return new AjaxElementCssSelector(name, value, attr, isRequired);
		default:
			return new StringElementCssSelector(name, value, attr, isRequired, index,regex);
		}
	}
	/**
	 * 构造器<b>该方法对Element不做检测，传递的Element必须是描述select的元素
	 * @param element
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static AbstractElementCssSelector create(Element element){
		String name = element.attr("name");
		String value = element.attr("value");
		String type = element.attr("type");
		String attr = element.attr("attr");
		String pattern = element.attr("pattern");
		String regex = element.attr("regex");
		String required = element.attr("required");
		String sIndex = element.attr("index");
		boolean isRequired = false;
		if(StringUtils.isNotBlank(required)){
			isRequired = Boolean.parseBoolean(required);
		}
		int index = 0;
		if(StringUtils.isNotBlank(sIndex)){
			index = Integer.parseInt(sIndex);
		}
		AbstractElementCssSelector selector = ElementCssSelectorFactory.create(name, type, value, attr, isRequired,index,regex,pattern);
		// 检测子元素
		Elements children = element.children();
		for(Element e : children){
			if("action".equals(e.tagName())){
				SelectorAction action = ActionFactory.create(e, element.attr("type"));
				if(action!=null)
					selector.addAction(action);
			}
			// 只有Url类型的选择器嵌套自选择器
			else if("element".equals(e.tagName())){
				((PageElementSelector)selector).addSelector(create(e));
			}
		}
		return selector;
	}
}
