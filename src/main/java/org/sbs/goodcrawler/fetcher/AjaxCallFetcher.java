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
package org.sbs.goodcrawler.fetcher;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc 获取ajax异步调用的URL返回内容。
 */
public class AjaxCallFetcher {
	private final Log log = LogFactory.getLog(AjaxCallFetcher.class);
	private final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
	private ResynchronizingAjaxController ac = new ResynchronizingAjaxController();
//	private Object lock = new Object();
	
	public AjaxCallFetcher(){
		init();
	}
	
	public void init(){
//		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
//		java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setJavaScriptTimeout(0);
		webClient.setAjaxController(ac);
		log.info("webClinet inited.");
	}
	
	/**
	 * 获取xpath所在元素经过action动作以后的ajax异步调用地址
	 * @param page
	 * @param xpath
	 * @param type
	 * @param action
	 * @return
	 */
	public URL getAjaxCallUrl(HtmlPage page ,String xpath,String type,String action){
		Object x = page.getFirstByXPath(xpath);
		if(null!=x){
			if(x.getClass().getSimpleName().toLowerCase().equals(type.toLowerCase())){
			Method[] ms = x.getClass().getMethods();
				for(Method m:ms){
					if(m.getName().toLowerCase().equals(action.toLowerCase()) && m.getParameterTypes().length==0){
						try {
							m.invoke(x, null);
							ac = (ResynchronizingAjaxController) webClient.getAjaxController();
							return ac.getResynchronizedCallUlr(3000L);
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						} finally {
						}
						break;
					}
				}
			}
		}
		return null;
	}
	/**
	 *  获取xpath所在元素经过action动作以后的返回内容
	 * @param page
	 * @param xpath
	 * @param type
	 * @param action
	 * @return
	 */
	public Object getActionResultFrist(HtmlPage page,String xpath,String type,String action){
		Object x = page.getFirstByXPath(xpath);
		if(null!=x){
			if(x.getClass().getSimpleName().toLowerCase().equals(type.toLowerCase())){
			Method[] ms = x.getClass().getMethods();
				for(Method m:ms){
					if(m.getName().toLowerCase().equals(action.toLowerCase()) && m.getParameterTypes().length==0){
						try {
							Object o = m.invoke(x, null);
							webClient.waitForBackgroundJavaScript(3000L);
							return o;
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						} finally {
						}
						break;
					}
				}
			}
		}
		return null;
	}
	
	public List<Object> getActionResults(HtmlPage page,String xpath,String type,String action){
		List<?> xs = page.getByXPath(xpath);
		List<Object> results = Lists.newArrayList();
		if(null!=xs){
			for(Object x:xs){
				if(x.getClass().getSimpleName().toLowerCase().equals(type.toLowerCase())){
				Method[] ms = x.getClass().getMethods();
					for(Method m:ms){
						if(m.getName().toLowerCase().equals(action.toLowerCase()) && m.getParameterTypes().length==0){
							try {
								Object o = m.invoke(x, null);
								webClient.waitForBackgroundJavaScript(3000L);
								results.add(o);
							} catch (IllegalAccessException
									| IllegalArgumentException
									| InvocationTargetException e) {
								e.printStackTrace();
							} finally {
							}
							break;
						}
					}
				}
			}
		}
		return results;
	}
	/**
	 * 获取xpath所在对象
	 * @param page
	 * @param xpath
	 * @param type
	 * @param action
	 * @return
	 */
	public Object getElement(HtmlPage page,String xpath){
		return page.getFirstByXPath(xpath);
	}
	
	public List<?> getElements(HtmlPage page,String xpath){
		return page.getByXPath(xpath);
	}
	
	/**
	 * fetch Page As Xml
	 * @param url
	 * @param javascript
	 * @return
	 */
	public String fetchPageAsXml(String url,boolean javascript){
		try {
			HtmlPage page = webClient.getPage(url);
			webClient.setJavaScriptTimeout(0);
			webClient.waitForBackgroundJavaScript(3*1000);
			return page.asXml();
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取HtmlPage
	 * @param url
	 * @param javascript
	 * @return
	 */
	public HtmlPage fetchPage(String url,boolean javascript){
		try {
			HtmlPage page = webClient.getPage(url);
			webClient.setJavaScriptTimeout(0);
			webClient.waitForBackgroundJavaScript(3*1000);
			return page;
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Test
	public void test(){
		String url = "http://v.youku.com/v_show/id_XNDc2MDkzMTIw.html";
		String anchor = "//*[@id=\"groups_tab\"]/div[1]/ul/li[1]/a";
		AjaxCallFetcher fetcher = new AjaxCallFetcher();
		HtmlPage p = fetcher.fetchPage(url, true);
		URL r = fetcher.getAjaxCallUrl(p,anchor,"HtmlAnchor","click");
		Object o = fetcher.getActionResultFrist(p,anchor, "htmlanchor", "click");
		System.out.println(r.toString());
		System.out.println(o.getClass().getSimpleName());
		System.out.println(((HtmlPage)o).asText());
	}
}
