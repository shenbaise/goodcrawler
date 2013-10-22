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
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.Configuration;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.ElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.IFConditions;
import org.sbs.goodcrawler.extractor.selector.factory.ElementCssSelectorFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


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
	private final List<ExtractTemplate> templates = Lists.newArrayList();
	/**
	 * 获取所有模板提取的信息
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getContentAll(Document document) throws ExtractException{
		Map<String, Object> content = Maps.newHashMap(); 
		for(ExtractTemplate template:templates){
			Map<String, Object> m = template.getConten(document);
			if(m!=null && m.size()>0)
				content.putAll(m);
		}
		return content;
	}
	/**
	 * 获取所所有模板提取的信息，分开方式。<br>
	 * 可以通过get(模板名称的方式获取到某个独立的模板提取的信息)
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getContentSeprator(Document document) throws ExtractException{
		Map<String, Object> content = Maps.newHashMap();
		for(ExtractTemplate template : templates){
			Map<String, Object> m = template.getConten(document);
			if(m!=null && m.size()>0)
				content.put(template.getName(), m);
		}
		return content;
	}
	/**
	 * 从配置文件中加载抽取配置信息
	 * @param doc
	 * @return
	 * @throws ConfigurationException
	 */
	public ExtractConfig loadConfig(Document doc) throws ConfigurationException{
		Elements extractElement = doc.select("extract");
		super.jobName = doc.select("job").attr("name");
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
			extractTemplate.setName(template.attr("name"));
			// 提取元素
			Elements selectElement = template.select("elements").first().children();
			for(Element element:selectElement){
				if("element".equals(element.tagName())){
					ElementCssSelector<?> selector = ElementCssSelectorFactory.create(element);
					extractTemplate.addCssSelector(selector);
				}else if ("if".equals(element.tagName())) {
					IFConditions ifConditions = IFConditions.create(element);
					extractTemplate.addConditions(ifConditions);
				}
			}
			this.templates.add(extractTemplate);
		}
		return this;
	}
	
	public int getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
	public List<ExtractTemplate> getTemplates() {
		return templates;
	}
	
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("ExtractConfig [threadNum=")
				.append(threadNum)
				.append(", templates=")
				.append(templates != null ? templates.subList(0,
						Math.min(templates.size(), maxLen)) : null)
				.append(", jobName=").append(jobName).append("]");
		return builder.toString();
	}
	
	// test
	public static void main(String[] args) {
		ExtractConfig extractConfig = new ExtractConfig();
		Document document;
		try {
			document = Jsoup.parse(new File("conf/youku_conf.xml"), "utf-8");
			System.out.println(extractConfig.loadConfig(document).toString());
			Map<String, Object> r=extractConfig.getContentSeprator(Jsoup.parse(new URL("http://www.youku.com/show_page/id_z59617246746d11e0a046.html"), 10000));
			System.out.println(r);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (ExtractException e) {
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
	private List<IFConditions> conditions = Lists.newArrayList();
	/**
	 * 以该模板的配置提取document的信息
	 * @param document
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getConten(Document document) throws ExtractException{
		try {
			Map<String, Object> content = Maps.newHashMap();
			for(ElementCssSelector<?> selector:cssSelectors){
				selector.setDocument(document);
				Map<String, ?> m = selector.getContentMap();
				if((null==m || m.size()==0) && selector.isRequired()){
					return null;
				}else {
					if(null!=m && m.size()>0)
						content.putAll(m);
				}
			}
			for(IFConditions con:conditions){
				if(con.test(content)){
					for(ElementCssSelector<?> selector:con.getSelectors()){
						if("thumbnail".equals(selector.getName())){
							System.out.println("..");
						}
						Map<String, Object> m = selector.setDocument(document).getContentMap();
						if((null==m || m.size()==0) && selector.isRequired()){
							return null;
						}else {
							if(null!=m && m.size()>0)
								content.putAll(m);
						}
					}
				}
			}
			return content;
		} catch (Exception e) {
			throw new ExtractException("信息提取错误："+e.getMessage());
		}
	}
	/**
	 * 在提取信息之前过滤Url
	 * @param url
	 * @return
	 */
	public boolean urlFilter(String url){
		for(Pattern pattern :urlPattern){
			if(pattern.matcher(url).matches()){
				return true;
			}
		}
		return false;
	}
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
	
	public void addCssSelector(ElementCssSelector<?> selector){
		this.cssSelectors.add(selector);
	}
	
	public List<IFConditions> getConditions() {
		return conditions;
	}

	public void setConditions(List<IFConditions> conditions) {
		this.conditions = conditions;
	}
	
	public void addConditions(IFConditions condition){
		this.conditions.add(condition);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
