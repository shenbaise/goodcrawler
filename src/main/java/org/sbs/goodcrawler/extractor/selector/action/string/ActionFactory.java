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

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.sbs.goodcrawler.extractor.selector.action.SelectorAction;
import org.sbs.goodcrawler.extractor.selector.action.file.DownLoadFileAction;
import org.sbs.goodcrawler.extractor.selector.action.file.DownLoadImageResizeAction;
import org.sbs.goodcrawler.extractor.selector.action.file.FileActionType;
import org.sbs.goodcrawler.extractor.selector.action.integer.IntegerAbsAction;
import org.sbs.goodcrawler.extractor.selector.action.integer.IntegerActionType;
import org.sbs.goodcrawler.extractor.selector.action.integer.IntegerBetweenAction;
import org.sbs.goodcrawler.extractor.selector.exception.IntegerBetweenExpressionException;

/**
 * @author whiteme
 * @date 2013年10月17日
 * @desc Actiong工厂
 */
public class ActionFactory {
	
	public static SelectorAction create(Element element,String c){
		if("string".equals(c)){
			StringActionType $type = EnumUtils.getEnum(StringActionType.class, element.attr("operation"));
			if(null == $type){
				try {
					throw new Exception("配置文件错误："+element.tagName()+"请检查元素的operation属性");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			switch ($type) {
			case after:
				return new StringAfterAction(element.attr("split"));
			case afterLast:
				return new StringAfterLastAction(element.attr("split"));
			case before:
				return new StringBeforeAction(element.attr("split"));
			case beforeLast:
				return new StringBeforeLastAction(element.attr("split"));
			case between:
				return new StringBetweenAction(element.attr("exp"));
			case filter:
				return new StringFilterAction(element.attr("filter"), element.attr("charType"));
			case replace:
				return new StringReplaceAction(element.attr("search"),element.attr("replacement"));
			case split:
				return new StringSplitAction(element.attr("split"),element.attr("index"));
			case sub:
				return new StringSubAction(element.attr("exp"));
			case suffix:
				return new StringSuffixAction(element.attr("suffix"));
			case perfix:
				return new StringPerfixAction(element.attr("perfix"));
			default:
				break;
			}
		}else if("integer".equals(c)  || "int".equals(c)) {
			IntegerActionType $type = EnumUtils.getEnum(IntegerActionType.class, element.attr("operation"));
			switch ($type) {
			case abs:
				return new IntegerAbsAction();
			case between:
				try {
					return new IntegerBetweenAction(element.attr("exp"),element.attr("default"));
				} catch (IntegerBetweenExpressionException e) {
					e.printStackTrace();
				}
			default:
				break;
			}
		}else if("date".equals(c)){
			
		}else if("numerica".equals(c)){
			IntegerActionType $type = EnumUtils.getEnum(IntegerActionType.class, element.attr("operation"));
			switch ($type) {
			case abs:
				return new IntegerAbsAction();
			case between:
				try {
					return new IntegerBetweenAction(element.attr("exp"),element.attr("default"));
				} catch (IntegerBetweenExpressionException e) {
					e.printStackTrace();
				}
			default:
				break;
			}
		}else if ("file".equals(c)) {
			FileActionType $type = EnumUtils.getEnum(FileActionType.class, element.attr("operation"));
			switch ($type) {
			case download:
				String dir = element.attr("dir");
				String temp = element.attr("fileName");
				boolean md5File = false ,asyn;
				if(StringUtils.isNotBlank(temp)){
					if("{md5}".equals(temp)){
						md5File = true;
					}
				}
				else md5File = true;
				
				temp = element.attr("asyn");
				if(StringUtils.isNotBlank(temp)){
					asyn = Boolean.parseBoolean(temp);
				}
				else {
					asyn = true;
				}
				return new DownLoadFileAction(dir, md5File, asyn);
			case download_resize:
				String dir2 = element.attr("dir");
				String temp2 = element.attr("fileName");
				boolean md5File2 = false ,asyn2;
				if(StringUtils.isNotBlank(temp2)){
					if("{md5}".equals(temp2)){
						md5File2 = true;
					}
				}
				else md5File2 = true;
				temp2 = element.attr("asyn");
				
				if(StringUtils.isNotBlank(temp2)){
					asyn2 = Boolean.parseBoolean(temp2);
				}
				else {
					asyn2 = true;
				}
				DownLoadImageResizeAction resizeAction = new DownLoadImageResizeAction(dir2, md5File2, asyn2);
				
				temp2 = element.attr("width");
				if(StringUtils.isNotBlank(temp2)){
					resizeAction.setW(Integer.parseInt(temp2));
				}
				
				temp2 = element.attr("height");
				if(StringUtils.isNotBlank(temp2)){
					resizeAction.setH(Integer.parseInt(temp2));
				}
				temp2 = element.attr("quality");
				if(StringUtils.isNotBlank(temp2)){
					resizeAction.setQuality(Float.parseFloat(temp2));
				}
				temp2 = element.attr("del");
				if(StringUtils.isNotBlank(temp2)){
					resizeAction.setDeleteOldFile(Boolean.parseBoolean(temp2));
				}
				return resizeAction;
			default:
				break;
			}
		}
		return null;
	}
}
