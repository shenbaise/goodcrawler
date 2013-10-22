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
package org.sbs.util.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sbs.goodcrawler.conf.PropertyConfigurationHelper;
import org.sbs.goodcrawler.extractor.selector.exception.DownLoadException;
import org.sbs.util.image.ImageResize;

import com.google.common.collect.Lists;

/**
 * @author whiteme
 * @date 2013年10月20日
 * @desc 多线程下载，该类不是线程安全的，多个线程使用时请为每个类各自创建实例
 */
public class MultiThreadDownload {
	private static final Log log = LogFactory.getLog(MultiThreadDownload.class);
	private static final String down_error_log_file = PropertyConfigurationHelper.getInstance().getString("status.save.path.down.error", "status/down-error.log");
	/**
	 * 缓冲区大小
	 */
	private static int BUFFER_SIZE = 1024*1024;
	/**
	 * 分块大小
	 */
	private long blockSize = 1024*1024;
	/**
	 *  如不设置blockSize，则需设置threadNum，如设置了threadNum，blockSize不需设置
	 */
	private int threadNum = 3;
	/**
	 * 链接超时时间
	 */
	private int setConnectTimeout = 10000;
	/**
	 * 读取超时时间
	 */
	private int setReadTimeout = 10000;
	/**
	 * 已经下载的长度
	 */
	private long downLen = -1;
	/**
	 * 文件总长度
	 */
	private long contentLen = 0;
	/**
	 * 开始下载的时间
	 */
	private Date date ;
	/**
	 * 执行下载的线程池
	 */
	private DownLoadPool pool = DownLoadPool.getInstance();
	/**
	 * 格式化百分比
	 */
	private DecimalFormat decimalFormat = new DecimalFormat("##.##%");
	/**
	 * 是否下载完成
	 */
	private boolean finished = false;
	/**
	 * lock
	 */
	private final Object object = new Object();
	
	public MultiThreadDownload(int threadNum,
			int setConnectTimeout, int setReadTimeout) {
		super();
		this.threadNum = threadNum;
		this.setConnectTimeout = setConnectTimeout;
		this.setReadTimeout = setReadTimeout;
	}
	
	public MultiThreadDownload(long blockSize) {
		super();
		this.blockSize = blockSize;
		this.threadNum = 0;
	}
	
	public MultiThreadDownload(int threadNum) {
		super();
		this.threadNum = threadNum;
		this.blockSize = 0;
	}
	
	public MultiThreadDownload(){}
	
	public long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getSetConnectTimeout() {
		return setConnectTimeout;
	}

	public void setSetConnectTimeout(int setConnectTimeout) {
		this.setConnectTimeout = setConnectTimeout;
	}

	public int getSetReadTimeout() {
		return setReadTimeout;
	}

	public void setSetReadTimeout(int setReadTimeout) {
		this.setReadTimeout = setReadTimeout;
	}

	public synchronized long addLen(int downSize){
		this.downLen = this.downLen + downSize;
		synchronized (object) {
        	object.notify();
		}
		return this.downLen;
	}
	
	/**
	 * 下载网络文件（异步）
	 * @param url
	 * @param path
	 * @param fileName
	 */
	@SuppressWarnings("rawtypes")
	public void downFile(final URL url,final String path,final String fileName,boolean listen) throws DownLoadException{
		Callable callable = new Callable() {
			@Override
			public Future call() throws Exception {
				downLoad(url,path,fileName);
				return null;
			}
		};
		pool.submit(callable);
		// 监听状态
		if(listen)
			listenStatus();
	}
	/**
	 * 下载一个图片然后压缩，异步进行
	 * @param url
	 * @param path
	 * @param fileName
	 * @param listen
	 * @throws DownLoadException
	 */
	@SuppressWarnings("rawtypes")
	public void downImageThenResizeAsyn(final URL url,final String path,final String fileName,final int width,final float quality) throws DownLoadException{
		Callable callable = new Callable() {
			@Override
			public Future call() throws Exception {
				File img = downLoad(url,path,fileName);
				ImageResize imageResize = new ImageResize();
				imageResize.resizeAsynchronous(img, width, quality, true);
				return null;
			}
		};
		pool.submit(callable);
	}
	
	/**
	 * 异步下载压缩
	 * @param url
	 * @param path
	 * @param fileName
	 * @param width
	 * @param height
	 * @param quality
	 * @param del
	 * @throws DownLoadException
	 */
	@SuppressWarnings("rawtypes")
	public void downImageThenResizeAsyn(final URL url,final String path,final String fileName,final int width,final float quality,final boolean del) throws DownLoadException{
		Callable callable = new Callable() {
			@Override
			public Future call() throws Exception {
				File img = downLoad(url,path,fileName);
				ImageResize imageResize = new ImageResize();
				imageResize.resizeAsynchronous(img, width ,quality, del);
				return null;
			}
		};
		pool.submit(callable);
	}
	
	/**
	 * 同步下载压缩图片
	 * @param url
	 * @param path
	 * @param fileName
	 * @param width
	 * @param quality
	 * @throws DownLoadException
	 */
	public void downImageThenResizeSyn(final URL url,final String path,final String fileName,final int width,final float quality) throws DownLoadException{
		File img = downLoad(url,path,fileName);
		ImageResize imageResize = new ImageResize();
		imageResize.resizeAsynchronous(img, width, quality, true);
	}
	/**
	 * 下载文件
	 * @param url
	 * @param path
	 * @param fileName
	 * @return
	 * @throws DownLoadException
	 */
	@SuppressWarnings("unchecked")
	public File downLoad(final URL url,String path,String fileName) throws DownLoadException{
		// 重置状态
		this.downLen = -1;
		this.contentLen = 0;
		this.date = new Date();
		this.finished = false;
		try {
			URLConnection con = url.openConnection();
			//获得资源长度
			this.contentLen = con.getContentLength();
			//检查文件名
			if(StringUtils.isBlank(fileName)){
				fileName = StringUtils.substringAfterLast(url.getPath(), "/");
			}
			//检查目录
			File _path = new File(path);
			if(!_path.exists()){
				_path.mkdirs();
			}
			final File file = new File(path+File.separator+fileName);
			if(file.exists())
				file.delete();
			if(this.threadNum==0&&this.blockSize>0){
				this.threadNum = (int) (contentLen /blockSize);
			}
			long subLen = contentLen / threadNum; 
			List<Future<DownLoadBean>> result = Lists.newArrayList();
			for(int i=0;i<threadNum;i++){
				final int pos = (int) (i*subLen);
				final int end = (int) ((i+1)*subLen)-1;
				final int current = pos;
				Future<DownLoadBean> f = (Future<DownLoadBean>) pool.submit(new Callable<DownLoadBean>() {
					int $pos = pos;
					int $end = end;
					int $current = current;
					@Override
					public DownLoadBean call() throws Exception {
						//创建一个buff
						BufferedInputStream bis = null;
				        RandomAccessFile fos = null;                                               
				        byte[] buf = new byte[BUFFER_SIZE];
				        URLConnection con = null;
				        try {
				            con = url.openConnection();
				            con.setAllowUserInteraction(true);
				            //设置获取资源数据的范围，从startPos到endPos
			                con.setRequestProperty("Range", "bytes=" + $pos + "-" + $end);
			                fos = new RandomAccessFile(file, "rw");
			                //从startPos开始
			                fos.seek($pos);
				            //下面一段向根据文件写入数据，curPos为当前写入的未知，这里会判断是否小于endPos，
				            //如果超过endPos就代表该线程已经执行完毕
				            bis = new BufferedInputStream(con.getInputStream());
				            while ($current < $end) {
				                int len = bis.read(buf, 0, BUFFER_SIZE);
				                if (len == -1) {
				                    break;
				                }
				                fos.write(buf, 0, len);
				                $current = $current + len;
				                if ($current-1 > $end) {
				                    throw new DownLoadException("下载内容出错，当前位置超出结束位置");
				                }
				                addLen(len);
				            }
				            bis.close();
				            fos.close();
				        } catch (IOException ex) {
				        	/* 异常时记录当前位置（不能保证准确，没事事务性）
				        	StringBuffer sb = new StringBuffer();
				        	sb.append($pos).append("\t").append($current).append("\t").append($end).append("\n");
				            FileUtils.write(new File(file.getAbsolutePath()+".pos"), sb, true);
				            */
				            throw new RuntimeException(ex);
				        }
				        log.debug(this.hashCode()+":下载完成块["+$pos+","+$end+"]");
						return new DownLoadBean($pos, $end, $current);
					}
				});
				result.add(f);
			}
			Long resultTotal = 0L;
			for(Future<DownLoadBean> f:result){
				DownLoadBean dInfo = f.get();
				resultTotal += dInfo.getCurrent() - dInfo.getPos() ;
			}
			// 　判断文件是否完整下载
			if(contentLen!=resultTotal+1){
				// 记录下载错误的链接，从而可以从中恢复
				FileUtils.write(new File(down_error_log_file), url.toString()+"\n", true);
				throw new DownLoadException("文件下载不完整");
			}else {
				finished = true;
				return file;
			}
		} catch (IOException | InterruptedException | ExecutionException e) {
			// 记录下载错误的链接
			try {
				FileUtils.write(new File(down_error_log_file), url.toString()+"\n", true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 监听并打印下载状态
	 */
	private void listenStatus(){
		new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (object) {
						while(true){
							Thread.sleep(1000L);
							object.wait();
							log.debug(getPercent()+"\t"+getSpeed());
							if(finished()){
								break;
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.run();
	}
	/**
	 * 返回下载进度
	 * @return
	 */
	public String getPercent(){
		if(this.downLen==0){
			return decimalFormat.format(0f);
		}
		Float p =  ((float)this.downLen/(float)this.contentLen);
		return decimalFormat.format(p);
	}
	/**
	 * 返回下载速度
	 * @return
	 */
	public String getSpeed(){
		int cost = ((int) (System.currentTimeMillis() - date.getTime()))/1000;
		int s = (int) (this.downLen/cost)/1024;
		return String.valueOf(s+"(KB)/s");
	}
	/**
	 * 是否下载完成
	 * @return
	 */
	public boolean finished(){
		return this.finished;
	}
	
	public static void main(String[] args) {
		try {
			MultiThreadDownload multiThreadDownload = new MultiThreadDownload(1024*1024L);
			URL url = new URL("http://zhangmenshiting.baidu.com/data2/music/65517089/307842151200128.mp3?xcode=53102624c6c63d206dbeaf3b8ae12d9080af3c8af038c7a6");
//			URL url = new URL("http://www.baidu.com/img/bdlogo.gif");
			// 异步下载文件
			multiThreadDownload.downFile(url, "d:\\multidown\\", "",true);
			// 同步下载文件
//			multiThreadDownload.downLoad(url, "d:\\multidown\\", "");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DownLoadException e) {
			e.printStackTrace();
		} 
	}
}

/**
 * 
 * @author whiteme
 * @date 2013年10月20日
 * @desc 一个块的下载信息
 */
class DownLoadBean {
	private URL url;
	private File distFile;
	private int pos=0;
	private int end = 0;
	private int current = 0;
	
	public DownLoadBean(int pos, int end, int current) {
		super();
		this.pos = pos;
		this.end = end;
		this.current = current;
	}
	
	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getLength() {
		return end;
	}

	public void setLength(int length) {
		this.end = length;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DownLoadInfo [url=").append(url).append(", distFile=")
				.append(distFile).append(", pos=").append(pos)
				.append(", length=").append(end).append(", current=")
				.append(current).append("]");
		return builder.toString();
	}
}