package org.sbs.extract;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sbs.goodcrawler.bootstrap.foreman.FetchForeman;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.jobconf.ExtractConfig;
import org.sbs.goodcrawler.jobconf.FetchConfig;

public class Tester {
	
	public void test(String conFile,String url){
		ExtractConfig extractConfig = new ExtractConfig();
		FetchConfig fetchConfig = new FetchConfig();
		Document document;
		try {
			document = Jsoup.parse(new File(conFile), "utf-8");
			System.out.println(extractConfig.loadConfig(document).toString());
			FetchForeman fetchForeman = new FetchForeman();
			fetchForeman.start(fetchConfig.loadConfig(document));
			Map<String, Object> r=extractConfig
					.getContentSeprator(Jsoup.parse(new URL(url), 10000),url);
			System.out.println(r);
		}catch (IOException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (ExtractException e) {
			e.printStackTrace();
		}
	}
	
}
