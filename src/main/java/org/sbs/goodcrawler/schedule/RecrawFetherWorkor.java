package org.sbs.goodcrawler.schedule;

import org.sbs.goodcrawler.exception.QueueException;
import org.sbs.goodcrawler.fetcher.FetchWorker;
import org.sbs.goodcrawler.fetcher.PageFetcher;
import org.sbs.goodcrawler.jobconf.FetchConfig;
import org.sbs.pendingqueue.PendRecraw;
import org.sbs.pendingqueue.PendingManager;
import org.sbs.url.WebURL;

public class RecrawFetherWorkor extends FetchWorker {
	
	private PendRecraw pendRecraw = null;
	public RecrawFetherWorkor(FetchConfig conf, PageFetcher fetcher) {
		super(conf, fetcher);
		pendRecraw = PendingManager.getUrlsToRecraw(conf.jobName);
	}

	@Override
	public void run() {
		WebURL url ;
		try {
			while(!isStop()){
				while(null!=(url=pendRecraw.getElementT())){
					fetchPageWhitoutExtractUrl(url);
					// 确保当前任务完成后跳出
					if(isStop())
						break;
				}
			}
		} catch (QueueException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSuccessed() {
		pendRecraw.processedSuccess();
	}

	@Override
	public void onFailed(WebURL url) {
		pendingPages.processedFailure();
	}

	@Override
	public void onIgnored(WebURL url) {
		pendingPages.processedIgnored();
	}

}
