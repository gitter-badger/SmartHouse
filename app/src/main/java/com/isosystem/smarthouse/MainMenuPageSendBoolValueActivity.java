package com.isosystem.smarthouse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.isosystem.smarthouse.connection.MessageDispatcher;
import com.isosystem.smarthouse.data.MenuTreeNode;
import com.isosystem.smarthouse.logging.Logging;

import java.io.File;
import java.util.HashMap;

public class MainMenuPageSendBoolValueActivity extends Activity {

	MenuTreeNode node; // Текущий узел
	TextView mIncomingValue; // Входящее значение
	Context mContext;

	final String TAG_FALSE = "0";
	final String TAG_TRUE = "1";
	final String TAG_UNKNOWN = "2";

	String mIncomingFalseText = "Выключено";
	String mIncomingTrueText = "Включено";
	String mIcomingUnknownText = "";
	
	String mOutgoingFalseText = "Выключить";
	String mOutgoingTrueText = "Включить";

	String mGiveMeValueMessage;
	String mOutgoingValueMessage;

	ValueMessageReceiver mReceiver;

	MessageDispatcher mDispatcher;

	MyApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_send_bool);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mContext = this;
		mApp = (MyApplication) getApplicationContext();

		try {
			// Создаем и подключаем броадкаст ресивер
			mReceiver = new ValueMessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Globals.BROADCAST_INTENT_VALUE_MESSAGE);
			registerReceiver(mReceiver, filter);
			Logging.v("Регистрируем ресивер PageBool");
		} catch (Exception e) {
			Logging.v("Исключение при попытке зарегистрировать ресивер");
			e.printStackTrace();
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
		
		// Получение текущей ноды
		node = (MenuTreeNode) getIntent().getSerializableExtra("Node");

		// Сокрытие ActionBar и StatusBar
		if (getActionBar() != null) {
            getActionBar().hide();
        }
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		decorView.setSystemUiVisibility(8);

		// Берем хеш-таблицу параметров узла
		HashMap<String, String> pMap = node.paramsMap;

		// Значение из контроллера
		mIncomingValue = (TextView) findViewById(R.id.psb_incoming_value);
		mIncomingValue.setText(mIcomingUnknownText);

		TextView mHeaderText = (TextView) findViewById(R.id.psb_tv_headertext);
		mHeaderText.setText(pMap.get("HeaderText"));

		TextView mDescriptionText = (TextView) findViewById(R.id.psb_tv_description_text);
		mDescriptionText.setText(pMap.get("DescriptionText"));

		ImageView mImage = (ImageView) findViewById(R.id.psb_image);
		File imageFile = new File(pMap.get("SelectedImage"));
		if (imageFile.exists()) {
			mImage.setImageBitmap(BitmapFactory.decodeFile(imageFile
					.getAbsolutePath()));
		}

		String label = pMap.get("IncomingFalseText");
		if (!TextUtils.isEmpty(label.trim())) {
			mIncomingFalseText = label;
		}

		label = pMap.get("IncomingTrueText");
		if (!TextUtils.isEmpty(label.trim())) {
			mIncomingTrueText = label;
		}
		
		label = pMap.get("OutgoingFalseText");
		if (!TextUtils.isEmpty(label.trim())) {
			mOutgoingFalseText = label;
		}

		label = pMap.get("OutgoingTrueText");
		if (!TextUtils.isEmpty(label.trim())) {
			mOutgoingTrueText = label;
		}

		mGiveMeValueMessage = pMap.get("GiveMeValueMessage");
		mOutgoingValueMessage = pMap.get("OutgoingValueMessage");

		// Кнопка отправки TRUE
		Button mSendTrueButton = (Button) findViewById(R.id.psb_set_true_button);
		mSendTrueButton.setOnClickListener(sendTrueButtonListener);
		mSendTrueButton.setText(mOutgoingTrueText);
		
		// Кнопка отправки FALSE
		Button mSendFalseButton = (Button) findViewById(R.id.psb_set_false_button);
		mSendFalseButton.setOnClickListener(sendFalseButtonListener);
		mSendFalseButton.setText(mOutgoingFalseText);
		
		// Кнопка назад
		Button mBackButton = (Button) findViewById(R.id.psb_back_button);
		mBackButton.setOnClickListener(backButtonListener);

		// Установка шрифта
		SetFont(R.id.psb_incoming_value);
		SetFont(R.id.psb_set_true_button);
		SetFont(R.id.psb_set_false_button);
		SetFont(R.id.psb_back_button);
		SetFont(R.id.psb_tv01);
		SetFont(R.id.psb_tv02);
		SetFont(R.id.psb_tv_description_text);
		SetFont(R.id.psb_tv_headertext);

		// Создаем объект диспетчера
		mDispatcher = new MessageDispatcher(this);
		// Отсылаем сообщение типа "Дай мне значение"
		mDispatcher.SendRawMessage(mGiveMeValueMessage);
	}

	/**
	 * Устанавливаем красивый шрифт. В качестве входного параметра - id элемента
	 * из R.java
	 */
	private void SetFont(int id) {
		Typeface font = Typeface.createFromAsset(getAssets(), "myfont.ttf");
		TextView et = (TextView) findViewById(id);
		et.setTypeface(font);
		et.invalidate();
	}

	/**
	 * Слушатель для кнопки отсылки TRUE значения.
	 */
	private OnClickListener sendTrueButtonListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			mDispatcher.sendBooleanMessage(mOutgoingValueMessage, 1, true);
			((Activity)mContext).finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
	};

	/**
	 * Слушатель для кнопки отсылки FALSE значения.
	 */
	private OnClickListener sendFalseButtonListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			mDispatcher.sendBooleanMessage(mOutgoingValueMessage, 0, true);
			((Activity)mContext).finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
	};

	/**
	 * Слушатель для кнопки "Назад"
	 */
	private OnClickListener backButtonListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
	};

	@Override
	public void onStop() {
		
		try {
			unregisterReceiver(mReceiver);
			Logging.v("Освобождаем ресивер PageBool");
		} catch (Exception e) {
			Logging.v("Исключение при попытке освободить ресивер");
			e.printStackTrace();
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
		super.onStop();
	}

	class ValueMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msg = intent.getStringExtra("message");

			if (msg.length() < 3) {
				// TODO:ОШИБКА
				return;
			}

			msg = msg.substring(2);

			int incomingValue = -1;

			try {
				incomingValue = Integer.parseInt(msg);
			} catch (Exception e) {
				// TODO: ОШИБКА
			}

			if (incomingValue == 0) {
				mIncomingValue.setText(mIncomingFalseText);
				mIncomingValue.invalidate();
			} else if (incomingValue == 1) {
				mIncomingValue.setText(mIncomingTrueText);
				mIncomingValue.invalidate();
			} else if (incomingValue == 2) {
				mIncomingValue.setText(mIcomingUnknownText);
				mIncomingValue.invalidate();
			} else {
				// TODO: оШИБКА
			}
		}
	}

}
