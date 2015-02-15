package com.isosystem.smarthouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.isosystem.smarthouse.logging.Logging;

import java.io.File;
import java.util.HashMap;

public class MainFormattedScreensAdapterGrid extends BaseAdapter {

	private Context mContext;
	private MyApplication mApplication;

	public MainFormattedScreensAdapterGrid(Context c) {
		mContext = c;
		mApplication = (MyApplication) c.getApplicationContext();
	}

	public int getCount() {
		return mApplication.mFormattedScreens.mFormattedScreens.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	View.OnClickListener mFormattedScreenListener(final int cnt) {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// 03.12 - тестирование динамического окна форм. вывода
					Intent intent = new Intent(mContext,
							FormattedScreensActivity.class);

					intent.putExtra("formScreenIndex", cnt);
					mContext.startActivity(intent);
					
					((Activity)mContext).overridePendingTransition(R.anim.flipin,R.anim.flipout);

				} catch (Exception e) {
					Logging.v("");
					e.printStackTrace();

					Intent i = new Intent(mContext.getApplicationContext(),
							MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mContext.startActivity(i);
				}
			}
		};
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mApplication);

		boolean square_tile = prefs.getBoolean(
				"formatted_screens_tile_align_height", true);
		int formatted_screens_tile_width = Integer.parseInt(prefs.getString(
				"formatted_screens_tile_width", "250"));
		int formatted_screens_tile_height = Integer.parseInt(prefs.getString(
				"formatted_screens_tile_height", "250"));
		int formatted_screens_tile_image_size = Integer.parseInt(prefs
				.getString("formatted_screens_tile_image_size", "96"));

		View v = convertView;
		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(this.mContext);
			v = vi.inflate(R.layout.formscreen_adapter_grid_item, null);

			// ”становка высоты и ширины плитки
			if (square_tile) {
				v.setLayoutParams(new GridView.LayoutParams(
						formatted_screens_tile_width,
						formatted_screens_tile_width));
			} else {
				v.setLayoutParams(new GridView.LayoutParams(
						formatted_screens_tile_width,
						formatted_screens_tile_height));
			}

			v.invalidate();
		}

		ImageView mImage = (ImageView) v
				.findViewById(R.id.formscreen_adapter_left_image);

		// Ѕерем хеш-таблицу параметров узла
		HashMap<String, String> pMap = mApplication.mFormattedScreens.mFormattedScreens
				.get(position).paramsMap;

		if (pMap.containsKey("GridImage")) {
			File imageFile = new File(pMap.get("GridImage"));

			if (imageFile.exists()) {
				mImage.setImageBitmap(BitmapFactory.decodeFile(imageFile
						.getAbsolutePath()));
			} else {
				FrameLayout frm = (FrameLayout) v
						.findViewById(R.id.FrameLayout1);
				frm.setVisibility(View.GONE);
			}
		} else {
			FrameLayout frm = (FrameLayout) v.findViewById(R.id.FrameLayout1);
			frm.setVisibility(View.GONE);
		}

		mImage.getLayoutParams().width = formatted_screens_tile_image_size;
		mImage.getLayoutParams().height = formatted_screens_tile_image_size;

		Typeface font = Typeface.createFromAsset(mContext.getAssets(),
				"russo.ttf");

		int formatted_screens_tile_label_size = Integer.parseInt(prefs
				.getString("formatted_screens_tile_label_size", "21"));

		TextView mTitle = (TextView) v
				.findViewById(R.id.formscreen_adapter_title);
		mTitle.setTypeface(font);
		mTitle.setTextSize(formatted_screens_tile_label_size);
		mTitle.setTextColor(Color.parseColor("#ffffff"));
		mTitle.setText(mApplication.mFormattedScreens.mFormattedScreens
				.get(position).mName);

		v.setOnClickListener(mFormattedScreenListener(position));
		return v;
	}
}