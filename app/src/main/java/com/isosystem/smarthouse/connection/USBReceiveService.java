package com.isosystem.smarthouse.connection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications.MessageType;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ������ ��� ������ ��������� �� ����������� ����������� � ������
 */
public class USBReceiveService extends IntentService {
	MyApplication mApplication;

	UsbManager usbManager;
	HashMap<String, UsbDevice> deviceList;
	UsbDevice usbDevice;
	UsbInterface usbInterface;
	UsbEndpoint usbEndPointIn;
	UsbDeviceConnection usbConnection;

	// ����� ���������
	StringBuilder mMessageBuffer;
	// ���������� ������
	static Handler mMessageHandler;

	// ������������� ������� ������
	static Handler mBufferCleanHandler;
	// ����� ����� ������� ���������� ���������
	// ������� ������ ������ ��� ������� ������
	int mBufferClearTimeout = 2000;

	public USBReceiveService() {
		super("USBReceive");
		// Logging.v("������������� ReceiveService");
	}

	public void onCreate() {
		super.onCreate();
		mApplication = (MyApplication) getApplicationContext();

		mMessageBuffer = new StringBuilder();

		// ���������� ����-���� ��� ������� ������
		// �� ������������ �����
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mApplication);

		try {
			mBufferClearTimeout = Integer.parseInt(prefs.getString(
					"buffer_clean_timeout_value", "2000"));
		} catch (Exception e) {
			// Logging.v("������ ��� ������� ������� �������� ������� ���������� ������ �� preferences");
			e.printStackTrace();
		}

		mBufferCleanHandler = new Handler();
		mBufferCleanHandler.postDelayed(mBufferClearRunnable,
				mBufferClearTimeout);

		mMessageHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Bundle bundle = msg.getData();

				// ���������
				String message = bundle.getString("incoming_message");
				Logging.v("������ ���������: [" + message + "]");

				// ��������� ��������� � �����
				mMessageBuffer.append(message);
				Logging.v("�������� ������ ���������: ["
						+ mMessageBuffer.toString() + "]");

				// ����� ���������, ������� ���������� � @ ��� & ��� $
				// � ������������� �
				Pattern p = Pattern.compile("[@&$](.*?)�");
				Matcher m = p.matcher(mMessageBuffer);
				while (m.find()) {
					messageProcess(m.group());
					Logging.v("��������� �������: [" + m.group() + "]");
				}

				// ����� �������� ��������� �� ���������, ��� ��������� ��
				// ������
				mMessageBuffer = new StringBuilder(m.replaceAll(""));
				Logging.v("�������� ������ ����� ������ ������: ["
						+ mMessageBuffer.toString() + "]");
			} // handle message
		}; // handler

		Logging.v("�������� ReceiveService");

		/** 1. �������� ������ ��������� */
		try {
			usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			deviceList = usbManager.getDeviceList();
		} catch (Exception e) {
			// Logging.v("���������� ��� ������� �������� ������ USB ���������");
			e.printStackTrace();
			this.stopSelf();
			return;
		}

		// ��������� ����
		if (deviceList.isEmpty()) {
			// Logging.v("������ ����������, �.�. �� ���������� USB-����������");
			this.stopSelf();
			return;
		}

		// Logging.v("������� " + String.valueOf(deviceList.size()) +
		// " ���������");

		/** 2. ���� ����� ��������� ��������� ������ ���������� */
		for (UsbDevice device : deviceList.values()) {

			// Logging.v("Product ID " + String.valueOf(device.getProductId())
			// + " Vendoid ID " + String.valueOf(device.getVendorId())
			// + " Device ID " + String.valueOf(device.getDeviceId())
			// + " Class ID " + String.valueOf(device.getDeviceClass()));

			if ((device.getProductId() == 257)
					&& (device.getVendorId() == 65535)) {

				// Logging.v("���������� ����������");

				try {
					usbDevice = device;
				} catch (Exception e) {
					// Logging.v("���������� ��� ������� ����� ��������� USB-����������");
					e.printStackTrace();
					this.stopSelf();
					return;
				}
			}// if
		}

		// ������ ��������� ���
		if (usbDevice == null) {
			// Logging.v("���������� ��������� �� ����������");
			this.stopSelf();
			return;
		}

		// Logging.v("������� " + String.valueOf(usbDevice.getInterfaceCount())
		// + " �����������");

		/** 3. ����� ��������� ���������� */
		for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
			UsbInterface tempInterfce = usbDevice.getInterface(i);
			/*
			 * Logging.v("Endpoint count " +
			 * String.valueOf(tempInterfce.getEndpointCount()) +
			 * " Interface ID " + String.valueOf(tempInterfce.getId()) +
			 * " Protocol ID " +
			 * String.valueOf(tempInterfce.getInterfaceProtocol()) +
			 * " Class ID " + String.valueOf(tempInterfce.getInterfaceClass()));
			 */
			if (tempInterfce.getEndpointCount() > 1) {
				usbInterface = tempInterfce;
			}
		}

		// ������ ����������� ���
		if (usbInterface == null) {
			// Logging.v("���������� ����������� �� ����������");
			this.stopSelf();
			return;
		}

		// Logging.v("������� " +
		// String.valueOf(usbInterface.getEndpointCount())
		// + " �������� �����");

		/** 4. ����� EndPoint */
		for (int i = 0; i < usbInterface.getEndpointCount(); i++) {

			UsbEndpoint tempPoint = usbInterface.getEndpoint(i);
			/*
			 * Logging.v("Endpoint direction " +
			 * String.valueOf(tempPoint.getDirection()) + " Endpoint type" +
			 * String.valueOf(tempPoint.getType()) + " Protocol address " +
			 * String.valueOf(tempPoint.getAddress()));
			 */
			if (tempPoint.getDirection() == UsbConstants.USB_DIR_IN) {
				usbEndPointIn = tempPoint;
			}
		}// for

		// ������ �������� ����� ���
		if (usbEndPointIn == null) {
			// Logging.v("���������� �������� ����� �� ����������");
			this.stopSelf();
			return;
		}

		// ��������� ���������� ��� USB-����������
		try {
			usbConnection = usbManager.openDevice(usbDevice);
		} catch (Exception e) {
			// Logging.v("���������� ��� ������� ������� USB-����������");
			e.printStackTrace();
			this.stopSelf();
			return;
		}

		// ��������� ��������� � ������ ���������
		String msg = "USB-���������� ������������";

//		mApplication = (MyApplication) getApplicationContext();
//
//		if (!mApplication.equals(null)) {
//
//			if (mApplication.mAlarmMessages != null) {
//				try {
//					mApplication.mAlarmMessages.addAlarmMessage(mApplication,
//							msg, MessageType.USBConnectionMessage);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}

		mApplication.mAlarmMessages.addAlarmMessage(mApplication,
				msg, MessageType.USBConnectionMessage);
		
		// �������� ��������� � ���, ��� ������ ��������� ���������
		Intent i = new Intent();
		i.setAction(Globals.BROADCAST_INTENT_ALARM_MESSAGE);
		getApplicationContext().sendBroadcast(i);

		mApplication.isUsbConnected = true;
	}

	// Runnable ��� ������� ������ ����� N ����.
	private Runnable mBufferClearRunnable = new Runnable() {
		public void run() {
			// ������� ������
			mMessageBuffer = new StringBuilder();

			Logging.v("������� ������!");

			mBufferCleanHandler.removeCallbacks(mBufferClearRunnable);
			mBufferCleanHandler.postDelayed(mBufferClearRunnable,
					mBufferClearTimeout);
		}
	};

	private Boolean checkUsbDevice() {
		UsbManager manager;
		HashMap<String, UsbDevice> dList;

		manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		dList = manager.getDeviceList();

		if (dList.isEmpty())
			return false;

		for (UsbDevice device : dList.values()) {
			if ((device.getProductId() == 257)
					&& (device.getVendorId() == 65535)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		while (checkUsbDevice()) {
			// ����� ��� ������ ���������
			byte[] mReadBuffer = new byte[128];
			// ���������� �������� ����
			int transferred = -1;

			// ����� ���������
			try {
				usbConnection.claimInterface(usbInterface, true);
			} catch (Exception e) {
				// Logging.v("���������� ��� ������� ����� ���������");
				e.printStackTrace();
				continue;
			}

			// �������� ��������� �� endpoint
			try {
				transferred = usbConnection.bulkTransfer(usbEndPointIn,
						mReadBuffer, mReadBuffer.length, 0);
			} catch (Exception e) {
				// Logging.v("���������� ��� ������� ���������� ���������� ���������");
				e.printStackTrace();
				continue;
			}

			// ����������� ���������
			try {
				usbConnection.releaseInterface(usbInterface);
			} catch (Exception e) {
				// Logging.v("���������� ��� ������� ���������� ���������");
				e.printStackTrace();
				continue;
			}

			/** ������������ ��������� ������ */
			if (transferred >= 0) {

				// ���� ������ ��������� - ����� ����-���� ������� ������
				mBufferCleanHandler.removeCallbacks(mBufferClearRunnable);
				mBufferCleanHandler.postDelayed(mBufferClearRunnable,
						mBufferClearTimeout);

				String mReceivedMessage = new String(mReadBuffer, 0,
						transferred, Charset.forName("windows-1251"));

				// Logging.v("��������:[" + mReceivedMessage + "]");

				Bundle b = new Bundle();
				b.putString("incoming_message", mReceivedMessage);

				Message msg = new Message();
				msg.setData(b);

				mMessageHandler.sendMessage(msg);

			} // end if transferred
		} // end while
	} // end onHandleIntent

	/**
	 * ��������� ���������� ���������: 1) ��������� ������ ������, ����� ������
	 * ��� ���������
	 * 
	 * @param message
	 *            ��������� ���������
	 */
	private void messageProcess(String message) {
		Intent i = new Intent();

		// �������� ���������� ������� = �
		message = message.substring(0, message.length() - 1);

		if (message.charAt(0) == '$') {
			// ������ ��������� ���������
			if (message.length() > 2) {
				// ������� $ � ������
				String alarmMessage = message.substring(2);

				// Logging.v("�������� ��������� ���������: " + alarmMessage);

				mApplication.mAlarmMessages.addAlarmMessage(
						getApplicationContext(), alarmMessage,
						MessageType.ControllerMessage);

				// ������ ���������
				i.setAction(Globals.BROADCAST_INTENT_ALARM_MESSAGE);
				getApplicationContext().sendBroadcast(i);
			} else {
				// ������ ��������� ������ 1 ��� 2 �������, ��� ������ ��� $
				// Logging.v("������ ������������ ��������� ���������: " +
				// message);
			} // end if length
		} else if (message.charAt(0) == '&') {
			// ������ ������� ��������
			i.setAction(Globals.BROADCAST_INTENT_VALUE_MESSAGE);
			i.putExtra("message", message);
			// ������ ���������
			getApplicationContext().sendBroadcast(i);
		} else if (message.charAt(0) == '@') {
			// ������ ��������� ���������������� ������
			i.setAction(Globals.BROADCAST_INTENT_FORMSCREEN_MESSAGE);
			i.putExtra("message", message);
			// ������ ���������
			getApplicationContext().sendBroadcast(i);
		} else {
			// Logging.v("������ ��������� ������������ �������: " + message);
		} // end char[0]
	} // end method

	public void onDestroy() {
		try {
			// Logging.v("����������� ReceiveService");

			try {
				usbConnection.releaseInterface(usbInterface);
			} catch (Exception e) {
				// Logging.v("���������� ��� ������� ���������� ���������");
				e.printStackTrace();
			}

			if (mBufferCleanHandler != null) {
				mBufferCleanHandler.removeCallbacks(mBufferClearRunnable);
			}

			// ��������� ��������� + ���������
			String msg = "USB-���������� �����������";
			mApplication.mAlarmMessages.addAlarmMessage(
					getApplicationContext(), msg,
					MessageType.USBConnectionMessage);

			Intent i = new Intent();
			i.setAction(Globals.BROADCAST_INTENT_ALARM_MESSAGE);
			getApplicationContext().sendBroadcast(i);

			mApplication.isUsbConnected = false;

			super.onDestroy();
		} catch (Exception e) {
			// Logging.v("���������� ��� ������� ���������� ReceiveService");
			e.printStackTrace();
			super.onDestroy();
		}
	}
}