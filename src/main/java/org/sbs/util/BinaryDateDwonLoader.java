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
package org.sbs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.sbs.goodcrawler.fetcher.CustomFetchStatus;
import org.sbs.goodcrawler.page.PageFetchResult;
import org.sbs.url.URLCanonicalizer;

/**
 * @author whiteme
 * @date 2013年7月24日
 * @desc 
 */
public class BinaryDateDwonLoader {
	protected final Object mutex = new Object();
	protected static final Logger logger = Logger.getLogger(BinaryDateDwonLoader.class);
	protected DefaultHttpClient httpClient;
	protected long lastFetchTime = 0;
	protected int delayTime = 200;
	protected int maxSize = 1024*1024*5;
	
	private BinaryDateDwonLoader(){
		httpClient = new DefaultHttpClient();
	}
	
	private static BinaryDateDwonLoader dwonLoader = null;
	
	public static BinaryDateDwonLoader getInstance(){
		if(dwonLoader==null){
			dwonLoader = new BinaryDateDwonLoader();
		}
		return dwonLoader;
	}
	
	private PageFetchResult fetchHeader(String webUrl) {
		PageFetchResult fetchResult = new PageFetchResult();
		String toFetchURL = webUrl;
		HttpGet get = null;
		try {
			get = new HttpGet(toFetchURL);
			synchronized (mutex) {
				long now = (new Date()).getTime();
				if (now - lastFetchTime < delayTime) {
					Thread.sleep(delayTime - (now - lastFetchTime));
				}
				lastFetchTime = (new Date()).getTime();
			}
			get.addHeader("Accept-Encoding", "gzip");
			HttpResponse response = httpClient.execute(get);
			fetchResult.setEntity(response.getEntity());
			fetchResult.setResponseHeaders(response.getAllHeaders());
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				if (statusCode != HttpStatus.SC_NOT_FOUND) {
					if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
						Header header = response.getFirstHeader("Location");
						if (header != null) {
							String movedToUrl = header.getValue();
							movedToUrl = URLCanonicalizer.getCanonicalURL(movedToUrl, toFetchURL);
							fetchResult.setMovedToUrl(movedToUrl);
						} 
						fetchResult.setStatusCode(statusCode);
						return fetchResult;
					}
					logger.info("Failed: " + response.getStatusLine().toString() + ", while fetching " + toFetchURL);
				}
				fetchResult.setStatusCode(response.getStatusLine().getStatusCode());
				// 关闭链接
				response.getEntity().getContent().close();
				return fetchResult;
			}

			fetchResult.setFetchedUrl(toFetchURL);
			String uri = get.getURI().toString();
			if (!uri.equals(toFetchURL)) {
				if (!URLCanonicalizer.getCanonicalURL(uri).equals(toFetchURL)) {
					fetchResult.setFetchedUrl(uri);
				}
			}

			if (fetchResult.getEntity() != null) {
				long size = fetchResult.getEntity().getContentLength();
				if (size == -1) {
					Header length = response.getLastHeader("Content-Length");
					if (length == null) {
						length = response.getLastHeader("Content-length");
					}
					if (length != null) {
						size = Integer.parseInt(length.getValue());
					} else {
						size = -1;
					}
				}
				if (size > maxSize) {
					fetchResult.setStatusCode(CustomFetchStatus.PageTooBig);
					get.abort();
					return fetchResult;
				}

				fetchResult.setStatusCode(HttpStatus.SC_OK);
				return fetchResult;
			}
			get.abort();
			
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage() + " while fetching " + toFetchURL);
			fetchResult.setStatusCode(CustomFetchStatus.FatalTransportError);
			return fetchResult;
		} catch (IllegalStateException e) {
			// ignoring exceptions that occur because of not registering https
			// and other schemes
		} catch (Exception e) {
			if (e.getMessage() == null) {
				logger.error("Error while fetching " + toFetchURL);
			} else {
				logger.error(e.getMessage() + " while fetching " + toFetchURL);
			}
		} finally {
			try {
				if (fetchResult.getEntity() == null && get != null) {
					get.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fetchResult.setStatusCode(CustomFetchStatus.UnknownError);
		return fetchResult;
	}
	
	/**
	 * @param url
	 * @param distPath
	 * @return
	 * 下载文件到指定目录，文件名不变
	 */
	public String download(String url,String distPath){
		PageFetchResult result = fetchHeader(url);
		if(null!=result && null!=result.getEntity()){
			String fileName = EncryptUtils.encodeMD5(url);
			File path = new File(distPath);
			if(!path.exists()){
				path.mkdirs();
			}
			String file = distPath + File.separator + fileName+ url.substring(url.lastIndexOf('.'));
			File imgFile = new File(file);
			
			try {
				OutputStream outputStream = new FileOutputStream(imgFile);
				result.getEntity().writeTo(outputStream);
				outputStream.close();
				Thumbnails.of(file).width(200)
				.outputQuality(0.6f)
				.toFile(file);
				return imgFile.getName();
			} catch (FileNotFoundException e) {
				logger.warn(e.getMessage());
			} catch (IOException e) {
//				e.printStackTrace();
				logger.warn(e.getMessage());
			}
		}
		return null;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String img = "http://apollo.s.dpool.sina.com.cn/nd/dataent/moviepic/pics/157/moviepic_8d48be1e004c5b05464a7a427d6722e4.jpg";
		BinaryDateDwonLoader dateDwonLoader = BinaryDateDwonLoader.getInstance();
		PageFetchResult result = dateDwonLoader.fetchHeader(img);
		try {
			OutputStream outputStream = new FileOutputStream(new File("d:\\"+img.substring(img.lastIndexOf('/')+1)));
			result.getEntity().writeTo(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
