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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class Extractor66ys extends Extractor {
	private Log log = LogFactory.getLog(this.getClass());
	/**
	 * @param conf
	 */
	public Extractor66ys(JobConfiguration conf) {
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
				
				// 提取Url，放入待抓取Url队列
				Elements links = doc.getElementsByTag("a"); 
		        if (!links.isEmpty()) { 
		            for (Element link : links) { 
		                String linkHref = link.absUrl("href"); 
		                if(filterUrls(linkHref)){
		                	WebURL url = new WebURL();
		                	url.setURL(linkHref);
		                	url.setJobName(conf.getName());
		                	try {
								pendingUrls.addUrl(url);
							} catch (QueueException e) {
								 log.error(e.getMessage());
							}
		                }
		            }
		        }
		        // 抽取信息
				Map<String, String> selects = conf.getSelects();
				ExtractedPage<String,Object> epage = pendingStore.new ExtractedPage<String, Object>();
				epage.setUrl(page.getWebURL());
				HashMap<String, Object> result = new HashMap<>();
				Elements text = doc.select("#text");
				if(null==text || text.size()==0){
					if(doc.select("#dede_content").size()==0){
						return null;
					}
				}
				
				// 电视剧、电影、综艺大类分类
				String u = page.getWebURL().getURL();
				if(u.contains("/dsj")){
					result.put("t", "电视剧");
				}else if(u.contains("/zy")){
					result.put("t", "综艺");
				}else if (u.contains("/dm")) {
					result.put("t", "动漫");
				}else {
					result.put("t", "电影");
				}
				// 片名
				String name = doc.select("h1").text();
				if(StringUtils.isBlank(name)){
					name = doc.select(".title").text();
				}
				
				if(StringUtils.isNotBlank(name)){
					name = name.replace("《", "").replace("<<", "")
							.replace("》", "").replace(">>", "")
							.replace("】", "").replace("【", "")
							.replace("-", "").replace("_", "");
					result.put("n", name);
				}
				
				// 如果名称中包含“演唱会”、“mv”则将大类归为音乐
				if(StringUtils.isNotBlank(name) 
						&& (name.contains("演唱会") || name.contains("MV")
								|| name.contains("mv"))){
					result.put("t", "音乐");
				}
				// 保存Url
				result.put("url", page.getWebURL().getURL());
				
				for(Entry<String,String> entry:selects.entrySet()){
//					result.put(entry.getKey(),doc.select(entry.getValue()).html());
					Elements elements = doc.select(entry.getValue());
					if(elements.size()==0)
						continue;
					else {
						if("content".equals(entry.getKey()) || "dede_content".equals(entry.getKey())){
							
							for(Element element :elements){
								// 拿到图片
								Elements imgs = element.select("img[src]");
								List<String> imageList = new ArrayList<>();
								for(Element img:imgs){
									imageList.add(img.attr("src"));
								}
								result.put("img", imageList);
								
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
												result.put("jj", infotext_.substring(start,end));
											}else {
												end = infotext_.lastIndexOf(".");
												if(end>0 && start <end){
													result.put("jj", infotext_.substring(start,end));
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
								
								// 信息不足，全保存
								if(result.size()<4){
									result.put("c", element.text());
								}
								
								// 下载地址
								// html body div.wrap div.mainleft div.contentinfo div#text table tbody tr td anchor a
								Elements elements2 = elements.select("td a");
								List<String> downList = new ArrayList<>();
								for(Element download:elements2){
									downList.add(download.attr("href"));
								}
								result.put("d", downList);
							}
						}
					}
//					result.put(entry.getKey(), elements.html());
				}
				// 转换年代为时间
				if(StringUtils.isNotBlank((String) result.get("nd"))){
					try {
						String nd = (String) result.get("nd");
						if(nd.contains("年")){
							nd = nd.split("年")[0];
						}else if(nd.contains("/")){
							nd = nd.split("/")[0];
						}else if (nd.contains("-")) {
							nd = nd.split("-")[0];
						}
						result.put("nd", Integer.parseInt(nd));
					} catch (Exception e) {
						result.put("nd", 1800);
					}
					
				}
				// id
				if(StringUtils.isBlank((String)result.get("n"))){
					if(StringUtils.isNotBlank((String)result.get("pm"))){
						result.put("n", result.get("pm"));
					}
				}
				
				epage.setMessages(result);
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
		infoName.put("片　　名", "bm");
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
		infoName.put("集数", "js");
		infoName.put("监制", "jz");
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
		infoName.put("简　　介", "jj");
		infoName.put("简　介", "jj");
		infoName.put("剧情介绍", "jj");
		infoName.put("下载地址", "d");
		infoName.put("在线观看", "k");
		infoName.put("年　　代", "nd");
		infoName.put("年代", "nd");
		infoName.put("出品时间", "nd");
		infoName.put("上映时间", "nd");
		infoName.put("时间", "pc");
		infoName.put("音频", "yy");
		infoName.put("演员", "zy");
	}
	
	public HashMap<String, Object> getInfoName(String s,HashMap<String, Object> map){
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
