/*
 * ��������� ���������� ��� ������� "����� ���"
 * 
 * author: ����������� �������
 * email: nick.godov@gmail.com
 * last edit: 11.09.2014
 */

package com.isosystem.smarthouse;

/**
 * ���������� ��������� ��� ����������
 */
public final class Globals {
	
	// ������� �����
	
	// ����� smarthouse
	public static final String EXTERNAL_ROOT_DIRECTORY = "smarthouse";
	// ����� images */
	public static final String EXTERNAL_IMAGES_DIRECTORY = "images";
	// ����� wallpapers */
	public static final String EXTERNAL_WALLPAPERS_DIRECTORY = "wallpapers";
	// ����� images � assets
	public static final String ASSETS_IMAGES_DIRECTORY = "imgs";
	// ����� logs 
	public static final String EXTERNAL_LOGS_DIRECTORY = "logs";
	// ����� menutree 
	public static final String EXTERNAL_MENUTREE_DIRECTORY = "menutree";
	// ����� messages 
	public static final String EXTERNAL_ALARM_MESSAGES_DIRECTORY = "messages";
	// ����� fscreens 
	public static final String EXTERNAL_FORMATTED_SCREENS_DIRECTORY = "fscreens";
	// ����� screensaver 
	public static final String EXTERNAL_SCREENSAVER_IMAGES_DIRECTORY = "screensaver";
	// ����� screensaver 
	public static final String EXTERNAL_PREFERENCES_DIRECTORY = "preferences";
	
	// ���������� �����
	public static final String INTERNAL_MENU_FILE = "menu.obj";
	public static final String INTERNAL_FORMATTED_SCREENS_FILE = "fs.obj";
	public static final String INTERNAL_MESSAGES_FILE = "messages.obj";
	// ������� �����
	
	//���� � ������� ����
	public static final String MENU_TREE_FILENAME = "menu.obj";
	//���� � ���������� �����������
	public static final String ALARM_MESSAGES_FILENAME = "messages.obj";		
	//���� � ������ ���������������� ������
	public static final String FORMATTED_SCREENS_FILENAME = "fscreens.obj";	
	//���� � ������ ���������������� ������
	public static final String PREFERENCES_FILENAME = "preferences.xml";	


	// ����
	
	//������� ��� �����
	public static final String LOG_TAG = "SMARTHOUSE";
	
	// ��������
	
	//Action ��� ���������� ���������
	public static final String BROADCAST_INTENT_ALARM_MESSAGE = "SMARTHOUSE.ALARM_MESSAGE_RECEIVED";
	//Action ��� ��������� �� ���������
	public static final String BROADCAST_INTENT_VALUE_MESSAGE = "SMARTHOUSE.VALUE_MESSAGE_RECEIVED";
	//Action ��� ��������� ��� ���� ���������������� ������
	public static final String BROADCAST_INTENT_FORMSCREEN_MESSAGE = "SMARTHOUSE.FORMSCREEN_MESSAGE_RECEIVED";
	
	// ������
	
	//��������� ������
	public static final String SERVICE_PASSWORD = "924";

	// ����� �������
	
	//����� ������� 
	public static final Boolean DEBUG_MODE = true;
	
	// ���������, ����������� ���� 

	// ������ �����������
	public static final String PREFERENCES_PASSWORD_STRING = "PREFERENCES_PASSWORD";
	// ������� �������� - ������������ �� �����
	public static final String PREFERENCES_USE_BUFFER_STRING = "PREFERENCES_USE_BUFFER";
	// ����� ���������� ������
	public static final String PREFERENCES_BUFFER_UPDATE_TIME_STRING = "PREFERENCES_BUFFER_UPDATE_TIME";
	
}