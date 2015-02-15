package com.isosystem.smarthouse.settings;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImportExportFilesActivity extends Activity {

	Activity mActivity;
	MyApplication mApplication;

	Spinner mMainMenuImportSpinner;
	Spinner mFormattedScreensImportSpinner;
	Spinner mPrefsImportSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_import_export_files);

		mActivity = this;
		mApplication = (MyApplication) getApplicationContext();

		Button btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActivity.finish();
			}
		});

		// ������� ����
		mMainMenuImportSpinner = (Spinner) findViewById(R.id.main_menu_spinner);
		try {
			mMainMenuImportSpinner
					.setAdapter(new ArrayAdapter<String>(mActivity,
							android.R.layout.simple_spinner_item, getFiles()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMainMenuImportSpinner.invalidate();

		// ������� ���� ������
		mFormattedScreensImportSpinner = (Spinner) findViewById(R.id.fs_spinner);
		try {
			mFormattedScreensImportSpinner
					.setAdapter(new ArrayAdapter<String>(mActivity,
							android.R.layout.simple_spinner_item, getFiles()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mFormattedScreensImportSpinner.invalidate();

		// ������� ��������
		mPrefsImportSpinner = (Spinner) findViewById(R.id.pref_spinner);
		try {
			mPrefsImportSpinner.setAdapter(new ArrayAdapter<String>(mActivity,
					android.R.layout.simple_spinner_item, getFiles()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mPrefsImportSpinner.invalidate();

		// ������ �������� ����
		ImageButton mImportMainMenuButton = (ImageButton) findViewById(R.id.button_import_mainmenu);
		mImportMainMenuButton.setOnClickListener(mMainMenuImportListener());

		// ������� �������� ����
		ImageButton mExportMainMenuButton = (ImageButton) findViewById(R.id.button_export_mainmenu);
		mExportMainMenuButton.setOnClickListener(mMainMenuExportListener());

		// ������ ���� ������
		ImageButton mImportFormattedScreensButton = (ImageButton) findViewById(R.id.button_import_fs);
		mImportFormattedScreensButton
				.setOnClickListener(mFormattedScreensImportListener());

		// ������� ���� ������
		ImageButton mExportFormattedScreensButton = (ImageButton) findViewById(R.id.button_export_fs);
		mExportFormattedScreensButton
				.setOnClickListener(mFormattedScreensExportListener());

		// ������ ��������
		ImageButton mImportPrefsButton = (ImageButton) findViewById(R.id.button_import_pref);
		mImportPrefsButton.setOnClickListener(mPrefsImportListener());

		// ������� ��������
		ImageButton mExportPrefsButton = (ImageButton) findViewById(R.id.button_export_pref);
		mExportPrefsButton.setOnClickListener(mPrefsExportListener());

	}

	// ��������� ������ ������ � ����� sdcard/smarthouse
	private ArrayList<String> getFiles() throws IOException {
		ArrayList<String> files = new ArrayList<String>();
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + Globals.EXTERNAL_ROOT_DIRECTORY);

		if (file.isDirectory()) {
			File[] listFile = file.listFiles();
            for (File f : listFile) {
                if (f.isDirectory()) {
                    File[] dirFiles = f.listFiles();
                    for (File dir : dirFiles) {
                        files.add(dir.getPath());
                    }
                } else {
                    files.add(f.getAbsolutePath());
                }
            }
		} // if
		return files;
	} // method

	View.OnClickListener mMainMenuExportListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String path = Globals.EXTERNAL_ROOT_DIRECTORY
						+ File.separator + Globals.INTERNAL_MENU_FILE;

				if (!mApplication.mProcessor.exportMenuTreeToExternalStorage()) {
					Notifications
							.showError(
									mActivity,
									"������ ��������. ���������, ��� ������� ��������� ���������� � � ��� ���� ����� �� ������ �����");
				} else {
					Notifications.showPositiveMessage(mActivity,
							"���� ������� �������������� � ���� " + path);
				}
			}
		};
	}

	// ��������� ��� ������� �������� ����. ������� �������� - ���� � ����������
	// �����
	View.OnClickListener mMainMenuImportListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String path = (String) mMainMenuImportSpinner.getSelectedItem();

				// ���� path ������
				if (path == null || path.trim().isEmpty()) {
					Notifications
							.showError(mActivity, "���� � ����� �� ������");
					return;
				}

				File import_file = new File(path);

				// ���� path ������������
				if (!import_file.isFile()) {
					Notifications.showError(mActivity,
							"��������� ���� �� �������� ������");
					return;
				}

				if (!mApplication.mProcessor
						.importMenuTreeFromExternalStorage(path)
						|| !mApplication.mProcessor
								.saveMenuTreeToInternalStorage()) {
					Notifications
							.showError(
									mActivity,
									"������ ��� ������� ����! ���������, ��� ������ ������ ���� ��� "
											+ "��� ��������� ���� ��������� �� ���������� ���� ����������.");
				} else {
					Notifications.showPositiveMessage(mActivity,
							"������� ���� ������� �������������!");
				}
			}
		};
	}

	// ��������� ��� ������� ���� ������. ������� �������� - ���� � ����������
	// �����
	View.OnClickListener mFormattedScreensImportListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String path = (String) mFormattedScreensImportSpinner
						.getSelectedItem();

				// ���� path ������
				if (path == null || path.trim().isEmpty()) {
					Notifications
							.showError(mActivity, "���� � ����� �� ������");
					return;
				}

				File import_file = new File(path);

				// ���� path ������������
				if (!import_file.isFile()) {
					Notifications.showError(mActivity,
							"��������� ���� �� �������� ������");
					return;
				}

				if (!mApplication.mProcessor
						.importFormattedScreensFromExternalStorage(path)
						|| !mApplication.mProcessor
								.saveFormattedScreensToInternalStorage()) {
					Notifications
							.showError(
									mActivity,
									"������ ��� ������� ���� ������! ���������, ��� ������ ������ ���� ��� "
											+ "��� ��������� ���� ��������� �� ���������� ���� ������.");
				} else {
					Notifications.showPositiveMessage(mActivity,
							"���� ������ ������� �������������!");
				}
			}
		};
	}

	View.OnClickListener mFormattedScreensExportListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String path = Globals.EXTERNAL_ROOT_DIRECTORY
						+ File.separator + Globals.INTERNAL_FORMATTED_SCREENS_FILE;
				
				if (!mApplication.mProcessor
						.exportFormattedScreensToExternalStorage()) {
					Notifications
							.showError(
									mActivity,
									"������ ��������. ���������, ��� ������� ��������� ���������� � � ��� ���� ����� �� ������ �����");
				} else {
					Notifications.showPositiveMessage(mActivity,
							"���� ������ ������� �������������� � ���� " + path);
				}
			}
		};
	}

	// ��������� ��� ������� ���� ������. ������� �������� - ���� � ����������
	// �����
	View.OnClickListener mPrefsImportListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String path = (String) mPrefsImportSpinner.getSelectedItem();

				// ���� path ������
				if (path == null || path.trim().isEmpty()) {
					Notifications
							.showError(mActivity, "���� � ����� �� ������");
					return;
				}

				File import_file = new File(path);

				// ���� path ������������
				if (!import_file.isFile()) {
					Notifications.showError(mActivity,
							"��������� ���� �� �������� ������");
					return;
				}

				if (!mApplication.mProcessor
						.importPrefsFromExternalStorage(path)) {
					Notifications
							.showError(
									mActivity,
									"������ ��� ������� ��������! ���������, ��� ������ ������ ���� ��� "
											+ "��� ��������� ���� ��������� �� ���������� XML-���������.");
				} else {
					Notifications.showPositiveMessage(mActivity,
							"��������� ������� �������������!");
				}
			}
		};
	}

	View.OnClickListener mPrefsExportListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String path = Globals.EXTERNAL_ROOT_DIRECTORY
						+ File.separator + "preferences.xml";
				
				if (!mApplication.mProcessor.exportPrefsToExternalStorage()) {
					Notifications
							.showError(
									mActivity,
									"������ ��������. ���������, ��� ������� ��������� ���������� � � ��� ���� ����� �� ������ �����");
				} else {
					Notifications.showPositiveMessage(mActivity,
							"��������� ������� �������������� � ���� " + path);
				}
			}
		};
	}

	// View.OnClickListener mMainMenuExportListener() {
	// return new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// }
	// };
	// }

}