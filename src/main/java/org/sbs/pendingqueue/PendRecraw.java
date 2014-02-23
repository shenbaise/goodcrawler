/**
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

import org.sbs.url.WebURL;


/**
 * @author shenbaise(shenbaise@outlook.com)
 * 定时更新的url--更新中的电视剧等
 */
public class PendRecraw extends AbsPendingQueue<WebURL> {
	private static final long serialVersionUID = -2733220512896685281L;

	protected PendRecraw(String jobName) {
		super(jobName);
	}
	
	public static void main(String[] args) {
		System.out.println(new PendRecraw("hello").pendingStatus());
	}
}
