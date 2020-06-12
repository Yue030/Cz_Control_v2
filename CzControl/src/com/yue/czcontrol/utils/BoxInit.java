package com.yue.czcontrol.utils;

import javax.swing.JComboBox;

import com.yue.czcontrol.exception.NameNotFoundException;

public interface BoxInit {
	/**
	 * Set the JComboBox's item
	 * @param box JComboBox
	 */
	void initBox(JComboBox<String> box);
	
	/**
	 * Use member name to get ID
	 * @param name member's name
	 * @return String name
	 * @throws NameNotFoundException When the NameNotFound
	 */
	String getID(String name) throws NameNotFoundException;
}
