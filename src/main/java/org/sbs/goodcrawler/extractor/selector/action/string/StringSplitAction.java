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

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.sbs.goodcrawler.extractor.selector.action.StringSelectorAction;

import com.google.common.collect.Sets;

/**
 * @author whiteme
 * @date 2013年10月11日
 * @desc 根据配置对提取内容进行Split操作，并返回指定位置的截取字串。</br>
 * 如果有多个位置(多个以，分隔)，则多个位置之间的的字串仍然按指定的分隔符连接。
 */
public class StringSplitAction extends StringSelectorAction{
	/**
	 * 分隔符
	 */
	private String split ;
	private Set<Integer> set = Sets.newHashSet();
	/**
	 * 构造器
	 * @param split
	 * @param index
	 */
	public StringSplitAction(String split,String index){
		if(StringUtils.isNotBlank(split)){
			this.split = split;
		}else {
			this.split = ",";
		}
		if(StringUtils.isNotBlank(index)){
			String is[] = index.split(",");
			for(String i:is){
				if(StringUtils.isNumeric(i)){
					set.add(Integer.getInteger(i));
				}
			}
		}
	}
	/**
	 * 根据配置对提取内容进行Split操作，并返回指定位置的截取字串。</br>
	 * 如果有多个位置(多个以，分隔)，则多个位置之间的的字串仍然按指定的分隔符连接。</br>
	 * 如果没设置位置，默认0。即第一个。
	 */
	@Override
	public String doAction(String content) {
		if(StringUtils.isNotBlank(content)){
			if(set.size()>0){
				String[] subs = StringUtils.split(content, this.split);
				StringBuilder sb = new StringBuilder();
				for (Integer i :set) {
					if(i<subs.length){
						if(i>-1)
							sb.append(subs[i]).append(this.split);
					}
				}
				return sb.substring(0,sb.length()-1);
			}else {
				return StringUtils.split(content, this.split)[0];
			}
		}
		return "";
	}
	
	public static void main(String[] args) {
		String ixx = "3,,,";
		String[] xx = ixx.split(",");	// 1
		assert xx.length == 1;
		ixx = "3,, ,";
		xx = ixx.split(",");
		assert xx.length == 3;
		System.out.println(xx.length);
	}
}
