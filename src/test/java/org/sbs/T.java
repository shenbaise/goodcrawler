package org.sbs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class T {
	
	/**
	 * @param args
	 * @desc 
	 */
	public static void main(String[] args) {
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				byte[] b = new byte[128];
				while(true){
					try {
						int i = System.in.read(b);
						String input = new String(b);
						input = input.replace("\n", "").replace("\r", "").trim();
						if(input.equalsIgnoreCase("quit")){
							
							System.exit(0);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
		
		
//		String textString = "片名：床的另一边发布时间：2013-07-18评分：7.2";
//		Pattern pattern = Pattern.compile("(\\d{1}[.]\\d{1,2})");
//		Matcher m = pattern.matcher(textString);
//		while(m.find()){
//			System.out.println(m.group(1));
//		}
		
//		pattern = Pattern.compile("http://.*.wasu.cn/Play/show/id/\\d+");
//		System.out.println(pattern.matcher("http://www.wasu.cn/Play/show/id/216708").matches());
//		System.out.println(Pattern.matches("http://.*.wasu.cn/Play/show/id/\\d+", "http://www.wasu.cn/Play/show/id/216708"));
//		
		
	}

}
