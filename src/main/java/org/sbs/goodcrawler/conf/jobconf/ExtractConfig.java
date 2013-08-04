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
package org.sbs.goodcrawler.conf.jobconf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.Configuration;
import org.sbs.util.EnumUtils;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 
 */
public class ExtractConfig extends Configuration {
	private Log log = LogFactory.getLog(this.getClass());
	private String type = "default";
	private int threadNum = 2;
	private List<Template> templates = Lists.newArrayList();
	
	
	public ExtractConfig() {
	}
	
	/**
	 * 加载配置
	 * @param confDoc
	 * @return
	 */
	public ExtractConfig loadConfig(Document confDoc){
		Document doc = confDoc;
		super.jobName = doc.select("job name").text();
		
		Elements e = doc.select("extract");
		this.type = e.select("type").text();
		if(StringUtils.isNotBlank(e.select("threadNum").text())){
			this.threadNum = Integer.parseInt(e.select("threadNum").text());
		}
		
		Elements $templates = e.select("template");
		
		for(Element template:$templates){
			Template mTemplate = new Template();
			// url
			Elements $urls = template.select("urls url");
			List<String> mUrl = Lists.newArrayList();
			for(Element url :$urls){
				mUrl.add(url.text());
			}
			mTemplate.setUrls(mUrl);;
			// select
			Elements $selects = template.select("elements element");
			List<Selector> mSelects = Lists.newArrayList();
			
			for(Element select:$selects){
				Selector s = getSelector(select,0);
				mSelects.add(s);
			}
			
			mTemplate.setSelectors(mSelects);
			this.templates.add(mTemplate);
		}
		return this;
	}
	
	public Selector getSelector(Element element,int leavel){
		String perfix = "";
		for(int i=0;i<leavel;i++){
			perfix= ""+leavel;
		}
		leavel++;
		Selector mSelect = new Selector();
		mSelect.setName(element.attr("name"));
		mSelect.setType(element.attr("type"));
		mSelect.setValue(element.attr("value"));
		mSelect.setAttr(element.attr("attr"));
		String required = element.attr("required");
		
		// next url
		if("url".equals(element.attr("type"))){
			SelectNextUrl nextUrl = new SelectNextUrl();
			Elements eu = element.select("element"+leavel++);
			nextUrl.setSelector(getSelector(eu.first(), leavel));
			nextUrl.setRefSelector(mSelect);
			mSelect.setNextUrl(nextUrl);
		}
				
		if(StringUtils.isNotBlank(required)){
			mSelect.setRequired(Boolean.parseBoolean(required));
		}
		// action
		Elements action = element.select("action"+perfix);
		if(!action.isEmpty()){
			String opration = action.attr("opration");
			String separator = action.attr("separator");
			String retain = action.attr("retain");
			if(StringUtils.isNotBlank(opration)
					&& StringUtils.isNotBlank(separator)
					&& StringUtils.isNotBlank(retain)){
				if(EnumUtils.getOperationType(opration).equals(SelectActionOperationType.split)){
					SelectAction selectAction = new SplitAction(separator, Integer.parseInt(retain));
					mSelect.setAction(selectAction);
				}
			}
		}
		// condition
		Elements conditions = element.select("if");
		List<Condition> mConditions = Lists.newArrayList();
		for(Element element2:conditions){
			Condition condition = new Condition();
			condition.setRefSelector(mSelect);
			condition.setExpression(element2.attr("test"));
			
			Elements selectElements = element2.select("element"+perfix+leavel);
			List<Selector> selectors = Lists.newArrayList();
			for(Element e:selectElements){
				selectors.add(getSelector(e,leavel));
			}
			condition.setSelectors(selectors);
			mConditions.add(condition);
		}
		mSelect.setConditions(mConditions);
		
		return mSelect;
	}
	
	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
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
		}
	}
	
}
