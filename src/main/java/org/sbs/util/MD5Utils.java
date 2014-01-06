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
package org.sbs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author shenbaise(shenbaise@outlook.com)
 * @date 2013-6-29
 * md5工具
 */
public class MD5Utils {
    
    private static final String ALGORIGTHM_MD5 = "MD5";
    private static final int CACHE_SIZE = 4096;
    
    public static String createMD5(String input) {
        try {
			return createMD5(input, null);
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
    }
    
    public static String createMD5(String input, String charset) throws Exception {
        byte[] data;
        if (charset != null && !"".equals(charset)) {
            data = input.getBytes(charset);
        } else {
            data = input.getBytes();
        }
        MessageDigest messageDigest = getMD5();
        messageDigest.update(data);
        return byteArrayToHexString(messageDigest.digest());
    }
    
    public static String generateFileMD5(String filePath) throws Exception {
        String md5 = "";
        File file = new File(filePath);
        if (file.exists()) {
            MessageDigest messageDigest = getMD5();
            InputStream in = new FileInputStream(file);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                messageDigest.update(cache, 0, nRead);
            }
            in.close();
            byte data[] = messageDigest.digest();
            md5 = byteArrayToHexString(data);
         }
        return md5;
    }
    
    private static String byteArrayToHexString(byte[] data) {
        // 用来将字节转换成 16 进制表示的字符
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' 
        };
        // 每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
        char arr[] = new char[16 * 2];
        int k = 0; // 表示转换结果中对应的字符位置
        // 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
        for (int i = 0; i < 16; i++) {
            byte b = data[i]; // 取第 i 个字节
            // 取字节中高 4 位的数字转换, >>>为逻辑右移，将符号位一起右移
            arr[k++] = hexDigits[b >>> 4 & 0xf];
            // 取字节中低 4 位的数字转换
            arr[k++] = hexDigits[b & 0xf];
        }
        // 换后的结果转换为字符串
        return new String(arr);
    }
    
    
    private static MessageDigest getMD5() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(ALGORIGTHM_MD5);
    }
    
}