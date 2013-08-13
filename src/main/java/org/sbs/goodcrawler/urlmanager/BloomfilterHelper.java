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
package org.sbs.goodcrawler.urlmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-30
 * Bloomfilter的帮助类
 */
public class BloomfilterHelper implements Serializable{
	
	private static final long serialVersionUID = -160403070863080075L;

	private BloomFilter<String> bf = null;
	
	private static BloomfilterHelper instance = null;
	
	private BloomfilterHelper(){
		init();
	};
	
	public static BloomfilterHelper getInstance(){
		if(null==instance)
			instance = new BloomfilterHelper();
		return instance;
	}
	
	/**
	 * @desc 初始化队列
	 */
	private void init() {
		File file = new File(PropertyConfigurationHelper.getInstance()
				.getString("status.save.path", "status")
				+ File.separator
				+ "filter.good");
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				instance = (BloomfilterHelper) ois.readObject();
				ois.close();
				fis.close();
				bf = instance.bf;
				System.out.println("recovery Bloomfilter...");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} 
		if(null==bf){
			bf = new BloomFilter<String>(0.001, 10000);
		}
	}
	
	/**
	 * @param s
	 * @return
	 * @desc 检测是否存在该Url
	 */
	public boolean exist(String url){
		if(StringUtils.isBlank(url))
			return true;
		return bf.containsOradd(url.getBytes());
	}
	/**
	 * add
	 * @param url
	 */
	public void add(String url){
		bf.add(url);
	}
	
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
