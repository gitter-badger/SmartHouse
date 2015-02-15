package com.isosystem.smarthouse.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.isosystem.smarthouse.data.MenuTree;
import com.isosystem.smarthouse.data.MenuTree.MenuScreenType;
import com.isosystem.smarthouse.data.MenuTree.NodeType;
import com.isosystem.smarthouse.data.MenuTreeNode;
import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ����� ������� ��������� ���� ���������� ������ ������ ����
 */
public class AddMenuItemActivity extends Activity implements
		RadioGroup.OnCheckedChangeListener {

	/** ������� ��� ������ ����, ���� ��������� ���� */
	Spinner menuSpinner;
	/** ������� ��� ���� �������� ����� */
	Spinner mScreenTypeSpinner;

	MyApplication mApplication;
	Context mContext;
	
	Gallery mGallery;
	ArrayList<String> mImages = null;
	ImageView mGalleryPicker; //����� ����������� �������
	
	/** ����� �������������� */
	boolean mEditMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_mainmenu_add);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mContext = this;
		mApplication = (MyApplication) getApplicationContext();

		/** ���������� ����� ������ ���� (�������� ��� ��������������) */
		try {
			mEditMode = getIntent().getBooleanExtra("isEdited", false);
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������� ����� ������ ����");
			e.printStackTrace();
		}
		
		/** ������ ����� */
		Button mReturnButton = (Button) findViewById(R.id.btn_cancel);
		mReturnButton.setOnClickListener(mReturnListener);

		/** ������ �������� */
		Button mAddButton = (Button) findViewById(R.id.btn_ok);
		mAddButton.setOnClickListener(mAddListener);

		// ����������� ��������� �������� �� �������
		mGalleryPicker = (ImageView) findViewById(R.id.point_image);
		mGalleryPicker.setTag("");

		// ������� ����������� ��� ������
		mGallery = (Gallery) findViewById(R.id.point_image_gallery);
		mImages = getImages();
		mGallery.setAdapter(new GalleryAdapter(mImages, this));
		mGallery.setOnItemClickListener(galleryImageSelectListener);
		
		/** ������� � ������� ����� ���� */
		menuSpinner = (Spinner) findViewById(R.id.add_to_menu_spinner);
		if (mEditMode) {
			GenerateSpinner();
		} else {			
			View divider = findViewById(R.id.divider_add_to_menu);
			divider.setVisibility(View.GONE);
			TextView tv = (TextView) findViewById(R.id.add_to_menu_label);
			tv.setVisibility(View.GONE);
			menuSpinner.setVisibility(View.GONE);
		}

		/** ������� � ������� ���� �������� ����� */
		mScreenTypeSpinner = (Spinner) findViewById(R.id.point_type_spinner);
		GenerateScreenTypeSpinner();

		/** ����������� � ����������� */
		RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		mRadioGroup.setOnCheckedChangeListener(this);
		RadioButton rButton = (RadioButton) findViewById(R.id.radio0);
		
		if (rButton.isChecked()) {
			mScreenTypeSpinner.setEnabled(false);
			mScreenTypeSpinner.setClickable(false);
		} else {
			mScreenTypeSpinner.setEnabled(true);
			mScreenTypeSpinner.setClickable(true);
		}
		
		// ���������� �������� � ������ ��������� ������
		mScreenTypeSpinner.invalidate();
		
		/**
		 * ���� ����� ��������������, ����� ��������� ��� ���� ������� ��
		 * ������������� ����
		 */
		if (mEditMode) {
			// �������������� ����
			setFieldValues();
		} else {
			// �������� ����
			mApplication.mTree.tempNode = new MenuTreeNode(false);
		}
	}

	/**
	 * ��������� ���� ���� ������� �� ������������� ���� ���� ��������� � ������
	 * ��������������
	 */
	private void setFieldValues() {
		/** ��������� ��� ������ */
		EditText et1 = (EditText) findViewById(R.id.name_value);
		et1.setText(mApplication.mTree.tempNode.nodeTitle);

		/** �������� ����� ���� � ������ */
		try {
			// �������� �������������� ����
			int position = mApplication.mTree
					.GetSpinnerPositionForNode(mApplication.mTree.tempNode.parentNode);
			menuSpinner.setSelection(position);
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������� ����� �������������� ���� � ������");
			e.printStackTrace();
		}

		/**
		 * ����� ���� ���� (����, ����). ���� ���� - ����, ����� ���� ���������
		 * ����
		 */
		try {
			if (mApplication.mTree.tempNode.nodeType == NodeType.NODE_MENU) {
				RadioButton rButton = (RadioButton) findViewById(R.id.radio0);
				rButton.setChecked(true);
				onCheckedChanged(null, R.id.radio0);
				// ����� ���� ��������� ����
			} else if (mApplication.mTree.tempNode.nodeType == NodeType.NODE_LEAF) {
				RadioButton rButton = (RadioButton) findViewById(R.id.radio1);
				rButton.setChecked(true);
				onCheckedChanged(null, R.id.radio1);
				mScreenTypeSpinner.setSelection(mApplication.mTree.tempNode.screenType.ordinal());
			} else {
				Logging.v("��� ������� ����������� ��� ���� (�� ���� � �� ����)");
			}
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������� ��� ���� � ������� ��� �����");
			e.printStackTrace();
		} // try
		
		HashMap<String, String> pMap = mApplication.mTree.tempNode.paramsMap;
		
		if (pMap != null) {
			if (pMap.get("GridImage")!=null) {
				// ����� ����������� ��� ������ ����
				int pos = mImages.indexOf(pMap.get("GridImage"));
				
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
		} // if !null
	}// method
	
	/**
	 * ���������� ������� ��� ������ ������ ����, ���� ��������� ����� �����
	 */
	private void GenerateSpinner() {
		menuSpinner.setAdapter(null);

		ArrayList<String> menuNodes;

		/**
		 * ���� ���� ��������� � ������ ��������������, ���������� ��������
		 * ������ ����� ��� �������� �������������� ���� � ��� ��������
		 */
		if (mEditMode) {
			menuNodes = mApplication.mTree.getMenuNodes(mApplication.mTree.tempNode);
		} else {
			menuNodes = mApplication.mTree.getMenuNodes();
		}

		ArrayAdapter<String> ad = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, menuNodes);
		ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		menuSpinner.setAdapter(ad);
		ad.notifyDataSetChanged();
	}
	
	/**
	 * ���������� ������� ��� ������ ���� ��������� ����
	 */
	private void GenerateScreenTypeSpinner() {

		mScreenTypeSpinner.setAdapter(null);

		// �������� ������ ����� ����
		ArrayList<String> screenTypes = new ArrayList<String>();
		for (MenuTree.MenuScreenType type : MenuTree.MenuScreenType.values()) {
			screenTypes.add(type.toString());
		}

		ArrayAdapter<String> ad = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, screenTypes);
		mScreenTypeSpinner.setAdapter(ad);
		ad.notifyDataSetChanged();
	}

	/** 
	 * ��������� ������� �� ����������� 
	 */
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (checkedId == R.id.radio0) {
			mScreenTypeSpinner.setEnabled(false);
			mScreenTypeSpinner.setClickable(false);
		} else if (checkedId == R.id.radio1) {
			mScreenTypeSpinner.setEnabled(true);
			mScreenTypeSpinner.setClickable(true);
		}
		mScreenTypeSpinner.invalidate();
	}

	/**
	 * ������ "�����"
	 */
	private OnClickListener mReturnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("�� ������������� ������ �����?")
					.setPositiveButton("�����",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									
									//������� tempNode
									mApplication.mTree.tempNode = null;
									mApplication.mTree.tempParentNode = null;
									
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

	/**
	 * �������� ����� �� ����������
	 * @return TRUE ���� ���� ��������� ���������
	 */
	private Boolean validateFields() {
		// ��� ������ ����
		EditText menuItemName = (EditText) findViewById(R.id.name_value);
		// ����� - ����
		RadioButton typeMenu = (RadioButton) findViewById(R.id.radio0);
		// ����� - �������� �����
		RadioButton typePoint = (RadioButton) findViewById(R.id.radio1);
		
		// ������� ����� ������ ����
		String itemName = menuItemName.getText().toString();
		if (TextUtils.isEmpty(itemName) ||
				 (itemName.trim().length() == 0)) {
			menuItemName.requestFocus();
			Toast.makeText(mContext, "��� ������ ���� ������� �����������!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		// �������� �� ����� ��������
		if(mEditMode) {
			if (menuSpinner.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
				Toast.makeText(mContext, "�� ������� ����� ���������� ������ ����", Toast.LENGTH_SHORT).show();
				return false; 
			}
		} else {
			if (mApplication.mTree.tempParentNode == null) {
				Notifications.showError(mContext, "����������� ������ (������������ ���� �� ����������)");
				return false;
			}
		}

		// �������� �� ��� ������ ����
		if ((!typeMenu.isChecked()) && (!typePoint.isChecked())) {
			Toast.makeText(mContext, "�� ������ ��� ������ ����.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		// �������� �� ����� ���� ��������� ������
		if ((mScreenTypeSpinner.getSelectedItemPosition() == AdapterView.INVALID_POSITION)
				&& (typePoint.isChecked())) {
			Toast.makeText(mContext, "�� ������ ��� ��������� ������ ����.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
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
	 * ��������� ������� ������ "�����������" ���������� ����������: 1) ��� ��
	 * ������ ���� ��������� (��������, ���, ���� ��������) 2) ���� ���
	 * "����� ����", ����� �������� � ������ ����� ����� � �������� ������
	 */
	private OnClickListener mAddListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// ���� ���� �� ������ ���������
			if (!validateFields()) return;
			
			// ��� ������ ����
			EditText menuItemName = (EditText) findViewById(R.id.name_value);
			// ����� - ����
			RadioButton typeMenu = (RadioButton) findViewById(R.id.radio0);
			// ����� - �������� �����
			RadioButton typePoint = (RadioButton) findViewById(R.id.radio1);
			
			// �������� ������ ����
			mApplication.mTree.tempNode.nodeTitle = menuItemName.getText().toString();
						
			// ������������� ��� ������ ����
			if (typeMenu.isChecked()) {
				// ���� ������� ����
				
				// ���� ������������ ������ ����, � ���� �������� �����
				if (mApplication.mTree.tempNode.nodeType == MenuTree.NodeType.NODE_LEAF) {
					mApplication.mTree.tempNode.childNodes = new ArrayList<MenuTreeNode>();
					mApplication.mTree.tempNode.paramsMap = new HashMap<String, String>();
				}
				mApplication.mTree.tempNode.nodeType = MenuTree.NodeType.NODE_MENU;
			} else if (typePoint.isChecked()) {
				
				// ���� ������������ ������ �������� �����, � ���� ����
				if (mApplication.mTree.tempNode.nodeType == MenuTree.NodeType.NODE_MENU) {
					mApplication.mTree.tempNode.childNodes = new ArrayList<MenuTreeNode>();
					mApplication.mTree.tempNode.paramsMap = new HashMap<String, String>();
				}
				
				mApplication.mTree.tempNode.nodeType = MenuTree.NodeType.NODE_LEAF;
			}
			
			ImageView mSelectedImage = (ImageView) findViewById(R.id.point_image);

			// ��������� �������� (��������� � �������)
			HashMap<String, String> mParamsMap = mApplication.mTree.tempNode.paramsMap;
			if (mParamsMap != null) {
				mParamsMap.put("GridImage", mSelectedImage.getTag().toString());
			} else {
				mParamsMap = new HashMap<String, String>();
				mParamsMap.put("GridImage", mSelectedImage.getTag().toString());
			}		
			mApplication.mTree.tempNode.paramsMap = mParamsMap;
			
			MenuTreeNode parentNode;
			
			if (mEditMode) {
				parentNode = mApplication.mTree
						.GetNodeForSpinnerPosition(menuSpinner
								.getSelectedItemPosition());
			} else {
				parentNode = mApplication.mTree.tempParentNode;
			}
									
			// ��������� ���� ��� ��������� ������
			if (mApplication.mTree.tempNode.nodeType == MenuTree.NodeType.NODE_MENU) { // ����
				
				// ���� ����� �������������� - ����� ������� ����� �� ������ ���������
				if (mEditMode) {
					mApplication.mTree.tempNode.parentNode.childNodes.remove(mApplication.mTree.tempNode);
					mApplication.mTree.tempNode.parentNode = null;
				}
								
				// �������� ����� ����� ����� � ���������
				parentNode.childNodes.add(mApplication.mTree.tempNode);
				mApplication.mTree.tempNode.parentNode = parentNode;
				
				// ���������� ������
				try {
					mApplication.mProcessor.saveMenuTreeToInternalStorage();
				} catch (Exception e) {
					Logging.v(getResources().getString(R.string.exception_reload_menu_tree));
					e.printStackTrace();
				}

				((Activity) mContext).finish();
			} else if (mApplication.mTree.tempNode.nodeType == MenuTree.NodeType.NODE_LEAF) { // �������� �����				
				
				// �������� ������ ���� � �����������
				Intent intent = new Intent();
				intent.putExtra("isEdited", mEditMode);
				
				if (mEditMode) {
					mApplication.mTree.tempNode.parentNode.childNodes.remove(mApplication.mTree.tempNode);
					mApplication.mTree.tempNode.parentNode = null;
				}
				
				// �������� ����� ����� ����� � ���������
				parentNode.childNodes.add(mApplication.mTree.tempNode);
				mApplication.mTree.tempNode.parentNode = parentNode;
								
				// ��� ��������� ����
				int position = mScreenTypeSpinner.getSelectedItemPosition();

				if (position == 0) { // ���� ������� ���������� ��������
					mApplication.mTree.tempNode.screenType = MenuScreenType.SetIntValue;
					intent.setClass(mContext, AddMenuItemSendValue.class);
				} else if (position == 1) { // ���� ������
					mApplication.mTree.tempNode.screenType = MenuScreenType.SetPasswordValue;
					intent.setClass(mContext, AddMenuItemSendPassword.class);
				} else if (position == 2) { // ���� �������� ��������
					mApplication.mTree.tempNode.screenType = MenuScreenType.SetBooleanValue;	
					intent.setClass(mContext, AddMenuItemSendBool.class);
				} else if (position == 3) {
					mApplication.mTree.tempNode.screenType = MenuScreenType.SendMessage;	
					intent.setClass(mContext, AddMenuItemSendMessage.class);
				}
				mContext.startActivity(intent);
			}
		} // onClick
	}; // addListener
}
