/**
 * @工程 goodcrawler
 * @文件 GcElementTest.java
 * @时间 2013年12月19日 下午4:58:46
 * @作者  shenbaise（shenbaise1001@126.com）
 * @描述 
 */
package org.sbs.htmlunit.element;

import org.junit.Test;
import org.sbs.goodcrawler.extractor.GCElement;
import org.sbs.goodcrawler.extractor.selector.AbstractElementCssSelector;
import org.sbs.goodcrawler.extractor.selector.StringElementCssSelector;

/**
 * @author shenbaise（shenbaise1001@126.com）
 * @desc
 */
public class GcElementTest {
	
	@Test
	public void insTest(){
		StringElementCssSelector secsCssSelector = new StringElementCssSelector("", "", "", true);
		System.out.println(secsCssSelector instanceof GCElement);
		assert secsCssSelector instanceof AbstractElementCssSelector;
		assert secsCssSelector instanceof GCElement;
//		assert secsCssSelector instanceof AbstractHtmlElement;
	}
}
