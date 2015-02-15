package com.isosystem.smarthouse.settings;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

import com.isosystem.smarthouse.Globals;
import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.MyApplication;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AddFormattedScreenActivity extends Activity {

	MyApplication mApplication;
	Context mContext;

	// ����� ��������������
	boolean mEditMode;
	// ������� �������������� ����
	int mEditedPosition;
	
	Gallery mGallery;
	ArrayList<String> mImages = null;
	ImageView mGalleryPicker; //����� ����������� �������
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_formscreen_add);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mContext = this;
		mApplication = (MyApplication) mContext.getApplicationContext();

		/** ���������� ����� ������ ���� (�������� ��� ��������������) */
		try {
			mEditMode = getIntent().getBooleanExtra("isEdited", false);
		} catch (Exception e) {
			Logging.v("���������� ��� ������� ������� ����� ������ ����");
			e.printStackTrace();
		}
		
		Button mAddButton = (Button) findViewById(R.id.btn_ok);
		mAddButton.setOnClickListener(mAddListener);
		
		Button mReturnButton = (Button) findViewById(R.id.btn_cancel);
		mReturnButton.setOnClickListener(mReturnListener);
		
		// ����������� ��������� �������� �� �������
		mGalleryPicker = (ImageView) findViewById(R.id.tile_image);
		mGalleryPicker.setTag("");

		// ������� ����������� ��� ������
		mGallery = (Gallery) findViewById(R.id.tile_image_gallery);
		mImages = getImages();
		mGallery.setAdapter(new GalleryAdapter(mImages, this));
		mGallery.setOnItemClickListener(galleryImageSelectListener);
		
		// � ������ ��������������, ��������� ������� �������������� ����
		if (mEditMode) {
			try {
				mEditedPosition = getIntent().getIntExtra("edited_screen_position", -1);
			} catch (Exception e) {
				Logging.v("���������� ��� ������� ������� ����� ������ ����");
				e.printStackTrace();
			}
		}
		
		// ���� �� ������� ������� ������� �������������� ����
		if (mEditedPosition == -1) {
			Notifications.showError(mContext, "������ ��� ������� ��������������� ���� ���������������� ������");
			this.finish();
		}

		// � ������ �������������� ���������� ����� � �������
		if (mEditMode) {
			setFieldValues();
		}
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
	 * ���������� ����� � ������ ��������������
	 */
	private void setFieldValues() {
		EditText etTitle = (EditText) findViewById(R.id.fs_name_value);
		etTitle.setText(mApplication.mFormattedScreens.mFormattedScreens.get(mEditedPosition).mName);
		
		EditText etTransferBegin = (EditText) findViewById(R.id.fs_start_value);
		etTransferBegin.setText(mApplication.mFormattedScreens.mFormattedScreens.get(mEditedPosition).mInputStart);

		EditText etTransferEnd = (EditText) findViewById(R.id.fs_end_value);
		etTransferEnd.setText(mApplication.mFormattedScreens.mFormattedScreens.get(mEditedPosition).mInputEnd);
		
		HashMap<String, String> pMap = mApplication.mFormattedScreens.mFormattedScreens.get(mEditedPosition).paramsMap;
		
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
	}

	private OnClickListener mReturnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	/**
	 * ��������� ������� ������ "�����������"
	 */
	private OnClickListener mAddListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			EditText etTitle = (EditText) findViewById(R.id.fs_name_value);
			EditText etTransferBegin = (EditText) findViewById(R.id.fs_start_value);
			EditText etTransferEnd = (EditText) findViewById(R.id.fs_end_value);

			if ((TextUtils.isEmpty(etTitle.getText().toString().trim()))
					|| (TextUtils.isEmpty(etTransferBegin.getText().toString().trim()))
					|| (TextUtils.isEmpty(etTransferEnd.getText().toString().trim()))
					|| (mGalleryPicker.getDrawable() == null) || (mGalleryPicker.getDrawable().toString().trim().isEmpty())) {
				Notifications.showError(mContext, "�� ��� ���� ���������");
				return;
			} else {
				String name = etTitle.getText().toString();
				String start = etTransferBegin.getText().toString();
				String end = etTransferEnd.getText().toString();

				HashMap<String, String> mParamsMap = new HashMap<String, String>();
				mParamsMap.put("GridImage", mGalleryPicker.getTag().toString());

				// � ������ �������������� ������ ���� � ������������� ����
				// ����� ������� ����� ����
				if (mEditMode) {
					mApplication.mFormattedScreens.changeFormattedScreen(mContext, mEditedPosition, name, start, end, mParamsMap);
				} else {
					mApplication.mFormattedScreens.addFormattedScreen(mContext, name, start, end, mParamsMap);
				}
			}
			finish();
		}

	};

}
