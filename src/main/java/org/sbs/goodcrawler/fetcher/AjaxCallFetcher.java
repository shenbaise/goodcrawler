/**
 * @工程 goodcrawler
 * @文件 AjaxCallFetcher.java
 * @时间 2013年12月16日 下午5:54:29
 * @作者  赵自强（shenbaise1001@126.com）
 * @描述 
 */
package org.sbs.goodcrawler.fetcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author 赵自强（shenbaise1001@126.com）
 * @desc 获取ajax异步调用的URL返回内容。
 */
public class AjaxCallFetcher {
	private final Log log = LogFactory.getLog(AjaxCallFetcher.class);
	private final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
	private final ResynchronizingAjaxController ac = new ResynchronizingAjaxController();
	public AjaxCallFetcher(){
		init();
	}
	
	public void init(){
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("net.sourceforge.htmlunit").setLevel(java.util.logging.Level.OFF);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.setJavaScriptTimeout(0);
		webClient.setAjaxController(ac);
		log.info("webClinet inited");
	}
	
	public String getAjaxCallUrl(String url){
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
//				webClient.waitForBackgroundJavaScript(3*1000);
				System.out.println(" @@");
				System.out.println( " t1 ### "+ac.getResynchronizedCallUlr(10000).toString());
			}
		} catch (FailingHttpStatusCodeException e) {
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
