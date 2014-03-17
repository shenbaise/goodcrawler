/**
 * 
 */
package org.sbs.goodcrawler.plugin.storage.p;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sbs.goodcrawler.bootstrap.foreman.FetchForeman;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.fetcher.CustomFetchStatus;
import org.sbs.goodcrawler.page.ExtractedPage;
import org.sbs.goodcrawler.page.Page;
import org.sbs.goodcrawler.page.PageFetchResult;
import org.sbs.goodcrawler.page.Parser;
import org.sbs.url.WebURL;
import org.sbs.util.UrlUtils;

import com.google.common.collect.Maps;

/**
 * @author Administrator
 *
 */
public class WasuEsStorePlugin extends IESStoragePlugin {
	
	private Parser parser = new Parser(false);
	private UrlUtils urlUtils = new UrlUtils();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	private Log log  = LogFactory.getLog(WasuEsStorePlugin.class);

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.plugin.storage.p.IESStoragePlugin#process(org.sbs.goodcrawler.page.ExtractedPage)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExtractedPage<?, ?> process(ExtractedPage<?, ?> page) {
		try {
			log.info(page.getMessages() + "\n" + page.getUrl().getURL().toString());
			HashMap map = ((HashMap)(page.getMessages().get(config.indexName)));
			if(page!=null && null!=map && map.size()>2){
				if("电视剧".equals((String)map.get("category")) || "动漫".equals((String)map.get("category"))){
					@SuppressWarnings("rawtypes")
					LinkedHashSet<String> urls = (LinkedHashSet<String>) ((HashMap) map.get("s")).get("play_url");
					
					HashMap<String, HashMap<String, LinkedHashSet<String>>> m = Maps.newHashMap();
					HashMap<String, LinkedHashSet<String>> m2 = Maps.newHashMap();
					m2.put(config.jobName, urls);
					m.put("swf_url", m2);
					map.put("play_url", urls);
					map.remove("s");
				}
				page.setMessage(config.indexName, map);
				return page;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 传入html地址，解析得到播放地址。
	 * @param url
	 * @param cssSelect	#playUrl_2 value
	 * @return
	 * @throws ExtractException
	 */
	public String getPlayUrl(String url,String cssSelect) throws ExtractException{
		// 转换电视剧、动漫的URL。
		Document doc = null;
		PageFetchResult result = null;
		try {
			WebURL webUrl = new WebURL();
			webUrl.setURL(url);
			result = FetchForeman.fetcher.fetchHeader(webUrl);
			// 获取状态
			int statusCode = result.getStatusCode();
			if (statusCode == CustomFetchStatus.PageTooBig) {
				return null;
			}
			if (statusCode != HttpStatus.SC_OK){
				return null;
			}else {
				Page page = new Page(webUrl);
				if (!result.fetchContent(page)) {
					return null;
				}
				if (!parser.parse(page, webUrl.getURL())) {
					return null;
				}
			doc = Jsoup.parse(new String(page.getContentData(),page.getContentCharset()), urlUtils.getBaseUrl(page.getWebURL().getURL()));
			Elements elements = doc.select(cssSelect);
			return elements.first().attr("value");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExtractException(e.getMessage());
		}finally{
			if(result!=null)
				result.discardContentIfNotConsumed();
		}
	}
}
