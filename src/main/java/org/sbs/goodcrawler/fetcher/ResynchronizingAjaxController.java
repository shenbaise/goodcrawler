/**
 * ##########################  GoodCrawler  ############################
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
package org.sbs.goodcrawler.fetcher;

import java.lang.ref.WeakReference;
import java.net.URL;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author shenbaise（shenbaise@outlook.com）
 * @desc 当ajax调用连接获得后直接返回连接，不去请求。<b>非线程安全</b>
 */
public class ResynchronizingAjaxController extends AjaxController {
	private static final long serialVersionUID = -7294411299347390276L;
	
	private URL rurl = null;
	private transient WeakReference<Thread> originatedThread_;
	/** 默认超时5秒**/
	private final long timeOut_ = 5000L;
	
	public ResynchronizingAjaxController(){
		originatedThread_ = new WeakReference<Thread>(Thread.currentThread());
	}
	
	/**
	 * always return false
	 */
	@Override
	public boolean processSynchron(HtmlPage page, WebRequest request,
			boolean async) {
		if (async && isInOriginalThread()) {
            rurl = request.getUrl();
            return false;
        }
        return false;
	}
	
	public URL getResynchronizedCallUlr(){
		long time = 0;
		int r = 20;
		while(null==rurl){
    		try {
    			if(time>=timeOut_)
    				break;
				Thread.sleep(20);
				time+=r;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
		if(rurl!=null){
	    	URL url = rurl;
	    	rurl = null;
	    	return url;
		}
		return null;
	}
	
	public URL getResynchronizedCallUlr(long timeOut){
		long time = 0;
		int r = 20;
		while(null==rurl){
    		try {
    			if(time>=timeOut)
    				break;
				Thread.sleep(20);
				time+=r;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
		if(rurl!=null){
	    	URL url = rurl;
	    	rurl = null;
	    	return url;
		}
		return null;
	}
	
	boolean isInOriginalThread() {
        return Thread.currentThread() == originatedThread_.get();
    }
}