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
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.coobird.thumbnailator.Thumbnails;

/**
 * @author whiteme
 * @date 2013年7月22日
 * @desc 图片下载及压缩
 */
public class ImgUtil {
	private static Log log = LogFactory.getLog(ImgUtil.class);
	public static String path = new File("").getAbsolutePath();
	/**
	 * @param url
	 * @param movieType
	 * @return 下载图片生成缩略图，放置在movieType目录下
	 */
	public static boolean downAndResize(String url,String movieType){
		String disFile = path + File.separator + movieType + url.substring(url.lastIndexOf('/'));
		File dir = new File(path + File.separator + movieType);
		if(!dir.exists()){
			dir.mkdirs();
		}
		try {
			// 下载
			FileUtils.copyURLToFile(new URL(url), new File(disFile), 10000, 20000);
			// 压缩
			Thumbnails.of(disFile).width(200)
			.outputQuality(0.6f)
			.toFile(disFile);
		} catch (IOException e) {
			log.error("###图片下载压缩失败#"+url);
			return false;
		}
		return true;
	}
	
	/**
	 * 下载压缩，Url的md5做文件名
	 * @param url
	 * @param distPath
	 * @return
	 */
	public static String downThenResize(String url,String distPath){
		String fileName = EncryptUtils.encodeMD5(url);
		File path = new File(distPath);
		if(!path.exists()){
			path.mkdirs();
		}
		String file = distPath + File.separator + fileName+ url.substring(url.lastIndexOf('.'));
		File imgFile = new File(file);
		
		try {
			// 下载
			FileUtils.copyURLToFile(new URL(url), imgFile, 10000, 20000);
			// 压缩
			Thumbnails.of(imgFile).width(200)
			.outputQuality(0.6f)
			.toFile(imgFile);
			return file;
		} catch (IOException e) {
			log.error("###图片下载压缩失败#"+url);
			return null;
		}
	}
}
