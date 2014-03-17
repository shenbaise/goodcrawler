package org.sbs.extract;

import java.io.File;
import java.io.IOException;
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

public class TestYouku {
	
	private static String confFile = "conf/youku_conf.xml";
	//电视剧: 精忠岳飞 2013
	private static String tv = "http://www.youku.com/show_page/id_zd4edea60e0d011df97c0.html";
//	http://v.youku.com/v_show/id_XNTc4NzczNDM2.html
	// 电影: 上帝保佑美国 2012
	private static String movie = "http://www.youku.com/show_page/id_z0fca04b0bceb11e0bf93.html";
//	http://v.youku.com/v_show/id_XNjY4ODk3MjI4.html
	// 综艺  快乐大本营
	private static String zy = "http://www.youku.com/show_page/id_zd18a7caa2d4311e29498.html";
	// 动漫 柯南
	private static String dm = "http://www.youku.com/show_page/id_zcc003400962411de83b1.html";
	@Test
	public void tv(){
		new Tester().test(confFile, tv);
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
		new Tester().test("conf/test_youku_dm.xml", "http://v.youku.com/v_show/id_XMzk1NjM1MjAw.html");
	}
}
