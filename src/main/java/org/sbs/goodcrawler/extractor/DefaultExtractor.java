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
package org.sbs.goodcrawler.extractor;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.jobconf.ExtractConfig;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.WebURL;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2
 * 默认的提取器
 */
public class DefaultExtractor extends Extractor {
	
	private Log log = LogFactory.getLog(this.getClass());
	public DefaultExtractor(ExtractConfig conf) {
		super(conf);
	}

	@Override
	public ExtractedPage<String, Object> onExtract(Page page) {
		if(null!=page){
			ExtractedPage<String,Object> epage = null;
			try {
				Document doc = Jsoup.parse(new String(page.getContentData(),page.getContentCharset()), urlUtils.getBaseUrl(page.getWebURL().getURL()));
				// 提取Url，放入待抓取Url队列
				Elements links = doc.getElementsByTag("a"); 
		        if (!links.isEmpty()) { 
		            for (Element link : links) { 
		                String linkHref = link.absUrl("href"); 
		                if(filterUrls(linkHref)){
		                	WebURL url = new WebURL();
		                	url.setURL(linkHref);
		                	url.setJobName(conf.jobName);
		                	try {
		                		// TODO 考虑队列容量，调整Url策略。
								pendingUrls.addUrl(url);
							} catch (QueueException e) {
								 log.error(e.getMessage());
							}
		                }
		            }
		        }
		        // 抽取信息
				try {
					
					epage = pendingStore.new ExtractedPage<String, Object>();
					Map<String, Object> result = conf.getContentSeprator(doc,page.getWebURL().getURL());
					if(null!=result && result.size()>0){
						epage.setUrl(page.getWebURL());
						epage.setMessages((HashMap<String, Object>) result);
						pendingStore.addExtracedPage(epage);
						epage.setResult(ExtractResult.success);
						return epage;
					}
					epage.setResult(ExtractResult.ignored);
				} catch (QueueException e) {
					 log.error(e.getMessage());
				} catch (ExtractException e) {
					e.printStackTrace();
				}
				
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage());
			}
			return epage;
		}
		return null;
	}

	@Override
	public ExtractedPage<?, ?> beforeExtract(Page page) {
		return null;
	}

	@Override
	public ExtractedPage<?, ?> afterExtract(Page page) {
		return null;
	}

}
