package com.isosystem.smarthouse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.isosystem.smarthouse.connection.MessageDispatcher;
import com.isosystem.smarthouse.data.MenuTreeNode;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.utils.BooleanFormulaEvaluator;
import com.isosystem.smarthouse.utils.EvaluatorResult;
import com.isosystem.smarthouse.utils.MathematicalFormulaEvaluator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author kaz
 * 
 */
public class MainMenuPageSendIntValueActivity extends Activity {

	MenuTreeNode node; // ������� ����
	TextView mIncomingValue; // �������� ��������
	EditText mOutgoingValue; // ��������� ��������
	Context mContext;

	String mInvalidValueText;
	String mIncomingValueFormula;
	String mFractionDigits;
	String mOutgoingValueFormula;
	String mOutgoingValueValidation;
	String mGiveMeValueMessage;
	String mOutgoingValueMessage;

	ValueMessageReceiver mReceiver;

	MessageDispatcher mDispatcher;

	MyApplication mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_send_value);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		try {
			Runtime.getRuntime().exec("service call activity 42 s16 com.android.systemui");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mApp = (MyApplication) getApplicationContext();

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle reply = msg.getData();
				String message = reply.getString("msg");
				mIncomingValue.setText(message);
			}
		};

		mContext = this;
		mApp = (MyApplication) getApplicationContext();

		// ��������� ������� ����
		node = (MenuTreeNode) getIntent().getSerializableExtra("Node");

		// ��������� �������������� ����� ��������
        if (getActionBar() != null) {
            getActionBar().hide();
        }
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		decorView.setSystemUiVisibility(8);
		// <<-----------------------------------

		// ����� ���-������� ���������� ����
		HashMap<String, String> pMap = node.paramsMap;

		// �������� �� �����������
		mIncomingValue = (TextView) findViewById(R.id.psv_incoming_value);
		// �������� ������������
		mOutgoingValue = (EditText) findViewById(R.id.psv_outgoing_value);

		TextView mHeaderText = (TextView) findViewById(R.id.psv_tv_headertext);
		mHeaderText.setText(pMap.get("HeaderText"));

		TextView mDescriptionText = (TextView) findViewById(R.id.psv_tv_description_text);
		mDescriptionText.setText(pMap.get("DescriptionText"));

		ImageView mImage = (ImageView) findViewById(R.id.psv_image);
		File imageFile = new File(pMap.get("SelectedImage"));
		if (imageFile.exists()) {
			mImage.setImageBitmap(BitmapFactory.decodeFile(imageFile
					.getAbsolutePath()));
		}

		// ���������� ��������� ��������� �� ���-�������
		mInvalidValueText = pMap.get("InvalidValueText");
		mIncomingValueFormula = pMap.get("IncomingValueFormula");
		mFractionDigits = pMap.get("FractionDigits");
		mOutgoingValueFormula = pMap.get("OutgoingValueFormula");
		mOutgoingValueValidation = pMap.get("OutgoingValueValidation");
		mGiveMeValueMessage = pMap.get("GiveMeValueMessage");
		mOutgoingValueMessage = pMap.get("OutgoingValueMessage");

		// ������ ��������� � �����
		Button mSendButton = (Button) findViewById(R.id.psv_send_button);
		mSendButton.setOnClickListener(sendButtonListener);
		Button mBackButton = (Button) findViewById(R.id.psv_back_button);
		mBackButton.setOnClickListener(backButtonListener);

		// ��������� ������
		SetFont(R.id.psv_incoming_value);
		SetFont(R.id.psv_outgoing_value);
		SetFont(R.id.psv_send_button);
		SetFont(R.id.psv_back_button);
		SetFont(R.id.psv_tv01);
		SetFont(R.id.psv_tv02);
		SetFont(R.id.psv_tv_description_text);
		SetFont(R.id.psv_tv_headertext);

		// ������� ������ ����������
		mDispatcher = new MessageDispatcher(this);
		mDispatcher.SendRawMessage(mGiveMeValueMessage);
	}

	/**
	 * ������������� �������� �����. � �������� �������� ��������� - id ��������
	 * �� R.java
	 */
	private void SetFont(int id) {
		Typeface font = Typeface.createFromAsset(getAssets(), "myfont.ttf");
		TextView et = (TextView) findViewById(id);
		et.setTypeface(font);
		et.invalidate();
	}

	/**
	 * ��������� ��� ������ ������� �������� ������ ����� ��������� �������
	 * ����� ����������� ����. ����������: 1. ���������� �������� � �������
	 * ������� 2. �������� ��������� ������������� �������� 3. ������� ��������
	 * �� ����������
	 */
	private OnClickListener sendButtonListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {

            String variable = mOutgoingValue.getText().toString();

            Logging.v("����������� �������� :" + variable);

            // 1. ��������� �������� � ������� �������
            MathematicalFormulaEvaluator evaluator = new MathematicalFormulaEvaluator(
                    mOutgoingValueFormula, variable, "0", true);
            EvaluatorResult evalResult = evaluator.eval();

            // ��������� ���������
            if (!evalResult.isCorrect) {
                Notifications
                        .showError(
                                mContext,
                                "������ ��� ��������� ���������� ��������. �������� ������� ����������� ��� ������� ��������� ������ �����������");
            } else {
                Logging.v("�������� ����� �������:" + evalResult.numericRoundedResult);
                // 2. ��������� ������������� ��������
                BooleanFormulaEvaluator bEvaluator = new BooleanFormulaEvaluator(
                        mOutgoingValueValidation,
                        evalResult.numericRoundedResult);
                EvaluatorResult boolEvalResult = bEvaluator.eval();

                if (!boolEvalResult.isCorrect) {
                    // ��������� �� �������
                    Notifications
                            .showError(
                                    mContext,
                                    "������ ��� ������� ��������� ���������� ��������. �������� ������� ����������� ��� ������� ��������� ������ �����������");
                } else {
                    if (!boolEvalResult.booleanResult) {
                        // �������� �� ������ ���������
                        Notifications.showError(mContext, mInvalidValueText);
                    } else {
                        // 3. �������� ������ ��������� � ���������� �����������

                        mDispatcher.SendValueMessage(mOutgoingValueMessage,
                                evalResult.numericRoundedResult, true);
                        ((Activity) mContext).finish();
                        overridePendingTransition(R.anim.flipin, R.anim.flipout);
                    }
                }
            }
        }

    };
	/**
	 * ��������� ��� ������ "�����"
	 */
	private OnClickListener backButtonListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
	};
	
	@Override
	public void onStart() {
		super.onStart();		
		try {
			// ������� � ���������� ��������� �������
			mReceiver = new ValueMessageReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Globals.BROADCAST_INTENT_VALUE_MESSAGE);
			registerReceiver(mReceiver, filter);
			Logging.v("������������ ������� Page");
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ���������������� �������");
			e.printStackTrace();
			finish();
			overridePendingTransition(R.anim.flipin,R.anim.flipout);
		}
	}
	
	@Override
	public void onStop() {
		if (mReceiver != null) {
			try {
				unregisterReceiver(mReceiver);
				Logging.v("����������� ������� Page");
			} catch (Exception e) {
				Logging.v("���������� ��� ������� ���������� �������");
				e.printStackTrace();
				finish();
				overridePendingTransition(R.anim.flipin,R.anim.flipout);
			}
		}
		super.onStop();
	}
	
	class ValueMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// �������� ���������. ��� ������ ���� ���������� �������� � ������
			// ����������� ������ ����� �������
			String msg = intent.getStringExtra("message");

			if (msg.length() < 3) {
				Logging.v("�������� ������ ���������");
				return;
			}

			msg = msg.substring(2);

			// 1. ��������� �������� � ������� �������
			MathematicalFormulaEvaluator evaluator = new MathematicalFormulaEvaluator(
					mIncomingValueFormula, msg, mFractionDigits, true);
			EvaluatorResult evalResult = evaluator.eval();

			// ��������� ���������
			if (!evalResult.isCorrect) {
				Notifications
						.showError(
								mContext,
								"������ ��� ��������� ��������� ��������. �������� ������� ����������� ��� ������� ��������� ������ �����������");
			} else {
				mIncomingValue.setText(evalResult.numericRoundedResult);
				mIncomingValue.invalidate();
			}
		}
	}
}
