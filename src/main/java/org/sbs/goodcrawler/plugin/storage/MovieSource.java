package org.sbs.goodcrawler.plugin.storage;

import java.util.List;
import java.util.TreeMap;

/**
 * 影片资源，可能更新比较频繁
 */
public class MovieSource {

	/**
	 * 主键，使用片名或者片名的拼音
	 */
	private String id;
	/**
	 * 下载资源
	 */
	private TreeMap<String, List<Source>> download;
	/**
	 * 在线观看资源
	 */
	private TreeMap<String, List<Source>> online;

	public MovieSource() {
	};

	public MovieSource(String id, TreeMap<String, List<Source>> download,
			TreeMap<String, List<Source>> online) {
		super();
		this.id = id;
		this.download = download;
		this.online = online;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TreeMap<String, List<Source>> getDownload() {
		return download;
	}

	public void setDownload(TreeMap<String, List<Source>> download) {
		this.download = download;
	}

	public TreeMap<String, List<Source>> getOnline() {
		return online;
	}

	public void setOnline(TreeMap<String, List<Source>> online) {
		this.online = online;
	}

	public static void main(String[] args) {
		System.out.println("Hello World!");
	}

	/**
	 * 资源
	 */
	class Source {
		/**
		 * 资源连接
		 */
		private String url;
		/**
		 * 有效值（值为负，小于-20删除之）， 有人举报连接无效值-1，举报连接有效+2（考虑无效连接投诉率高，有效连接反馈率低）
		 */
		private int value;

		public Source() {
		}

		public Source(String url, int value) {
			super();
			this.url = url;
			this.value = value;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	}
}
