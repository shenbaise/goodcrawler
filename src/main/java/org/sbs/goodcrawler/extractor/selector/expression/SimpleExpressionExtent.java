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
package org.sbs.goodcrawler.extractor.selector.expression;

/**
 * @author whiteme
 * @date 2013年10月16日
 * @desc 简单表达式的扩展
 */
public class SimpleExpressionExtent implements GrExpression{
	
	SimpleExpression leftExpression ;
	SimpleExpression rightExpression;
	String logic;
	
	public SimpleExpressionExtent(SimpleExpression leftExpression,
			SimpleExpression rightExpression, String logic) {
		super();
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
		this.logic = logic;
	}


	public boolean test() {
		try {
			if("and".equals(logic.toLowerCase())){
				return this.leftExpression.test() && this.rightExpression.test();
			}else {
				return this.leftExpression.test() || this.rightExpression.test();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
