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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;

/**
 * @author whiteme
 * @date 2013年7月25日
 * @desc 字符处理
 */
public class StringHelper {
	/**
	 * @param isoString
	 * @return
	 */
	public static String isoToUtf8(String isoString){
		if(StringUtils.isBlank(isoString))
			return "";
		try {
			return new String(isoString.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param urlString
	 * @return
	 */
	public static String urlDecodeString(String urlString){
		if(StringUtils.isBlank(urlString))
			return "";
		try {
			return URLDecoder.decode(urlString, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 删除特殊字符、字母、数字、空格
	 * @param s
	 * @return
	 */
	public static String removeAB12Blank(String s){
		return CharMatcher.anyOf("abcdefghijklmnopqrstuvwxyz;&ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.~!@#$%^&*()-+= 》《><?").removeFrom(s);
	}
	/**
	 * 删除数字、字母、特殊字符
	 * @param s
	 * @return
	 */
	public static String removeAB12(String s){
		return CharMatcher.anyOf("abcdefghijklmnopqrstuvwxyz;&ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.~!@#$%^&*()-+=》《><?").removeFrom(s);
	}
}
