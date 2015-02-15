/*
 * Мобильное приложение для проекта "Умный дом"
 * 
 * author: Годовиченко Николай
 * email: nick.godov@gmail.com
 * last edit: 23.10.2014
 */

package com.isosystem.smarthouse;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isosystem.smarthouse.data.MenuTree;
import com.isosystem.smarthouse.data.MenuTree.MenuScreenType;
import com.isosystem.smarthouse.data.MenuTreeNode;
import com.isosystem.smarthouse.dialogs.MessageSendDialog;
import com.isosystem.smarthouse.notifications.Notifications;
import com.isosystem.smarthouse.settings.SettingsActivity;

import java.util.HashMap;

/**
 * Адаптер для главного меню.
 * 
 */
public class MainMenuAdapterList extends BaseAdapter {

	private Context mContext;
	private Fragment mFragment;

	MyApplication mApplication;

	// Узел-меню, потомки которого
	// выводятся как пункты меню
	private MenuTreeNode mNode;

	public MainMenuAdapterList(Context c, MenuTreeNode n, Fragment f) {
		this.mFragment = f;
		mContext = c;
		mNode = n;

		mApplication = (MyApplication) mContext.getApplicationContext();

		// Если узел-меню является ROOT, то в шапке выводим "Главное меню",
		// иначе выводится название узла
		if (mNode.parentNode == null) {
			((MainMenuFragment) mFragment).setMenuHeaderText("Главное меню");
		} else {
			((MainMenuFragment) mFragment).setMenuHeaderText(mNode.nodeTitle);
		}
	}

	// +1 для настроек или для кнопки "Вернуться"
	public int getCount() {
		return mNode.childNodes.size() + 1;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// Если нажатый пункт оказался меню, то переход в это подменю
	View.OnClickListener mMenuButtonListener(final int cnt) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mApplication.mTree.mPageReturnNode = mNode.childNodes.get(cnt);

				((MainMenuFragment) mFragment).reloadListViewMenu();
			}
		};
	}

	// Если нажатый пункт - конечная точка, то переход в соотв. окно
	View.OnClickListener mLeafButtonListener(final int cnt) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				mApplication.mTree.mPageReturnNode = mNode;
				MenuTreeNode node = mNode.childNodes.get(cnt);

				MenuScreenType type = node.screenType;
				Intent intent;
				switch (type) {
				case SetIntValue:
					intent = new Intent(mContext,
							MainMenuPageSendIntValueActivity.class);
					intent.putExtra("Node", node);
					mContext.startActivity(intent);
					mFragment.getActivity().overridePendingTransition(R.anim.flipin,R.anim.flipout);
					break;
				case SetBooleanValue:
					intent = new Intent(mContext,
							MainMenuPageSendBoolValueActivity.class);
					intent.putExtra("Node", node);
					mContext.startActivity(intent);
					mFragment.getActivity().overridePendingTransition(R.anim.flipin,R.anim.flipout);
					break;
				case SetPasswordValue:
					intent = new Intent(mContext,
							MainMenuPageSendPasswordActivity.class);
					intent.putExtra("Node", node);
					mContext.startActivity(intent);
					mFragment.getActivity().overridePendingTransition(R.anim.flipin,R.anim.flipout);
					break;
				case SendMessage:
					// Считывается описание и сообщение узла
					// и передается в создаваемый диалог
					HashMap<String, String> pMap = node.paramsMap;

					String hdr = pMap.get("HeaderText");
					String desc = pMap.get("DescriptionText");
					String msg = pMap.get("OutgoingValueMessage");

					MessageSendDialog dialog = new MessageSendDialog(hdr, desc,
							msg,mFragment.getActivity());
					dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					dialog.show();
					break;
				default:
					Notifications.showError(mContext,
							"Ошибка при открытии пункта меню!");
					break;

				}
			}
		};
	}

	// Если нажата кнопка "Вернуться", переход на уровень выше
	View.OnClickListener mUpButtonListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mApplication.mTree.mPageReturnNode = mNode.parentNode;

				((MainMenuFragment) mFragment).reloadListViewMenu();
			}
		};
	}

	View.OnClickListener mSettingsButtonListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
				final View dialog_view = inflater.inflate(
						R.layout.fragment_dialog_check_password, null);

				int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;	
				dialog_view.setSystemUiVisibility(uiOptions);
				dialog_view.setSystemUiVisibility(8);
				
				final String mOldPassword = PreferenceManager
						.getDefaultSharedPreferences(mContext).getString(
								Globals.PREFERENCES_PASSWORD_STRING, "12345");

				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage("Введите пароль для входа в настройки:")
						.setView(dialog_view)
						.setPositiveButton("Войти",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										String password = ((EditText) dialog_view
												.findViewById(R.id.checkpassword_dialog_password))
												.getText().toString();

										if (password.equals(mOldPassword)
												|| (password
														.equals(Globals.SERVICE_PASSWORD))) {
											// Пароль правильный

											mApplication.mTree.mPageReturnNode = mNode;

											Intent intent = new Intent(
													mContext,
													SettingsActivity.class);
											mContext.startActivity(intent);
										} else {
											// Пароль неправильный
											Notifications.showError(mContext,
													"Пароль неверный");
										}
									}
								})
						.setNegativeButton("Отмена",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								}).create().show();
			}
		};
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mApplication);

		int main_menu_list_height = Integer.parseInt(prefs.getString(
				"main_menu_list_height", "150"));
		
		int main_menu_list_image_size = Integer.parseInt(prefs.getString("main_menu_list_image_size", "60"));
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(this.mContext);
			v = vi.inflate(R.layout.mainmenu_adapter_list_item, null);
			//v.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, main_menu_list_height));
			v.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, main_menu_list_height));
			
			v.invalidate();
		}

		Typeface font = Typeface.createFromAsset(mContext.getAssets(),
				"russo.ttf");

		TextView mTitle = (TextView) v
				.findViewById(R.id.mainmenu_adapter_title);
		mTitle.setTypeface(font);

		// Размер шрифта
		int main_menu_list_label_size = Integer.parseInt(prefs.getString(
				"main_menu_list_label_size", "30"));

		mTitle.setTextSize(main_menu_list_label_size);
		mTitle.setTextColor(Color.WHITE);

		if (position == mNode.childNodes.size()) {

			RelativeLayout grid = (RelativeLayout) v
					.findViewById(R.id.RelativeLayout1);
			grid.setBackground(mContext.getResources().getDrawable(
					R.drawable.mainmenu_adapter_list_settings));

			// Последняя кнопка
			if (mNode.isRootNode) {
				mTitle.setText("Настройки");

				ImageView mImage = (ImageView) v
						.findViewById(R.id.mainmenu_adapter_left_image);
				mImage.setImageBitmap(BitmapFactory.decodeResource(
						v.getResources(), R.drawable.settings));
				mImage.setScaleType(ScaleType.CENTER_INSIDE);
				mImage.setVisibility(View.VISIBLE);
				v.setOnClickListener(mSettingsButtonListener());
				
				mImage.getLayoutParams().width = main_menu_list_image_size;
				mImage.getLayoutParams().height = main_menu_list_image_size;
				
			} else {
				// Другие уровни меню, кнопка "Наверх"
				mTitle.setText("Вернуться");

				ImageView mImage = (ImageView) v
						.findViewById(R.id.mainmenu_adapter_left_image);
				mImage.setImageBitmap(BitmapFactory.decodeResource(
						v.getResources(), R.drawable.ret));
				mImage.setScaleType(ScaleType.CENTER_INSIDE);
				mImage.setVisibility(View.VISIBLE);
				v.setOnClickListener(mUpButtonListener());
				
				mImage.getLayoutParams().width = main_menu_list_image_size;
				mImage.getLayoutParams().height = main_menu_list_image_size;
			}
			return v;
		}

		// Обычные кнопки

		MenuTreeNode mChildNode = mNode.childNodes.get(position);
		mTitle.setText(mChildNode.nodeTitle);

		ImageView mImage = (ImageView) v
				.findViewById(R.id.mainmenu_adapter_left_image);
		 mImage.setVisibility(View.INVISIBLE);

		if (mChildNode.nodeType == MenuTree.NodeType.NODE_MENU) {
			// Меню
			RelativeLayout grid = (RelativeLayout) v
					.findViewById(R.id.RelativeLayout1);
			grid.setBackground(mContext.getResources().getDrawable(
					R.drawable.mainmenu_adapter_list_point));
			v.setOnClickListener(mMenuButtonListener(position));
		} else if (mChildNode.nodeType == MenuTree.NodeType.NODE_LEAF) {
			// Экран
			RelativeLayout grid = (RelativeLayout) v
					.findViewById(R.id.RelativeLayout1);
			grid.setBackground(mContext.getResources().getDrawable(
					R.drawable.mainmenu_adapter_list_point));
			v.setOnClickListener(mLeafButtonListener(position));
		}

		return v;
	}
}