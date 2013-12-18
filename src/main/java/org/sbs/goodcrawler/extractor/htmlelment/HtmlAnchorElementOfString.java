/**
 * @工程 goodcrawler
 * @文件 HtmlAnchorElement.java
 * @时间 2013年12月18日 下午5:35:49
 * @作者 shenbaise（shenbaise1001@126.com）
 * @描述 
 */
package org.sbs.goodcrawler.extractor.htmlelment;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.sbs.goodcrawler.fetcher.ResynchronizingAjaxController;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc 该类获取由javascript生成的ajax异步调用的地址。
 */
public class HtmlAnchorElementOfString extends AbstractHtmlElement<String>{
	
	private String content;
	
	@Override
	public String getContent() {
		if(page!=null){
			if (null != content && !newPage) {
				return content;
			}
			if(type .equals(HtmlElementExtractType.id)){
				DomElement d = page.getElementById(value);
				d.getAttribute("");
			}else if (type.equals(HtmlElementExtractType.xpath)) {
				HtmlAnchor anchor = page.getFirstByXPath(value);
				try {
					anchor.click();
					AjaxController ac = webClient.getAjaxController();
					if(ac instanceof ResynchronizingAjaxController){
						ResynchronizingAjaxController rac = (ResynchronizingAjaxController)ac;
						URL url = rac.getResynchronizedCallUlr(2000);
						if(url !=null){
							content = url.toString();
							return content;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public Map<String, String> getContentMap() {
		if(newPage){
			getContent();
		}
		if(null==content)
			return null;
		Map<String, String> m = new HashMap<String, String>(1);
		m.put(name, this.content);
		return m;
	}
}
