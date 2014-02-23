/**
 * 
 */
package org.sbs.goodcrawler.plugin.storage.p;

import org.sbs.goodcrawler.jobconf.StoreConfig;
import org.sbs.goodcrawler.page.ExtractedPage;

/**
 * @author shenbaise(shenbaise1001@126.com)
 * es存储的扩展接口
 */
public abstract class IESStoragePlugin {
	protected StoreConfig config;
	
	public StoreConfig getConfig() {
		return config;
	}

	public void setConfig(StoreConfig config) {
		this.config = config;
	}

	public abstract ExtractedPage<?, ?> process(ExtractedPage<?, ?> page);
}
