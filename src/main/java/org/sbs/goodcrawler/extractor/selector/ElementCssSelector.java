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

import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @param <T>
 * @date 2013年10月10日
 * @desc 元素选择器。基础属性包括名称、css选择器|xpath、属性、是否是必须的required
 */
@SuppressWarnings("rawtypes")
public abstract class ElementCssSelector<T> {
	/**
	 * 选择器名称
	 */
	protected String name;
	/**
	 * css selector
	 */
	protected String value;
	/**
	 * 选择器的属性，img、src、text等
	 */
	protected String attr;
	/**
	 * attr对应的SelectorAttr枚举
	 */
	protected SelectorAttr $Attr;
	/**
	 * 是否required
	 */
	protected boolean isRequired;
	/**
	 * 选择器要处理的文档
	 */
	protected Document document;
	/**
	 * 子选择器
	 */
	protected List<ElementCssSelector> childSelectors = Lists.newArrayList();
	/**
	 * 为true表示需要处理新的document<br>
	 * false表示该document已经处理过
	 */
	protected boolean newDoc = true;
	/**
	 * 构造器
	 */
	public ElementCssSelector(){};
	
	/**
	 * 构造器
	 * @param name
	 * @param value
	 * @param atrr
	 * @param isRequired
	 * @param document
	 */
	public ElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super();
		this.name = name;
		this.value = value;
		this.attr = attr;
		this.$Attr = org.apache.commons.lang3.EnumUtils.getEnum(SelectorAttr.class, this.attr);
		this.isRequired = isRequired;
	}
	
	/**
	 * 获取该选择器从文档中抽取的内容
	 * @return
	 */
	protected abstract T getContent();
	/**
	 * 获取该选择器从文档中抽取的内容，其中k为选择器名称，即name属性。v是选择器抽取到的内容。
	 * @return
	 */
	protected abstract Map<String, T> getContentMap();

	
	public String getName() {
		return name;
	}

	public ElementCssSelector setName(String name) {
		this.name = name;
		return this;
	}

	public String getValue() {
		return value;
	}

	public ElementCssSelector setValue(String value) {
		this.value = value;
		return this;
	}

	public String getAttr() {
		return attr;
	}

	public ElementCssSelector setAttr(String attr) {
		this.attr = attr;
		return this;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public ElementCssSelector setRequired(boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}

	public Document getDocument() {
		return document;
	}

	public ElementCssSelector setDocument(Document document) {
		this.document = document;
		this.newDoc = true;
		return this;
	}

	public List<ElementCssSelector> getChildSelectors() {
		return childSelectors;
	}

	public void setChildSelectors(List<ElementCssSelector> childSelectors) {
		this.childSelectors = childSelectors;
	}
	
	public void addChildSelector(ElementCssSelector selector){
		this.childSelectors.add(selector);
	}

	public SelectorAttr get$Attr() {
		return $Attr;
	}

	public ElementCssSelector set$Attr(SelectorAttr $Attr) {
		this.$Attr = $Attr;
		return this;
	}

	public ElementCssSelector setNewDoc(boolean newDoc) {
		this.newDoc = newDoc;
		return this;
	}
	
	/**
	 * 重置标记，对内容需要重新处理，或者document已经更新
	 */
	protected void isNewDoc(){
		this.newDoc = true;
	}
	
}
