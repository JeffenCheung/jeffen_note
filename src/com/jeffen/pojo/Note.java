package com.jeffen.pojo;

public class Note {
	private long id;
	private String title;
	private String weather;
	private String body;
	private String created;
	private String yyyymm;
	private String hhmm;
	private String addDatetime;
	private String updDatetime;
	private String isStared;
	private String isDeleted;
	private long showIndex;
	private long sharedCnt;

	private int yearAlarm;
	private int monthAlarm;
	private int dayAlarm;
	private int hourAlarm;
	private int minuteAlarm;

	private int position;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getYyyymm() {
		return yyyymm;
	}

	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
	}

	public String getHhmm() {
		return hhmm;
	}

	public void setHhmm(String hhmm) {
		this.hhmm = hhmm;
	}

	public String getAddDatetime() {
		return addDatetime;
	}

	public void setAddDatetime(String addDatetime) {
		this.addDatetime = addDatetime;
	}

	public String getUpdDatetime() {
		return updDatetime;
	}

	public void setUpdDatetime(String updDatetime) {
		this.updDatetime = updDatetime;
	}

	public String getIsStared() {
		return isStared;
	}

	public void setIsStared(String isStared) {
		this.isStared = isStared;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public long getShowIndex() {
		return showIndex;
	}

	public void setShowIndex(long showIndex) {
		this.showIndex = showIndex;
	}

	public long getSharedCnt() {
		return sharedCnt;
	}

	public void setSharedCnt(long sharedCnt) {
		this.sharedCnt = sharedCnt;
	}

	public int getYearAlarm() {
		return yearAlarm;
	}

	public void setYearAlarm(int yearAlarm) {
		this.yearAlarm = yearAlarm;
	}

	public int getMonthAlarm() {
		return monthAlarm;
	}

	public void setMonthAlarm(int monthAlarm) {
		this.monthAlarm = monthAlarm;
	}

	public int getDayAlarm() {
		return dayAlarm;
	}

	public void setDayAlarm(int dayAlarm) {
		this.dayAlarm = dayAlarm;
	}

	public int getHourAlarm() {
		return hourAlarm;
	}

	public void setHourAlarm(int hourAlarm) {
		this.hourAlarm = hourAlarm;
	}

	public int getMinuteAlarm() {
		return minuteAlarm;
	}

	public void setMinuteAlarm(int minuteAlarm) {
		this.minuteAlarm = minuteAlarm;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
