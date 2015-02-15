package com.isosystem.smarthouse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isosystem.smarthouse.connection.MessageDispatcher;
import com.isosystem.smarthouse.data.FormattedScreen;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.notifications.Notifications;

public class FormattedScreensActivity extends Activity {
	MyApplication mApplication;
	Context mContext;

	Button mBackButton;

	MessageDispatcher mDispatcher;

	FormScreenMessageReceiver mReceiver;
	FormattedScreen mScreen;

	Handler hl_timeout;

	float mFontSize = 30;
	int mLinesCount = 9;
	int mLineSize = 43;
	
	LinearLayout mLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_formscreen);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mContext = this;

		mApplication = (MyApplication) getApplicationContext();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mApplication);

		mLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout1);
		
		try {
			mFontSize = Float.parseFloat(prefs.getString(
					"formatted_screen_font_size", "30"));
		} catch (Exception e) {
			// Logging.v("Ошибка при попытке считать значение периода обновления буфера из preferences");
			e.printStackTrace();
		}
		
		Logging.v("Размер шрифта:[" + mFontSize +"]");
		
		try {
			mLinesCount = Integer.parseInt(prefs.getString(
					"formatted_screen_lines_count", "9"));
		} catch (Exception e) {
			// Logging.v("Ошибка при попытке считать значение периода обновления буфера из preferences");
			e.printStackTrace();
		}
		
		Logging.v("Количество линий:[" + mLinesCount +"]");
		
		try {
			mLineSize = Integer.parseInt(prefs.getString(
					"formatted_screen_line_size", "43"));
		} catch (Exception e) {
			// Logging.v("Ошибка при попытке считать значение периода обновления буфера из preferences");
			e.printStackTrace();
		}
		
		Logging.v("Длина строки:[" + mLineSize +"]");

		try {
			// Создаем и подключаем броадкаст ресивер
			mReceiver = new FormScreenMessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Globals.BROADCAST_INTENT_FORMSCREEN_MESSAGE);
			registerReceiver(mReceiver, filter);
			Logging.v("Регистрируем ресивер FormScreen");
		} catch (Exception e) {
			Logging.v("Исключение при попытке зарегистрировать ресивер");
			e.printStackTrace();
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}

		// Разворачиваем на полный экран
        if (getActionBar() != null) {
            getActionBar().hide();
        }
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		decorView.setSystemUiVisibility(8);

		int position = -1;
		try {
			position = getIntent().getIntExtra("formScreenIndex", -1);
		} catch (Exception e) {
			Logging.v("Исключение при попытке взять номер окна из extras");
			e.printStackTrace();
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
		
		Logging.v("Номер окна форматированного вывода:[" + position +"]");

		try {
			mScreen = ((MyApplication) getApplicationContext()).mFormattedScreens.mFormattedScreens
					.get(position);
		} catch (Exception e) {
			Logging.v("Исключение при попытке взять окно номер "
					+ String.valueOf(position)
					+ " . Возможно, такой позиции нет.");
			e.printStackTrace();
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}

		setLines(mLinesCount);

		ImageButton mBackButton = (ImageButton) findViewById(R.id.frm_backbutton);
		mBackButton.setOnClickListener(mBackListener);

		mDispatcher = new MessageDispatcher(this);
		mDispatcher.SendRawMessage(mScreen.mInputStart);
	}

	/**
	 * Установка текстовых строк и добавление их на экран
	 * @param lines количество строк
	 */
	private void setLines(int lines) {
		Typeface font = Typeface.createFromAsset(getAssets(), "PTM75F.ttf");
		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,1.0f);
		View tempView = mLinearLayout.getChildAt(0);
		mLinearLayout.removeViewAt(0);
		
		for (int i=0;i<lines;i++) {						
			TextView textview = new TextView(this);
			textview.setMaxLines(1);
			textview.setSingleLine();
			textview.setTypeface(font);
			textview.setTextColor(Color.WHITE);
			textview.setTextSize(mFontSize);
			textview.setPadding(15, 0, 0, 0);
			textview.setLayoutParams(params);
			
			// Добавление текстового поля
			mLinearLayout.addView(textview);
		}
				
		//Добавление кнопки
		mLinearLayout.addView(tempView);
	}

	/** Очистка всех строк */
	private void clearAll() {
		TextView textView;
		
		for (int i = 0; i < mLinesCount; i++) {
			textView = (TextView) mLinearLayout.getChildAt(i);
			textView.setText("");
		}
	}

	/**
	 * Очистка строки
	 * @param i номер строки
	 */
	private void clearString(int i) {
		if (i > mLinesCount)
			i = mLinesCount;
		
		Logging.v("Очистка строки :[" + i +"]");
		
		try {
			TextView textView = (TextView) mLinearLayout.getChildAt(i);
			textView.setText("");
		} catch (Exception e) {
			Logging.v("Исключение при попытке очистить строку "
					+ String.valueOf(i));
			e.printStackTrace();
		}
	}

	/**
	 * Добавить сообщение в строку с начала
	 * @param i номер строки
	 * @param msg сообщение
	 */
	private void addMessageToString(int i, String msg) {
		if (i > mLinesCount)
			i = mLinesCount;
		
		Logging.v("Номер:[" + i +"]. Сообщение:[" + msg +"]");
		
		try {
			clearString(i);
			TextView textView = (TextView) mLinearLayout.getChildAt(i);
			textView.setText(msg);
		} catch (Exception e) {
			Logging.v("Исключение при попытке добавить текст в строку "
					+ String.valueOf(i));
			e.printStackTrace();
			// Notifications
			// .showError(getApplicationContext(),
			// "Ошибка при обработке сообщения от контроллера");
		}
	}

	/**
	 * Добавить сообщение в строку с определенной позиции
	 * @param stringNumber номер строки
	 * @param position номер позиции
	 * @param msg сообщение
	 */
	private void addMessageToStringFromPosition(int stringNumber, int position,
			String msg) {
		
		Logging.v("Строка: " + String.valueOf(stringNumber));
		Logging.v("Позиция: " + String.valueOf(position));
		Logging.v("Текст: " + msg);
		
		if (stringNumber > mLinesCount)
			stringNumber = mLinesCount;
		
		TextView textView = (TextView) mLinearLayout.getChildAt(stringNumber);
		
		// Создаем стрингбилдер старого сообщения
		String oldMsg = textView.getText().toString();
		
		Logging.v("Старое сообщение: " + oldMsg);
		
		StringBuilder builder = new StringBuilder(oldMsg);

		// Высчитываем итоговую длину строки
		int finalStringLength = position + msg.length();

		Logging.v("Финальная длина строки: " + String.valueOf(finalStringLength));
		
		// Если итоговая длина строки больше, чем старая строка
		// добиваем нужное количество пробелов
		while (builder.length() < finalStringLength) {
			builder.append(' ');
		}

		// Меняем нужные символы в строке
		builder.replace(position, position + msg.length(), msg);

		Logging.v("Итоговая строка: " + builder.toString());
		
		// Добавляем новую строку в нужное место
		textView.setText(builder.toString());
	}

	/**
	 * Обработка полученного сообщения
	 * @param msg Сообщение
	 */
	private void processMessage(String msg) {
		String original_message = msg;
		// Отлов неправильных форматов сообщения
		if (msg.trim().length() == 0
				|| msg.length() < 2
				|| msg.charAt(0) != '@'
				|| (msg.charAt(1) != 'E' && msg.charAt(1) != 'C' && msg
						.charAt(1) != 'P')) {
			if (Globals.DEBUG_MODE) {
				Logging.v("Неформат:[" + original_message + "]");
				Notifications.showError(this, "Неправильный формат сообщения: "
						+ original_message);
			}
			return;
		}

		// Сообщение @E - стереть экран
		if (msg.charAt(1) == 'E') {
			Logging.v("Стираем весь экран");
			clearAll();
		} else if (msg.charAt(1) == 'C') {
			// Сообщение вида @C[N][пробел][текст]
			// Необходимо распарсить [N] и [текст]

			// Удаление "@C" из строки
			msg = msg.substring(2);
			
			Logging.v("Удалено @C:[" + msg + "]");

			// [N] в string
			String stringNumber = "";

			// Парсинг [N]
			for (int i = 0; i < msg.length()
					&& Character.isDigit(msg.charAt(0));) {
				stringNumber = stringNumber + msg.charAt(0);
				msg = msg.substring(1);
			}
			
			Logging.v("Номер строки:[" + stringNumber + "]. Осталось: [" + msg +"]");

			// [N] в int
			int parsedStringNumber = -1;

			try {
				parsedStringNumber = Integer.parseInt(stringNumber);
			} catch (Exception e) {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"Неправильный формат сообщения: "
									+ original_message);
				}
				e.printStackTrace();
				return;
			}

			if (parsedStringNumber == -1) {
				Notifications.showError(this, "Неправильный формат сообщения: "
						+ original_message);
				return;
			}

			// Парсинг [текст]
			if (msg.length() > 0) {
				// Сообщение вида @CN[пробел][текст]
				// Удаление [пробел]
				msg = msg.substring(1);
				
				Logging.v("Удаление пробела:[" + msg +"]");
				
				// Запись [текст] в строку [N]
				addMessageToString(parsedStringNumber, msg);
			} else {
				// Команда вида @CN
				// Стирание строки N
				clearString(parsedStringNumber);
			}
		} else if (msg.charAt(1) == 'P') {
			// Сообщение вида @P[X],[N][пробел][текст]
			// Необходимо распарсить [X],[N] и [текст]

			// Удаление "@P" из строки
			msg = msg.substring(2);

			// Считывание [X]

			// [X] в string
			String positionNumber = "";

			// Парсинг [X]
			for (int i = 0; i < msg.length()
					&& Character.isDigit(msg.charAt(0));) {
				positionNumber = positionNumber + msg.charAt(0);
				msg = msg.substring(1);
			}

			// [X] в int
			int parsedPositionNumber = -1;

			try {
				parsedPositionNumber = Integer.parseInt(positionNumber);
			} catch (Exception e) {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"Неправильный формат сообщения: "
									+ original_message);
				}
				e.printStackTrace();
				return;
			}

			// Считывание [N]

			// Удаление "," из строки
			msg = msg.substring(1);

			// [N] в string
			String stringNumber = "";

			// Парсим номер строки
			for (int i = 0; i < msg.length()
					&& Character.isDigit(msg.charAt(0));) {
				stringNumber = stringNumber + msg.charAt(0);
				msg = msg.substring(1);
			}

			// [N] в int
			int parsedStringNumber = -1;

			try {
				parsedStringNumber = Integer.parseInt(stringNumber);
			} catch (Exception e) {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"Неправильный формат сообщения: "
									+ original_message);
				}
				e.printStackTrace();
				return;
			}

			if (parsedStringNumber == -1) {
				// Logging.v("Ошибка при попытке парсинга строки " +
				// stringNumber);
				return;
			}

			// Парсинг [текст]
			if (msg.length() > 0) {
				// Удаление [пробел]
				msg = msg.substring(1);
				// Запись [текст] в строку [N] в позицию [X]
				addMessageToStringFromPosition(parsedStringNumber,
						parsedPositionNumber, msg);
			} else {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"Неправильный формат сообщения: "
									+ original_message);
				}
			}
		} // end of if-elseif
	} // end of processmessage
	
	private OnClickListener mBackListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
	};

	@Override
	public void onStop() {
		super.onStop();
		mDispatcher.SendRawMessage(mScreen.mInputEnd);
		try {
			unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class FormScreenMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Получено сообщение. Оно должно быть обработано формулой с нужным
			// количеством знаков после запятой
			String msg = intent.getStringExtra("message");
			
			Logging.v("Пришло в активити:[" + msg + "]");

			try {
				processMessage(msg);
			} catch (Exception e) {
				Notifications.showError(mContext,
						"Неправильный формат сообщения " + msg);
				e.printStackTrace();
			}
		}
	} // end BroadcastReceiver
}