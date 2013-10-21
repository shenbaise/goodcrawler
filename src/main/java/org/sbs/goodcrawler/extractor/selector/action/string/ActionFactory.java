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

import org.apache.commons.lang3.EnumUtils;
import org.jsoup.nodes.Element;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;
import org.sbs.goodcrawler.extractor.selector.action.integer.IntegerAbsAction;
import org.sbs.goodcrawler.extractor.selector.action.integer.IntegerActionType;
import org.sbs.goodcrawler.extractor.selector.action.integer.IntegerBetweenAction;
import org.sbs.goodcrawler.extractor.selector.exception.IntegerBetweenExpressionException;

/**
 * @author whiteme
 * @date 2013年10月17日
 * @desc Actiong工厂
 */
public class ActionFactory {
	
	public static SelectorAction create(Element element,String c){
		if("string".equals(c)){
			StringActionType $type = EnumUtils.getEnum(StringActionType.class, "$"+element.attr("operation"));
			if(null == $type){
				try {
					throw new Exception("配置文件错误："+element.tagName()+"请检查元素的operation属性");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			switch ($type) {
			case $after:
				return new StringAfterAction(element.attr("split"));
			case $afterLast:
				return new StringAfterLastAction(element.attr("split"));
			case $before:
				return new StringBeforeAction(element.attr("split"));
			case $beforeLast:
				return new StringBeforeLastAction(element.attr("split"));
			case $between:
				return new StringBetweenAction(element.attr("exp"));
			case $filter:
				return new StringFilterAction(element.attr("filter"), element.attr("charType"));
			case $replace:
				return new StringReplaceAction(element.attr("search"),element.attr("replacement"));
			case $split:
				return new StringSplitAction(element.attr("split"),element.attr("index"));
			case $sub:
				return new StringSubActiong(element.attr("exp"));
			default:
				break;
			}
		}else if("integer".equals(c)) {
			
		}else if("date".equals(c)){
			
		}else if("numerica".equals(c)){
			IntegerActionType $type = IntegerActionType.valueOf(element.attr("type"));
			switch ($type) {
			case $abs:
				return new IntegerAbsAction();
			case $between:
				try {
					return new IntegerBetweenAction(element.attr("exp"),element.attr("default"));
				} catch (IntegerBetweenExpressionException e) {
					e.printStackTrace();
				}
			default:
				break;
			}
		}
		return null;
	}
}
