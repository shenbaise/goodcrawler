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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.BloomfilterHelper;
import org.sbs.goodcrawler.urlmanager.WebURL;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2
 * 默认信息提取工
 */
public class DefaultExtractWorker extends ExtractWorker{
	
	private Log log = LogFactory.getLog(this.getClass());
	BloomfilterHelper bloomfilterHelper = BloomfilterHelper.getInstance();
	
	public DefaultExtractWorker(JobConfiguration conf, Extractor extractor) {
		super(conf, extractor);
	}

	@Override
	public void run() {
		Page page ;
		try {
			while(null!=(page=pendingPages.getPage())){
				work(page);
			}
		} catch (QueueException e) {
			 log.error(e.getMessage());
		}
	}

	@Override
	public void onSuccessed() {
		// do nothing 
	}

	@Override
	public void onFailed(Page page) {
		pendingPages.addFailedPage(page);
	}

	@Override
	public void onIgnored(Page page) {
		// nothing to do
	}

	@Override
	public List<WebURL> getPendingFetchUrls(Page page) {
		List<WebURL> list = new ArrayList<>();
		if(null!=page){
			try {
				Document doc = Jsoup.parse(new String(page.getContentData(),page.getContentCharset()));
				// 获取所有Url并加入Url队列
				Elements links = doc.getElementsByTag("a");
				List<String> regs = conf.getUrlFilterReg();
				List<Pattern> patterns = new ArrayList<>();
				for(String reg:regs){
					Pattern p = Pattern.compile(reg);
					patterns.add(p);
				}
				boolean b = true;
				for (Element link : links) {
				  String linkHref = link.attr("href");
//				  String linkText = link.text();
				  // 检测重复
				  if(bloomfilterHelper.exist(linkHref))
					  continue;
				  // 正则过滤
				  for(Pattern pattern : patterns){
					  if(!pattern.matcher(linkHref).matches()){
						  b = false;
						  break;
					  }
				  }
				  if(!b){
					  continue;
				  }
				  // 域名过滤 
				  // robots过滤
				  
				  WebURL webURL = new WebURL();
				  webURL.setURL(linkHref);
				  webURL.setParentUrl(page.getWebURL().getURL());
				  this.pendingUrls.addUrl(webURL);
				  
				  list.add(webURL);
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (QueueException e) {
			}
		}
		return null;
	}

	@Override
	public ExtractedPage extract(Page page) {
		return null;
	}

}
