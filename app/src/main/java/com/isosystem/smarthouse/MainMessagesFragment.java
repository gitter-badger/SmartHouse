/*
 * Мобильное приложение для проекта "Умный дом"
 * 
 * author: Годовиченко Николай
 * email: nick.godov@gmail.com
 * last edit: 07.12.2014
 */

package com.isosystem.smarthouse;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.notifications.Notifications;

public class MainMessagesFragment extends Fragment {

	ListView mMessagesList;
	Button mBackButton;
	MainMessagesAdapter mAdapter;
	AlarmMessageReceive mReceiver;

	DigitalClock mClock;
	


	int counter = 1;

	ImageView mUsbConnectedIcon;
	ImageView mPowerSupplyIcon;

	TextView mMessagesNumber;
	ImageView mMessagesIcon;

	Drawable mDefaultBackground;

	View rootView;
	MyApplication mApplication;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_main_messages, container,
				false);

		mDefaultBackground = rootView.getBackground();

		mApplication = (MyApplication) rootView.getContext()
				.getApplicationContext();

		mUsbConnectedIcon = (ImageView) rootView
				.findViewById(R.id.image_usb_connection);
		checkUsbConnectionIcon();

		mPowerSupplyIcon = (ImageView) rootView
				.findViewById(R.id.image_power_connection);
		checkPowerSupplyIcon();

		// Иконка для количества сообщений
		mMessagesIcon = (ImageView) rootView.findViewById(R.id.imageView3);

		// Текстовая надпись для количества сообщений
		mMessagesNumber = (TextView) rootView.findViewById(R.id.textView1);
		mMessagesNumber.setTypeface(Typeface.createFromAsset(rootView
				.getContext().getAssets(), "fonto.ttf"));
		mMessagesNumber.setTextColor(Color.BLACK);

		// Настройка иконки и надписи
		setMessageNumberIcon();

		mMessagesList = (ListView) rootView
				.findViewById(R.id.message_activity_msg_list);
		refreshListView();

		// Удаление алармового сообщения при нажатии на него
		mMessagesList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				mApplication.mAlarmMessages
						.clearMessage(position, mApplication);
				refreshListView();
				setMessageNumberIcon();
			}
		});
		
		ImageButton mDeleteAllMessages = (ImageButton) rootView
				.findViewById(R.id.delete_messages);
		mDeleteAllMessages
				.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						mApplication.mAlarmMessages.clearAllMessages(mApplication);
						Notifications.showOkMessage(getActivity(), "Сообщения удалены");

						refreshListView();
						setMessageNumberIcon();
						return true;
					}
				});

		// Надпись внизу, URL
		TextView textView = (TextView) rootView.findViewById(R.id.mfl_url);
		Typeface font = Typeface.createFromAsset(rootView.getContext()
				.getAssets(), "code.otf");
		textView.setTypeface(font);
		textView.setText(this.getString(R.string.company_url));
		textView.setTextSize(15.0f);
		textView.invalidate();

		// Надпись внизу, телефон
		textView = (TextView) rootView.findViewById(R.id.mfl_phone);
		font = Typeface.createFromAsset(rootView.getContext().getAssets(),
				"code.otf");
		textView.setTypeface(font);
		textView.setText(this.getString(R.string.company_phone));
		textView.setTextSize(15.0f);
		textView.invalidate();

		// Надпись "Сообщения"
		textView = (TextView) rootView.findViewById(R.id.menuheader_text);
		font = Typeface.createFromAsset(rootView.getContext().getAssets(),
				"russo.ttf");
		textView.setTypeface(font);
		textView.setTextColor(Color.parseColor("white"));
		textView.setTextSize(35.0f);
		textView.setText(this.getString(R.string.title_alarm_messages));
		textView.setGravity(Gravity.CENTER);
		textView.invalidate();
		
		return rootView;
	}

	private void setMessageNumberIcon() {

			try {
				// Если новых сообщений нет - прячем иконку
				if (mApplication.mAlarmMessages.mAlarmMessages.size() == 0) {
					mMessagesIcon.setVisibility(View.INVISIBLE);
					mMessagesNumber.setVisibility(View.INVISIBLE);
				} else {
					mMessagesIcon.setVisibility(View.VISIBLE);
					mMessagesNumber.setVisibility(View.VISIBLE);
					mMessagesNumber.setText(String
							.valueOf(mApplication.mAlarmMessages.mAlarmMessages
									.size()));
				}
			} catch (Exception e) {
				Logging.v("Исключение при попытке выполнить onReceive в MainActivity");
				e.printStackTrace();
			}

		if (mMessagesNumber.getText().toString().length() == 1) {
			mMessagesNumber.setTextSize(25.0f);
		} else if (mMessagesNumber.getText().toString().length() == 2) {
			mMessagesNumber.setTextSize(23.0f);
		} else if (mMessagesNumber.getText().toString().length() == 3) {
			mMessagesNumber.setTextSize(21.0f);
		} else if (mMessagesNumber.getText().toString().length() == 4) {
			mMessagesNumber.setTextSize(19.0f);
		}

		mMessagesIcon.invalidate();
		mMessagesNumber.invalidate();
	}

	@Override
	public void onStart() {
		// Старт ресивера для приема алармовых сообщений
		try {
			mReceiver = new AlarmMessageReceive();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Globals.BROADCAST_INTENT_ALARM_MESSAGE);
			filter.addAction("SMARTHOUSE.POWER_SUPPLY_CHANGED");
			rootView.getContext().registerReceiver(mReceiver, filter);
			Logging.v("Регистрируем ресивер MainActivity");
		} catch (Exception e) {
			Logging.v("Исключение при попытке зарегистрировать ресивер");
			e.printStackTrace();
		}

		checkPowerSupplyIcon();
		checkUsbConnectionIcon();
		setMessageNumberIcon();

		refreshListView();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mApplication);

		// Если в настройках выставлено использование своего фона
		if (!prefs.getBoolean("use_default_main_messages_background", true)) {
			String filepath = prefs.getString(
					"choose_main_messages_background", "no-image");
			// Если установлен путь к изображению
			if (!filepath.equals("no-image")) {
				BitmapDrawable navigationBackground = new BitmapDrawable(
						filepath);
				// Если фон плиткой
				if (prefs.getBoolean("main_messages_background_tile", true)) {
					navigationBackground.setTileModeXY(Shader.TileMode.REPEAT,
							Shader.TileMode.REPEAT);
				}
				rootView.setBackgroundDrawable(navigationBackground);
			}
		} else {
			rootView.setBackgroundDrawable(mDefaultBackground);
		}

		super.onStart();
	}

	private void checkPowerSupplyIcon() {
		if (isSupplyEnabled()) {
			mPowerSupplyIcon.setImageResource(R.drawable.tablet_power_on);
		} else {
			mPowerSupplyIcon.setImageResource(R.drawable.tablet_power_off);
		}
	}

	private void checkUsbConnectionIcon() {
		if (mApplication.isUsbConnected) {
			mUsbConnectedIcon.setImageResource(R.drawable.tablet_connection_on);
		} else {
			mUsbConnectedIcon
					.setImageResource(R.drawable.tablet_connection_off);
		}
	}

	private Boolean isSupplyEnabled() {
		Intent intent = mApplication.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
        int plugged = 0;
		plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		boolean result = (plugged != 0 && plugged!=-1);
		return result;
	}

	@Override
	public void onStop() {
		// Остановка ресивера для приема алармовых сообщений
		try {
			rootView.getContext().unregisterReceiver(mReceiver);
			Logging.v("Освобождаем ресивер MainActivity");
		} catch (Exception e) {
			Logging.v("Исключение при попытке освободить ресивер");
			e.printStackTrace();
		}
		super.onStop();
	}

	private void refreshListView() {
			MainMessagesAdapter adapter = new MainMessagesAdapter(
					rootView.getContext(),
					mApplication.mAlarmMessages.mAlarmMessages);
			mMessagesList.setAdapter(adapter);
	}

	// Если пришло алармовое сообщение - обновление
	// списка с сообщениями
	class AlarmMessageReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("SMARTHOUSE.POWER_SUPPLY_CHANGED"))
				checkPowerSupplyIcon();

			checkUsbConnectionIcon();
			setMessageNumberIcon();
			refreshListView();
		}
	} // end of class
}