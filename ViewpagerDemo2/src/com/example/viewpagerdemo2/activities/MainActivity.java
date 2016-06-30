package com.example.viewpagerdemo2.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.viewpagerdemo2.R;
import com.example.viewpagerdemo2.adapters.FragAdapter;
import com.example.viewpagerdemo2.common.Constant;
import com.example.viewpagerdemo2.fragment.MovieFragment;
import com.example.viewpagerdemo2.utils.LruCacheUtils;
import com.example.viewpagerdemo2.utils.UpdateApk;
import com.example.viewpagerdemo2.utils.Utils;
import com.example.viewpagerdemo2.utils.Version;
import com.example.viewpagerdemo2.utils.WindowMessageID;

public class MainActivity extends BaseActivity {

	private final String TAG = "MainActivity";
	public FrameLayout r1_bg;
	private TextView tv_time, time_colon;
	private ImageView iv_titile;
	private RadioButton rb_app_store, rb_teteplay, rb_movie, rb_arts;
	private boolean isRunnint = false;
	private ViewPager vPager;
	private RadioGroup title_group, rg_video_type_bottom;
	private FragAdapter adapter;
	private float fromXdelta;
	private float toX;
	private AnimationSet mAnimationSet;
	private TranslateAnimation mTranslateAnimation;
	private List<Fragment> fragments;
	public List<PackageInfo> packLst;
	private static int title_position = 0;
	protected String technology = "";
	private float countSize;// 软件更新总大小
	private float currentSzie;// 软件更新当前下载进度
	private TextView tv_main_date, tv_update_msg;
	private MovieFragment rf;
	private TeleplayFragment mf;
	private AppFragment af;
	private ArtsFragment artsFrag;
	private boolean isHasFouse;
	private LinearLayout ll_rb;
	private LruCacheUtils mCacheUtils;
	public static String homeFrom;
	public static String homeParams;
	private boolean activityIsRun = false;
	public boolean net_state = Constant.CONNECT_FAILED;
	private Handler handlerUpdata;
	private HandlerThread handlerThread;
	private CheckNewVersionTask mCheckNewVersionTask;
	private Dialog alertDialog;
	private boolean homeAcivityIsForeground = true;
	private Boolean isPass = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void loadViewLayout() {

	}

	@Override
	protected void findViewById() {

	}

	@Override
	protected void setListener() {

	}
	private Handler homeHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			onMessage(msg);
		}
		
	};
	private void onMessage(final Message msg)
	{
		if (msg != null) {
			switch(msg.what)
			{
			case WindowMessageID.ERROR:
				Toast.makeText(getApplicationContext(), "服务器内部异常", 1).show();
				break;
			case WindowMessageID.DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "下载失败", 1).show();
				break;
			case WindowMessageID.GET_INFO_SUCCESS:
				break;
			case WindowMessageID.REFLESH_TIME:
				if ((activityIsRun == false)||(homeAcivityIsForeground == false)) {
					homeHandler.removeMessages(WindowMessageID.REFLESH_TIME);
					break;
				}
				tv_time.setText(Utils.getStringTime(" "));
				tv_main_date.setText(Utils.getStringData(context));
				break;
			}
		}
	}
	private class CheckNewVersionTask extends AsyncTask<Void, Void, Version> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Version result) {
			super.onPostExecute(result);
			if (result != null) {
				showNewVersionDialog(result);
			}
		}

		@Override
		protected Version doInBackground(Void... params) {
			return new UpdateApk(getApplicationContext()).hasNewVersion();
		}

	}

	private void showNewVersionDialog(final Version version)
	{
		if (alertDialog == null) {
			alertDialog = new AlertDialog.Builder(this)
			.setTitle("新版本提示")
			.setMessage(version.getIntroduction())
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					UpdateApk update = new UpdateApk(MainActivity.this);
					update.setmVersion(version);
					update.checkUpdate();
				}
			})
			.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						homeHandler.removeMessages(WindowMessageID.REFLESH_TIME);
						homeAcivityIsForeground = true;
						homeHandler.sendEmptyMessage(WindowMessageID.REFLESH_TIME);
					}
					return false;
				}
			})
			.setNeutralButton("跳过",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					isPass = true;
					homeHandler.removeMessages(WindowMessageID.REFLESH_TIME);
					homeAcivityIsForeground = true;
					homeHandler.sendEmptyMessage(WindowMessageID.REFLESH_TIME);
				}
			})
			.create();
			
		}
		if (!alertDialog.isShowing()) {
			alertDialog.show();
		}
		homeAcivityIsForeground = false;
	}
}
