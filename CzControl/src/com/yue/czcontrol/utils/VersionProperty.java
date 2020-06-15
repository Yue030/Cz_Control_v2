package com.yue.czcontrol.utils;

public interface VersionProperty {
	/**
	 * Release Year
	 */
	int DATE_YEAR = 2020;
	
	/**
	 * Release Mouth and Day
	 */
	int DATE_MD = 616;
	
	/**
	 * Release on AM(3) or PM(7)
	 */
	int RELEASE_AM_PM = 3;
	
	/**
	 * Release Time
	 */
	int RELEASE_TIME = 506;
	
	/**
	 * Release count of day
	 */
	int RELEASE_COUNT = 1;
	
	/**
	 * Release Date
	 */
	String RELEASE_DATE = "2020/06/16";
	
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
