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
package org.sbs.goodcrawler.extractor.selector.action.string;

import org.apache.commons.lang3.StringUtils;
import org.sbs.goodcrawler.extractor.selector.action.StringSelectorAction;

/**
 * @author whiteme
 * @date 2013年10月11日
 * @desc 该action对StringSelecto选择的内容按配置进行替换操作。
 */
public class StringReplaceAction extends StringSelectorAction {
	
	private String searchString;
	private String replacement;
	
	public StringReplaceAction(String searchString, String replacement) {
		super();
		this.searchString = searchString;
		this.replacement = replacement;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	/**
	 * 根据配置的查找字符和替换字符对抽取内容进行替换操作
	 */
	@Override
	public String doAction(String content) {
		return StringUtils.replace(content, searchString, replacement);
	}
	
	public static void main(String[] args) {
		String string  = "@#$%$FGDFGFGHS#@$$Y";
		StringReplaceAction action = new StringReplaceAction("#", ",");
		System.out.println(action.doAction(string));
	}

}
