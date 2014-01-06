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
package org.sbs.pendingqueue;

import org.sbs.goodcrawler.page.Page;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29 等待处理的页面
 */
public class PendingPages extends AbsPendingQueue<Page> {
	private static final long serialVersionUID = -5671808882701246813L;
	protected PendingPages(String jobName) {
		super(jobName);
	}
}
