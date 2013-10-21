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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * 下载
 * @author whiteme
 * @date 2013年10月20日
 * @desc
 */
public class BatchDownloadFile implements Runnable {
    //下载文件信息 
    private DownloadInfo downloadInfo;
    //一组开始下载位置
    private long[] startPos;
    //一组结束下载位置
    private long[] endPos;
    //休眠时间
    private static final int SLEEP_SECONDS = 500;
    //子线程下载
    private DownloadFile[] fileItem;
    //文件长度
    private int length;
    //是否第一个文件
    private boolean first = true;
    //是否停止下载
    private boolean stop = false;
    //临时文件信息
    private File tempFile;
    
    public BatchDownloadFile(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
        String tempPath = this.downloadInfo.getFilePath() + File.separator + downloadInfo.getFileName() + ".position";
        tempFile = new File(tempPath);
        //如果存在读入点位置的文件
        if (tempFile.exists()) {
            first = false;
            //就直接读取内容
            try {
                readPosInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //数组的长度就要分成多少段的数量
            startPos = new long[downloadInfo.getSplitter()];
            endPos = new long[downloadInfo.getSplitter()];
        }
    }
    
    @Override
    public void run() {
        //首次下载，获取下载文件长度
        if (first) {
            length = this.getFileSize();//获取文件长度
            if (length <=0) {
                LogUtils.log("read file length is error!");
                stop = true;
            } else if (length > 0) {
                /**
                 * eg 
                 * start: 1, 3, 5, 7, 9
                 * end: 3, 5, 7, 9, length
                 */
                for (int i = 0, len = startPos.length; i < len; i++) {
                    int size = i * (length / len);
                    startPos[i] = size;
                    
                    //设置最后一个结束点的位置
                    if (i == len - 1) {
                        endPos[i] = length;
                    } else {
                        size = (i + 1) * (length / len);
                        endPos[i] = size;
                    }
                    LogUtils.log("start-end Position[" + i + "]: " + startPos[i] + "-" + endPos[i]);
                }
            } else {
                LogUtils.log("get file length is error, download is stop!");
                stop = true;
            }
        }
        
        //子线程开始下载
        if (!stop) {
            //创建单线程下载对象数组
            fileItem = new DownloadFile[startPos.length];//startPos.length = downloadInfo.getSplitter()
            for (int i = 0; i < startPos.length; i++) {
                try {
                    //创建指定个数单线程下载对象，每个线程独立完成指定块内容的下载
                    fileItem[i] = new DownloadFile(
                        downloadInfo.getUrl(), 
                        this.downloadInfo.getFilePath() + File.separator + downloadInfo.getFileName(), 
                        startPos[i], endPos[i], i
                    );
                    fileItem[i].start();//启动线程，开始下载
                    LogUtils.log("Thread: " + i + ", startPos: " + startPos[i] + ", endPos: " + endPos[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * <b>function:</b>读取写入点的位置信息
     * @author hoojo
     * @createDate 2011-9-23 下午05:30:29
     * @throws IOException
     */
    private void readPosInfo() throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(tempFile));
        int startPosLength = dis.readInt();
        startPos = new long[startPosLength];
        endPos = new long[startPosLength];
        for (int i = 0; i < startPosLength; i++) {
            startPos[i] = dis.readLong();
            endPos[i] = dis.readLong();
        }
        dis.close();
    }
    
    /**
     * <b>function:</b> 获取下载文件的长度
     * @author hoojo
     * @createDate 2011-9-26 下午12:15:08
     * @return
     */
    private int getFileSize() {
        int fileLength = -1;
        try {
        	URL url = new URL(this.downloadInfo.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            DownloadFile.setHeader(conn);
            int stateCode = conn.getResponseCode();
            //判断http status是否为HTTP/1.1 206 Partial Content或者200 OK
            if (stateCode != HttpURLConnection.HTTP_OK && stateCode != HttpURLConnection.HTTP_PARTIAL) {
                LogUtils.log("Error Code: " + stateCode);
                return -2;
            } else if (stateCode >= 400) {
                LogUtils.log("Error Code: " + stateCode);
                return -2;
            } else {
                //获取长度
                fileLength = conn.getContentLength();
                LogUtils.log("FileLength: " + fileLength);
            }
            DownloadFile.printHeader(conn);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileLength;
    }
}