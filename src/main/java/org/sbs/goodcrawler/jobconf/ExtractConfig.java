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
package org.sbs.goodcrawler.jobconf;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.Configuration;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.extractor.selector.ElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.IFconditions;
import org.sbs.goodcrawler.extractor.selector.factory.ElementCssSelectorFactory;

import com.google.common.collect.Lists;


/**
 * @author whiteme
 * @date 2013年10月13日
 * @desc 内容抽取配置对象
 */
//@SuppressWarnings("rawtypes")
public class ExtractConfig extends Configuration {
	/**
	 * 默认使用个线程提取信息
	 */
	private int threadNum = 10;
	/**
	 * 抽取信息的模板列表
	 */
	private List<ExtractTemplate> templates = Lists.newArrayList();
	
	/**
	 * 从配置文件中加载抽取配置信息
	 * @param doc
	 * @return
	 * @throws ConfigurationException
	 */
	private ExtractConfig loadConfig(Document doc) throws ConfigurationException{
		Elements extractElement = doc.select("extract");
		String temp = extractElement.select("threadNum").text();
		
		if(StringUtils.isNotBlank(temp)){
			this.threadNum = Integer.parseInt(temp);
		}
		
		Elements templateElement = extractElement.select("extract").select("template");
		Iterator<Element> it = templateElement.iterator();
		while(it.hasNext()){
			Element template = it.next();
			ExtractTemplate extractTemplate = new ExtractTemplate();
			// 模板对应的Url规则，满足其一就使用该模板进行提取
			Elements urlPatternElement = template.select("url");
			List<Pattern> patterns = Lists.newArrayList();
			for(Element urlElement :urlPatternElement){
				patterns.add(Pattern.compile(urlElement.text()));
			}
			extractTemplate.setUrlPattern(patterns);
			
			// 提取元素
			Elements selectElement = template.select("elements").first().children();
			for(Element element:selectElement){
				if("element".equals(element.tag())){
					String name = element.attr("name");
					String value = element.attr("value");
					String type = element.attr("type");
					String attr = element.attr("attr");
					String required = element.attr("required");
					boolean isRequired = true;
					if(StringUtils.isNotBlank(required)){
						isRequired = Boolean.parseBoolean(required);
					}
					ElementCssSelector selector = ElementCssSelectorFactory.create(name, type, value, attr, isRequired);
					extractTemplate.addCssSelector(selector);
					
				}else if("if".equals(element.tag())){
					
				}
			}
			
			List<Node> nodes = template.childNodes();
			Node elementsNode = nodes.get(5);
			List<Node> cnodes = elementsNode.childNodes();
			
			System.out.println("...");
		}
		
		return this;
	}
	
	@Override
	public String toString() {
		return null;
	}
	
	
	public static void main(String[] args) {
		ExtractConfig extractConfig = new ExtractConfig();
		Document document;
		try {
			document = Jsoup.parse(new File("conf/youku_conf.xml"), "utf-8");
			extractConfig.loadConfig(document);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}

/**
 * 
 * @author whiteme
 * @date 2013年10月15日
 * @desc 抽取模板，一个提取任务可以拥有多个收取模板
 */
@SuppressWarnings("rawtypes")
class ExtractTemplate{
	/**
	 * 模板名称
	 */
	private String name;
	/**
	 * 该模板对应的模板模式，如果没有设置则，对所有页面以次模板提取信息
	 */
	private List<Pattern> urlPattern = Lists.newArrayList();
	/**
	 * 该模板对应的css选择器，使用jsoup进行提取。
	 */
	private List<ElementCssSelector> cssSelectors = Lists.newArrayList();
	/**
	 * 条件分支
	 */
	private List<IFconditions> conditions = Lists.newArrayList();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Pattern> getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(List<Pattern> urlPattern) {
		this.urlPattern = urlPattern;
	}
	
	public void addUrlPattern(Pattern urlPattern){
		this.urlPattern.add(urlPattern);
	}

	public List<ElementCssSelector> getCssSelectors() {
		return cssSelectors;
	}
	
	public void setCssSelectors(List<ElementCssSelector> cssSelectors) {
		this.cssSelectors = cssSelectors;
	}
	
	public void addCssSelector(ElementCssSelector selector){
		this.cssSelectors.add(selector);
	}
	
	public List<IFconditions> getConditions() {
		return conditions;
	}

	public void setConditions(List<IFconditions> conditions) {
		this.conditions = conditions;
	}
	
	public void addConditions(IFconditions condition){
		this.conditions.add(condition);
	}
}
