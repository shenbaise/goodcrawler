/**
 * ########################  SHENBAISE'S WORK  ##########################
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
package org.sbs.goodcrawler.plugin.extract;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sbs.goodcrawler.conf.jobconf.JobConfiguration;
import org.sbs.goodcrawler.extractor.DefaultExtractor;
import org.sbs.goodcrawler.extractor.Extractor;
import org.sbs.goodcrawler.job.Page;
import org.sbs.goodcrawler.storage.PendingStore.ExtractedPage;

/**
 * @author whiteme
 * @date 2013年7月28日
 * @desc 
 */
public class ExtractYouku extends Extractor {
//http://player.youku.com/player.php/sid/XNTg3ODI3NTIw/v.swf?isAutoPlay=true 
	public ExtractYouku(JobConfiguration conf) {
		super(conf);
	}

	@Override
	public ExtractedPage<?, ?> onExtract(Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtractedPage<?, ?> beforeExtract(Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtractedPage<?, ?> afterExtract(Page page) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static void main(String[] args) {
		try {
			Document doc = Jsoup.parse(new URL("http://v.youku.com/v_show/id_XNTg3ODI3NTIw.html"), 10000);
			System.out.println(doc.select("#link2").attr("value"));
			System.out.println(doc.select(".act-name a").text());
			System.out.println(doc.select("html body.page_show div.s_main col2_21 div#vpofficialtitle_wrap div#vpofficialtitle div.base div.show_intro div.guide div.crumbs span#cateInfo a").text());
//			System.out.println(doc.html());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
