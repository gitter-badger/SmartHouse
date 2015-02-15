package com.isosystem.smarthouse.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isosystem.smarthouse.connection.MessageDispatcher;
import com.isosystem.smarthouse.data.FormattedScreen;
import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.R;

import java.util.Random;

public class TestFormattedScreenActivity extends Activity {
	Context mContext;
	MyApplication mApplication;

	MessageDispatcher mDispatcher;

	FormattedScreen mScreen;
	Button mBackButton;

	float mFontSize = 30;
	int mLinesCount = 9;
	int mLineSize = 43;

	LinearLayout mLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_formscreen);
		// ������������� �� ������ �����
        if (getActionBar() != null) {
            getActionBar().hide();
        }
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		decorView.setSystemUiVisibility(8);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mContext = this;
		mApplication = (MyApplication) getApplicationContext();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mApplication);

		ImageButton mBackButton = (ImageButton) findViewById(R.id.frm_backbutton);
		mBackButton.setOnClickListener(mBackListener);

		ImageButton mAddMessageButton = (ImageButton) findViewById(R.id.frm_test_add_message);
		mAddMessageButton.setOnClickListener(mAddMessageListener);

		try {
			mFontSize = Float.parseFloat(prefs.getString(
					"formatted_screen_font_size", "30"));
		} catch (Exception e) {
			// Logging.v("������ ��� ������� ������� �������� ������� ���������� ������ �� preferences");
			e.printStackTrace();
		}
		try {
			mLinesCount = Integer.parseInt(prefs.getString(
					"formatted_screen_lines_count", "9"));
		} catch (Exception e) {
			// Logging.v("������ ��� ������� ������� �������� ������� ���������� ������ �� preferences");
			e.printStackTrace();
		}
		try {
			mLineSize = Integer.parseInt(prefs.getString(
					"formatted_screen_line_size", "43"));
		} catch (Exception e) {
			// Logging.v("������ ��� ������� ������� �������� ������� ���������� ������ �� preferences");
			e.printStackTrace();
		}

		mLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout1);

		setLines(mLinesCount);
		setExampleText();
	}

	/**
	 * ��������� ��������� ����� � ���������� �� �� �����
	 * 
	 * @param lines
	 *            ���������� �����
	 */
	private void setLines(int lines) {
		Typeface font = Typeface.createFromAsset(getAssets(), "PTM75F.ttf");
		LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		View tempView = mLinearLayout.getChildAt(0);
		mLinearLayout.removeViewAt(0);

		for (int i = 0; i < lines; i++) {
			TextView textview = new TextView(this);
			textview.setMaxLines(1);
			textview.setSingleLine();
			textview.setTypeface(font);
			textview.setTextColor(Color.WHITE);
			textview.setTextSize(mFontSize);
			textview.setPadding(15, 0, 0, 0);
			textview.setLayoutParams(params);

			// ���������� ���������� ����
			mLinearLayout.addView(textview);
		}

		// ���������� ������
		mLinearLayout.addView(tempView);
	}

	/**
	 * ����� �������� ��������� �� �����
     */
	private void setExampleText() {
		TextView textView = (TextView) mLinearLayout.getChildAt(0);
		for (int i = 1; i <= mLineSize; i++)
			textView.setText(textView.getText() + String.valueOf(i % 10));

		for (int i = 1; i < mLinesCount; i++) {
			String example_string = String.valueOf(i) + " ���������� "
					+ String.valueOf(i) + ": "
					+ String.valueOf(new Random().nextInt(100000));
			textView = (TextView) mLinearLayout.getChildAt(i);
			textView.setText(example_string);
		}
	}

	/**
	 *  ������� � ���������
     */
	private OnClickListener mBackListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext,
					ApplicationPreferencesActivity.class);
			mContext.startActivity(intent);
		}
	};

	/**
	 *  ���������� ���������
     */
	private OnClickListener mAddMessageListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final EditText messageInput = new EditText(mContext);

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("������� ��������� ��� ���������������� ������")
					.setView(messageInput)
					.setPositiveButton("�������� ���������",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									String message = messageInput.getText()
											.toString();
									processMessage(message);
									dialog.cancel();
								}
							})
					.setNegativeButton("������",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).create().show();

		}
	};

	/** ������� ���� ����� */
	private void clearAll() {
		TextView textView;
		for (int i = 0; i < mLinesCount; i++) {
			textView = (TextView) mLinearLayout.getChildAt(i);
			textView.setText("");
		}
	}

	/**
	 * ������� ������
	 * 
	 * @param i
	 *            ����� ������
	 */
	private void clearString(int i) {
		if (i > mLinesCount)
			i = mLinesCount;

		try {
			TextView textView = (TextView) mLinearLayout.getChildAt(i);
			textView.setText("");
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ������ "
					+ String.valueOf(i));
			e.printStackTrace();
		}
	}

	/**
	 * �������� ��������� � ������ � ������
	 * 
	 * @param i
	 *            ����� ������
	 * @param msg
	 *            ���������
	 */
	private void addMessageToString(int i, String msg) {
		if (i > mLinesCount)
			i = mLinesCount;

		try {
			clearString(i);
			TextView textView = (TextView) mLinearLayout.getChildAt(i);
			textView.setText(msg);
		} catch (Exception e) {
			Logging.v("���������� ��� ������� �������� ����� � ������ "
					+ String.valueOf(i));
			e.printStackTrace();
			// Notifications
			// .showError(getApplicationContext(),
			// "������ ��� ��������� ��������� �� �����������");
		}
	}

	/**
	 * �������� ��������� � ������ � ������������ �������
	 * 
	 * @param stringNumber
	 *            ����� ������
	 * @param position
	 *            ����� �������
	 * @param msg
	 *            ���������
	 */
	private void addMessageToStringFromPosition(int stringNumber, int position,
			String msg) {

		Logging.v("������: " + String.valueOf(stringNumber));
		Logging.v("�������: " + String.valueOf(position));
		Logging.v("�����: " + msg);

		if (stringNumber > mLinesCount)
			stringNumber = mLinesCount;

		TextView textView = (TextView) mLinearLayout.getChildAt(stringNumber);

		// ������� ������������ ������� ���������
		String oldMsg = textView.getText().toString();

		Logging.v("������ ���������: " + oldMsg);

		StringBuilder builder = new StringBuilder(oldMsg);

		// ����������� �������� ����� ������
		int finalStringLength = position + msg.length();

		Logging.v("��������� ����� ������: "
				+ String.valueOf(finalStringLength));

		// ���� �������� ����� ������ ������, ��� ������ ������
		// �������� ������ ���������� ��������
		while (builder.length() < finalStringLength) {
			builder.append(' ');
		}

		// ������ ������ ������� � ������
		builder.replace(position, position + msg.length(), msg);

		Logging.v("�������� ������: " + builder.toString());

		// ��������� ����� ������ � ������ �����
		textView.setText(builder.toString());
	}

	/**
	 * ��������� ����������� ���������
	 * 
	 * @param msg
	 *            ���������
	 */
	private void processMessage(String msg) {
		String original_message = msg;
		// ����� ������������ �������� ���������
		if (msg.trim().length() == 0
				|| msg.length() < 2
				|| msg.charAt(0) != '@'
				|| (msg.charAt(1) != 'E' && msg.charAt(1) != 'C' && msg
						.charAt(1) != 'P')) {
			if (Globals.DEBUG_MODE) {
				Notifications.showError(this, "������������ ������ ���������: "
						+ original_message);
			}
			return;
		}

		// ��������� @E - ������� �����
		if (msg.charAt(1) == 'E') {
			clearAll();
		} else if (msg.charAt(1) == 'C') {
			// ��������� ���� @C[N][������][�����]
			// ���������� ���������� [N] � [�����]

			// �������� "@C" �� ������
			msg = msg.substring(2);

			// [N] � string
			String stringNumber = "";

			// ������� [N]
			for (int i = 0; i < msg.length()
					&& Character.isDigit(msg.charAt(0));) {
				stringNumber = stringNumber + msg.charAt(0);
				msg = msg.substring(1);
			}

			// [N] � int
			int parsedStringNumber = -1;

			try {
				parsedStringNumber = Integer.parseInt(stringNumber);
			} catch (Exception e) {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"������������ ������ ���������: "
									+ original_message);
				}
				e.printStackTrace();
				return;
			}

			if (parsedStringNumber == -1) {
				Notifications.showError(this, "������������ ������ ���������: "
						+ original_message);
				return;
			}

			// ������� [�����]
			if (msg.length() > 0) {
				// ��������� ���� @CN[������][�����]
				// �������� [������]
				msg = msg.substring(1);
				// ������ [�����] � ������ [N]
				addMessageToString(parsedStringNumber, msg);
			} else {
				// ������� ���� @CN
				// �������� ������ N
				clearString(parsedStringNumber);
			}
		} else if (msg.charAt(1) == 'P') {
			// ��������� ���� @P[X],[N][������][�����]
			// ���������� ���������� [X],[N] � [�����]

			// �������� "@P" �� ������
			msg = msg.substring(2);

			// ���������� [X]

			// [X] � string
			String positionNumber = "";

			// ������� [X]
			for (int i = 0; i < msg.length()
					&& Character.isDigit(msg.charAt(0));) {
				positionNumber = positionNumber + msg.charAt(0);
				msg = msg.substring(1);
			}

			// [X] � int
			int parsedPositionNumber = -1;

			try {
				parsedPositionNumber = Integer.parseInt(positionNumber);
			} catch (Exception e) {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"������������ ������ ���������: "
									+ original_message);
				}
				e.printStackTrace();
				return;
			}

			// ���������� [N]

			// �������� "," �� ������
			msg = msg.substring(1);

			// [N] � string
			String stringNumber = "";

			// ������ ����� ������
			for (int i = 0; i < msg.length()
					&& Character.isDigit(msg.charAt(0));) {
				stringNumber = stringNumber + msg.charAt(0);
				msg = msg.substring(1);
			}

			// [N] � int
			int parsedStringNumber = -1;

			try {
				parsedStringNumber = Integer.parseInt(stringNumber);
			} catch (Exception e) {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"������������ ������ ���������: "
									+ original_message);
				}
				e.printStackTrace();
				return;
			}

			if (parsedStringNumber == -1) {
				// Logging.v("������ ��� ������� �������� ������ " +
				// stringNumber);
				return;
			}

			// ������� [�����]
			if (msg.length() > 0) {
				// �������� [������]
				msg = msg.substring(1);
				// ������ [�����] � ������ [N] � ������� [X]
				addMessageToStringFromPosition(parsedStringNumber,
						parsedPositionNumber, msg);
			} else {
				if (Globals.DEBUG_MODE) {
					Notifications.showError(this,
							"������������ ������ ���������: "
									+ original_message);
				}
			}
		} // end of if-elseif
	} // end of processmessage
}