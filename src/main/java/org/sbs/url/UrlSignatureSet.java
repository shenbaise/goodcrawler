/**
 * ##########################  GoodCrawler  ############################
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
package org.sbs.url;

import java.util.HashSet;
import java.util.Set;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * 保存Url的签名信息，md5或者simhash
 * 已经废弃，去重复使用bloomfilter实现
 */
@Deprecated 
public class UrlSignatureSet {
	private static Set<String> signatureSet =  new HashSet<>(1024*1024*10);
	
	public static void add(String b){
		signatureSet.add(b);
	}
	
	public static boolean duplicate(String b){	
		return signatureSet.equals(b);
	}
}
