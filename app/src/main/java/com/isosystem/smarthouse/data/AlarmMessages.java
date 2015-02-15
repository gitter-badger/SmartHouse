package com.isosystem.smarthouse.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.notifications.Notifications.MessageType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ����� "��������� ���������"
 */
public class AlarmMessages implements Serializable {
	// ������ ���������
	public ArrayList<AlarmMessage> mAlarmMessages;

	public AlarmMessages() {
		mAlarmMessages = new ArrayList<AlarmMessage>();
	}

	/**
	 * ���������� ������ ���������: 1) ���������� ��������� � ������ 2) Toast �
	 * ���������� 3) ���������� ����� � �����������
	 */

	public void addAlarmMessage(Context c, String msg, MessageType type) {
		// ������������ ��������� ������� ��� ��������� ���������� ���������
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		// ��������� - ����� �� ������������ �����
		if (prefs.getBoolean("enable_alarm_messages_sound_alert", false)) {
			MyApplication mApplication = (MyApplication) c
					.getApplicationContext();
			mApplication.soundMessages.playAlarmSound();
		}

		// 1. ���������� � ������
		AlarmMessage alarmMessage = new AlarmMessage(msg, type);
		this.mAlarmMessages.add(0, alarmMessage);

		if (type == MessageType.USBConnectionMessage) {
			Notifications.showUsbMessage(c, msg);
		} else if (type == MessageType.ControllerMessage) {
			Notifications.showControllerAlarmMessage(c, msg);
		} else if (type == MessageType.PowerSupplyMessage) {
			Notifications.showPowerSupplyMessage(c, msg);
		}

		// 3. ���������� ����� � ����������
		saveMessagesToFile(c);
	}

	/**
	 * ���������� ��������� � ����
	 * 
	 * @param c
	 *            ��������
	 */
	public void saveMessagesToFile(Context c) {
		MyApplication myApp = (MyApplication) c.getApplicationContext();

		// // ���������� 04.02
		// try {
		// myApp.mProcessor.serializeAlarmMessages(myApp.mAlarmMessages);
		// } catch (Exception e) {
		// Logging.v("���������� ��� ������� �������� ���� � �����������");
		// e.printStackTrace();
		// }

		try {
			myApp.mProcessor.saveMessagesToInternalStorage();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ���� � �����������");
			e.printStackTrace();
		}

	}

	/**
	 * �������� ��������: 1) �������� ��������� 2) ���������� ����� �
	 * �����������
	 * 
	 * @param index
	 *            ������ ��������� � ������
	 * @param c
	 *            ��������
	 */
	public void clearMessage(int index, Context c) {
		// 1. �������� ���������
		this.mAlarmMessages.remove(index);
		// 2. ������ �� ����
		this.saveMessagesToFile(c);
	}

	/**
	 * �������� ���� ���������: 1) �������� ��������� 2) ���������� ����� �
	 * �����������
	 * 
	 * @param c
	 *            ��������
	 */
	public void clearAllMessages(Context c) {
		// 1. �������� ���� ���������
		this.mAlarmMessages = new ArrayList<AlarmMessage>();
		// 2. ������ �� ����
		this.saveMessagesToFile(c);
	}

}