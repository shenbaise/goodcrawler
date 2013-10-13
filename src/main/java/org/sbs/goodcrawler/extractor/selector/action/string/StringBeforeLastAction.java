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
 * @date 2013年10月13日
 * @desc 截取最后一个分隔符之前的字符串
 */
public class StringBeforeLastAction extends StringSelectorAction {
	/**
	 * 分割的字符串
	 */
	String separator;
	/**
	 * 构造器
	 * @param separator
	 */
	public StringBeforeLastAction(String separator){
		this.separator = separator;
	}
	/**
	 * 截取最后一个separator之前的字符串
	 */
	@Override
	public String doAction(String content) {
		if(StringUtils.isNotBlank(content)){
			return StringUtils.substringBeforeLast(content, separator);
		}
		return "";
	}

}
