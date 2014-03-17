package org.sbs.extract;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.sbs.goodcrawler.bootstrap.foreman.FetchForeman;
import org.sbs.goodcrawler.exception.ConfigurationException;
import org.sbs.goodcrawler.exception.ExtractException;
import org.sbs.goodcrawler.jobconf.ExtractConfig;
import org.sbs.goodcrawler.jobconf.FetchConfig;

public class TestWasu {
	
	private static String confFile = "conf/wasu_conf.xml";
	//电视剧: 新闺蜜时代
	private static String tv = "http://www.wasu.cn/Tele/index/id/1146253";
//	http://v.youku.com/v_show/id_XNTc4NzczNDM2.html
	// 电影: 赤裸特工
	private static String movie = "http://www.wasu.cn/Play/show/id/216708";
//	http://v.youku.com/v_show/id_XNjY4ODk3MjI4.html
	// 综艺  唱出爱火花_20140220_张潇洋 开门见山 女王驾到
	private static String zy = "http://www.wasu.cn/Play/show/id/2194006";
	// 动漫 海绵宝宝
	private static String dm = "http://www.wasu.cn/Tele/index/id/351623";
	
	private static String zwdajs = "http://www.wasu.cn/Tele/index/id/1007994";
	
	@Test
	public void tv(){
		new Tester().test(confFile, "http://www.wasu.cn/Tele/index/id/2169706");
	}
	
	@Test
	public void movie(){
		new Tester().test(confFile, movie);
	}
	
	@Test
	public void zy(){
		new Tester().test(confFile, zy);
	}
	
	@Test
	public void dm(){
		
		new Tester().test(confFile,dm);
	}
	
	@Test
	public void t(){
		try {
			Document elements = Jsoup.parse(new URL("http://www.wasu.cn/Play/show/id/2339162"), 100000);
			// 分类
//			System.out.println(elements.select(".play_information_t .r .one a"));// title
//			System.out.println(elements.select(".play_information_t .r .two a "));// eara
//			System.out.println(elements.select(".play_information_t .r .three a")); // type
//			System.out.println(elements.select(".play_information_t .r .four a")); // director
//			System.out.println(elements.select(".play_information_t .r .five .r a")); // actor
//			System.out.println(elements.select(".play_information_b .one b"));
			
			System.out.println(elements.select(".play_seat a").get(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
