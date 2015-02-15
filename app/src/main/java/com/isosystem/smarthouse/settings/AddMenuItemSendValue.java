package com.isosystem.smarthouse.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.isosystem.smarthouse.dialogs.FormulaCheckDialog;
import com.isosystem.smarthouse.dialogs.OutgoingMessageCheckDialog;
import com.isosystem.smarthouse.dialogs.ValidationFormulaCheckDialog;
import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("deprecation")
public class AddMenuItemSendValue extends Activity {
	Context mContext;
	MyApplication mApplication;
	
	Gallery mGallery;
	ArrayList<String> mImages = null;
	ImageView mGalleryPicker; //����� ����������� �������
	
	//����� ���� (�������������� ��� ��������)
	boolean mEditMode;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_mainmenu_add_send_value);

		// ������ ���������� ������
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mApplication = (MyApplication) getApplicationContext();
		mContext = this;

		// ������ ��������
		Button addBtn = (Button) findViewById(R.id.btn_ok);
		addBtn.setOnClickListener(mAddListener);

		// ������ ��������
		Button backBtn = (Button) findViewById(R.id.btn_cancel);
		backBtn.setOnClickListener(mBackListener);

		// ����������� ��������� �������� �� �������
		mGalleryPicker = (ImageView) findViewById(R.id.tile_image);
		mGalleryPicker.setTag("");

		// ������� ����������� ��� ������
		mGallery = (Gallery) findViewById(R.id.tile_image_gallery);
		mImages = getImages();
		mGallery.setAdapter(new GalleryAdapter(mImages, this));
		mGallery.setOnItemClickListener(galleryImageSelectListener);

		// ��������� ��������� ��� ������ "���������"
		
		ImageButton mTooltipButton = (ImageButton) findViewById(R.id.button_help_header);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_description);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_error);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_incoming_formula);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_decimal_places);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_outgoing_formula);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_validation_formula);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_get_value);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		mTooltipButton = (ImageButton) findViewById(R.id.button_help_outgoing_prefix);
		mTooltipButton.setOnClickListener(tooltipsButtonListener);

		// �������� ������� ��������� ��������
		ImageButton mIncomingValueFormulaDialogButton = (ImageButton) this
				.findViewById(R.id.button_check_incoming_formula);
		mIncomingValueFormulaDialogButton
				.setOnClickListener(incomingValueFormulaListener);

		// �������� ������� ���������� ��������
		ImageButton mOutgoingValueFormulaDialogButton = (ImageButton) this
				.findViewById(R.id.button_check_outgoing_formula);
		mOutgoingValueFormulaDialogButton
				.setOnClickListener(outgoingValueFormulaListener);

		// �������� ���������� ���������� ��������
		ImageButton mOutgoingValidationDialogButton = (ImageButton) this
				.findViewById(R.id.button_check_validation_formula);
		mOutgoingValidationDialogButton
				.setOnClickListener(outgoingValueValidationListener);

		// �������� ���������� ���������
		ImageButton mOutgoingMessageDialogButton = (ImageButton) this
				.findViewById(R.id.button_check_outgoing_prefix);
		mOutgoingMessageDialogButton
				.setOnClickListener(outgoingMessageListener);
		
		// ����������� ������ ������ ����
		try {
			mEditMode = getIntent().getBooleanExtra("isEdited", false);
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������� ����� ������ ����");
			e.printStackTrace();
		}
				
		// ���������� ����� � ������ ��������������
		if (mEditMode) {		
			setFieldValues();
		}
	}

	
/*
	/**
	 * ��������� �������� ����� � ������ ��������������
	 */
	private void setFieldValues() {
		HashMap<String, String> pMap = mApplication.mTree.tempNode.paramsMap;
		
		if (pMap == null) return;
			
		// ��������� ��������
		EditText mHeaderText = (EditText) findViewById(R.id.header_text);
		if (pMap.get("HeaderText")!=null)
			mHeaderText.setText(pMap.get("HeaderText"));
				
		// ��������� ��������
		EditText mDescText = (EditText) findViewById(R.id.description_text);
		if (pMap.get("DescriptionText")!=null)
			mDescText.setText(pMap.get("DescriptionText"));
		
		// ��������� ��� ����� ����������� ��������
		EditText mInvalidValueText = (EditText) findViewById(R.id.error_text);
		if (pMap.get("InvalidValueText")!=null)
			mInvalidValueText.setText(pMap.get("InvalidValueText"));

		if (pMap.get("SelectedImage")!=null) {
			// ����� ����������� ��� ������ ����
			int pos = mImages.indexOf(pMap.get("SelectedImage"));
			
			// ���� ����������� ���� �������
			// ���������� ������ � �������,
			// ������������ ����������� � �����
			if (pos!=-1) {
				mGallery.setSelection(pos);
				Bitmap b = BitmapFactory.decodeFile(mImages.get(pos));
				mGalleryPicker.setImageBitmap(b);
				mGalleryPicker.setTag(mImages.get(pos));
			}
		}

		// ������� ��� ��������� ��������� ��������
		EditText mIncomingValueFormula = (EditText) findViewById(R.id.incoming_formula_text);
		if (pMap.get("IncomingValueFormula")!=null)
			mIncomingValueFormula.setText(pMap.get("IncomingValueFormula"));
				
		// ���������� ������ ����� �������
		EditText mFractionDigits = (EditText) findViewById(R.id.decimal_places_text);
		if (pMap.get("FractionDigits")!=null)
			mFractionDigits.setText(pMap.get("FractionDigits"));

		// ������� ��� ��������� ���������� ��������
		EditText mOutgoingValueFormula = (EditText) findViewById(R.id.outgoing_formula_text);
		if (pMap.get("OutgoingValueFormula")!=null)
			mOutgoingValueFormula.setText(pMap.get("OutgoingValueFormula"));

		// ������� ����� ��� ��������� ��������
		EditText mOutgoingValueValidation = (EditText) findViewById(R.id.validation_formula_text);
		if (pMap.get("OutgoingValueValidation")!=null)
			mOutgoingValueValidation.setText(pMap.get("OutgoingValueValidation"));
		
		// ������ �������� �������� �� �����������
		EditText mGiveMeValueMessage = (EditText) findViewById(R.id.get_value_text);
		if (pMap.get("GiveMeValueMessage")!=null)
			mGiveMeValueMessage.setText(pMap.get("GiveMeValueMessage"));
	
		// ������� ��� �������� ���������� ��������
		EditText mOutgoingValueMessage = (EditText) findViewById(R.id.outgoing_prefix_text);
		if (pMap.get("OutgoingValueMessage")!=null)
			mOutgoingValueMessage.setText(pMap.get("OutgoingValueMessage"));
	}
	
	/**
	 * �� ����� �� �������� �������, ��������������� imagepicker ������
	 */
	private OnItemClickListener galleryImageSelectListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Bitmap b = BitmapFactory.decodeFile(mImages.get(position));
			mGalleryPicker.setImageBitmap(b);
			mGalleryPicker.setTag(mImages.get(position));
		}
	};

	/**
	 * �������� ���������� ��������. ������� ��������������, ������� ��������� � �������
	 * ���������� � ������
	 */
	private OnClickListener outgoingMessageListener = new OnClickListener() {
		// �������� ������� ��� ��������� ��������� ��������
		@Override
		public void onClick(final View v) {
			EditText mOutgoingValueFormula = (EditText) findViewById(R.id.outgoing_formula_text);
			EditText mOutgoingValueValidation = (EditText) findViewById(R.id.validation_formula_text);
			EditText mOutgoingPrefix = (EditText) findViewById(R.id.outgoing_prefix_text);

			OutgoingMessageCheckDialog dialog = new OutgoingMessageCheckDialog(
					mOutgoingValueFormula.getText().toString(),
					mOutgoingValueValidation.getText().toString(),
					mOutgoingPrefix.getText().toString());

			dialog.show(getFragmentManager(), "Outgoing message check");
		}
	};

	/**
	 * ��������� ��� �������� ������� ��� ��������� ��������. �������� � ������
	 * ������� �������� ���������� �������� � ������� ���������
	 */
	private OnClickListener outgoingValueValidationListener = new OnClickListener() {
		// �������� ������� ��� ��������� ��������� ��������
		@Override
		public void onClick(final View v) {
			EditText mOutgoingValueFormula = (EditText) findViewById(R.id.outgoing_formula_text);
			EditText mOutgoingValueValidation = (EditText) findViewById(R.id.validation_formula_text);

			ValidationFormulaCheckDialog dialog = new ValidationFormulaCheckDialog(
					mOutgoingValueFormula.getText().toString(),
					mOutgoingValueValidation.getText().toString());

			dialog.show(getFragmentManager(), "Outgoing value validation check");
		}
	};

	/**
	 * ��������� ��� �������� ������� ��� ��������� ��������� ��������. �������
	 * ���������� �������� ���������� ������ ����� ������� ����� ���� �������
	 * ����� ������ � ��������� �������� � ����������� ������ ����� �������.
	 */
	private OnClickListener incomingValueFormulaListener = new OnClickListener() {
		// �������� ������� ��� ��������� ��������� ��������
		@Override
		public void onClick(final View v) {
			// ���� � ��������� ����������� ������ ����� �������
			EditText mFractionDigits = (EditText) findViewById(R.id.decimal_places_text);
			// �������� ���� ������� ��������� ��������� ��������
			EditText mIncomingValueFormula = (EditText) findViewById(R.id.incoming_formula_text);

			// ��������� ������ �������� �������. �������� �������� ���� �������
			// � ���������� ������
			FormulaCheckDialog dialog = new FormulaCheckDialog(
					mIncomingValueFormula.getText().toString(), mFractionDigits
							.getText().toString());
			dialog.show(getFragmentManager(), "Incoming value formula check");
		}
	};

	/**
	 * ��������� ��� �������� ������� ��� ��������� ���������� ��������. �������
	 * ������ � ��������� ��������. �.�. ��� ����� ����� ��������, �� ����������
	 * ������ ��������� ������� � 0
	 */
	private OnClickListener outgoingValueFormulaListener = new OnClickListener() {
		// �������� ������� ��� ��������� ��������� ��������
		@Override
		public void onClick(final View v) {
			// �������� ���� ������� ��������� ��������� ��������
			EditText mOutgoingValueFormula = (EditText) findViewById(R.id.outgoing_formula_text);

			// ��������� ������ �������� �������. �������� �������� ���� �������
			// � 0, �.�. ��� ����� ����� ��������
			FormulaCheckDialog dialog = new FormulaCheckDialog(
					mOutgoingValueFormula.getText().toString(), "0");
			dialog.show(getFragmentManager(), "Outgoing value formula check");
		}
	};

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
				tooltip = getResources().getString(R.string.header_text_tooltip);
				break;
			// ��������
			case R.id.button_help_description:
				tooltip = "�����, ������� ����� ������������ ��� ����������, ������ - ������";
				break;
			// ��������� �� ������ ���������
			case R.id.button_help_error:
				tooltip = "���������, ������� ������ ������������, ���� ��������� �������� �� ������� ���������, ������ - ������";
				break;
			// ������� ��� ��������� ��������� ��������
			case R.id.button_help_incoming_formula:
				tooltip = "�������� ��������� ��������. ���������� ��� ��������: x. ������ ����, ���� ��������� �� �����. �� ������������ ������� ���������! ��������� ��. ����������";
				break;
			// ���������� ������ ����� �������
			case R.id.button_help_decimal_places:
				tooltip = "���������� ������ ����� ������� � ������������ �������� ��������. �������� ���� ������ ��� ������ ��������";
				break;
			case R.id.button_help_outgoing_formula:
				tooltip = "�������� ���������� ��������. ���������� ��� ��������: x. ���������� �������� ����������� �� ������. ������ ����, ���� ��������� �� �����. �� ������������ ������� ���������! ��������� ��. ����������";
				break;
			case R.id.button_help_validation_formula:
				tooltip = "��������� ������������� �������� ��������. ���������� ��� ������������� ��������: x. ������ ����, ���� ��������� �� �����. ��������� ��. ����������";
				break;
			case R.id.button_help_get_value:
				tooltip = "���������, ������� ����� �������� ����������� ��� ������ ���� � ����������� ������� ������� �������� ������������ ��������. ��������� ���������� ��� ���������";
				break;
			case R.id.button_help_outgoing_prefix:
				tooltip = "��������� �� ���������, ������� ����� ������� �����������. ���������� ������ ������� ���������. ��� �������, ��������� ������� ���������� ��������� ��������, ��������� ���������� ������ � ������ ��������� <[�������],[���������� ������],[��������]>";
				break;
			default:
				tooltip = "���� �� ������ ��� ���������, �������� ������������ �� ������, ������ ��������, ��� ������� �� ������� ��� ���������";
				break;
			}
			// ����� Toast � ����������
			Notifications.showTooltip(mContext, tooltip);
		}
	};

	/**
	 * ���������� ����� � ������������ �������
	 */
	private ArrayList<String> getImages() {
		ArrayList<String> images = new ArrayList<String>();
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + Globals.EXTERNAL_ROOT_DIRECTORY + File.separator + Globals.EXTERNAL_IMAGES_DIRECTORY);

		if (file.isDirectory()) {
			File[] listFile = file.listFiles();
            for (File f : listFile) {
                images.add(f.getAbsolutePath());
            }
		}
		return images;
	}

	/**
	 * ������ ���������� ������ ������
	 */
	private void undoMenuItemAdding() {
		try {
			((MyApplication) getApplicationContext()).mProcessor.loadMenuTreeFromInternalStorage();
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ��������� ���� �� �����");
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� ������ ������ ����
	 */
	private boolean addNewMenuItem() {
		EditText mHeaderText = (EditText) findViewById(R.id.header_text);
		EditText mDescText = (EditText) findViewById(R.id.description_text);
		EditText mInvalidValueText = (EditText) findViewById(R.id.error_text);

		EditText mIncomingValueFormula = (EditText) findViewById(R.id.incoming_formula_text);
		EditText mFractionDigits = (EditText) findViewById(R.id.decimal_places_text);
		EditText mOutgoingValueFormula = (EditText) findViewById(R.id.outgoing_formula_text);
		EditText mOutgoingValueValidation = (EditText) findViewById(R.id.validation_formula_text);
		EditText mGiveMeValueMessage = (EditText) findViewById(R.id.get_value_text);
		EditText mOutgoingValueMessage = (EditText) findViewById(R.id.outgoing_prefix_text);

		ImageView mSelectedImage = (ImageView) findViewById(R.id.tile_image);

		// �������� ������������� ������������ �����
		// ������������ ����: 3 ������� � 2 ��������� + ��������
		if ((mHeaderText.getText().toString() == null) || (mHeaderText.getText().toString().trim().isEmpty())
				|| (mDescText.getText().toString() == null) || (mDescText.getText().toString().trim().isEmpty())
				|| (mInvalidValueText.getText().toString() == null) || (mInvalidValueText.getText().toString().trim().isEmpty())
				|| (mGiveMeValueMessage.getText().toString() == null) || (mGiveMeValueMessage.getText().toString().trim().isEmpty())
				|| (mOutgoingValueMessage.getText().toString() == null) || (mOutgoingValueMessage.getText().toString().trim().isEmpty())) {
			Notifications.showError(mContext,
					"�� ��������� ����������� ���� (��� �������� *)");
			return false;
		}

		// ��������� �������� (��������� � �������)
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

		// ������� ��������� ��������� ��� ��������� ��������
		mParamsMap.put("InvalidValueText", mInvalidValueText.getText()
				.toString());

		mParamsMap.put("IncomingValueFormula", mIncomingValueFormula.getText()
				.toString());

		mParamsMap.put("FractionDigits", mFractionDigits.getText().toString());

		mParamsMap.put("OutgoingValueFormula", mOutgoingValueFormula.getText()
				.toString());

		mParamsMap.put("OutgoingValueValidation", mOutgoingValueValidation
				.getText().toString());

		mParamsMap.put("GiveMeValueMessage", mGiveMeValueMessage.getText()
				.toString());

		mParamsMap.put("OutgoingValueMessage", mOutgoingValueMessage.getText()
				.toString());

		mParamsMap.put("SelectedImage", mSelectedImage.getTag().toString());

		// ���������� ����������
		mApplication.mTree.tempNode.paramsMap = mParamsMap;
				
		try {
			mApplication.mProcessor.saveMenuTreeToInternalStorage();
		} catch (Exception e) {
			Logging.v(getResources().getString(R.string.exception_reload_menu_tree));
			e.printStackTrace();
			return false;
		}

		return true;
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
									// ���������� ������ ����
									boolean menuItemAdded = addNewMenuItem();
									if (menuItemAdded)
										finish();
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

	/**
	 * ������ "��������"
	 */
	private OnClickListener mBackListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("�� ������������� ������ �����?")
					.setPositiveButton("�����",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// ������ ���������� �������
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