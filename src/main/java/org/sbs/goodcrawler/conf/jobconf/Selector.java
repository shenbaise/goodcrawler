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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.util.EnumUtils;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc
 */
public class Selector {
	
	private String name;
	private String type;
	private SelectorType _type;
	private String value;
	private String attr;
	private SelectorAttr _attr;
	private boolean required = false;
	private List<Condition> conditions;
	private SelectAction action;
	private SelectNextUrl nextUrl;
	private Object target;
	private boolean giveup = false;

	public Selector(){
		
	}

	public Selector(String name, String type, String value, String attr,
			boolean required) {
		super();
		this.name = name;
		this.type = type;
		this._type = EnumUtils.getSelectorType(type);
		this.value = value;
		this.attr = attr;
		this._attr = EnumUtils.getSelectorAttr(attr);
		this.required = required;
	}
	/**
	 * 抽取信息
	 * @param doc
	 * @return
	 */
	public HashMap<String, Object> processAll(Document doc){
		giveup = false;
		HashMap<String, Object> result = process2(doc);
		if(null!=conditions && conditions.size()>0){
			for(Condition condition:conditions){
				if(condition.test())
				if(null!=condition && condition.getSelectors().size()>0){
					List<Selector> cSelectors = condition.getSelectors();
					for(Selector selector:cSelectors){
						HashMap<String, Object> m = selector.process2(doc);
						if((null==m || m.size()==0) && selector.required){
							giveup = true;
							break;
						}
						if(null!=m&&m.size()>0){
							result.putAll(m);
						}
					}
				}
			}
		}
		if(null!=nextUrl){
			HashMap<String, Object> m = nextUrl.process(doc);
			if(m!=null & m.size()>0){
				result.putAll(m);;
			}
		}
		return result;
	}
	
	/**
	 * @param doc
	 * @return
	 */
	public HashMap<String, Object> process2(Document doc){
		Object object = process(doc);
		HashMap<String,Object> result = Maps.newHashMap();
		if(object!=null){
			result.put(name, object);
			this.target = object;
		}
		if(null!=nextUrl){
			HashMap<String, Object> m = nextUrl.process(doc);
			if(m!=null & m.size()>0){
				result.putAll(m);;
			}
			// 移除url元素
			result.remove(name);
		}
		return result;
	}
	
	/**
	 * 抽取信息
	 * @param doc
	 * @return
	 */
	public Object process(Document doc) {
//		if(type.equals("url")){
//			System.out.println(type);
//		}
		if (doc != null) {
			Elements elements = doc.select(value);
			switch (_type) {
			case $string:
				StringBuilder sb = new StringBuilder();
				switch (_attr) {
				case text:
					// 检测Action
					for (Element e : elements) {
						sb.append(e.text());
					}
					if(null!=action){
						return action.doAction(sb.toString());
					}
					return sb.toString();
				default:
					for (Element e : elements) {
						sb.append(e.attr(attr));
					}
					if(null!=action){
						return action.doAction(sb.toString());
					}
					return sb.toString();
				}

			case $int:
				switch (_attr) {
				case text:
					String temp = CharMatcher.DIGIT.retainFrom(elements.text());
					if (temp.length() > 0) {
						return Integer.parseInt(temp);
					} else {
						return 0;
					}
				default:
					String temp2 = CharMatcher.DIGIT.retainFrom(elements.attr(attr));
					if (temp2.length() > 0) {
						return Integer.parseInt(temp2);
					} else {
						return 0;
					}
				}
			case $set:
				HashSet<String> set = Sets.newHashSet();
				switch (_attr) {
				case text:
					for (Element e : elements) {
						set.add(e.text());
					}
					return set;
				default:
					for (Element e : elements) {
						set.add(e.attr(attr));
					}
					return set;
				}
			case $list:
				List<String> list = Lists.newArrayList();
				
				switch (_attr) {
				case text:
					for (Element e : elements) {
						list.add(e.text());
					}
					return list;
				default:
					for (Element e : elements) {
						list.add(e.attr(attr));
					}
					return list;
				}
			case $url:
				HashSet<String> set2 = Sets.newHashSet();
				
				switch (_attr) {
				case text:
					for (Element e : elements) {
						set2.add(e.text());
					}
					return set2;
				default:
					for (Element e : elements) {
						set2.add(e.attr(attr));
					}
					return set2;
				}
			default:
				StringBuilder sb2 = new StringBuilder();
				switch (_attr) {
				case text:
					for (Element e : elements) {
						sb2.append(e.text());
					}
					return sb2.toString();
				default:
					for (Element e : elements) {
						sb2.append(e.attr(value));
					}
					return sb2.toString();
				}
			}
		}
		return null;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this._type = EnumUtils.getSelectorType(type);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
		this._attr = EnumUtils.getSelectorAttr(attr);
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public SelectorAttr getSelectorAttr() {
		return _attr;
	}

	public void setSelectorAttr(SelectorAttr selectorAttr) {
		this._attr = selectorAttr;
	}

	public SelectorType get_type() {
		return _type;
	}

	public void set_type(SelectorType _type) {
		this._type = _type;
	}

	public SelectorAttr get_attr() {
		return _attr;
	}

	public void set_attr(SelectorAttr _attr) {
		this._attr = _attr;
	}

	public SelectAction getAction() {
		return action;
	}

	public void setAction(SelectAction action) {
		this.action = action;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public SelectNextUrl getNextUrl() {
		return nextUrl;
	}

	public void setNextUrl(SelectNextUrl nextUrl) {
		this.nextUrl = nextUrl;
	}

	public boolean isGiveup() {
		return giveup;
	}

	public void setGiveup(boolean giveup) {
		this.giveup = giveup;
	}
}