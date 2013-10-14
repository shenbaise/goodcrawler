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
 * @desc 截取两个字符之间的部分。
 */
public class StringBetweenAction extends StringSelectorAction{
	/**
	 * 截取表达式。t表示第一个t,t之间的部分<br>
	 * t,x表示第一个t,x之间的部分<br>
	 * t,x,#表示多个t,x之间的部分以#进行分割。
	 */
	private String exp ;
	public StringBetweenAction(String exp){
		this.exp = exp;
	}
	/**
	 * 截取表达式。<br>
	 * t表示第一个t,t之间的部分<br>
	 * t,x表示第一个t,x之间的部分<br>
	 * t,x,#表示多个t,x之间的部分以#进行分割。
	 */
	@Override
	public String doAction(String content) {
		if(StringUtils.isNotBlank(content)){
			String[] ss = StringUtils.split(exp,",");
			if(ss.length==1){
				return StringUtils.substringBetween(content, ss[0]);
			}else if (ss.length==2) {
				return StringUtils.substringBetween(content, ss[0],ss[1]);
			}else if (ss.length==3) {
				String[] contents = StringUtils.substringsBetween(content, ss[0],ss[1]);
				if(null!=contents){
					StringBuilder sb = new StringBuilder();
					for(String s:contents){
						sb.append(s).append(ss[2]);
					}
					return sb.substring(0, sb.length()-1);
				}
			}else {
				return content;
			}
		}
		return "";
	}
	
	
	public static void main(String[] args) {
		// test
		String string = "abcdabcdefeacabfdsgdsadse";
		StringBetweenAction action = new StringBetweenAction("f");
		System.out.println(action.doAction(string));
		assert action.doAction(string) == "eacab";
		StringBetweenAction action2 = new StringBetweenAction("a,c");
		System.out.println(action2.doAction(string));
		assert action2.doAction(string) ==  "b";
		StringBetweenAction action3 = new StringBetweenAction("a,c,#");
		System.out.println(action3.doAction(string));
	}
}
