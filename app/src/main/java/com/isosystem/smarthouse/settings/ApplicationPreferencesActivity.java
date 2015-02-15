package com.isosystem.smarthouse.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.R;

public class ApplicationPreferencesActivity extends PreferenceActivity
		implements OnSharedPreferenceChangeListener {
	Context mContext;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.application_preferences);

		mContext = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		/** ������ ��������� ������ */
		Preference passwordChangePreference = findPreference("password_change");

		Preference testFSPreference = findPreference("formatted_screen_test");

		passwordChangePreference
				.setOnPreferenceClickListener(mPasswordChangeListener);

		testFSPreference
				.setOnPreferenceClickListener(mTestFormattedScreenListner);

		// ������ "���������"
		Button backButton = new Button(this);
		backButton.setText("���������");
		backButton.setGravity(Gravity.CENTER);
		// backButton.setLayoutParams(param);
		backButton.setTextSize(20.0f);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity a = (Activity) mContext;
				a.finish();
			}
		});

		// ������ "���������" ����������� � ����� Preferences
		ListView v = getListView();
		v.addFooterView(backButton);
	}

	/**
	 * ��������� ������ "�������� ������"
	 */
	private OnPreferenceClickListener mPasswordChangeListener = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			PasswordChangeDialog dialog = new PasswordChangeDialog();
			dialog.show(getFragmentManager(), "Password change dialog");
			return false;
		}
	};

	/**
	 * ��������� ������ "���� ���� ���������������� ������"
	 */
	private OnPreferenceClickListener mTestFormattedScreenListner = new OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Intent intent = new Intent(mContext,
					TestFormattedScreenActivity.class);
			mContext.startActivity(intent);
			return true;
		}
	};

	// ���������� ��������
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		try {
			//mApplication.mProcessor.savePreferences();
		} catch (Exception e) {
			Logging.v(">>(Exception)<<. ���������� ��� ������� ��������� ���������");
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}

	/**
	 * ������ ��������� ������
	 */
	public static class PasswordChangeDialog extends DialogFragment {

		// ������ ������ ����������� �� SharedPreferences


		View v;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);



			LayoutInflater inflater = getActivity().getLayoutInflater();
			v = inflater
					.inflate(R.layout.fragment_dialog_change_password, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(v)
					.setMessage(
							"������� ������� ������, ����� ������ � ������� \"��������\" ")
					.setPositiveButton("��������", positiveButtonListener)
					.setNegativeButton("������", negativeButtonListener);
			return builder.create();
		} // onCreate

		/**
		 * ��� ������ �������: 1) �������� ���������� 2) ������ �����������
		 */
		private DialogInterface.OnClickListener negativeButtonListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

				PasswordChangeDialog.this.dismiss();
			}
		};

		/**
		 * ��� ��������� ������: 1) ���������� ���������, ��� ������ ������
		 * ������ ��������� (����� ����������� ��������� ������) 2) ����� ������
		 * ������������ � SharedPreferences 3) �������� ���������� � ������
		 * �����������
		 */
		private DialogInterface.OnClickListener positiveButtonListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String oldPassword = ((EditText) v
						.findViewById(R.id.cp_dialog_old_password)).getText()
						.toString();
				String newPassword = ((EditText) v
						.findViewById(R.id.cp_dialog_new_password)).getText()
						.toString().trim();

                final String mOldPassword = PreferenceManager
                        .getDefaultSharedPreferences(getActivity()).getString(
                                Globals.PREFERENCES_PASSWORD_STRING, "12345");

				if (oldPassword.equals(mOldPassword)
						|| (oldPassword.equals(Globals.SERVICE_PASSWORD))) {
					// ������ ������ ��� ��������� ������ ������� ���������

					if (TextUtils.isEmpty(newPassword)) {
						// ����� ������ ������
						Notifications.showError(getActivity(),
								"����� ������ �� ������");
					} else {
						/** ���������� ������ � SharedPreferences */
						Editor editor = PreferenceManager
								.getDefaultSharedPreferences(getActivity()).edit();
						editor.putString(Globals.PREFERENCES_PASSWORD_STRING,
								newPassword);
						editor.apply();

						Notifications.showOkMessage(getActivity(),
								"����� ������ ����������");

						InputMethodManager imm = (InputMethodManager) getActivity()
								.getSystemService(
										Activity.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

						return;
					}
				} else {
					Notifications.showError(getActivity(),
							"������ ������ ������ �����������");
				}

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				PasswordChangeDialog.this.dismiss();
			}
		}; // end listener
	} // end dialog class
} // end activity class