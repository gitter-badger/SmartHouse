/*
 * Мобильное приложение для проекта "Умный дом"
 * 
 * author: Годовиченко Николай
 * email: nick.godov@gmail.com
 * last edit: 07.12.2014
 */

package com.isosystem.smarthouse;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.isosystem.smarthouse.logging.Logging;
import com.isosystem.smarthouse.utils.ScreenDimActivity;
import com.isosystem.smarthouse.utils.ScreenSaverActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	MyApplication mApplication;
	Activity mThisActivity;

	// Для хранителя экрана, счетчик бездействия
	Handler mScreenSaverHandler;

	// Использовать ли хранитель экрана
	Boolean mUseScreenSaver = false;
		
	// Время бездействия (секунды)
	int mScreenSaverIdleTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mThisActivity = this;
		mApplication = (MyApplication) getApplicationContext();
		
		// Запрет на отключение экрана
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		loadExternalData();
		
		// Включение полноэкранного режим планшета
        if (getActionBar() != null) {
            getActionBar().hide();
        }
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		decorView.setSystemUiVisibility(8);
		// <<-----------------------------------
		
		setContentView(R.layout.activity_main);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// Страницы пейджера:
		// 0) Дерево меню
		// 1) Меню сообщений
		// 2) Меню окон форматированного вывода
		// Дефолтная страница пейджера - 0 *1* 2
		mViewPager.setCurrentItem(1);
	}
		
	private void loadExternalData() {
		// Загружаем дерево меню
		
		if (!mApplication.mProcessor.loadMenuTreeFromInternalStorage()) {
			Logging.v("Загрузка меню из внутреннего хранилища прошла с ошибкой");
		}
		
//		try {
//			mApplication.mAlarmMessages = mApplication.mProcessor.deserializeAlarmMessages();
//		} catch (Exception e) {
//			Logging.v("Исключение при попытке считать алармовые сообщения из sdcard");
//			e.printStackTrace();
//		}
		
 //Убрано 04.02 - проверка ошибки записи во внутренний файл когда программа не запущена
//		if (!mApplication.mProcessor.loadMessagesFromInternalStorage()) {
//			Logging.v("Загрузка сообщений из внутреннего хранилища прошла с ошибкой");
//		}
		
		if (!mApplication.mProcessor.loadFscreensFromInternalStorage()) {
			Logging.v("Загрузка окон вывода из внутреннего хранилища прошла с ошибкой");
		}
	}
	
	@Override
	protected void onStart() {	
		checkExternalDirectoryStructure();
		super.onStart();
	}

	private void checkExternalDirectoryStructure() {
		String state = Environment.getExternalStorageState();
		
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			File externalFilesDir = Environment.getExternalStorageDirectory();
			
			File externalRootDirectory = new File(externalFilesDir + File.separator
					+ Globals.EXTERNAL_ROOT_DIRECTORY);
			externalRootDirectory.mkdirs();
			
			File externalImagesDirectory = new File(externalFilesDir
					+ File.separator + Globals.EXTERNAL_ROOT_DIRECTORY
					+ File.separator + Globals.EXTERNAL_IMAGES_DIRECTORY);
			externalImagesDirectory.mkdirs();
			copyImagesFromAssetsToExternalDirectory();
			
			File externalLogsDirectory = new File(externalFilesDir + File.separator
					+ Globals.EXTERNAL_ROOT_DIRECTORY + File.separator
					+ Globals.EXTERNAL_LOGS_DIRECTORY);
			externalLogsDirectory.mkdirs();

			File externalSSDirectory = new File(externalFilesDir + File.separator
					+ Globals.EXTERNAL_ROOT_DIRECTORY + File.separator
					+ Globals.EXTERNAL_SCREENSAVER_IMAGES_DIRECTORY);
			externalSSDirectory.mkdirs();
		}
	}
	
	/**
	 * Данный метод копирует несколько изображений из папки assets/imgs в папку
	 * images Изображения используются для галереи при добавлении окон
	 * управления. Т.к. изображение является обязательным полем для добавления
	 * окна, необходимо предоставить несколько изображений и проверять их
	 * наличие при запуске программы
	 *
	 */
	private void copyImagesFromAssetsToExternalDirectory() {

		AssetManager assetManager = getAssets();

		// Список изображений в assets/imgs
		String[] imagesFilesList = null;

		// Получаем список файлов
		try {
			imagesFilesList = assetManager
					.list(Globals.ASSETS_IMAGES_DIRECTORY);
		} catch (IOException e) {
			Logging.v(">>(Exception)<<. Исключение при получении списка изображений в папке assets/imgs");
			e.printStackTrace();
		}

        if (imagesFilesList == null) {
            return;
        }

		// Путь к изображениям на внешнем хранилище
		String imagesExternalDirectory = Environment
				.getExternalStorageDirectory()
				+ File.separator
				+ Globals.EXTERNAL_ROOT_DIRECTORY
				+ File.separator
				+ Globals.EXTERNAL_IMAGES_DIRECTORY;

		// Копируем каждое найденное изображение из assets/imgs в images
		for (String imageFile : imagesFilesList) {
			InputStream inputStream = null;
			OutputStream outputStream = null;

			// Пытаемся копировать
			try {

				// Новый файл, куда будет писаться файл из assets\imgs
				File outputFile = new File(imagesExternalDirectory, imageFile);

				// Если файл уже существует, пропускаем итерацию цикла
				if (outputFile.exists()) {
					continue;
				}

				// Создаем входной поток
				inputStream = assetManager.open(Globals.ASSETS_IMAGES_DIRECTORY
						+ File.separator + imageFile);

				// Создаем выходной поток
				outputStream = new FileOutputStream(outputFile);

				// Копируем файл
				byte[] buffer = new byte[1024];
				int read;
				while ((read = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, read);
				}

				// Закрываем и обнуляем потоки
				inputStream.close();
				inputStream = null;
				outputStream.flush();
				outputStream.close();
				outputStream = null;
			} catch (IOException e) {
				Logging.v(">>(Exception)<<. Исключение при попытке скопировать "
						+ imageFile + " из assets/imgs в images");
				e.printStackTrace();
			}
		} //end for
	} // end method
	
	
	@Override
	protected void onResume() {		
		// Выход из приложения
		if (getIntent().getBooleanExtra("LOGOUT", false))
		    finish();
				
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mApplication);
		// Проверка использования хранителя экрана
		mUseScreenSaver = prefs.getBoolean("enable_screen_saver", false);

		// Считывание времени бездействия и запуск Handler`а
		if (mUseScreenSaver) {
			mScreenSaverIdleTime = Integer.parseInt(prefs.getString(
					"screen_saver_idle_time", "25"));

			mScreenSaverHandler = new Handler();
			// Количество секунд * 1000
			mScreenSaverHandler.postDelayed(mScreenSaverRunnable,
					mScreenSaverIdleTime * 1000);
		}
				
		super.onResume();
	}

	/**
	 * Остановка счетчика бездействия
	 */
	@Override
	protected void onPause() {
		if (mScreenSaverHandler != null) {
			mScreenSaverHandler.removeCallbacks(mScreenSaverRunnable);
		}
		super.onPause();
	}

	/**
	 * Запуск хранителя экрана, если сработал счетчик бездействия:
	 * 1) Определяется количество режимов хранителя экрана (слайд-шоу И/ИЛИ затемнение)
	 * 2) Если два режима, то определить какой режим активен в данный момент:
	 * - считать из настроек время работы затемнения - определить по текущему времени,
	 * активно ли затемнение
	 * 3) В зависимости от режима хранителя, запуск слайд-шоу или затемнение
	 */
	private Runnable mScreenSaverRunnable = new Runnable() {
		public void run() {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(mApplication);

			// FALSE, если затемнение
			// TRUE, если слайд-шоу
			Boolean screenSaverMode = true;

			// Используется ли затемнение экрана
			Boolean useScreenDim = prefs.getBoolean("enable_screen_dim", false);

			// Если используется затемнение,
			// необходимо определить наступило ли время работы режима
			if (useScreenDim) {
				String time = prefs.getString("screen_dim_enable_time",
						"19:00-8:00");

				// Парсинг и конца работы
				// Например, 19:00-8:00 разбивается на [19:00] и [8:00]
				String[] time_period = time.split("-");

				// Парсинг значения начала работы
				// Например 19:00 разбивается на [19] и [00]
				String hour_start = time_period[0].split(":")[0];
				String minute_start = time_period[0].split(":")[1];
				
				// Парсинг значения начала работы
				// Например 8:00 разбивается на [8] и [00]
				String hour_end = time_period[1].split(":")[0];
				String minute_end = time_period[1].split(":")[1];

				// Получение временного промежутка в виде целых числа типа 1800
				// Например 19:45 преобразовывается в 1945
				int start_time = Integer.parseInt(hour_start) * 100
						+ Integer.parseInt(minute_start);
				int end_time = Integer.parseInt(hour_end) * 100
						+ Integer.parseInt(minute_end);

				// Получение текущего времени в виде целого числа типа 1800
				SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
				int current_time = Integer.parseInt(sdf.format(Calendar
						.getInstance().getTime()));

				// Необходимо сравнить текущее время и время для затемнения,
				// чтобы определить - какой вариант хранителя использовать.
				// Если временной промежуток задан в течение одного дня
				// "с 10 до 18", тогда current_time должно быть
				// больше start_time, но меньше end_time
				// Если промежуток "с 19 до 12", тогда current_time должно быть
				// или больше start_time, или меньше end_time
				// Если старт и конец работы совпадают - круглосуточно действует
				// затемнение
				if (start_time < end_time && current_time >= start_time
						&& current_time <= end_time)
					screenSaverMode = false;
				else if (start_time > end_time
						&& (current_time >= start_time || current_time <= end_time))
					screenSaverMode = false;
				else if (start_time == end_time)
					screenSaverMode = false;
			}

			if (screenSaverMode) {
				// Слайд-шоу
				Intent i = new Intent(mThisActivity, ScreenSaverActivity.class);
				startActivity(i);
			} else {
				// Затемнение экрана
				Intent i = new Intent(mThisActivity, ScreenDimActivity.class);
				startActivity(i);
			}
		}
	};

	/**
	 * При взаимодействии пользователя с планшетом, обновление счетчика бездействия
	 */
	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		if (mUseScreenSaver) {		
			// Обновляем счетчик бездействия
			mScreenSaverHandler.removeCallbacks(mScreenSaverRunnable);
			mScreenSaverHandler.postDelayed(mScreenSaverRunnable,
					mScreenSaverIdleTime * 1000);
		}
	}

	/**
	 * Ручной переход в нужный фрагмент. Нужен для реализации кнопок для
	 * перехода по фрагментам "влево-вправо". Вызывается из фрагментов.
	 */
	public void setCurrentItem(int item) {
		mViewPager.setCurrentItem(item, true);
	}

	/**
	 * Возвращает объект фрагмента по номеру и выдает общее количество
	 * фрагментов
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new MainMessagesFragment();
			} else if (position == 1) {
				return new MainMenuFragment();
			} else if (position == 2) {
				return new MainFormattedScreensFragment();
			} else {
				Logging.v("Ошибка при возвращении объекта фрагмента. Входной параметр "
						+ String.valueOf(position)
						+ " вышел за пределы количества фрагментов");
				return new MainMessagesFragment();
			}
		}

		@Override
		public int getCount() {
			return 3;
		}
	} // end class SectionsPagerAdapter
}
