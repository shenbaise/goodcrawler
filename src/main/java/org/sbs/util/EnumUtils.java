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
package org.sbs.util;

import org.apache.commons.lang3.StringUtils;
import org.sbs.goodcrawler.conf.jobconf.SelectActionOperationType;
import org.sbs.goodcrawler.conf.jobconf.SelectorAttr;
import org.sbs.goodcrawler.conf.jobconf.SelectorType;
import org.sbs.goodcrawler.storage.StorageType;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 
 */
public class EnumUtils {
	/**
	 * 获取选择器属性枚举
	 * @param attr
	 * @return
	 */
	public static SelectorAttr getSelectorAttr(String attr){
		if(StringUtils.isNotBlank(attr)){
			if(SelectorAttr.href.name().equals(attr)){
				return SelectorAttr.href;
			}else if(SelectorAttr.src.name().equals(attr)){
				return SelectorAttr.src;
			}else if (SelectorAttr.text.name().equals(attr)) {
				return SelectorAttr.text;
			}else if (SelectorAttr.value.name().equals(attr)) {
				return SelectorAttr.value;
			}
		}
		return  SelectorAttr.text;
	}
	/**
	 * 获取存储类型枚举
	 * @param storageType
	 * @return
	 */
	public static StorageType getStorageType(String storageType){
		if(StringUtils.isNotBlank(storageType)){
			if(StorageType.Hbase.name().equals(storageType)){
				return StorageType.Hbase;
			}else if (storageType.equals(StorageType.ElasticSearch.name())) {
				return StorageType.ElasticSearch;
			}else if (storageType.equals(StorageType.LocalFile.name())){
				return StorageType.LocalFile;
			}else if (storageType.equals(StorageType.Mongodb.name())) {
				return StorageType.Mongodb;
			}else if (storageType.equals(StorageType.Mysql.name())){
				return StorageType.Mysql;
			}
		}
		return StorageType.LocalFile;
	}
	
	/**
	 * Selector type
	 * @param type
	 * @return
	 */
	public static SelectorType getSelectorType(String type){
		if(StringUtils.isNotBlank(type)){
			if (SelectorType.$int.name().endsWith(type)) {
				return SelectorType.$int;
			}else if (SelectorType.$list.name().endsWith(type)) {
				return SelectorType.$list;
			}else if (SelectorType.$set.name().endsWith(type)) {
				return SelectorType.$set;
			}else if (SelectorType.$string.name().endsWith(type)) {
				return SelectorType.$string;
			}else if (SelectorType.$url.name().endsWith(type)) {
				return SelectorType.$url;
			}
		}
		return SelectorType.$string;
	}
	
	public static SelectActionOperationType getOperationType(String type){
		if(StringUtils.isNotBlank(type)){
			if(SelectActionOperationType.split.name().equals(type)){
				return SelectActionOperationType.split;
			}
		}
		
		return SelectActionOperationType.split;
	}
}
