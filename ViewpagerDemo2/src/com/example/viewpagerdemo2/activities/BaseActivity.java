package com.example.viewpagerdemo2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.viewpagerdemo2.R;
import com.example.viewpagerdemo2.utils.Utils;

public abstract class BaseActivity extends FragmentActivity {

	protected Context context ;
	protected SharedPreferences sp;
	protected Animation breathingAnimation;
	protected int mWidth,mHeight;
	protected double screenSize;
	protected String version;
	protected String from;
	protected String devicetype;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
		context = BaseActivity.this;
		sp = getSharedPreferences("baseSP", MODE_PRIVATE);
		breathingAnimation = AnimationUtils.loadAnimation(context, R.anim.breathing);
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
		System.out.println("mWidth = "+mWidth+" , mHeight = "+mHeight);
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2)+Math.pow(dm.heightPixels, 2));
		screenSize = diagonalPixels/(160*dm.density);
		System.out.println("screenSize = "+screenSize);
		version = Utils.getVersion(context);
		System.out.println("version = "+version);
	}

	/**
	 * 初始化
	 */
	protected abstract void initView();
	/**
	 * 加载布局文件
	 */
	protected abstract void loadViewLayout();
	/**
	 * 初始化控件
	 */
	protected abstract void findViewById();
	/**
	 * 初始化监听
	 */
	protected abstract void setListener();
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void openActivity(Class<?> pClass)
	{
		openActivity(pClass,null);
	}
	protected void openActivity(Class<?> pClass,Bundle pBundle)
	{
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
	/**
	 * 应用崩溃toast
	 */
	protected void handleFatalError()
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "程序终止", Toast.LENGTH_LONG).show();
				finish();
			}
		});
	}
	/**
	 * Activity 关闭和启动
	 */
	public void finish()
	{
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
