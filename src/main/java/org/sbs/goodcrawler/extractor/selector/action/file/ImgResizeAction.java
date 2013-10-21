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

import org.sbs.goodcrawler.extractor.selector.action.FileSelectAction;
import org.sbs.goodcrawler.extractor.selector.exception.DownLoadException;

/**
 * @author whiteme
 * @date 2013年10月20日
 * @desc 图片压缩处理（可以异步进行，文件压缩在文件下载之后，在本地操作安全性高。失败之后容易恢复）
 */
public class ImgResizeAction extends FileSelectAction {
	private int w = 200;
	private int h = 240;
	private boolean proportion = true;
	private boolean deleteOldFile = true;
	
	/**
	 * 构造函数
	 * @param w
	 * @param h
	 * @param proportion
	 * @param deleteOldFile
	 */
	public ImgResizeAction(int w, int h, boolean proportion,
			boolean deleteOldFile) {
		super();
		this.w = w;
		this.h = h;
		this.proportion = proportion;
		this.deleteOldFile = deleteOldFile;
	}

	public ImgResizeAction(int w, int h, boolean proportion) {
		super();
		this.w = w;
		this.h = h;
		this.proportion = proportion;
	}
	
	public ImgResizeAction(int w, int h) {
		super();
		this.w = w;
		this.h = h;
	}
	
	/**
	 * 压缩图片并返回路径
	 */
	@Override
	public String doAction(String remoteFile) throws DownLoadException {
		
		return null;
	}

}
