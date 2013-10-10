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
package org.sbs.goodcrawler.extractor;

/**
 * @author whiteme
 * @date 2013年10月10日
 * @desc 选择元素。基础属性包括名称、css选择器|xpath、属性、是否是必须的required
 */
public abstract class ElementCSS {
	/**
	 * 选择器名称
	 */
	protected String name;
	/**
	 * css selector
	 */
	protected String value;
	/**
	 * 选择器的属性，img、src、text等
	 */
	protected String atrr;
	/**
	 * 石佛required
	 */
	protected boolean isRequired;
}
