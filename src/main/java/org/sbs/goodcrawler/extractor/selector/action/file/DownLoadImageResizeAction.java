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
 * @desc 下载图片然后压缩
 */
public class DownLoadImageResizeAction extends FileSelectAction {
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
	
	private int w = 180;
	private int h = 240;
	private float quality = 0.6f;
	private boolean deleteOldFile = true;
	
	/**
	 * 构造函数
	 * @param dir
	 * @param md5File
	 * @param asynchronous
	 * @param blockSize
	 * @param w
	 * @param h
	 * @param quality
	 * @param deleteOldFile
	 */
	public DownLoadImageResizeAction(String dir, boolean md5File,
			boolean asynchronous, long blockSize, int w, int h,
			Float quality, boolean deleteOldFile) {
		if(!dir.contains("{")){
			this.dir = new File(dir);
			if(!this.dir.exists()){
				this.dir.mkdir();
			}
		}else {
			this.dynamicPath = Lists.newArrayList(StringUtils.substringsBetween(dir, "{", "}"));
			// 非动态部分
			String ss[] = StringUtils.split(dir, "/");
			for(String s:ss){
				if(!s.contains("{")){
					this.dynamicPath.add(0, s);
				}
			}
		}
		this.md5File = md5File;
		this.asynchronous = asynchronous;
		this.blockSize = blockSize;
		this.w = w;
		this.h = h;
		this.quality = quality;
		this.deleteOldFile = deleteOldFile;
	}
	/**
	 * 构造函数
	 * @param dir
	 * @param md5File
	 */
	public DownLoadImageResizeAction(String dir, boolean md5File) {
		super();
		if(!dir.contains("{")){
			this.dir = new File(dir);
			if(!this.dir.exists()){
				this.dir.mkdir();
			}
		}else {
			this.dynamicPath = Lists.newArrayList(StringUtils.substringsBetween(dir, "{", "}"));
			String ss[] = StringUtils.split(dir, "/");
			for(String s:ss){
				if(!s.contains("{")){
					this.dynamicPath.add(0, s);
				}
			}
		}
		this.md5File = md5File;
	}
	/**
	 * 构造函数
	 * @param dir
	 * @param md5File
	 * @param asynchronous
	 */
	public DownLoadImageResizeAction(String dir, boolean md5File,boolean asynchronous) {
		super();
		if(!dir.contains("{")){
			this.dir = new File(dir);
			if(!this.dir.exists()){
				this.dir.mkdir();
			}
		}else {
			this.dynamicPath = Lists.newArrayList(StringUtils.substringsBetween(dir, "{", "}"));
			String ss[] = StringUtils.split(dir, "/");
			for(String s:ss){
				if(!s.contains("{")){
					this.dynamicPath.add(0, s);
				}
			}
		}
		this.md5File = md5File;
		this.asynchronous = asynchronous;
	}

	/**
	 * 下载文件，返回文件路径
	 */
	@Override
	public String doAction(Map<String, Object> result,String remoteFile) throws DownLoadException {
		String path = getDownDir(result);
		URL url;
		try {
			url = new URL(remoteFile);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new DownLoadException("下载异常："+e.getMessage());
		}
		String fileName = "";
		if(md5File){
			try {
				fileName = MD5Utils.createMD5(remoteFile);
				String suffix = StringUtils.substringAfterLast(url.getPath(), ".");
				if(StringUtils.isBlank(suffix)){
					suffix = "jpg";
				}
				fileName = fileName +"."+ suffix;
			} catch (Exception e) {
			}
		}else {
			fileName = StringUtils.substringAfterLast(url.getPath(), "/");
		}
		MultiThreadDownload download = new MultiThreadDownload(1024*1024L);
		if(asynchronous){
			download.downImageThenResizeAsyn(url, path, fileName, w, quality);
		}else {
			download.downImageThenResizeSyn(url, path, fileName, w, quality);
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
	
	public File getDir() {
		return dir;
	}
	public boolean isMd5File() {
		return md5File;
	}
	public void setMd5File(boolean md5File) {
		this.md5File = md5File;
	}
	public boolean isAsynchronous() {
		return asynchronous;
	}
	public void setAsynchronous(boolean asynchronous) {
		this.asynchronous = asynchronous;
	}
	public long getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	
	public float getQuality() {
		return quality;
	}
	public void setQuality(float quality) {
		this.quality = quality;
	}
	public boolean isDeleteOldFile() {
		return deleteOldFile;
	}
	public void setDeleteOldFile(boolean deleteOldFile) {
		this.deleteOldFile = deleteOldFile;
	}
	public static void main(String[] args) {
//		System.out.println(StringUtils.substringAfterLast("sdfasd.asdfaf.gif", "."));
		DownLoadImageResizeAction downLoadFileAction = new DownLoadImageResizeAction("d:/multidown", true);
		try {
			downLoadFileAction.doAction(null,"http://zhangmenshiting.baidu.com/data2/music/65517089/307842151200128.mp3?xcode=53102624c6c63d206dbeaf3b8ae12d9080af3c8af038c7a6");
		} catch (DownLoadException e) {
			e.printStackTrace();
		}
	}
}
