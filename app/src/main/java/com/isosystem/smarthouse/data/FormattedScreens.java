package com.isosystem.smarthouse.data;

import android.content.Context;

import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * ����� "���� ���������������� ������"
 */
public class FormattedScreens implements Serializable {

	// ������ ����
	public ArrayList<FormattedScreen> mFormattedScreens;

	public FormattedScreens() {
		mFormattedScreens = new ArrayList<FormattedScreen>();
	}

	/**
	 * ���������� ������ ���� ���������������� ������
	 */
	public void addFormattedScreen(Context c, String name, String start, String end, HashMap<String, String> pmap) {
		mFormattedScreens.add(new FormattedScreen(name, start, end, pmap));
		this.saveScreensToFile(c);
	}
	
	public ArrayList<String> getFormattedScreens() {
		ArrayList<String> fsNames = new ArrayList<String>();
		for (int i=0;i<mFormattedScreens.size();i++) {
			fsNames.add(mFormattedScreens.get(i).mName);
		}
		return fsNames;
	}

	/**
	 * ���������� ���� � ����
	 * @param c ��������
	 */
	public void saveScreensToFile(Context c) {
		MyApplication myApp = (MyApplication) c.getApplicationContext();

		try {
			myApp.mProcessor.saveFormattedScreensToInternalStorage();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ���� � �����������");
			e.printStackTrace();
		}
	}

	public void swapScreens(Context c, int index1, int index2) {
		Collections.swap(this.mFormattedScreens, index1, index2);
		this.saveScreensToFile(c);
	}
	
	/**
	 * �������� ���� �� �������
	 */
	public void deleteScreenByIndex(Context c, int index) {
		this.mFormattedScreens.remove(index);
		this.saveScreensToFile(c);
	}

	public void changeFormattedScreen (Context c, int position, String name, String start, String end, HashMap<String, String> pmap) {
		mFormattedScreens.get(position).mName = name;
		mFormattedScreens.get(position).mInputStart = start;
		mFormattedScreens.get(position).mInputEnd = end;
		mFormattedScreens.get(position).paramsMap = pmap;
		
		this.saveScreensToFile(c);
	}
	
	/**
	 * �������� ���� ����
	 */
	public void deleteAllScreens(Context c) {
		this.mFormattedScreens.clear();
		this.saveScreensToFile(c);
	}
}