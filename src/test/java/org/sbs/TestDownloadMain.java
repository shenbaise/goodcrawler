package org.sbs;

import org.sbs.util.download.DownloadUtils;

public class TestDownloadMain {
 
    public static void main(String[] args) {
        /*DownloadInfo bean = new DownloadInfo("http://i7.meishichina.com/Health/UploadFiles/201109/2011092116224363.jpg");
        System.out.println(bean);
        BatchDownloadFile down = new BatchDownloadFile(bean);
        new Thread(down).start();*/
        
        //DownloadUtils.download("http://i7.meishichina.com/Health/UploadFiles/201109/2011092116224363.jpg");
        DownloadUtils.download("http://zhangmenshiting.baidu.com/data2/music/236522/236497176400128.mp3?xcode=679c87bb7023c00f14276288b40c9c38241181f3f92f109f", "aa.mp3", "c:/temp", 1);
    }
}