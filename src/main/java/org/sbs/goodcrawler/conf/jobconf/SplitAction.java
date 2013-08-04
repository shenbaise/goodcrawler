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
package org.sbs.goodcrawler.conf.jobconf;

import org.apache.commons.lang3.StringUtils;

/**
 * @author whiteme
 * @date 2013年8月3日
 * @desc 
 */
public class SplitAction extends SelectAction{
	private String separator = ":";
	private int retain = 0;
	
	
	public SplitAction(String separator, int retain) {
		super();
		this.separator = separator;
		this.retain = retain;
	}


	@Override
	public String doAction(String content) {
		if(StringUtils.isNotBlank(content)){
			String[] s = content.split(separator);
			if(s.length>retain){
				return s[retain];
			}
		}
		return null;
	}
}
