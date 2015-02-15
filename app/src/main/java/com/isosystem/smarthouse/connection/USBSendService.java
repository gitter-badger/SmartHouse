package com.isosystem.smarthouse.connection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.notifications.Notifications;

import java.util.HashMap;

public class USBSendService extends IntentService {

	UsbManager usbManager;
	HashMap<String, UsbDevice> deviceList;
	UsbDevice usbDevice;
	UsbInterface usbInterface;
	UsbEndpoint usbEndpointOut;
	UsbDeviceConnection usbConnection;

	Boolean wasSend;

	public USBSendService() {
		super("USBSEND");
		Logging.v("������������� �������");
	}

	public void onCreate() {
		super.onCreate();

		wasSend = false;
		
		Logging.v("������� ������");

		/**1. �������� ������ ��������� */
		
		try {
			usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			deviceList = usbManager.getDeviceList();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ������ USB ���������");
			e.printStackTrace();
			this.stopSelf();
			return;
		}

		// ��������� ����
		if (deviceList.isEmpty()) {
			Logging.v("������ ����������, �.�. �� ���������� USB-����������");
			this.stopSelf();
			return;
		}
		
		Logging.v("������� " + String.valueOf(deviceList.size()) + " ���������");
		
		/**2. ���� ����� ��������� ��������� ������ ���������� */
		
		// ���� � ������ USB-��������� ���� ����������
        for (UsbDevice device : deviceList.values()) {
        	
        	Logging.v("Product ID " + String.valueOf(device.getProductId()) +
        			 		 " Vendor ID " + String.valueOf(device.getVendorId()) +
        					 " Device ID " + String.valueOf(device.getDeviceId()) + 
        					 " Class ID " + String.valueOf(device.getDeviceClass()));
        	
        	if ((device.getProductId() == 257) && (device.getVendorId() == 65535)) {
        		
        		Logging.v("���������� ����������");
        		
        		try {
        			usbDevice = device;
        		} catch (Exception e) {
        			Logging.v("���������� ��� ������� ����� ��������� USB-����������");
        			e.printStackTrace();
        			this.stopSelf();
        			return;
        		}
        	}//if
        }
        
		// ������ ��������� ���
        if (usbDevice == null) {
			Logging.v("���������� ��������� �� ����������");
			this.stopSelf();
			return;
        }
		
		Logging.v("������� " + String.valueOf(usbDevice.getInterfaceCount()) + " �����������");
        
		/**3. ����� ��������� ���������� */
		
        for (int i = 0; i < usbDevice.getInterfaceCount();i++) {
        	UsbInterface tempInterfce = usbDevice.getInterface(i);
        	
        	Logging.v("Endpoint count " + String.valueOf(tempInterfce.getEndpointCount()) +
			 		 " Interface ID " + String.valueOf(tempInterfce.getId()) +
					 " Protocol ID " + String.valueOf(tempInterfce.getInterfaceProtocol()) + 
					 " Class ID " + String.valueOf(tempInterfce.getInterfaceClass()));
  
        	if (tempInterfce.getEndpointCount() > 1) {
        		usbInterface = tempInterfce;
        	}
        }
        
		// ������ ����������� ���
        if (usbInterface == null) {
			Logging.v("���������� ����������� �� ����������");
			this.stopSelf();
			return;
        }

		Logging.v("������� " + String.valueOf(usbInterface.getEndpointCount()) + " �������� �����");
        
		/**4. ����� EndPoint */

		for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
			
			UsbEndpoint tempPoint = usbInterface.getEndpoint(i);
			
        	Logging.v("Endpoint direction " + String.valueOf(tempPoint.getDirection()) +
			 		 " Endpoint type" + String.valueOf(tempPoint.getType()) +
					 " Protocol address " + String.valueOf(tempPoint.getAddress()));
			
			if (tempPoint.getDirection() == UsbConstants.USB_DIR_OUT) {
				usbEndpointOut = tempPoint;
			}
		}//for

		
		// ������ �������� ����� ���
        if (usbEndpointOut == null) {
			Logging.v("���������� �������� ����� �� ����������");
			this.stopSelf();
			return;
        }
		
		// ��������� ���������� ��� USB-����������
		try {
			usbConnection = usbManager.openDevice(usbDevice);
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������� USB-����������");
			e.printStackTrace();
			this.stopSelf();
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		/** 1. ��������� ������������ ��������� �� extras */
		String msg;
		try {
			msg = intent.getStringExtra("message");
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� �� extras ���������");
			e.printStackTrace();
			this.stopSelf();
			return;
		}

		/** 2. ��������� ������� ���������� � ����������� � ���� */
		if ((usbConnection !=null) && (usbDevice!=null)) {

			// ����� ���������
			try {
				usbConnection.claimInterface(usbInterface, true);
			} catch (Exception e) {
				Logging.v("���������� ��� ������� ����� ���������");
				e.printStackTrace();
				this.stopSelf();
				return;
			}

			/** 
			 * �������� ��������� 
			 */
			
			// �������� ��������� � �������� ���������� ���� ��� ���������
			int result = -1;
			try {
				result = usbConnection.bulkTransfer(usbEndpointOut,
						msg.getBytes(), msg.getBytes().length, 0);
			} catch (Exception e) {
				Logging.v("���������� ��� ������� ������� ���������");
				e.printStackTrace();
				this.stopSelf();
				return;
			}

			// ����������� ���������
			try {
				usbConnection.releaseInterface(usbInterface);
			} catch (Exception e) {
				Logging.v("���������� ��� ������� ���������� ���������");
				e.printStackTrace();
				this.stopSelf();
				return;
			}
			
			if (result >= 0) {
				wasSend = true;
				Logging.v("������� ���������: " + msg);
			} else {
				wasSend = false;
				Logging.v("�� ������� ������� ���������: " + msg);
			}
		} else {
			wasSend = false;
			Logging.v("�� ������� ������� ���������: " + msg + ". ������ �����, USB-���������� �� ����������");
		}
	}

	public void onDestroy() {
		try {
			Logging.v("���������� ������");
			if (wasSend) {
				// ��������� ������� ��������		
				Logging.v("��������� ������� ��������");
				
				if (Globals.DEBUG_MODE) {
					Notifications.showOkMessage(getApplicationContext(),
							"��������� ������� ��������");
				}
			} else {
				Notifications
						.showError(getApplicationContext(),
								"������ ��� �������� ���������. ��������, USB-���������� �� �����������");
			}
			super.onDestroy();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ���������� ������");
			e.printStackTrace();
			super.onDestroy();
		}
	}
}