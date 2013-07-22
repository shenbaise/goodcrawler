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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author whiteme
 * @date 2013年7月22日
 * @desc 图片下载及压缩
 */
public class ImgUtil {
	
	public ImageCompress imageCompress = new ImageCompress();
	
	public String outputPath;
	
	public ImgUtil(String outputPath){
		this.outputPath = outputPath;
		File outPath = new File(this.outputPath);
		if(!outPath.exists()){
			outPath.mkdirs();
		}
	}
	
	/**
	 * @param strUrl
	 * @param distFile
	 * @return 
	 * 下载图片保存到指定路径
	 */
	public String download(String strUrl, String distFile) {
		URL url = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			url = new URL(strUrl);
			is = url.openStream();
			os = new FileOutputStream(outputPath + "/" + distFile);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			return outputPath + "/" + distFile;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
				if (null != is)
					is.close();
			} catch (IOException e) {
			}
		}
		
		return null;
	}
	
	/**
	 * @param strUrl
	 * @param distFileName（不包括后缀）
	 * @param w
	 * @param h
	 * @param q
	 * @return 
	 * 下载图片并压缩
	 */
	public boolean downloadImageCompress(String strUrl, String distFileName,int w,int h,float q){
		String img = download(strUrl, "temp" + strUrl.substring(strUrl.lastIndexOf('.')));
		if(StringUtils.isBlank(img)){
			return false;
		}
		try {
			// 生成压缩图片
			imageCompress.resize(new File(img), new File(outputPath + File.separator + distFileName + strUrl.substring(strUrl.lastIndexOf('.'))), w, h, q);
			// 删除原图片
			File tempFile = new File(img);
			tempFile.deleteOnExit();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		ImgUtil imgUtil = new ImgUtil("e:\\t");
		imgUtil.downloadImageCompress("http://apollo.s.dpool.sina.com.cn/nd/dataent/moviepic/pics/247/moviepic_399dded9b39a563ab85467e1c763e915.jpg", "z/z", 200, -1, 0.3f);
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		try {
			System.out.println(PinyinHelper.toHanyuPinyinString("哈哈你打他奶奶点个头哈",t3, "-"));
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
