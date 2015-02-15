package com.isosystem.smarthouse.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.R;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public class AddMenuItemSendMessage extends Activity {
	MyApplication mApplication;
	Context mContext;
	
	/** ����� ���� (��������������\��������) */
	boolean mEditMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_mainmenu_add_send_message);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mApplication = (MyApplication) getApplicationContext();
		mContext = this;

		// ������ ��������
		Button addBtn = (Button) findViewById(R.id.btn_ok);
		addBtn.setOnClickListener(mAddListener);

		// ������ ��������
		Button backBtn = (Button) findViewById(R.id.btn_cancel);
		backBtn.setOnClickListener(mBackListener);

		// ��������� ��������� ��� ������ "���������"
		ImageButton mTooltipButton = (ImageButton) findViewById(R.id.button_help_header);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_description);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_outgoing_prefix);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);
		
		/** ���������� ����� ������ ���� (�������� ��� �������������� */
		try {
			mEditMode = getIntent().getBooleanExtra("isEdited", false);
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������� ����� ������ ����");
			e.printStackTrace();
		}
		
		// ���� ����� �������������� - ��������� ����
		if (mEditMode) {		
			setFieldValues();
		}
	}
	
	/**
	 * ��������� �������� ����� � ������ ��������������
	 */
	private void setFieldValues() {
		
		HashMap<String, String> pMap = mApplication.mTree.tempNode.paramsMap;
		
		// ���� ����� �� ����, ���� ��� ������� ��� �������
		if (pMap == null) return;
			
		// ��������� ��������
		EditText mHeaderText = (EditText) findViewById(R.id.header_text);
		if (pMap.get("HeaderText")!=null)
			mHeaderText.setText(pMap.get("HeaderText"));
				
		// ��������� ��������
		EditText mDescText = (EditText) findViewById(R.id.description_text);
		if (pMap.get("DescriptionText")!=null)
			mDescText.setText(pMap.get("DescriptionText"));
		
		// �������� ���������
		EditText mOutgoingValueMessage = (EditText) findViewById(R.id.outgoing_prefix_text);
		if (pMap.get("OutgoingValueMessage")!=null)
			mOutgoingValueMessage.setText(pMap.get("OutgoingValueMessage"));
	}
	
	/**
	 * ��������� ��� ������ "���������". ��� ������� �� ������ ������������
	 * Toast � ����������
	 */
	private OnClickListener tooltipsButtonListener = new OnClickListener() {
		// �������� ������� ��� ��������� ��������� ��������
		@Override
		public void onClick(final View v) {
			String tooltip;
			switch (v.getId()) {
			// ���������
			case R.id.button_help_header:
				tooltip = "�����, ������� ����� ������������ � �������� ��������� �������, ������ - ������";
				break;
			// ��������
			case R.id.button_help_description:
				tooltip = "�����, ������� ����� ������������ ��� ����������, ������ - ������";
				break;
			case R.id.button_help_outgoing_prefix:
				tooltip = "���������, ������� ����� ������� �����������.";
				break;
			default:
				tooltip = "���� �� ������ ��� ���������, �������� ������������ �� ������, ������ ��������, ��� ������� �� ������� ��� ���������";
				break;
			}
			// ���������� Toast � ����������
			Notifications.showTooltip(mContext, tooltip);
		}
	};

	/**
	 * ������ ���������� ������ ������.
	 * � ���� ������, ���������� ��������� ���������� ������ ����
	 */
	private void undoMenuItemAdding() {
		
		try {
			mApplication.mProcessor.loadMenuTreeFromInternalStorage();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ��������� ���� �� �����");
			e.printStackTrace();
		}
	}
	
	private void addNewMenuItem() {

		EditText mHeaderText = (EditText) findViewById(R.id.header_text);
		EditText mDescText = (EditText) findViewById(R.id.description_text);
		EditText mOutgoingValueMessage = (EditText) findViewById(R.id.outgoing_prefix_text);

		// �������� ������������� ������������ �����
		// ������������ ����: 3 ������� � 2 ��������� + ��������
		if ((mHeaderText.getText().toString() == null)  || (mHeaderText.getText().toString().trim().isEmpty())
				|| (mDescText.getText().toString() == null) || (mDescText.getText().toString().trim().isEmpty())
				|| (mOutgoingValueMessage.getText().toString() == null) || (mOutgoingValueMessage.getText().toString().trim().isEmpty())) {
			Notifications.showError(mContext,
					"�� ��������� ����������� ���� (��� �������� *)");
			return;
		}

		// ������� ��� ��� �������� ��������� �������� (��� �������,
		// ���������)
		HashMap<String, String> mParamsMap = new HashMap<String, String>();

		// �������� ��� ������
		if (mApplication.mTree.tempNode.paramsMap != null) {
			if (mApplication.mTree.tempNode.paramsMap.get("GridImage")!=null) {
				mParamsMap.put("GridImage", mApplication.mTree.tempNode.paramsMap.get("GridImage"));
			}
		} // if !null
		
		// ID ��������
		mParamsMap.put("HeaderText", mHeaderText.getText().toString());

		// ��������� ����������� ��� �����
		mParamsMap.put("DescriptionText", mDescText.getText().toString());

		mParamsMap.put("OutgoingValueMessage", mOutgoingValueMessage.getText()
				.toString());

		mApplication.mTree.tempNode.paramsMap = mParamsMap;

		try {
			mApplication.mProcessor.saveMenuTreeToInternalStorage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		finish();
	}

	/**
	 * ������ "��������"
	 */
	private OnClickListener mAddListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(
					"�� ������������� ������ �������� ����� ����� ����?")
					.setPositiveButton("��������",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									addNewMenuItem();
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

	private OnClickListener mBackListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("�� ������������� ������ �����?")
					.setPositiveButton("�����",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									undoMenuItemAdding();
									((Activity) mContext).finish();
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
}
