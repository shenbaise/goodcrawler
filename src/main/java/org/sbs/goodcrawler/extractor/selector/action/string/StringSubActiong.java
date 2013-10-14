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
package org.sbs.goodcrawler.extractor.selector.action.string;

import org.apache.commons.lang3.StringUtils;
import org.sbs.goodcrawler.extractor.selector.action.StringSelectorAction;

/**
 * @author whiteme
 * @date 2013年10月12日
 * @desc 截取字串的Action<br>
 * 如，"4,4"表示，从第四位起，截取4个字符.<br>
 * "4"表示从4开始截取到最后
 */
public class StringSubActiong extends StringSelectorAction {
	/**
	 * 截取位置
	 */
	int pos = 0;
	/**
	 * 截取长度
	 */
	int lenth = 0;
	/**
	 * 构造器
	 * @param subExpression
	 */
	public StringSubActiong(String subExpression){
		if(StringUtils.isNotBlank(subExpression)){
			String[] ss = StringUtils.split(subExpression, ",");
			if(ss.length==1){
				this.pos = Integer.getInteger(ss[0]);
			}else if (ss.length==2) {
				this.pos = Integer.getInteger(ss[0]);
				this.lenth = Integer.getInteger(ss[1]);
			}
		}
	}
	
	/**
	 * 获取字符的特定截取子串
	 */
	@Override
	public String doAction(String content) {
		if(StringUtils.isNotBlank(content)){
			if(this.lenth==0 && this.pos>0){
				content = StringUtils.substring(content, this.pos);
			}else if (this.lenth>0 && this.pos>=0) {
				content = StringUtils.substring(content, this.pos, this.pos+this.lenth);
			}else{
				return content;
			}
		}
		return "";
	}

}
