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
			Set<String> keyset = m2.keySet();
			for (String key : keyset) {
				Object obj = null;
				Object t = null;
				obj = m2.get(key);
				if (obj instanceof Map) {
					if (null != (t = m1.get(key))) {
						((Map) t).putAll((Map) obj);
						m1.put(key, ((Map) t));
					} else {
						m1.put(key, (Map) obj);
					}
				} else if (obj instanceof List) {
					if (null != (t = m1.get(key))) {
						((List) t).addAll((List) obj);
						m1.put(key, ((List) t));
					} else {
						m1.put(key, (List) obj);
					}
				}else {
					if (null != (t = m2.get(key))) {
						m1.put(key, t);
					}
				}
			}
		}
		return m1;
	}
	
	
}
