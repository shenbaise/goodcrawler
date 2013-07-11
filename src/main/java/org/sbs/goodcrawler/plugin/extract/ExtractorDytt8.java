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
package org.sbs.goodcrawler.plugin.extract;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.extractor.Extractor;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.WebURL;
import org.sbs.util.StringUtil;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-7
 * extractor for 66ys
 */
public class ExtractorDytt8 extends Extractor {
	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * @param conf
	 */
	public ExtractorDytt8(JobConfiguration conf) {
		super(conf);
	}

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.extractor.Extractor#onExtract(org.sbs.goodcrawler.job.Page)
	 */
	@Override
	public ExtractedPage<?, ?> onExtract(Page page) {
		if(null!=page){
			try {
				
				Document doc = Jsoup.parse(new String(page.getContentData(),page.getContentCharset()), urlUtils.getBaseUrl(page.getWebURL().getURL()));
				if(null!=page.getWebURL().getURL()&&page.getWebURL().getURL().contains("game/"))
					return null;
				// 提取Url，放入待抓取Url队列
				Elements links = doc.getElementsByTag("a"); 
		        if (!links.isEmpty()) { 
		            for (Element link : links) { 
		                String linkHref = link.absUrl("href"); 
		                if(StringUtils.isNotBlank(linkHref) && filterUrls(linkHref)){
		                	try {
		                		WebURL url = new WebURL();
		                		
			                	url.setURL(linkHref);
			                	url.setJobName(conf.getName());
								pendingUrls.addUrl(url);
							} catch (QueueException e) {
								 log.error(e.getMessage());
							} catch (Exception e) {
								log.error(e.getMessage());
							}
		                }
		            }
		        }
		        // 抽取信息
				Map<String, String> selects = conf.getSelects();
				ExtractedPage<String,String> epage = pendingStore.new ExtractedPage<String, String>();
				epage.setUrl(page.getWebURL());
				HashMap<String, String> result = new HashMap<>();
				Elements text = doc.select("#Zoom");
				if(null==text || text.size()==0){
					return null;
				}
				String name = doc.select("h1").text();
				name = name.replace("《", "").replace("<<", "").replace("》", "").replace(">>", "");
				result.put("movie", name);
//				result.put("_id", name);
				String ts[] = doc.select("h2 a").text().split(" ");
				if(ts.length>=2){
					result.put("type", ts[1].trim());
				}else {
					result.put("type", "unknow");
				}
				result.put("url", page.getWebURL().getURL());
				for(Entry<String,String> entry:selects.entrySet()){
					Elements elements = doc.select(entry.getValue());
					if(elements.isEmpty())
						return null;
					else {
						if("content".equals(entry.getKey())){
							
							for(Element element :elements){
								// 拿到图片
								Elements imgs = element.select("img[src]");
								StringBuilder sb = new StringBuilder();
								for(Element img:imgs){
									sb.append(img.attr("src")).append(";");
								}
								result.put("img", sb.toString());
								// 影片信息
								Elements movieInfos = element.select("p");
								for(Element info:movieInfos){
									String infotext = info.text();
									try {
										String infotext_ = info.html();
										int start,end=0;
										start = infotext_.indexOf("◎简");
										if(start>0){
											end = infotext_.lastIndexOf("。");
											if(end>0 && start <end){
												result.put("jq", infotext_.substring(start,end));
											}else {
												end = infotext_.lastIndexOf(".");
												if(end>0 && start <end){
													result.put("jq", infotext_.substring(start,end));
												}
											}
										}
										infotext_ = null;
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									if(infotext.startsWith("◎")){
										String ss[] = infotext.split("◎");
										for(String s:ss){
											s.trim();
											result = getInfoName(s, result);
										}
									}else if(infotext.startsWith("【")){
										String ss[] = infotext.split("【");
										for(String s:ss){
											s.trim();
											result = getInfoName(s, result);
										}
									}else if (infotext.contains("：")) {
										infotext = info.html();
										String[] ss = infotext.split("<br />");
										for(String s:ss){
											s.trim();
											result = getInfoName(s, result);
										}
									}
									else if (infotext.contains(":")) {
										infotext = info.html();
										String[] ss = infotext.split("<br />");
										for(String s:ss){
											s.trim();
											result = getInfoName(s, result);
										}
									}
								}
								
								
//								if(result.size()<5){
//									result.put("content", value)
//								}
								
								// 下载地址
								Elements elements2 = elements.select("td");
								sb.setLength(0);
								for(Element download:elements2){
									sb.append(download.text()).append(";");
								}
								result.put("download", sb.toString());
							}
						}
					}
//					result.put(entry.getKey(), elements.html());
				}
				if(StringUtils.isNotBlank(result.get("ym"))){
					result.put("name", result.get("ym"));
				}
				epage.setMessages((HashMap<String, String>) result);
				try {
					pendingStore.addExtracedPage(epage);
				} catch (QueueException e) {
					 log.error(e.getMessage());
				}
				return epage;
			} catch (UnsupportedEncodingException e) {
				 log.error(e.getMessage());
			} 
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.extractor.Extractor#beforeExtract(org.sbs.goodcrawler.job.Page)
	 */
	@Override
	public ExtractedPage<?, ?> beforeExtract(Page page) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.extractor.Extractor#afterExtract(org.sbs.goodcrawler.job.Page)
	 */
	@Override
	public ExtractedPage<?, ?> afterExtract(Page page) {
		return null;
	}

	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {

	}
	
	private HashMap<String, String> infoName = new HashMap<>();
	{
		infoName.put("片　　名", "pm");
		infoName.put("片　名", "pm");
		infoName.put("片名", "pm");
		infoName.put("译　　名", "ym");
		infoName.put("译　名", "ym");
		infoName.put("译名", "ym");
		infoName.put("国　　家", "gj");
		infoName.put("国　家", "gj");
		infoName.put("国家", "gj");
		infoName.put("地　　区", "gj");
		infoName.put("地　区", "gj");
		infoName.put("出品时间", "nd");
		infoName.put("制片地区", "dq");
		infoName.put("编剧", "bj");
		infoName.put("集数", "gj");
		infoName.put("监制", "gj");
		infoName.put("类　　别", "lb");
		infoName.put("类　别", "lb");
		infoName.put("类别", "lb");
		infoName.put("语　　言", "yy");
		infoName.put("语　言", "yy");
		infoName.put("语言", "yy");
		infoName.put("字　　幕", "zm");
		infoName.put("字　幕", "zm");
		infoName.put("字幕", "zm");
		infoName.put("文件格式", "gs");
		infoName.put("视频尺寸", "cc");
		infoName.put("IMDB评分", "imdb-pf");
		infoName.put("评　　分", "pf");
		infoName.put("评　分", "pf");
		infoName.put("评分", "pf");
		infoName.put("文件大小", "dx");
		infoName.put("片　　长", "pc");
		infoName.put("片　长", "pc");
		infoName.put("片长", "pc");
		infoName.put("导　　演", "dy");
		infoName.put("导　演", "dy");
		infoName.put("导演", "dy");
		infoName.put("主　　演", "zy");
		infoName.put("主　演", "zy");
		infoName.put("主演", "zy");
		infoName.put("简　　介", "jq");
		infoName.put("简　介", "jq");
		infoName.put("剧情介绍", "jq");
		infoName.put("下载地址", "xzdz");
		infoName.put("在线观看", "zzdz");
		infoName.put("年　　代", "nd");
		infoName.put("年代", "nd");
		infoName.put("时间", "pc");
		infoName.put("音频", "yy");
		infoName.put("演员", "zy");
		
	}
	
	public HashMap<String, String> getInfoName(String s,HashMap<String, String> map){
		try {
			String temp = null;
			if(s.contains("：")){
				String ss[] = s.split("：");
				if(ss.length>=2){
					temp = infoName.get(ss[0].trim());
					if(StringUtils.isNotBlank(temp)){
						map.put(temp, ss[1].trim());
					}
				}
			}else if(s.contains("：")){
				String ss[] = s.split(":");
				if(ss.length>=2){
					temp = infoName.get(ss[0]);
					if(StringUtils.isNotBlank(temp)){
						map.put(temp, ss[1].trim());
					}
				}
			} else if (s.contains("】")) {
				String ss[] = s.split("】");
				if(ss.length>=2){
					temp = infoName.get(ss[0]);
					if(StringUtils.isNotBlank(temp)){
						map.put(temp, ss[1].trim());
					}
				}
			}
			else {
				if(s.length()>6){
					temp = infoName.get(s.substring(0, 4));
					if(StringUtils.isNotBlank(temp)){
						map.put(temp, s.substring(5).trim());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
