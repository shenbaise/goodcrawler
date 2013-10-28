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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.bootstrap.foreman.FetchForeman;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;
import org.sbs.goodcrawler.fetcher.CustomFetchStatus;
import org.sbs.goodcrawler.fetcher.Fetcher;
import org.sbs.goodcrawler.fetcher.PageFetchResult;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.job.Parser;
import org.sbs.goodcrawler.urlmanager.WebURL;
import org.sbs.util.UrlUtils;

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
	 * 选择器提取的URL
	 */
	private String url = "";
	private Parser parser = new Parser(false);
	private UrlUtils urlUtils = new UrlUtils();
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
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Object> getContent() throws ExtractException{
		if (null != content && !newDoc) {
			return content;
		}
		// 获取连接，然后拿到document。
		if(StringUtils.isNotBlank(this.url) && !newDoc){
			return content;
		}
		
		// 抽取document中对应的Selector
		if (super.document != null) {
			Elements elements = super.document.select(value);
			if(elements.isEmpty())
				return null;
			switch ($Attr) {
			case text:
				this.url = elements.first().text();
				break;
			default:
				this.url = elements.first().attr(attr);
				break;
			}
		}
		if(StringUtils.isNotBlank(this.url)){
			Document doc = null;
			try {
				WebURL webUrl = new WebURL();
				webUrl.setURL(this.url);
				PageFetchResult result = FetchForeman.fetcher.fetchHeader(webUrl);
				// 获取状态
				int statusCode = result.getStatusCode();
				if (statusCode == CustomFetchStatus.PageTooBig) {
					return null;
				}
				if (statusCode != HttpStatus.SC_OK){
					return null;
				}else {
					Page page = new Page(webUrl);
					if (!result.fetchContent(page)) {
						return null;
					}
					if (!parser.parse(page, webUrl.getURL())) {
						return null;
					}
				doc = Jsoup.parse(new String(page.getContentData(),page.getContentCharset()), urlUtils.getBaseUrl(page.getWebURL().getURL()));
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new ExtractException(e.getMessage());
			}
					
			content = Maps.newHashMap();
			if(selectors!=null)
			for(ElementCssSelector<?> selector :selectors){
				if(selector instanceof FileElementCssSelector){
					Map<String, Object> m = ((FileElementCssSelector)selector).setResult(content)
							.setDocument(doc)
							.getContentMap();
					if((null==m || m.size()==0) && selector.isRequired()){
						return null;
					}else {
						if(null!=m && m.size()>0)
							content.putAll(m);
					}
				}else{
					Map<String, Object> m = selector.setDocument(doc).getContentMap();
					if((null==m || m.size()==0) && selector.isRequired()){
						return null;
					}else {
						if(null!=m && m.size()>0)
							content.putAll(m);
					}
				}
			}
			return content;
		}
		newDoc = false;
		return null;
	}
	
	@Override
	public Map<String, HashMap<String, Object>> getContentMap() throws ExtractException{
		if(newDoc){
			getContent();
		}
		if(content == null || content.size()==0)
			return null;
		HashMap<String, HashMap<String, Object>> map = new HashMap<>(1);
		map.put(name, content);
		return map;
	}

	@Override
	public void addAction(SelectorAction action) {
		
	}
}
