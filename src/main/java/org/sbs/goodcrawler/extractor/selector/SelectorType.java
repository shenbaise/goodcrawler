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

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 选择器选择元素的预期类型
 */
public enum SelectorType {
	/**
	 * 字符，可以使用正则进行匹配
	 */
	$string,
	/**
	 * 整型，可带有区间、大小等限制
	 */
	$int,
	/**
	 * 该类型的选择器将被填充到list中
	 */
	$list,
	/**
	 * 该类型的选择器，将选择抽取的内入填充到set中
	 */
	$set,
	/**
	 * 该类型的选择器表明，其值是一个Url。该url会被再次抓取并抽取。
	 * </br><i>注意:</i><b>只有该类型的选择器可以嵌套选择器</b>
	 */
	$url,
	/**
	 * 该类型的选择器表明其选择内容将是数值类型的。
	 * </br>该选择器带有format
	 */
	$numerica,
	/**
	 * 该类型的选择器表明其选择的内容将是日期类型的。</br>该选择器带有format
	 */
	$date,
	/**
	 * ajax方式的动态选择器
	 */
	$ajax,
	/**
	 * 文件
	 */
	$file
}
