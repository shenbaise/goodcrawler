package org.sbs.goodcrawler.plugin.storage;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

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

/**
 * @author whiteme
 * @date 2013年7月18日
 * @desc 影片对象，直接转换为json，提交到es。
 */
public class Movie {
	public Movie() {
	}

	/**
	 * 主键，使用片名或者片名的拼音
	 */
	private String id;
	/**
	 * 影片名称、可省略
	 */
	private String title;
	/**
	 * 影片翻译名称（别名）
	 */
	private String translation;
	/**
	 * 国家
	 */
	private String nation;
	/**
	 * 地区
	 */
	private String area;
	/**
	 * 出品时间
	 */
	private String publishTime;
	/**
	 * 分类（电影，电视剧，综艺等）
	 */
	private String category;
	/**
	 * 影片类型（动作、爱情、科幻等）
	 */
	private Set<String> type;
	/**
	 * 集数
	 */
	private int number;
	/**
	 * 制片人
	 */
	private String studioManager;
	/**
	 * 导演
	 */
	private String director;
	/**
	 * 主演
	 */
	private Set<String> actors;
	/**
	 * 影片长度
	 */
	private int duration;
	/**
	 * 语言
	 */
	private String lang;
	/**
	 * 评分
	 */
	private float score;
	/**
	 * 视频尺寸 1024*768
	 */
	private String size;
	/**
	 * 年代
	 */
	private int year;
	/**
	 * 热度
	 */
	private int hot;
	/**
	 * 播放、点击、搜索次数
	 */
	private int hits;
	/**
	 * 简介
	 */
	private String summary;
	/**
	 * 缩略图（在存储到es时下载并压缩图像）
	 */
	private String thumbnail;

	/**
	 * 下载资源
	 */
	private TreeMap<String, List<String>> download;
	/**
	 * 在线观看资源
	 */
	private TreeMap<String, List<String>> online;

	public static void main(String[] args) {
		System.out.println("Hello World!");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Set<String> getType() {
		return type;
	}

	public void setType(Set<String> type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getStudioManager() {
		return studioManager;
	}

	public void setStudioManager(String studioManager) {
		this.studioManager = studioManager;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public Set<String> getActors() {
		return actors;
	}

	public void setActors(Set<String> actors) {
		this.actors = actors;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public TreeMap<String, List<String>> getDownload() {
		return download;
	}

	public void setDownload(TreeMap<String, List<String>> download) {
		this.download = download;
	}

	public TreeMap<String, List<String>> getOnline() {
		return online;
	}

	public void setOnline(TreeMap<String, List<String>> online) {
		this.online = online;
	}
	
	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}
	
	
	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	/**
	 * 合并2个movie
	 */
	public Movie merge(Movie movie) {
		if (StringUtils.isNotBlank(movie.getTranslation())) {
			this.translation = movie.getTranslation();
		}
		if (StringUtils.isNotBlank(movie.getNation())) {
			this.nation = movie.getNation();
		}
		if (StringUtils.isNotBlank(movie.getArea())) {
			this.area = movie.getArea();
		}
		if (StringUtils.isNotBlank(movie.getPublishTime())) {
			this.publishTime = movie.getPublishTime();
		}
		if (StringUtils.isNotBlank(movie.getCategory())) {
			this.category = movie.getCategory();
		}
		if (movie.getNumber() > this.number) {
			this.number = movie.getNumber();
		}
		if (StringUtils.isNotBlank(movie.getStudioManager())) {
			this.studioManager = movie.getStudioManager();
		}
		if (StringUtils.isNotBlank(movie.getLang())) {
			this.lang = movie.getLang();
		}
		if (StringUtils.isNotBlank(movie.getDirector())) {
			this.director = movie.getDirector();
		}
		if (movie.getDuration() > 0) {
			this.duration = movie.getDuration();
		}
		if (null != movie.getType() && movie.getType().size() > 0) {
			if (this.type != null) {
				this.type.addAll(movie.getType());
			} else
				this.type = movie.getType();
		}
		if (null != movie.getActors() && movie.getActors().size() > 0) {
			if (null != this.actors) {
				this.actors.addAll(movie.getActors());
			} else
				this.actors = movie.getActors();
		}
		if (movie.getScore() > 0) {
			this.score = movie.getScore();
		}
		if (StringUtils.isBlank(this.size)
				&& StringUtils.isNotBlank(movie.getSize())) {
			this.size = movie.getSize();
		}
		if (movie.getYear() > 0) {
			this.year = movie.getYear();
		}
		if (StringUtils.isBlank(this.summary)
				&& StringUtils.isNotBlank(movie.getSummary())) {
			this.summary = movie.getSummary();
		}
		if (StringUtils.isBlank(this.thumbnail)
				&& StringUtils.isNotBlank(movie.getThumbnail())) {
			this.thumbnail = movie.getThumbnail();
		}
		if (null != movie.getDownload() && movie.getDownload().size() > 0) {
			if (this.download != null) {
				this.download.putAll(movie.getDownload());
			} else
				this.download = movie.getDownload();
		}
		if (null != movie.getOnline() && movie.getOnline().size() > 0) {
			if (this.online != null) {
				this.online.putAll(movie.getOnline());
			} else
				this.online = movie.getOnline();
		}
		
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Movie [id=").append(id).append(", title=")
				.append(title).append(", translation=").append(translation)
				.append(", nation=").append(nation).append(", area=")
				.append(area).append(", publishTime=").append(publishTime)
				.append(", category=").append(category).append(", type=")
				.append(type).append(", number=").append(number)
				.append(", studioManager=").append(studioManager)
				.append(", director=").append(director).append(", actors=")
				.append(actors).append(", duration=").append(duration)
				.append(", lang=").append(lang).append(", score=")
				.append(score).append(", size=").append(size).append(", year=")
				.append(year).append(", hot=").append(hot).append(", hits=")
				.append(hits).append(", summary=").append(summary)
				.append(", thumbnail=").append(thumbnail).append(", download=")
				.append(download).append(", online=").append(online)
				.append("]");
		return builder.toString();
	}
	
	
}