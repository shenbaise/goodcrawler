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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
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
//			baiduTest();
			// testGoogle();
			execJavaScript();
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
	
	
	public void testHomePage() throws FailingHttpStatusCodeException,MalformedURLException,IOException {
		final WebClient webClient=new WebClient();
		final HtmlPage startPage=webClient.getPage("http://www.baidu.com");
		System.out.println("标题:"+startPage.getTitleText());
	}
	
	/**
	 * 执行js代码
	 * @throws Exception
	 */
	public static void execJavaScript() throws Exception {
		String TargetURL="http://person.sac.net.cn/pages/registration/sac-publicity.html";
//		String TargetURL="http://www.baidu.com";
		//模拟一个浏览器
		final WebClient webClient=new WebClient(BrowserVersion.FIREFOX_17);
//		final WebClient webClient=new WebClient(BrowserVersion.FIREFOX_10,"http://myproxyserver",8000);   //使用代理
//		final WebClient webClient2=new WebClient(BrowserVersion.INTERNET_EXPLORER_10);
		//设置webClient的相关参数
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		//模拟浏览器打开一个目标网址
		final HtmlPage page=webClient.getPage(TargetURL);
		System.out.println("page.asText=" +page.asText());
		System.out.println("page.getUrl=" +page.getUrl());
		//page.executeJavaScript("javascript:searchFinishPerson('6655',2);");
		System.out.println("------------------");
		ScriptResult sr=page.executeJavaScript("javascript:searchFinishPerson('6655',2);");
		HtmlPage newPage=(HtmlPage)sr.getNewPage();
		System.out.println("new page.asText="+newPage.asText());
		System.out.println("new page.getUrl="+newPage.getUrl());
	}

}
