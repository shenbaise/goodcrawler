package org.sbs;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class ListLinks {
    public static void main(String[] args) throws IOException {
        String url = "http://www.66e.cc/bd/20110611/7de42f3f410b41b99f55854553be25e1.htm";

        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select("p");
        for(Element e:elements){
//        	System.out.println(e.text());
        	String[] ss = e.toString().split("<br />");
        	for(String s:ss){
        		System.err.println(Jsoup.clean(s, Whitelist.none()));
        	}
        }
        
        Elements links = doc.select("a[href^=ftp]");
        for(Element e:links){
        	Elements xxElements = e.select("a[href^=ftp]");
        	for(Element x:xxElements){
        		System.out.println(x.text());
        	}
        	System.out.println(e.text());
        }
    }
}