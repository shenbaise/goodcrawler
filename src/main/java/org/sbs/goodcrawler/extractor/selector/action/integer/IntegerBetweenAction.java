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
package org.sbs.goodcrawler.extractor.selector.action.integer;

import org.apache.commons.lang3.StringUtils;
import org.sbs.goodcrawler.extractor.selector.action.IntegerSelectorAction;
import org.sbs.goodcrawler.extractor.selector.exception.IntegerBetweenExpressionException;

/**
 * @author whiteme
 * @date 2013年10月13日
 * @desc 检测数值是否在某个区间内。如果超过区间则有默认值代替
 */
public class IntegerBetweenAction extends IntegerSelectorAction {
	private int max;
	private int min;
	private int def;
	/**
	 * 构造器
	 * @param exp
	 * @param def
	 * @throws IntegerBetweenExpressionException
	 */
	public IntegerBetweenAction(String exp,String def) throws IntegerBetweenExpressionException{
		if(StringUtils.isNotBlank(exp)){
			String ss[] = exp.split(",");
			if(ss.length!=2){
				throw new IntegerBetweenExpressionException("数值区间表示错误");
			}else {
				max = Integer.getInteger(ss[1]);
				min = Integer.getInteger(ss[0]);
				if(max<min){
					int i = max;
					max = min;
					min = i;
				}
			}
		}
		if(StringUtils.isNotBlank(def)){
			this.def = Integer.getInteger(def);
		}
	}
	/**
	 * 检测数值是否在某个区间内。如果超过区间则有默认值代替
	 */
	@Override
	public int doAction(Integer i) {
		if(i>max || i<min){
			i = def;
		}
		return i;
	}

}
