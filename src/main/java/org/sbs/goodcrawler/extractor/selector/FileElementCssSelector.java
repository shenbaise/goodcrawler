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
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.action.FileSelectAction;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年10月20日
 * @desc 该选择元素需要配置相应的Action完成文件下载功能。该选择元素本身仅返回网页中相应的文本信息。
 * </br>该文本信息应该是一个Url格式的字符串，指向某个网络文件。
 */
public class FileElementCssSelector extends ElementCssSelector<String> {
	
	private String content;
	
	private List<FileSelectAction> actions = Lists.newArrayList();
	
	public FileElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}
	
	private Map<String, Object> result = null;
	
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
				switch ($Attr) {
				case text:
					this.content = elements.first().text();
					break;
				default:
					this.content = elements.first().attr(attr);
					break;
				}
				newDoc = false;
				if(null!=actions && actions.size()>0 && StringUtils.isNotBlank(content)){
					String temp = this.content;
					for(FileSelectAction action:actions){
						temp = action.doAction(result, temp);
					}
					this.content = temp;
				}else {
					if(StringUtils.isNotBlank(content))
						return content;
					else {
						return null;
					}
				}
				return this.content;
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
		if(StringUtils.isBlank(content))
			return null;
		Map<String, String> m = new HashMap<String, String>(1);
		m.put(name, this.content);
		return m;
	}

	public List<FileSelectAction> getActions() {
		return actions;
	}

	public void setActions(List<FileSelectAction> actions) {
		this.actions = actions;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void addAction(SelectorAction action) {
		this.actions.add((FileSelectAction) action);
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public FileElementCssSelector setResult(Map<String, Object> result) {
		this.result = result;
		return this;
	}
	
}
