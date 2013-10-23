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
package org.sbs.goodcrawler.extractor.selector.action.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.sbs.goodcrawler.extractor.selector.action.FileSelectAction;
import org.sbs.goodcrawler.extractor.selector.exception.DownLoadException;
import org.sbs.util.MD5Utils;
import org.sbs.util.download.MultiThreadDownload;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年10月20日
 * @desc 下载文件（必须同步进行，如果文件下载不完成，不能执行下面的不走）
 */
public class DownLoadFileAction extends FileSelectAction {
	/**
	 * 下载文件的存储路径
	 */
	File dir = null;
	/**
	 * 动态路径
	 */
	List<String> dynamicPath = null;
	/**
	 * 是否使用Url的md5值作为文件名。false则保留原文件名
	 */
	boolean md5File = true;
	/**
	 * 是否异步处理
	 */
	boolean asynchronous = true;
	/**
	 * 多线程下载时分块的大小
	 */
	long blockSize = 1024*1024L;
	/**
	 * 构造函数
	 * @param dir
	 * @param md5File
	 */
	public DownLoadFileAction(String dir, boolean md5File) {
		if(!dir.contains("{")){
			this.dir = new File(dir);
			if(!this.dir.exists()){
				this.dir.mkdir();
			}
		}else {
			this.dynamicPath = Lists.newArrayList(StringUtils.substringsBetween(dir, "{", "}"));
		}
		this.md5File = md5File;
	}
	/**
	 * 构造函数
	 * @param dir
	 * @param md5File
	 * @param asynchronous
	 */
	public DownLoadFileAction(String dir, boolean md5File,boolean asynchronous) {
		super();
		if(!dir.contains("{")){
			this.dir = new File(dir);
			if(!this.dir.exists()){
				this.dir.mkdir();
			}
		}else {
			this.dynamicPath = Lists.newArrayList(StringUtils.substringsBetween(dir, "{", "}"));
		}
		this.md5File = md5File;
		this.asynchronous = asynchronous;
	}

	/**
	 * 下载文件，返回文件路径
	 */
	@Override
	public String doAction(Map<String, Object> result,String remoteFile) throws DownLoadException {
		// 首先确认文件存放的本地路径
		String path = getDownDir(result);
		URL url;
		try {
			url = new URL(remoteFile);
		} catch (MalformedURLException e) {
			throw new DownLoadException("下载异常："+e.getMessage());
		}
		String fileName = "";
		if(md5File){
			try {
				fileName = MD5Utils.createMD5(remoteFile);
				fileName = fileName +"."+ StringUtils.substringAfterLast(url.getPath(), ".");
			} catch (Exception e) {
			}
		}else {
			fileName = StringUtils.substringAfterLast(url.getPath(), "/");
		}
		MultiThreadDownload download = new MultiThreadDownload(1024*1024L);
		if(asynchronous){
			download.downFile(url, path, fileName, false);
		}else {
			download.downLoad(url, path, fileName);
		}
		// 返回路径
		return StringUtils.replace(path + fileName, "\\", "/");
	}
	
	/**
	 * 首先确认文件存放的本地路径
	 * @param map
	 * @return
	 */
	public String getDownDir(Map<String, Object> map){
		if(null!=map){
			if(null!=dir){
				return dir.getPath();
			}else if(null!=dynamicPath){
				StringBuilder sb = new StringBuilder();
				for(String p:dynamicPath){
					sb.append(String.valueOf(map.get(p))).append(File.separator);
				}
				return sb.toString();
			}
		}
		return "";
	}
	
	public static void main(String[] args) {
//		System.out.println(StringUtils.substringAfterLast("sdfasd.asdfaf.gif", "."));
		DownLoadFileAction downLoadFileAction = new DownLoadFileAction("d:/multidown", true);
		try {
			downLoadFileAction.doAction(null,"http://zhangmenshiting.baidu.com/data2/music/65517089/307842151200128.mp3?xcode=53102624c6c63d206dbeaf3b8ae12d9080af3c8af038c7a6");
		} catch (DownLoadException e) {
			e.printStackTrace();
		}
	}
}
