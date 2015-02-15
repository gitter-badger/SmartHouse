/*
 * ��������� ���������� ��� ������� "����� ���"
 * 
 * author: ����������� �������
 * email: nick.godov@gmail.com
 * last edit: 11.09.2014
 */

package com.isosystem.smarthouse;

import android.app.Application;
import android.preference.PreferenceManager;

import com.isosystem.smarthouse.data.AlarmMessages;
import com.isosystem.smarthouse.data.FormattedScreens;
import com.isosystem.smarthouse.data.MenuTree;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.notifications.SoundMessages;
import com.isosystem.smarthouse.processor.XMLProcessor;

/**
 * ���� ����� �������� ������ ������ ���� {@link MenuTree}, � �����
 * ������-���������� ������ ���� {@link XMLProcessor}, ������� ���������
 * ��������� � ���������� ������ ���� � ���� �� ��������.
 * 
 * @author ����������� ������� (nick.godov@gmail.com)
 * @see MenuTree
 * @see XMLProcessor
 */
public class MyApplication extends Application {
	/** ������ ���� */
	public MenuTree mTree;

	/** ������ ��������� ��������� */
	public AlarmMessages mAlarmMessages;

	int pid;

	public Boolean isUsbConnected = false;

	/** ������ ���� ���������������� ������ */
	public FormattedScreens mFormattedScreens;

	/** ���������� ������ ���� (��������\����������) */
	public XMLProcessor mProcessor;

	public SoundMessages soundMessages;

	/**
	 * ��� ������ ����������:<br>
	 * 1) ��������� ������� ������� �����; <br>
	 * 2) �������� ������-����������; <br>
	 * 3) ��������� ������ ���� � ������� �������-�����������.
	 *
	 */
	@Override
	public void onCreate() {

		PreferenceManager.setDefaultValues(this, R.xml.application_preferences,
				false);

		this.pid = android.os.Process.myPid(); // Save for later use.
		soundMessages = new SoundMessages(getApplicationContext());
		mProcessor = new XMLProcessor(getApplicationContext());

		if (!mProcessor.loadMessagesFromInternalStorage()) {
			Logging.v("�������� ��������� �� ����������� ��������� ������ � �������");
		}
	}

	public void killApp() {
		android.os.Process.sendSignal(pid, android.os.Process.SIGNAL_KILL);
	}
}