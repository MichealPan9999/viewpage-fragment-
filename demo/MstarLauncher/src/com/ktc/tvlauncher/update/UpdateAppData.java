package com.ktc.tvlauncher.update;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ktc.tvlauncher.image.domain.AppsReInfo;
import com.ktc.tvlauncher.utils.Constant;
import com.ktc.tvlauncher.utils.HttpUtil;
import com.ktc.tvlauncher.utils.Logger;
import com.ktc.tvlauncher.utils.StringTool;

public class UpdateAppData implements Runnable {
	private Context context;
	private Handler handler;
	private boolean isBootUpdate;
	private int validUrlNum = 0;
	public UpdateAppData(Context context, Handler handler, boolean isBootUpdate) {
		this.context = context;
		this.handler = handler;
		this.isBootUpdate = isBootUpdate;
	}

	@Override
	public void run() {

		SharedPreferences sp = context.getSharedPreferences(
				Constant.SAVE_APPSINFO, context.MODE_PRIVATE);

		HttpUtil httpUtil = new HttpUtil();
		String content = httpUtil
				.getJsonContent(Constant.WASU_MOVIE_CAROUSEL);
		if (content != null) {
			AppsReInfo appInfo = StringTool.getWasuAppsInfo(content);
			if (appInfo != null) {
				int[] id = appInfo.getId();
				String[] linkUrl = appInfo.getLinkUrl();
				String[] name = appInfo.getName();
				String[] picUrl = appInfo.getPicUrl();
				String[] program_id = appInfo.getProgram_id();
				String[] showType = appInfo.getShowType();
				int[] totalsize = appInfo.getTotalsize();
				String[] videoUrl = appInfo.getVideoUrl();
				String[] viewPoint = appInfo.getViewPoint();

				boolean isSucees = true;
				for (int i = 0; i < 4; i++) {
					try {
						if (picUrl[i] != null) {
							String url = Constant.WASU_BASE_URL + picUrl[i];
							 FileOutputStream fos=new FileOutputStream("data/data/com.ktc.tvlauncher/files/appsImages/"+ i + ".jpg");
							isSucees = downImage(fos, url);
							
						}
					} catch (FileNotFoundException e) {
						isSucees = false;
						// Log.e(Constants.TAG,
						// "downImage false!!!!!!!!!!!!!!!");
						break;
					}
				}
				if (isSucees) {
					// Log.i(Constants.TAG, "downImage Sucess");
					SharedPreferences.Editor editor = context
							.getSharedPreferences(Constant.SAVE_APPSINFO,
									context.MODE_PRIVATE).edit();
					for (int i = 0, j = 0; i < picUrl.length; i++) {
						
						if (picUrl[i] != null) {
							if (program_id[i].equals("-1")){
								continue;
							}
							validUrlNum++;
							
							editor.putString("movie_linkurl_" + j, linkUrl[i]);
							editor.putInt("movie_id_" + j, id[i]);
							editor.putString("movie_name_" + j, name[i]);
							editor.putString("movie_picurl_" + j,
									i + ".jpg");
							editor.putString("movie_program_id_" + j,
									program_id[i]);
							editor.putString("movie_showType_" + j, showType[i]);
							editor.putInt("movie_totalsize_" + j, totalsize[i]);
							editor.putString("movie_videoUrl_" + j, videoUrl[i]);
							editor.putString("movie_viewPoint_" + j,
									viewPoint[i]);
							j++;
						}
					}
					editor.putInt("movie_valid_url_num", validUrlNum);
					SharedPreferences.Editor tvconfig = context
							.getSharedPreferences(Constant.TV_CONFIG,
									context.MODE_PRIVATE).edit();
					// 设置刷新周期为1天
					long validTime = System.currentTimeMillis();
					validTime = validTime + 1 * 24 * 60 * 60 * 1000;
					//validTime = validTime + 1;
					Log.d("Jason","更新App数据成功validTime = " + validTime);
					tvconfig.putLong("movie_validTime", validTime);
					tvconfig.commit();
					editor.putBoolean("app_update", true);
					editor.commit();
				}
				else{
					//没有更新成功
					Log.d("Jason","更新Apo数据不成功validTime = ");
					SharedPreferences.Editor editor = context
							.getSharedPreferences(Constant.SAVE_APPSINFO,
									context.MODE_PRIVATE).edit();
					editor.putBoolean("app_update", false);
					editor.commit();
				}
			}
		}

		if (content == null){
			if (isBootUpdate == false){
			  Message msg=handler.obtainMessage(Constant.NETWORK_TIMEOUT);
			  handler.sendMessage(msg);
			}
		}
		else{
			if (isBootUpdate == false){
			  Message msg=handler.obtainMessage(Constant.NETWORK_SUCESS);
			  handler.sendMessage(msg);	
			}
		}
	}

	public String getPicName(String pic) {
		String[] picStr = pic.split("/");
		return picStr[picStr.length - 1];
	}

	public boolean downImage(FileOutputStream fos, String imageUrl) {
		try {
			URL myFileUrl = new URL(imageUrl);
			InputStream in = myFileUrl.openStream();
			int len = -1;
			byte[] data = new byte[1024];
			while ((len = in.read(data)) != -1) {
				fos.write(data, 0, len);
				fos.flush();
			}
			in.close();
			fos.close();
			return true;
		} catch (IOException e) {
			// Log.i(Constants.TAG,
			// "--------------downImage false-------------");
			return false;
		}
	}
}
