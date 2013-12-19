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
package org.sbs.htmlunit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.sbs.goodcrawler.fetcher.FetcherInstance;
import org.sbs.goodcrawler.fetcher.PageFetchResult;
import org.sbs.goodcrawler.fetcher.PageFetcher;
import org.sbs.goodcrawler.fetcher.ResynchronizingAjaxController;
import org.sbs.goodcrawler.urlmanager.WebURL;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @desc htmlunit 测试。
 */
public class HtmlUnitTest {

	public static void main(String[] args) {
		try {
			// testCrawler();
			// baiduTest();
			// testGoogle();
			// execJavaScript();
			// get100Times_htmlunit();
			// get100Times_httpclient();
			testYouku();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 百度form提交
	 */
	public static void baiduTest() {
		final WebClient webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		webClient.getOptions().setThrowExceptionOnScriptError(true);
		// webClient.getOptions().getProxyConfig().set...();
		HtmlPage page;
		try {
			page = webClient.getPage("http://www.baidu.com");
			final HtmlForm form = page.getFormByName("f");
			HtmlInput input = form.getInputByName("wd");
			input.setValueAttribute("蛤");
			HtmlSubmitInput button = page.getHtmlElementById("su");
			Page p = button.click();
			// HtmlPage p = button.click();
			System.out.println(p.getWebResponse().getContentAsString());
			webClient.closeAllWindows();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * google form提交
	 * 
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public static void testGoogle() throws FailingHttpStatusCodeException,
			IOException {
		WebClient client = new WebClient();
		URL url = new URL("http://www.google.com.hk");
		HtmlPage page = (HtmlPage) client.getPage(url);
		HtmlForm form = page.getFormByName("f");
		HtmlTextInput input = (HtmlTextInput) form.getInputByName("q");
		input.setValueAttribute("james");
		HtmlSubmitInput ok = (HtmlSubmitInput) form.getInputByName("btnG");
		HtmlPage p = ok.click();
		System.out.println(p.asText());
	}

	public void testHomePage() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		final WebClient webClient = new WebClient();
		final HtmlPage startPage = webClient.getPage("http://www.baidu.com");
		System.out.println("标题:" + startPage.getTitleText());
	}

	/**
	 * 执行js代码
	 * 
	 * @throws Exception
	 */
	public static void execJavaScript() throws Exception {
		String url = "http://person.sac.net.cn/pages/registration/sac-publicity.html";
		// String url="http://www.baidu.com";
		// 模拟一个浏览器
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
		// final WebClient webClient=new
		// WebClient(BrowserVersion.FIREFOX_10,"http://myproxyserver",8000);
		// //使用代理
		// final WebClient webClient2=new
		// WebClient(BrowserVersion.INTERNET_EXPLORER_10);
		// 设置webClient的相关参数
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		// 模拟浏览器打开一个目标网址
		final HtmlPage page = webClient.getPage(url);
		System.out.println("page.asText=" + page.asText());
		System.out.println("page.getUrl=" + page.getUrl());
		// page.executeJavaScript("javascript:searchFinishPerson('6655',2);");
		System.out.println("------------------");
		ScriptResult sr = page
				.executeJavaScript("javascript:searchFinishPerson('6655',2);");
		HtmlPage newPage = (HtmlPage) sr.getNewPage();
		System.out.println("new page.asText=" + newPage.asText());
		System.out.println("new page.getUrl=" + newPage.getUrl());
	}

	public static void testYouku() throws Exception {
		String url = "http://v.youku.com/v_show/id_XNDc2MDkzMTIw.html";
		String xurl = "http://v.youku.com/v_vpofficiallistv5/id_119023280_showid_271942_page_2?__rt=1&__ro=listitem_page2";
		// String a = "<a page=\"2\">178-101</a>";
		// String url="http://www.baidu.com";
		// 模拟一个浏览器
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);

		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		// final WebClient webClient=new
		// WebClient(BrowserVersion.FIREFOX_10,"http://myproxyserver",8000);
		// //使用代理
		// final WebClient webClient2=new
		// WebClient(BrowserVersion.INTERNET_EXPLORER_10);
		// 设置webClient的相关参数
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.waitForBackgroundJavaScript(600*1000);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		
		webClient.getOptions().setJavaScriptEnabled(true);  
		/*
		webClient.setJavaScriptTimeout(3600*1000);  
		webClient.getOptions().setRedirectEnabled(true);  
		webClient.getOptions().setThrowExceptionOnScriptError(true);  
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);  
		webClient.getOptions().setTimeout(3600*1000);  
		webClient.waitForBackgroundJavaScript(600*1000);  
		*/
//		webClient.waitForBackgroundJavaScript(600*1000);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController()); 
		
		// 模拟浏览器打开一个目标网址
		final HtmlPage page = webClient.getPage(url);
//		该方法在getPage()方法之后调用才能生效
		webClient.waitForBackgroundJavaScript(1000*3);
		webClient.setJavaScriptTimeout(0);
//		Thread.sleep(1000 *3L);
//		String js = "javascript:checkShowFollow('271942','2');";
//		ScriptResult sr = page.executeJavaScript(js);
//		HtmlPage newPage = (HtmlPage) sr.getNewPage();
//		System.out.println("new page.asText=" + newPage.asText());
//		System.out.println("page.asText=" + page.asText());
//		System.out.println("page.getUrl=" + page.getUrl());
		List links = (List) page.getByXPath ("//*[@id=\"groups_tab\"]/div[1]/ul/li[1]/a");
		HtmlAnchor anchor = page.getFirstByXPath("//*[@id=\"rkLst0\"]/table/tbody/tr[1]/td[2]/a");
		System.out.println(anchor.getAttribute("href"));
//		page.getHtmlElementById(id)
		if(null!=links){
			System.out.println(links.size());
			HtmlAnchor link = (HtmlAnchor) links.get(0);
			System.out.println(link.asXml());
			HtmlPage p = link.click();
			webClient.waitForBackgroundJavaScript(1000*3L);
//			webClient.waitForBackgroundJavaScriptStartingBefore(1000L);
//			Thread.sleep(3000L);
			System.out.println(p.asText());
		}
	}
	
	@Test
	public void testYouku_n_Thread() throws Exception {
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String url = "http://v.youku.com/v_show/id_XNDc2MDkzMTIw.html";
				String xurl = "http://v.youku.com/v_vpofficiallistv5/id_119023280_showid_271942_page_2?__rt=1&__ro=listitem_page2";
				// 模拟一个浏览器
				final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);

				LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
				java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF);
				webClient.getOptions().setThrowExceptionOnScriptError(false);
				webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
				webClient.getOptions().setJavaScriptEnabled(true);
				webClient.getOptions().setActiveXNative(false);
				webClient.getOptions().setCssEnabled(false);
				webClient.getOptions().setThrowExceptionOnScriptError(false);
				webClient.setJavaScriptTimeout(0);
				// 在同一个线程中调用此方法有效。
				webClient.waitForBackgroundJavaScript(3*1000);
				final ResynchronizingAjaxController ac = new ResynchronizingAjaxController();
				webClient.setAjaxController(ac); 
				try {
					HtmlPage page = webClient.getPage(url);
					webClient.setJavaScriptTimeout(0);
					webClient.waitForBackgroundJavaScript(3*1000);
					List links = (List) page.getByXPath ("//*[@id=\"groups_tab\"]/div[1]/ul/li[1]/a");
					if(null!=links){
						System.out.println(links.size());
						HtmlAnchor link = (HtmlAnchor) links.get(0);
						System.out.println(link.asXml());
						System.err.println("now1 = " + (new Date().toLocaleString()));
						link.click();
//						webClient.waitForBackgroundJavaScript(3*1000);
						System.out.println(" @@");
						System.out.println( " t1 ### "+ac.getResynchronizedCallUlr(10000).toString());
					}
				} catch (FailingHttpStatusCodeException e) {
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "t1").start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String url = "http://v.youku.com/v_show/id_XNDc2MDkzMTIw.html";
				String xurl = "http://v.youku.com/v_vpofficiallistv5/id_119023280_showid_271942_page_2?__rt=1&__ro=listitem_page2";
				// 模拟一个浏览器
				final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);

				LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
				java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF);
				webClient.getOptions().setThrowExceptionOnScriptError(false);
				webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
				webClient.getOptions().setJavaScriptEnabled(true);
				webClient.getOptions().setActiveXNative(false);
				webClient.getOptions().setCssEnabled(false);
				webClient.getOptions().setThrowExceptionOnScriptError(false);
				webClient.setJavaScriptTimeout(0);
				webClient.waitForBackgroundJavaScript(3*1000);
				final ResynchronizingAjaxController ac = new ResynchronizingAjaxController();
				webClient.setAjaxController(ac); 
				try {
					HtmlPage page = webClient.getPage(url);
					webClient.setJavaScriptTimeout(0);
					webClient.waitForBackgroundJavaScript(3*1000);
					List links = (List) page.getByXPath ("//*[@id=\"groups_tab\"]/div[1]/ul/li[1]/a");
					if(null!=links){
						System.out.println(links.size());
						HtmlAnchor link = (HtmlAnchor) links.get(0);
						System.out.println(link.asXml());
						System.err.println("now2 = " + (new Date().toLocaleString()));
						Page p = link.click();
						System.out.println( " t2 ### "+ac.getResynchronizedCallUlr().toString());
//						System.out.println(p.getUrl().toString());
					}
				} catch (FailingHttpStatusCodeException e) {
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "t2").start();
		
	}
	public static void get100Times_htmlunit() {
		long c = System.currentTimeMillis();
		String url = "http://person.sac.net.cn/pages/registration/sac-publicity.html";
		// String url="http://www.baidu.com/";
		// 模拟一个浏览器
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
		// final WebClient webClient=new
		// WebClient(BrowserVersion.FIREFOX_10,"http://myproxyserver",8000);
		// //使用代理
		// final WebClient webClient2=new
		// WebClient(BrowserVersion.INTERNET_EXPLORER_10);
		// 设置webClient的相关参数
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		System.out.println("init costs = " + (System.currentTimeMillis() - c));
		c = System.currentTimeMillis();
		// 模拟浏览器打开一个目标网址
		try {
			for (int i = 0; i < 100; i++) {
				final HtmlPage page = webClient.getPage(url);
				System.out.println(page.getTitleText());
			}
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("get costs = " + (System.currentTimeMillis() - c));
		// about 243643 ms | 113722 ms
	}

	public static void get100Times_httpclient() {
		long c = System.currentTimeMillis();
		// String
		// url="http://person.sac.net.cn/pages/registration/sac-publicity.html";
		String url = "http://v.youku.com/v_show/id_XNDc2MDkzMTIw.html";
		PageFetcher fetcher = FetcherInstance.getFetcher();
		System.out.println("init costs = " + (System.currentTimeMillis() - c));
		c = System.currentTimeMillis();
		WebURL webUrl = new WebURL();
		webUrl.setURL(url);
		for (int i = 0; i < 1; i++) {
			PageFetchResult p = fetcher.fetchHeader(webUrl);
			try {
				// Jsoup.parse(p.getEntity().getContent(), "gb2312",
				// "http://person.sac.net.cn");
				System.out.println(new String(p.getEntity().getContent()
						.toString()));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("get costs = " + (System.currentTimeMillis() - c));
		// about 22018 ms || 36952 ms
	}
}
