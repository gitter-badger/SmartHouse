package com.isosystem.smarthouse.processor;

import android.content.Context;
import android.os.Environment;

import com.isosystem.smarthouse.data.AlarmMessages;
import com.isosystem.smarthouse.data.FormattedScreens;
import com.isosystem.smarthouse.data.MenuTree;
import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class XMLProcessor {

	MyApplication mApplication;

	String mExternalFilesDirectory;
	String menuFilePath;
	String fsFilePath;
	String messagesFilePath;
	String prefsFilePath;
	
	String package_name;
	
	String menu_internal_path;
	String fs_internal_path;
	String messages_internal_path;
	String preferences_internal_path;

	public XMLProcessor(Context c) {
		mApplication = (MyApplication) c.getApplicationContext();
		package_name = mApplication.getPackageName();

		mExternalFilesDirectory = Environment.getExternalStorageDirectory()
				+ File.separator + Globals.EXTERNAL_ROOT_DIRECTORY;
		menuFilePath = mExternalFilesDirectory + File.separator
				+ Globals.INTERNAL_MENU_FILE;
		fsFilePath = mExternalFilesDirectory + File.separator
				+ Globals.INTERNAL_FORMATTED_SCREENS_FILE;
		messagesFilePath = mExternalFilesDirectory + File.separator
				+ Globals.INTERNAL_MESSAGES_FILE;
		prefsFilePath = mExternalFilesDirectory + File.separator
				+ "preferences.xml";

		menu_internal_path = File.separator + "data" + File.separator + "data"
				+ File.separator + package_name + File.separator + "files"
				+ File.separator + Globals.INTERNAL_MENU_FILE;

		// ���� � ����� ���� ������ �� ���������� ���������
		fs_internal_path = File.separator + "data" + File.separator + "data"
				+ File.separator + package_name + File.separator + "files"
				+ File.separator + Globals.INTERNAL_FORMATTED_SCREENS_FILE;

		messages_internal_path = File.separator + "data" + File.separator
				+ "data" + File.separator + package_name + File.separator
				+ "files" + File.separator + Globals.INTERNAL_MESSAGES_FILE;

		preferences_internal_path = File.separator + "data" + File.separator
				+ "data" + File.separator + package_name + File.separator
				+ "shared_prefs" + File.separator + package_name
				+ "_preferences.xml";

		File files_dir = new File(File.separator + "data" + File.separator
				+ "data" + File.separator + package_name + File.separator
				+ "files" + File.separator);
		files_dir.mkdirs();
	}

	public boolean loadMenuTreeFromInternalStorage() {

		File menu_file = new File(menu_internal_path);

		// ���� ����� ��� ��� ��� ������ ���������
		// ��������� ����� ���� � ������������ �� ���������� ���������
		if (!menu_file.exists() || !menu_file.canRead()) {
			Logging.v("������ ��� ������� ������� ���� �� ����������� ���������, ���� �� ���������� ��� �� ����� ���� ��������, ������� ����� ����");
			mApplication.mTree = new MenuTree();
			mApplication.mProcessor.saveMenuTreeToInternalStorage();
			return false;
		} else {
			try {
				FileInputStream fis = mApplication
						.openFileInput(Globals.INTERNAL_MENU_FILE);
				try {
					ObjectInputStream objectStream = new ObjectInputStream(fis);
					mApplication.mTree = (MenuTree) objectStream.readObject();
					objectStream.close();
					fis.close();

				} catch (Exception e) {
					Logging.v("���������� ��� ������� ������� ��������� �����, ������� ����� ����");
					mApplication.mTree = new MenuTree();
					mApplication.mProcessor.saveMenuTreeToInternalStorage();
					e.printStackTrace();
					return false;
				}
			} catch (FileNotFoundException e) {
				Logging.v("���������� ��� ������� ��������� ���� ����, ������� ����� ����");
				mApplication.mTree = new MenuTree();
				mApplication.mProcessor.saveMenuTreeToInternalStorage();
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	public Boolean saveMenuTreeToInternalStorage() {
		// 1 ������� ����
		File menu_file = new File(menu_internal_path);
		menu_file.delete();

		try {
			// menu_file.mkdirs();
			menu_file.createNewFile();

			// 2 ���������� ������ � ����
			FileOutputStream fileOutputStream = new FileOutputStream(menu_file);
			ObjectOutputStream objectStream = new ObjectOutputStream(
					fileOutputStream);
			objectStream.writeObject(mApplication.mTree);

			fileOutputStream.flush();
			fileOutputStream.close();

			objectStream.flush();
			objectStream.close();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ���� �� ���������� ���������");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Boolean importMenuTreeFromExternalStorage(String path) {
		try {
			FileInputStream inputStream = new FileInputStream(path);
			ObjectInputStream objectStream = new ObjectInputStream(inputStream);
			mApplication.mTree = (MenuTree) objectStream.readObject();

			inputStream.close();
			objectStream.close();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������������� ����");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Boolean exportMenuTreeToExternalStorage() {
		try {
			// �������� ����� ��� ��������������� �����
			File external_directory = new File (mExternalFilesDirectory);
			external_directory.mkdirs();
			
			FileOutputStream fileOutputStream = new FileOutputStream(
					menuFilePath);
			ObjectOutputStream objectStream = new ObjectOutputStream(
					fileOutputStream);
			objectStream.writeObject(mApplication.mTree);

			fileOutputStream.flush();
			fileOutputStream.close();
			objectStream.flush();
			objectStream.close();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������������� ���� �� ������� ���������");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// ���� ������

	public boolean loadFscreensFromInternalStorage() {

		File fs_file = new File(fs_internal_path);

		// ���� ����� ��� ��� ��� ������ ���������
		// ��������� ����� ���� � ������������ �� ���������� ���������
		if (!fs_file.exists() || !fs_file.canRead()) {
			Logging.v("������ ��� ������� ������� ���� �� ����������� ���������, ���� �� ���������� ��� �� ����� ���� ��������, ������� ����� ���� ������");
			mApplication.mFormattedScreens = new FormattedScreens();
			mApplication.mProcessor.saveFormattedScreensToInternalStorage();
			return false;
		} else {
			try {
				FileInputStream fis = mApplication
						.openFileInput(Globals.INTERNAL_FORMATTED_SCREENS_FILE);
				try {
					ObjectInputStream objectStream = new ObjectInputStream(fis);
					mApplication.mFormattedScreens = (FormattedScreens) objectStream
							.readObject();
					objectStream.close();
					fis.close();

				} catch (Exception e) {
					Logging.v("���������� ��� ������� ������� ��������� �����, ������� ����� ���� ������");
					mApplication.mFormattedScreens = new FormattedScreens();
					mApplication.mProcessor
							.saveFormattedScreensToInternalStorage();
					e.printStackTrace();
					return false;
				}
			} catch (FileNotFoundException e) {
				Logging.v("���������� ��� ������� ��������� ���� ���� ������, ������� ����� ���� ������");
				mApplication.mFormattedScreens = new FormattedScreens();
				mApplication.mProcessor.saveFormattedScreensToInternalStorage();
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	public Boolean saveFormattedScreensToInternalStorage() {
		// 1 ������� ����
		File fs_file = new File(fs_internal_path);
		fs_file.delete();

		try {
			fs_file.createNewFile();

			// 2 ���������� ������ � ����
			FileOutputStream fileOutputStream = new FileOutputStream(fs_file);
			ObjectOutputStream objectStream = new ObjectOutputStream(
					fileOutputStream);
			objectStream.writeObject(mApplication.mFormattedScreens);

			fileOutputStream.flush();
			fileOutputStream.close();

			objectStream.flush();
			objectStream.close();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ���� ������ �� ���������� ���������");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Boolean importFormattedScreensFromExternalStorage(String path) {
		try {
			FileInputStream inputStream = new FileInputStream(path);
			ObjectInputStream objectStream = new ObjectInputStream(inputStream);
			mApplication.mFormattedScreens = (FormattedScreens) objectStream
					.readObject();

			inputStream.close();
			objectStream.close();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������������� ���� ������");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Boolean exportFormattedScreensToExternalStorage() {
		try {
			// �������� ����� ��� ��������������� �����
			File external_directory = new File (mExternalFilesDirectory);
			external_directory.mkdirs();
			
			FileOutputStream fileOutputStream = new FileOutputStream(fsFilePath);
			ObjectOutputStream objectStream = new ObjectOutputStream(
					fileOutputStream);
			objectStream.writeObject(mApplication.mFormattedScreens);

			fileOutputStream.flush();
			fileOutputStream.close();
			objectStream.flush();
			objectStream.close();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������������� ���� ������ �� ������� ���������");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// ��������� ���������
	
	public boolean loadMessagesFromInternalStorage() {

		File messages_file = new File(messages_internal_path);

		// ���� ����� ��� ��� ��� ������ ���������
		// ��������� ����� ���� � ������������ �� ���������� ���������
		if (!messages_file.exists() || !messages_file.canRead()) {
			Logging.v("������ ��� ������� ������� ���� �� ����������� ���������, ���� �� ���������� ��� �� ����� ���� ��������, ������� ����� ���������");
			mApplication.mAlarmMessages = new AlarmMessages();
			mApplication.mProcessor.saveMessagesToInternalStorage();
			return false;
		} else {
			try {
				FileInputStream fis = mApplication
						.openFileInput(Globals.INTERNAL_MESSAGES_FILE);
				try {
					ObjectInputStream objectStream = new ObjectInputStream(fis);
					mApplication.mAlarmMessages = (AlarmMessages) objectStream
							.readObject();
					objectStream.close();
					fis.close();

				} catch (Exception e) {
					Logging.v("���������� ��� ������� ������� ��������� �����, ������� ����� ���������");
					mApplication.mAlarmMessages = new AlarmMessages();
					mApplication.mProcessor.saveMessagesToInternalStorage();
					e.printStackTrace();
					return false;
				}
			} catch (FileNotFoundException e) {
				Logging.v("���������� ��� ������� ��������� ���� ���� ������, ������� ����� ���������");
				mApplication.mAlarmMessages = new AlarmMessages();
				mApplication.mProcessor.saveMessagesToInternalStorage();
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	public Boolean saveMessagesToInternalStorage() {
		// 1 ������� ����
		File messages_file = new File(messages_internal_path);
		messages_file.delete();

		try {
			messages_file.createNewFile();

			// 2 ���������� ������ � ����
			FileOutputStream fileOutputStream = new FileOutputStream(
					messages_file);
			ObjectOutputStream objectStream = new ObjectOutputStream(
					fileOutputStream);
			objectStream.writeObject(mApplication.mAlarmMessages);

			fileOutputStream.flush();
			fileOutputStream.close();

			objectStream.flush();
			objectStream.close();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ��������� �� ���������� ���������");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Boolean importPrefsFromExternalStorage(String path) {
		try {
			FileInputStream fin = new FileInputStream(path);
			FileOutputStream fout = new FileOutputStream(
					preferences_internal_path, false);

			int i;
			while ((i = fin.read()) != -1) {
				fout.write((byte) i);
			}

			fin.close();
			fout.close();

			return true;
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������������� ��������� �� �������� �����");
			e.printStackTrace();
			return false;
		}
	}

	public Boolean exportPrefsToExternalStorage() {
		try {
			// �������� ����� ��� ��������������� �����
			File external_directory = new File (mExternalFilesDirectory);
			external_directory.mkdirs();

			FileInputStream fin = new FileInputStream(preferences_internal_path);
			FileOutputStream fout = new FileOutputStream(
					prefsFilePath, false);

			int i;
			while ((i = fin.read()) != -1) {
				fout.write((byte) i);
			}

			fin.close();
			fout.close();

			return true;
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������������� ��������� �� ������� ����");
			e.printStackTrace();
			return false;
		}
	}

}