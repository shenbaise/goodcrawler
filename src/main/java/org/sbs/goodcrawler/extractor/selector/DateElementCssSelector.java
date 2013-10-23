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

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;

/**
 * @author whiteme
 * @date 2013年10月13日
 * @desc 
 */
public class DateElementCssSelector extends ElementCssSelector<Date> {
	
	private Set<String> patterns = Sets.newHashSet(
			"yyyy.MM.dd HH:mm:ss",
			"yyyy-MM-dd",
			"yyyy-MM-dd",
			"yyyy/MM/dd",
			"yyyy年MM月dd日",
			"yyyy年MM月",
			"yyyy MM dd",
			"yyyyMMdd",
			"yyyy",
			"yy/MM/dd",
			"yyyy.MM.dd G 'at' HH:mm:ss z",
			"EEE, MMM d, ''yy",
			"h:mm a",
			"hh 'o''clock' a, zzzz",
			"K:mm a, z" ,
			"yyyyy.MMMMM.dd GGG hh:mm aaa",
			"EEE, d MMM yyyy HH:mm:ss Z",
			"yyMMddHHmmssZ",
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
			);
	private Date date;
	
	public DateElementCssSelector() {
		super();
	}

	public DateElementCssSelector(String name, String value, String attr,
			boolean isRequired) {
		super(name, value, attr, isRequired);
	}
	
	public DateElementCssSelector(String name, String value, String attr,
			boolean isRequired,String parrtarn) {
		super(name, value, attr, isRequired);
		this.patterns.add(parrtarn);
	}

	@Override
	public Date getContent() throws ExtractException{
		try {
			if(null!=this.date && !newDoc){
				return date;
			}
			if(null!=document){
				Elements elements = super.document.select(value);
				if(elements.isEmpty())
					return null;
				String temp;
				switch ($Attr) {
				case text:
					temp = CharMatcher.DIGIT.retainFrom(elements.text());
					break;
				default:
					temp = CharMatcher.DIGIT.retainFrom(elements.attr(attr));
					break;
				}
				if(StringUtils.isNotBlank(temp)){
					try {
						this.date = DateUtils.parseDate(temp, patterns.toArray(new String[0]));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				newDoc = false;
				return this.date;
			}
		} catch (Exception e) {
			throw new ExtractException("信息提取错误:"+e.getMessage());
		}
		return null;
	}

	@Override
	public Map<String, Date> getContentMap() throws ExtractException{
		if(newDoc)
			getContent();
		if(null == date)
			return  null;
		HashMap<String, Date> map = new HashMap<>(1);
		map.put(name, date);
		return map;
	}

	public Set<String> getPatterns() {
		return patterns;
	}

	public void setPatterns(Set<String> patterns) {
		this.patterns = patterns;
	}

	public void addPattern(String pattern){
		this.patterns.add(pattern);
	}

	@Override
	public void addAction(SelectorAction action) {
		
	}
}
