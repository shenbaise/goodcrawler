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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.BloomfilterHelper;
import org.sbs.goodcrawler.urlmanager.PendingUrls;
import org.sbs.goodcrawler.urlmanager.WebURL;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-2
 * 默认的提取器
 */
public class DefaultExtractor extends Extractor {
	
	PendingUrls pendingUrls = PendingUrls.getInstance();
	BloomfilterHelper bloomfilterHelper = BloomfilterHelper.getInstance();
	List<Pattern> patterns = new ArrayList<>();
	List<String> domains = new ArrayList<>();
	
	public DefaultExtractor(JobConfiguration conf) {
		super(conf);
		List<String> regs = conf.getUrlFilterReg();
		for(String reg:regs){
			Pattern p = Pattern.compile(reg);
			patterns.add(p);
		}
		List<String> list = conf.getSeeds();
		for(String seed:list){
			domains.add(getDomain(seed));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.extractor.Extractor#extract(org.sbs.goodcrawler.job.Page)
	 */
	@Override
	public ExtractedPage<?, ?> extract(Page page) {
		if(null!=page){
			try {

/*		相对url转换为绝对url
		Document doc = Jsoup.parse(content,"http://product.pconline.com.cn/mobile/"); 
        Elements links = doc.getElementsByTag("a"); 
        if (!links.isEmpty()) { 
            for (Element link : links) { 
                String linkHref = link.absUrl("href"); 
                String linkText = link.text(); 
                urlMap.put(linkHref, linkText); 
                System.out.println(linkText + ":" + linkHref); 
            } 
        }
*/


				Document doc = Jsoup.parse(new String(page.getContentData(),page.getContentCharset()));
				// 获取所有Url并加入Url队列
				Elements links = doc.getElementsByTag("a");
				List<String> regs = conf.getUrlFilterReg();
				for (Element link : links) {
				  String linkHref = link.attr("href");
//				  String linkText = link.text();
				  
				  // 域名过滤 
				  // robots过滤
				  if(filterUrl(linkHref)){
					WebURL webURL = new WebURL();
					webURL.setURL(linkHref);
					webURL.setParentUrl(page.getWebURL().getURL());
					this.pendingUrls.addUrl(webURL);
				  }
				}
				
				// 抽取信息
				Map<String, String> selects = conf.getSelects();
				ExtracedPage<String,String> epage = new ExtractedPage();
				epage.setWebURL(page.getWebURL());
				Map<String,String> result = new HashMap<>();
				for(Entry entry:selects.entrySet()){
					result.put(entry.getKey(),doc.select(entry.getValue).txt());
				}
				epage.setMessages(result);
//				pendingStore.add(epage);//
				return result;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (QueueException e) {
			}
		}
		return null;
	}
	
	/**
	 * @param url
	 * @desc 过滤url
	 */
	public boolean filterUrl(String url){
		// 检测重复
		  if(bloomfilterHelper.exist(linkHref))
			  return false;
		  // 正则过滤
		  boolean b = true;
		  for(Pattern pattern : patterns){
			  if(!pattern.matcher(linkHref).matches()){
				  b = false;
				  break;
			  }
		  }
		  if(!b){
			  return false;
		  }
	}

	public String getDomain(String url){
		int domainStartIdx = url.indexOf("//") + 2;
		int domainEndIdx = url.indexOf('/', domainStartIdx);
		return url.substring(domainStartIdx, domainEndIdx);
	}
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {

	}

}
