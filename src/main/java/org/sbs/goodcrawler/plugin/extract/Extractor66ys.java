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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.extractor.Extractor;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.jobconf.ExtractConfig;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;
import org.sbs.goodcrawler.urlmanager.WebURL;
import org.sbs.util.BinaryDateDwonLoader;
import org.sbs.util.CharUtil;
import org.sbs.util.DateTimeUtil;
import org.sbs.util.StringHelper;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-7-7
 * extractor for 66ys
 * de precated . use defaultExtractor instead
 */
@Deprecated
public class Extractor66ys extends Extractor {
	private Log log = LogFactory.getLog(this.getClass());
	private BinaryDateDwonLoader dwonLoader = BinaryDateDwonLoader.getInstance();
	private int count = 0;
	/**
	 * @param conf
	 */
	public Extractor66ys(ExtractConfig conf) {
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
		                	url.setJobName(conf.jobName);
		                	try {
								pendingUrls.addUrl(url);
							} catch (QueueException e) {
								 log.error(e.getMessage());
							}
		                }
		            }
		        }
		        // 抽取信息
				Map<String, String> selects = Maps.newHashMap();
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
					result.put("category", "电视剧");
				}else if(u.contains("/zy")){
					result.put("category", "综艺");
				}else if (u.contains("/dm")) {
					result.put("category", "动漫");
				}else {
					result.put("category", "电影");
				}
				// 片名
				String name = doc.select("h1").text();
				if(StringUtils.isBlank(name)){
					name = doc.select(".title").text();
				}
				
				if(StringUtils.isNotBlank(name)){
					name =  CharMatcher.anyOf("[]【】 《》").removeFrom(name);
					name = CharMatcher.anyOf("/").replaceFrom(name, "|");
					result.put("title", name);
				}
				
				// 如果名称中包含“演唱会”、“mv”则将大类归为音乐
				if(StringUtils.isNotBlank(name) 
						&& (name.contains("演唱会") || name.contains("MV")
								|| name.toLowerCase().contains("mtv")
								|| name.contains("mv")
								|| name.contains("巡演")
								|| name.contains("巡回演出"))){
					result.put("category", "音乐");
				}
				// 保存资源源地址
				result.put("url", page.getWebURL().getURL());
				
				for(Entry<String,String> entry:selects.entrySet()){
					Elements elements = doc.select(entry.getValue());
					if(elements.size()==0)
						continue;
					else {
						if("content".equals(entry.getKey()) || "dede_content".equals(entry.getKey())){
							for(Element element :elements){
								// 拿到图片
								Elements imgs = element.select("img[src]");
								StringBuilder sb = new StringBuilder();
								for(Element img:imgs){
									sb.append(img.attr("src")).append(";");
								}
								result.put("thumbnail", sb.toString());
								
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
												result.put("summary", infotext_.substring(start,end));
											}else {
												end = infotext_.lastIndexOf(".");
												if(end>0 && start <end){
													result.put("summary", infotext_.substring(start,end));
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
								
								// 下载地址 （会有多个网站下载地址）
								// html body div.wrap div.mainleft div.contentinfo div#text table tbody tr td anchor a
								Elements elements2 = elements.select("td a");
								Set<String> downList = Sets.newLinkedHashSet();
								for(Element download:elements2){
									downList.add(download.attr("href"));
								}
								TreeMap<String, Object> dd = Maps.newTreeMap();
								dd.put("66ys", downList);
								result.put("download", dd);
							}
						}
					}
//					result.put(entry.getKey(), elements.html());
				}
				// 转换年代为时间
				String nd = (String) result.get("year");;
				if(StringUtils.isNotBlank(nd)){
					try {
						if(nd.contains("年")){
							nd = nd.split("年")[0];
						}else if(nd.contains("/")){
							nd = nd.split("/")[0];
						}else if (nd.contains(".")) {
							nd = nd.split("[.]")[0];
						}else if (nd.contains("-")) {
							nd = nd.split("-")[0];
						}else if (nd.contains("·")) {
							nd = nd.split("·")[0];
						}
						int tem = Integer.parseInt(CharMatcher.DIGIT.retainFrom(nd));
						if(tem>20000){
							tem = tem / 10;
						}
						if(tem>DateTimeUtil.getCurrentYear()){
							tem = DateTimeUtil.getCurrentYear();
						}
						result.put("year", tem);
					} catch (Exception e) {
						System.out.println((String) result.get("year"));
						result.put("year", 1800);
						e.printStackTrace();
					}
				}
				// 
				String tmp = (String)result.get("translation");
				if(StringUtils.isNotBlank(tmp)){
					tmp = CharMatcher.anyOf("[]【】 ").removeFrom(tmp);
					tmp = CharMatcher.anyOf("/").replaceFrom(tmp, "|");
					// 中英文名称做交换
					if(CharUtil.isChinese(tmp)){
						String title = (String) result.get("title");
						if(StringUtils.isNotBlank(title)){
							result.put("translation", title);
							result.put("title", tmp);
						}
					}
				}
				// 转换type为set
				String type = (String)result.get("type");
				if(StringUtils.isNotBlank(type)){
					type = StringHelper.removeAB12Blank(type);
					result.put("type", Sets.newLinkedHashSet(Splitter.on(CharMatcher.anyOf("/\\、，,|")).omitEmptyStrings().split(type)));
				}
				// 转actors为set
				String actor = (String)result.get("actors");
				if(StringUtils.isNotBlank(actor)){
					actor = StringHelper.removeAB12(actor);
					result.put("actors", Sets.newLinkedHashSet(Splitter.on(CharMatcher.anyOf("/\\、，,| ")).omitEmptyStrings().split(actor)));
				}
				// 转换片长、级数
				String duration = (String)result.get("duration");
				if(StringUtils.isNotBlank(duration)){
					duration = CharMatcher.DIGIT.retainFrom(duration);
					result.put("duration", duration);
				}
				// 集数
				String number = (String)result.get("number");
				if(StringUtils.isNotBlank(number)){
					number = CharMatcher.DIGIT.retainFrom(number);
					result.put("number", number);
				}
				count++;
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
		System.out.println(CharMatcher.anyOf("[]").removeFrom("shdflk [斯蒂芬] 斯蒂芬["));
		System.out.println("2013.7.1央一、2013.7.7央八".split("[.]")[0]);
		;
		String s = CharMatcher.WHITESPACE.removeFrom(" 剧 情 / 爱 情  / 家庭 /&&dfWQ/343/df ");
//		s = CharMatcher.inRange('a', 'z').removeFrom(s);
//		s = CharMatcher.inRange('0','9').removeFrom(s);
//		s = CharMatcher.JAVA_ISO_CONTROL.removeFrom(s);
		s = CharMatcher.anyOf("abcdefghijklmnopqrstuvwxyz;&ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.~!@#$%^&*()-+= ").removeFrom(s);
		List<String> list = Lists.newArrayList(Splitter.on(CharMatcher.anyOf("/\\")).omitEmptyStrings().trimResults().split(s));
		System.out.println(list);
		System.out.println(CharMatcher.SINGLE_WIDTH.matchesAnyOf("!@3 号  nihaoma"));
		
		System.out.println(Splitter.on(CharMatcher.anyOf("/\\、，,| ")).omitEmptyStrings().split("铃木亮平 | 清水富美加 | 片濑那奈 | 安田显  韦斯利·斯奈普斯 Wesley Snipes / 瑞利·史密斯 Riley Smith / 塔尼特·菲尼克斯 Tanit Phoenix /Kevin Howarth / Simona Brhlikova / Steven Elder / 帕特里克·博金 PatrickBergin "));
		System.out.println("今天是个很好的日子".substring("今天是个很好的日子".lastIndexOf("很好")+"很好".length()));
	}
	
	private TreeMap<String, String> infoName = new TreeMap<>();
	{
		
		infoName.put("中文名", "title");
		infoName.put("中文剧名", "title");
		infoName.put("影片原名", "title");
		infoName.put("剧 名", "title");
		infoName.put("原名", "title");
		infoName.put("片名", "title");
		infoName.put("中文译名", "translation");
		infoName.put("外文名", "translation");
		infoName.put("英文名", "translation");
		infoName.put("译名", "translation");
		infoName.put("国家", "nation");
		infoName.put("国别", "nation");
		infoName.put("地区", "area");
		infoName.put("出品时间", "publishTime");
		infoName.put("制片地区", "area");
		infoName.put("年代", "year");
		infoName.put("出品时间", "year");
		infoName.put("出品年代", "year");
		infoName.put("上映时间", "year");
		infoName.put("上映日期", "year");
		infoName.put("集数", "number");
		infoName.put("监制", "studioManager");
		infoName.put("大类", "category");
		infoName.put("类别", "type");
		infoName.put("分类", "type");
		infoName.put("种类", "type");
		infoName.put("类型", "type");
		infoName.put("语言", "lang");
		infoName.put("发音", "lang");
		infoName.put("视频尺寸", "size");
		infoName.put("评分", "score");
		infoName.put("片长", "duration");
		infoName.put("时长", "duration");
		infoName.put("导演", "director");
		infoName.put("主演", "actors");
		infoName.put("简介", "summary");
		infoName.put("剧情", "summary");
		infoName.put("介绍", "summary");
		infoName.put("下载地址", "download");
		//infoName.put("在线观看", "online");
		// TODO 66ys不需要在线观看
		infoName.put("时间", "year");
		infoName.put("演员", "actors");
	}
	
	public HashMap<String, Object> getInfoName(String s,HashMap<String, Object> map){
		try {
			String temp = null;
			if(s.contains("：")){
				String ss[] = s.split("：");
				if(ss.length>=2){
					temp = infoName.get(CharMatcher.WHITESPACE.removeFrom(ss[0]));
					if(StringUtils.isNotBlank(temp)){
						map.put(temp, CharMatcher.WHITESPACE.removeFrom(ss[1]));
					}
				}
			}else if(s.contains(":")){
				String ss[] = s.split(":");
				if(ss.length>=2){
					temp = infoName.get(CharMatcher.WHITESPACE.removeFrom(ss[0]));
					if(StringUtils.isNotBlank(temp)){
						map.put(temp, CharMatcher.WHITESPACE.removeFrom(ss[1]));
					}
				}
			} else if (s.contains("】")) {
				String ss[] = s.split("】");
				if(ss.length>=2){
					temp = infoName.get(CharMatcher.WHITESPACE.removeFrom(ss[0]));
					if(StringUtils.isNotBlank(temp)){
						map.put(temp, CharMatcher.WHITESPACE.removeFrom(ss[1]));
					}
				}
			}
			else {
				if(s.length()>6){
					Set<String> set = infoName.keySet();
					int p =0;
					s = CharMatcher.WHITESPACE.removeFrom(s);
					for (String key : set) {
						if(s.contains(key)){
							p = s.indexOf(key)+key.length();
							if(p>4)break;
							if(p<s.length()){
								String sub = s.substring(s.indexOf(key)+key.length());
								if(null==map.get(infoName.get(key))){
									map.put(infoName.get(key), sub);
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
