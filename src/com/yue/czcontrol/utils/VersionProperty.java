package com.yue.czcontrol.utils;

public interface VersionProperty {
	/**
	 * Release Year
	 */
	static final int DATE_YEAR = 2020;
	
	/**
	 * Release Mouth and Day
	 */
	static final int DATE_MD = 612;
	
	/**
	 * Release on AM(3) or PM(7)
	 */
	static final int RELEASE_AM_PM = 7;
	
	/**
	 * Release Time
	 */
	static final int RELEASE_TIME = 916;
	
	/**
	 * Release count of day
	 */
	static final int RELEASE_COUNT = 1;
	
	/**
	 * Release Date
	 */
	static final String RELEASE_DATE = "2020/06/12";
	
	/**
	 * get Version
	 * @return String
	 */
	String getVersion();
	
	/**
	 * get Release Date
	 * @return String
	 */
	String getReleaseDate();
}
