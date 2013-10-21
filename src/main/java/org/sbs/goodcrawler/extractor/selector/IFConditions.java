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
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.expression.SimpleExpression;
import org.sbs.goodcrawler.extractor.selector.factory.ElementCssSelectorFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/**
 * @author whiteme
 * @date 2013年10月15日
 * @desc 条件判别，满足相应条件，则返回相应的选择器
 */
@SuppressWarnings("rawtypes")
public class IFConditions {
	
	/**
	 * 条件，以AND OR = 分割
	 */
	private String conditions;
	
	public IFConditions(String conditions) {
		super();
		this.conditions = conditions;
	}

	/**
	 * 条件辨别式对应的一组选择器
	 */
	protected List<ElementCssSelector> selectors = Lists.newArrayList();
	/**
	 * 简单操作符号
	 */
	private Set<String> operations = Sets.newHashSet("=","!=", ">", "<", ">=", "<=");
	/**
	 * 简单与或关系
	 */
	private Set<String> cond = Sets.newHashSet("and", "or", "AND", "OR");

	/**
	 * 检测依赖的选择器是否满足条件
	 * 
	 * @param depend
	 * @return
	 */
	public boolean test(Map<String, Object> selectContent) throws ExtractException{
		TreeMap<Integer, String> conIndex = Maps.newTreeMap();
		Queue<SimpleExpression> expressionQueue = Queues.newArrayDeque();
		Queue<String> logicQueue = Queues.newArrayDeque();
		// a=b and c=d or c=e or x=y
		int index = 0;
		for (String co : cond) {
			index = 0;
			while ((index = conditions.indexOf(co, index+1)) > -1) {
				int i = index;
				conIndex.put(i, co);
			}
		}
		index = 0;
		for (Entry<Integer, String> entry : conIndex.entrySet()) {
			String subExp = conditions.substring(index, entry.getKey());
			for (String op : operations) {
				int i = subExp.indexOf(op);
				if (i > -1) {
					String[] ss = subExp.split(op);
					if(null==selectContent.get(ss[0].trim())){
						throw new ExtractException("表达式依赖的选择提取内容为空：["+this.conditions + "] 中的"+ss[0]);
					}
					expressionQueue.add(new SimpleExpression(StringUtils.trim((String)selectContent.get(ss[0].trim())), StringUtils.trim(ss[1]), op));
					logicQueue.add(entry.getValue());
				}
			}
			index = entry.getKey()+entry.getValue().length();
		}
		// 最后一个表达式
		String subExp = conditions.substring(index);
		for (String op : operations) {
			int i = subExp.indexOf(op);
			if (i > -1) {
				String[] ss = subExp.split(op);
				if(null==selectContent.get(ss[0].trim())){
					throw new ExtractException("表达式依赖的选择提取内容为空：["+this.conditions + "] 中的"+ss[0]);
				}
				expressionQueue.add(new SimpleExpression(StringUtils.trim((String)selectContent.get(ss[0].trim())), StringUtils.trim(ss[1]), op));
			}
		}
		boolean b;
		try {
			b = expressionQueue.poll().test();
			while(!expressionQueue.isEmpty()){
				b = cacl(b,logicQueue.poll(),expressionQueue.poll());
			}
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 * 一次逻辑运算
	 * @return
	 */
	private boolean cacl(boolean left ,String logic,SimpleExpression right){
		try {
			if("and".equals(logic.toLowerCase())){
				return left && right.test();
			}else {
				return left || right.test();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获取分支条件下选择器的选择内容
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getContent(Document document) throws ExtractException{
		if(null!=selectors && selectors.size()>0){
			Map<String, Object> content = Maps.newHashMap();
			for(ElementCssSelector<?> selector:selectors){
				content.putAll(selector.setDocument(document).getContentMap());
			}
			return content;
		}
		return Maps.newHashMap();
	}
	
	public List<ElementCssSelector> getSelectors() {
		return selectors;
	}
	public void setSelectors(List<ElementCssSelector> selectors) {
		this.selectors = selectors;
	}
	
	public void addSelector(ElementCssSelector selector){
		this.selectors.add(selector);
	}
	
	public static void main(String[] args) {
		String exp = "a= sd ea and  c= c bc d  and c=e and x=y";
		
		IFConditions ic = new IFConditions(exp);
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("a", "sd");
			map.put("c", "c bc d");
//			map.put("x", "y");
			System.out.println(ic.test(map));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////////////////
	///			IFC 构造工厂
	///////////////////////////////////////////////////////////////////
	/**
	 * 条件分支选择器创建方法<b>传入该方法的element必须是If分支的配置</br>
	 * 该方法不做检测。
	 * @param document
	 * @return
	 */
	public static IFConditions create(Element element){
		if(element!=null){
			String exp = element.attr("test");
			IFConditions iFconditions = new IFConditions(exp);
			Elements selectElements = element.select("element");
			for(Element e :selectElements){
				iFconditions.addSelector(ElementCssSelectorFactory.create(e));
			}
			return iFconditions;
		}
		return null;
	}
}
