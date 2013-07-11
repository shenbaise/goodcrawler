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
package org.sbs.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-4
 * json工具
 */
public class JsonUtil {

	/**
	 * 构造函数
	 */
	public JsonUtil() {
	}
	
	public static JSONObject generate(List<?> list) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("totalProperty", list.size());
        map.put("root", list);
        return JSONObject.fromObject(map);
    }
   
    public static JSONObject javabean2json(Object object) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("data", object);
        return JSONObject.fromObject(map);
    }
   
    public static JSONObject objectcollect2json(List<?> list, String total) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("totalProperty", total);
        map.put("root", list);
        return JSONObject.fromObject(map);
    }
    
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {

	}

}
