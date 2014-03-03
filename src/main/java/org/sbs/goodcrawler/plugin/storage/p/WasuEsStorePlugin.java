/**
 * 
 */
package org.sbs.goodcrawler.plugin.storage.p;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.page.ExtractedPage;

import com.google.common.collect.Sets;

/**
 * @author Administrator
 *
 */
public class WasuEsStorePlugin extends IESStoragePlugin {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	private Log log  = LogFactory.getLog(WasuEsStorePlugin.class);

	/* (non-Javadoc)
	 * @see org.sbs.goodcrawler.plugin.storage.p.IESStoragePlugin#process(org.sbs.goodcrawler.page.ExtractedPage)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExtractedPage<?, ?> process(ExtractedPage<?, ?> page) {
		try {
			log.info(page.getMessages());
			HashMap map = ((HashMap)(page.getMessages().get(config.indexName)));
			if(page!=null && null!=map && map.size()>2){
				String type = (String) map.get("type");
				Object o = map.get("year");
				Date year =  (Date) o;
				String actors = (String) map.get("actors");
				map.put("type",Sets.newHashSet(type));
				map.put("year", Integer.parseInt(sdf.format(year)));
				map.put("actors", Sets.newHashSet(StringUtils.split(actors," ")));
				return page;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
