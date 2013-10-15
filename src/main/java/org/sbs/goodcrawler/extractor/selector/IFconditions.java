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
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author whiteme
 * @date 2013年10月15日
 * @desc 条件判别，满足相应条件，则返回相应的选择器
 */
public class IFconditions {
	/**
	 * 依赖检测的选择器
	 */
	private Test depend;
	/**
	 * 条件，以AND OR = 分割
	 */
	private String conditions;
	/**
	 * 条件辨别式对应的一组选择器
	 */
	protected List<ElementCssSelector> selectors = Lists.newArrayList();
	
	/**
	 * 检测依赖的选择器是否满足条件
	 * @param depend
	 * @return
	 */
	public boolean test(ElementCssSelector depend){
		
		return false;
	}
	
	public boolean totest(){
		
		
		return false;
	}
	
	public class Test{
		Set<String> operations = Sets.newHashSet("=",">","<",">=","<=");
		Set<String> cond = Sets.newHashSet("and","or","AND","OR");
	}
}
