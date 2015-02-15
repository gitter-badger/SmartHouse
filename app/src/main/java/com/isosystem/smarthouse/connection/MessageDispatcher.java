package com.isosystem.smarthouse.connection;

import android.content.Context;
import android.content.Intent;

import com.isosystem.smarthouse.logging.Logging;

public class MessageDispatcher {

	private Context mContext;

	public MessageDispatcher(Context c) {
		this.mContext = c;
	}

	/**
	 * �������� �������� �����������
	 * � �������� ������� ���������� �������� ������� ��������� (�������� ������������ ��� �������� ����
	 * � ���������� �������� � ������� int
	 * ��������� ����������� ��� [�������][���������� ��������][��������]
	 * ��������, ���� �������: Z, � �� ������ 150, �� ��������� ����� Z3150
	 */
	public String SendValueMessage(String prefix, String value, Boolean isSending) {
		StringBuilder sendingMessage = new StringBuilder();
		
		sendingMessage.append(prefix);
		sendingMessage.append(value.length());
		sendingMessage.append(value);
		
		Logging.v("���������� ��������:" + sendingMessage.toString());
		
		if (isSending) {
			Send(sendingMessage.toString());
		}
		
		return sendingMessage.toString();
	}
	
	/**
	 * �������� �������� �������� �����������
	 * ������ ���������: [�������][0|1]
	 * 
	 * @param value ������� �������� (0 ��� 1)
	 * @return ���������� ���������
	 */
	public String sendBooleanMessage (String prefix, int value, Boolean isSending) {
		StringBuilder sendingMessage = new StringBuilder();
		
		sendingMessage.append(prefix);
		sendingMessage.append(value);
		
		/** ���� ����� ��������� � ascii-���������
		sendingMessage.append(EncodingUtils.getAsciiBytes(Globals.SEND_MESSAGE_BOOLEAN_PREFIX));
		sendingMessage.append(EncodingUtils.getAsciiBytes(String.valueOf(value)));
		*/
		
		if (isSending) {
			Send(sendingMessage.toString());
		}
		
		return sendingMessage.toString();
	}
	
	/**
	 * �������� �������� ��� ���������
	 */
	public void SendRawMessage (String message) {
		Send(message);
	}

	private void Send(String message) {
		Intent i = new Intent(mContext.getApplicationContext(),
				USBSendService.class);
		i.putExtra("message", message);
		mContext.startService(i);
	}

}
