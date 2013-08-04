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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author whiteme
 * @date 2013年7月28日
 * @desc map合并
 */
public class MapUtils {

	/**
	 * 合并m2到m1
	 * 
	 * @param m1
	 * @param m2
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap<String, Object> mager(HashMap<String, Object> m1,
			HashMap<String, Object> m2) {
		if (null != m1 && null != m2) {
			// 遍历m2
			Set<String> keyset = m2.keySet();
			for (String key : keyset) {
				Object obj1 = m1.get(key);
				Object obj2 = m2.get(key);
				if (obj2 instanceof Map && obj1 instanceof Map) {
					((Map) obj1).putAll((Map) obj2);
				} else if (obj2 instanceof List && obj1 instanceof List) {
					((List) obj1).addAll((List) obj2);
				} else if (obj2 instanceof Set && obj1 instanceof Set) {
					((Set)obj1).addAll((Set)obj2);
				}else {
					if (null != obj2) {
						m1.put(key, obj2);
					}
				}
			}
		}
		return m1;
	}
	
	public static void main(String[] args) {
		System.out.println((null instanceof Object));
		HashMap<String, Object> m1 = Maps.newHashMap();
		m1.put("a", "1");
		m1.put("b", "x");
		m1.put("d", Lists.newArrayList(1,2,34));
		HashMap<String, Object> m2 = Maps.newHashMap();
		m2.put("a", "x");
		m2.put("b", "y");
		m2.put("c", "3");
		m1.put("d", Lists.newArrayList(1,2,34,5));
		m1.put("e", Maps.newHashMap());
		System.out.println(mager(m1, m2));
		System.out.println(m1);
	}
}
