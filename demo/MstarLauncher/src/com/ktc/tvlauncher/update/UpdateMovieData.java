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

import com.ktc.tvlauncher.image.domain.MoviesReInfo;
import com.ktc.tvlauncher.utils.Constant;
import com.ktc.tvlauncher.utils.HttpUtil;
import com.ktc.tvlauncher.utils.Logger;
import com.ktc.tvlauncher.utils.StringTool;

public class UpdateMovieData implements Runnable {
	private Context context;
	private Handler handler;
	private boolean isBootUpdate;

	public UpdateMovieData(Context context, Handler handler, boolean isBootUpdate) {
		this.context = context;
		this.handler = handler;
		this.isBootUpdate = isBootUpdate;
	}

	@Override
	public void run() {

		SharedPreferences sp = context.getSharedPreferences(
				Constant.SAVE_MOVIESINFO, context.MODE_PRIVATE);

		HttpUtil httpUtil = new HttpUtil();
		String content = httpUtil
				.getJsonContent(Constant.WASU_MOVIE_URL);
		if (content != null) {
			MoviesReInfo movieInfo = StringTool.getWasuMoviesInfo(content);
			if (movieInfo != null) {
	            String[] actor = movieInfo.getActors();
	            String[] description = movieInfo.getDescription();
	            String[] director = movieInfo.getDirector();
	            int[] id = movieInfo.getId();
	            String[] linkUrl = movieInfo.getLinkUrl();
	            String[] picUrl = movieInfo.getPicUrl();
	            String[] picUrl2 = movieInfo.getPicUrl2();
	            String[] picUrl3 = movieInfo.getPicUrl3();
	            String[] showType = movieInfo.getShowType();
	            
	            String[] title = movieInfo.getTitle();
	            String[] viewPoint = movieInfo.getViewPoint();
	            String[] year = movieInfo.getYear();
		
				boolean isSucees = true;
				for (int i = 0; i < 6; i++) {
					try {
						if (picUrl[i] != null) {
						/*	FileOutputStream fos = context
									.openFileOutput(getPicName(picUrl[i]),
											context.MODE_PRIVATE);*/
							 FileOutputStream fos=new FileOutputStream("data/data/com.ktc.tvlauncher/files/moviesImages/"+i + ".jpg");
							isSucees = downImage(fos, picUrl[i]);
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
							.getSharedPreferences(Constant.SAVE_MOVIESINFO,
									context.MODE_PRIVATE).edit();
					for (int i = 0, j = 0; i < picUrl.length; i++) {
						if (picUrl[i] != null) {
		                	editor.putString("movie_pic_name_" + j, i + ".jpg");//图片的名字
		                	editor.putInt("movie_id_" + j, id[i]);
		                    editor.putString("movie_actor_" + j, actor[i]);
		                    editor.putString("movie_description_" + j, description[i]);
		                    editor.putString("movie_director_" + j, director[i]);
		                    editor.putInt("movie_picurl_" + j, id[i]);
		                    editor.putString("movie_linkUrl_" + j, linkUrl[i]);
		                    editor.putString("movie_picUrl_" + j, picUrl[i]);
		                    editor.putString("movie_picUrl2_" + j, picUrl2[i]);
		                    editor.putString("movie_picUrl3_" + j, picUrl3[i]);
		                    editor.putString("movie_showType_" + j, showType[i]);
		                    editor.putString("movie_title_" + j, title[i]);
		                    editor.putString("movie_viewPoint_" + j, viewPoint[i]);
		                    editor.putString("movie_year_" + j, year[i]);
		                    j++;
						}
					}
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
					editor.putBoolean("movie_update", true);
					editor.commit();
				}
				else{
					//没有更新成功
					Log.d("Jason","更新Movie数据不成功validTime ");
					SharedPreferences.Editor editor = context
							.getSharedPreferences(Constant.SAVE_MOVIESINFO,
									context.MODE_PRIVATE).edit();
					editor.putBoolean("movie_update", false);
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
