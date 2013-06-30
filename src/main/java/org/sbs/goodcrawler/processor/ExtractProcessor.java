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
package org.sbs.goodcrawler.processor;

import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.urlmanager.BloomfilterHelper;
import org.sbs.goodcrawler.urlmanager.PendingUrls;
import org.sbs.goodcrawler.urlmanager.WebURL;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * 提取页面信息
 */
public class ExtractProcessor extends GoodProcessor {
	
	PendingUrls pendingUrls = PendingUrls.getInstance();
	BloomfilterHelper bloomfilterHelper = BloomfilterHelper.getInstance();
	
	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.processor.GoodProcessor#porcess(org.sbs.crawler.Page)
	 */
	@Override
	public Object handle(Page page) {
		if(null!=page){
			try {
				Document doc = Jsoup.parse(new String(page.getContentData(),page.getContentCharset()));
				// 获取所有Url并加入Url队列
				Elements links = doc.getElementsByTag("a");
				for (Element link : links) {
				  String linkHref = link.attr("href");
//				  String linkText = link.text();
				  // 检测重复
				  if(bloomfilterHelper.exist(linkHref))
					  continue;
				  WebURL webURL = new WebURL();
				  webURL.setURL(linkHref);
				  webURL.setParentUrl(page.getWebURL().getURL());
				  pendingUrls.addUrl(webURL);
				  
				  
				}
				
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (QueueException e) {
			}
		}
		return null;
	}

	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
