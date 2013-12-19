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

import java.util.Map;

import org.sbs.goodcrawler.extractor.GCElement;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @param <E>
 * @desc html元素选择器。支持xpath、ID两种方式进行提取。 <br>
 *       主要用于提取<code>javascript</code>生成的内容以及由ajax调用生成的内容。 <br>
 *       <b>注意</b><i>除非有<code>javascript</code>生成的内容需要抓取，否则强烈不建议使用该元素</i>
 */
public abstract class AbstractHtmlElement<E> implements GCElement {
	/**
	 * webClient
	 */
	protected WebClient webClient;
	/**
	 * 页面
	 */
	protected HtmlPage page;
	/**
	 * 选择器名称
	 */
	protected String name;
	/**
	 * 选择器值，对应xpath.
	 */
	protected String value;
	/**
	 * 类型<i>anchor,button,embed,form,image,input....</i>
	 */
	protected String type;
	/**
	 * 动作<i>click,submit,dbclick...</i>
	 */
	protected String action;
	/**
	 * new page
	 */
	protected boolean newPage = true;
	/**
	 * is required
	 */
	protected boolean isRequired;

	/**
	 * 构造器
	 */
	public AbstractHtmlElement() {
	}

	/**
	 * 构造器
	 * 
	 * @param webClient
	 * @param page
	 * @param name
	 * @param value
	 * @param type
	 * @param isRequired
	 */
	public AbstractHtmlElement(WebClient webClient, HtmlPage page, String name,
			String value, String type, boolean isRequired) {
		super();
		this.webClient = webClient;
		this.page = page;
		this.name = name;
		this.value = value;
		this.type = type;
		this.isRequired = isRequired;
	}

	/**
	 * 返回内容
	 * 
	 * @return
	 */
	public abstract E getContent();

	public abstract Map<String, E> getContentMap();

	public HtmlPage getPage() {
		return page;
	}

	public void setPage(HtmlPage page) {
		this.page = page;
		this.newPage = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public WebClient getWebClient() {
		return webClient;
	}

	public void setWebClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNewPage() {
		return newPage;
	}

	public void setNewPage(boolean newPage) {
		this.newPage = newPage;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public void setNewPage() {
		this.newPage = true;
	}
}
