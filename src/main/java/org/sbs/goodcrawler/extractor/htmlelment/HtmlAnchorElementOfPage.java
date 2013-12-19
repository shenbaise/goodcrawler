/**
 * @工程 goodcrawler
 * @文件 HtmlAnchorElement.java
 * @时间 2013年12月18日 下午5:35:49
 * @作者 shenbaise（shenbaise1001@126.com）
 * @描述 
 */
package org.sbs.goodcrawler.extractor.htmlelment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc
 */
public class HtmlAnchorElementOfPage extends AbstractHtmlElement<Page>{
	
	private Page content;
	
	@Override
	public Page getContent() {
		if(page!=null){
			if (null != content && !newPage) {
				return content;
			}
			if (type.equals(HtmlElementExtractType.xpath)) {
				HtmlAnchor anchor = page.getFirstByXPath(value);
				try {
					Page p = anchor.click();
					webClient.waitForBackgroundJavaScript(1000*3L);
					this.content = p;
					return p;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				try {
					new Exception("需要使用xpath");
				} catch (Exception e) {
					throw e;
				}
			}
		}
		return null;
	}

	@Override
	public Map<String, Page> getContentMap() {
		if(newPage){
			getContent();
		}
		if(null==content)
			return null;
		Map<String, Page> m = new HashMap<String, Page>(1);
		m.put(name, this.content);
		return m;
	}
}
