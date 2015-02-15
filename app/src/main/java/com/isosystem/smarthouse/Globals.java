/*
 * Мобильное приложение для проекта "Умный дом"
 * 
 * author: Годовиченко Николай
 * email: nick.godov@gmail.com
 * last edit: 11.09.2014
 */

package com.isosystem.smarthouse;

/**
 * Глобальные константы для приложения
 */
public final class Globals {
	
	// ВНЕШНИЕ ПАПКИ
	
	// Папка smarthouse
	public static final String EXTERNAL_ROOT_DIRECTORY = "smarthouse";
	// Папка images */
	public static final String EXTERNAL_IMAGES_DIRECTORY = "images";
	// Папка wallpapers */
	public static final String EXTERNAL_WALLPAPERS_DIRECTORY = "wallpapers";
	// Папка images в assets
	public static final String ASSETS_IMAGES_DIRECTORY = "imgs";
	// Папка logs 
	public static final String EXTERNAL_LOGS_DIRECTORY = "logs";
	// Папка menutree 
	public static final String EXTERNAL_MENUTREE_DIRECTORY = "menutree";
	// Папка messages 
	public static final String EXTERNAL_ALARM_MESSAGES_DIRECTORY = "messages";
	// Папка fscreens 
	public static final String EXTERNAL_FORMATTED_SCREENS_DIRECTORY = "fscreens";
	// Папка screensaver 
	public static final String EXTERNAL_SCREENSAVER_IMAGES_DIRECTORY = "screensaver";
	// Папка screensaver 
	public static final String EXTERNAL_PREFERENCES_DIRECTORY = "preferences";
	
	// ВНУТРЕННИЕ ФАЙЛЫ
	public static final String INTERNAL_MENU_FILE = "menu.obj";
	public static final String INTERNAL_FORMATTED_SCREENS_FILE = "fs.obj";
	public static final String INTERNAL_MESSAGES_FILE = "messages.obj";
	// ВНЕШНИЕ ФАЙЛЫ
	
	//Файл с деревом меню
	public static final String MENU_TREE_FILENAME = "menu.obj";
	//Файл с алармовыми сообщениями
	public static final String ALARM_MESSAGES_FILENAME = "messages.obj";		
	//Файл с окнами форматированного вывода
	public static final String FORMATTED_SCREENS_FILENAME = "fscreens.obj";	
	//Файл с окнами форматированного вывода
	public static final String PREFERENCES_FILENAME = "preferences.xml";	


	// ЛОГИ
	
	//Префикс для логов
	public static final String LOG_TAG = "SMARTHOUSE";
	
	// РЕСИВЕРЫ
	
	//Action для алармового сообщения
	public static final String BROADCAST_INTENT_ALARM_MESSAGE = "SMARTHOUSE.ALARM_MESSAGE_RECEIVED";
	//Action для сообщения со значением
	public static final String BROADCAST_INTENT_VALUE_MESSAGE = "SMARTHOUSE.VALUE_MESSAGE_RECEIVED";
	//Action для сообщения для окна форматированного вывода
	public static final String BROADCAST_INTENT_FORMSCREEN_MESSAGE = "SMARTHOUSE.FORMSCREEN_MESSAGE_RECEIVED";
	
	// ПАРОЛЬ
	
	//Сервисный пароль
	public static final String SERVICE_PASSWORD = "924";

	// РЕЖИМ ОТЛАДКИ
	
	//Режим отладки 
	public static final Boolean DEBUG_MODE = true;
	
	// НАСТРОЙКИ, НАСТРОЕЧНЫЙ ФАЙЛ 

	// Пароль настройщика
	public static final String PREFERENCES_PASSWORD_STRING = "PREFERENCES_PASSWORD";
	// Булевое значение - использовать ли буфер
	public static final String PREFERENCES_USE_BUFFER_STRING = "PREFERENCES_USE_BUFFER";
	// Время обновления буфера
	public static final String PREFERENCES_BUFFER_UPDATE_TIME_STRING = "PREFERENCES_BUFFER_UPDATE_TIME";
	
}