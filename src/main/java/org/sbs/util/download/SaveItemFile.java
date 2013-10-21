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

import java.io.IOException;
import java.io.RandomAccessFile;

public class SaveItemFile {
    /*
     * 存储文件
     */
    private RandomAccessFile itemFile;
    
    public SaveItemFile() throws IOException {
        this("", 0);
    }
    
    /**
     * @param name 文件路径、名称
     * @param pos 写入点位置 position
     * @throws IOException
     */
    public SaveItemFile(String name, long pos) throws IOException {
        itemFile = new RandomAccessFile(name, "rw");
        //在指定的pos位置开始写入数据
        itemFile.seek(pos);
    }
    
    /**
     * <b>function:</b> 同步方法写入文件
     * @param buff 缓冲数组
     * @param start 起始位置
     * @param length 长度
     * @return
     */
    public synchronized int write(byte[] buff, int start, int length) {
        int i = -1;
        try {
            itemFile.write(buff, start, length);
            i = length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }
    
    public void close() throws IOException {
        if (itemFile != null) {
            itemFile.close();
        }
    }
}