package com.yue.czcontrol.utils;

import com.yue.czcontrol.exception.UploadFailedException;

public interface SocketSetting{
	
	/**
	 * add Data to DataBase
	 * @param msg msg
	 * @throws UploadFailedException upload failed
	 */
	void addData(String msg) throws UploadFailedException;
	
	/**
	 * get data from database
	 */
	void initData();
	
	/**
	 * send Message to server
	 * @param msg msg
	 */
	void message(String msg);
	
}
