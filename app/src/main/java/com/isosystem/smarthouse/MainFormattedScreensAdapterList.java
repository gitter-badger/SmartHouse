package com.isosystem.smarthouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.isosystem.smarthouse.logging.Logging;

public class MainFormattedScreensAdapterList extends BaseAdapter {

	private Context mContext;
	private MyApplication mApplication;

	public MainFormattedScreensAdapterList(Context c) {
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

		int formatted_screens_list_height = Integer.parseInt(prefs.getString(
				"formatted_screens_list_height", "150"));
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(this.mContext);
			v = vi.inflate(R.layout.formscreen_adapter_list_item, null);
			
			v.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, formatted_screens_list_height));
		}

		// Размер шрифта
		int main_menu_list_label_size = Integer.parseInt(prefs.getString(
				"main_menu_list_label_size", "30"));
		
		Typeface font = Typeface.createFromAsset(mContext.getAssets(),
				"russo.ttf");

		TextView mTitle = (TextView) v
				.findViewById(R.id.formscreen_adapter_title);
		mTitle.setTypeface(font);
		mTitle.setTextSize(main_menu_list_label_size);
		mTitle.setTextColor(Color.parseColor("#ffffff"));
		mTitle.setText(mApplication.mFormattedScreens.mFormattedScreens
				.get(position).mName);

		v.setOnClickListener(mFormattedScreenListener(position));
		return v;
	}
}