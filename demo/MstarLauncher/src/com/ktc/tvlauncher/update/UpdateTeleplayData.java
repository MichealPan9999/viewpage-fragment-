
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

import com.ktc.tvlauncher.image.domain.TeleplaysReInfo;
import com.ktc.tvlauncher.utils.Constant;
import com.ktc.tvlauncher.utils.HttpUtil;
import com.ktc.tvlauncher.utils.Logger;
import com.ktc.tvlauncher.utils.StringTool;
import android.util.Log;
public class UpdateTeleplayData implements Runnable {
	private Context context;
	private Handler handler;
	private boolean isBootUpdate;

	public UpdateTeleplayData(Context context, Handler handler, boolean isBootUpdate) {
		this.context = context;
		this.handler = handler;
		this.isBootUpdate = isBootUpdate;
	}

	@Override
	public void run() {

		SharedPreferences sp = context.getSharedPreferences(
				Constant.SAVE_TELEPLAYSINFO, context.MODE_PRIVATE);

		HttpUtil httpUtil = new HttpUtil();
		String content = httpUtil
				.getJsonContent(Constant.WASU_TELEPLAY_URL);
		if (content != null) {
			TeleplaysReInfo teleplayInfo = StringTool.getWasuTeleplaysInfo(content);
			if (teleplayInfo != null) {
	            String[] actor = teleplayInfo.getActors();
	            String[] description = teleplayInfo.getDescription();
	            String[] director = teleplayInfo.getDirector();
	            int[] id = teleplayInfo.getId();
	            String[] linkUrl = teleplayInfo.getLinkUrl();
	            String[] picUrl = teleplayInfo.getPicUrl();
	            String[] picUrl2 = teleplayInfo.getPicUrl2();
	            String[] picUrl3 = teleplayInfo.getPicUrl3();
	            String[] showType = teleplayInfo.getShowType();
	            
	            String[] title = teleplayInfo.getTitle();
	            String[] viewPoint = teleplayInfo.getViewPoint();
	            String[] year = teleplayInfo.getYear();
		
				boolean isSucees = true;
				for (int i = 0; i < 6; i++) {
					try {
						if (picUrl[i] != null) {
/*							FileOutputStream fos = context
									.openFileOutput(getPicName(picUrl[i]),
											context.MODE_PRIVATE);*/
							// FileOutputStream fos=new
							// FileOutputStream("data/data/com.ktc.launcher/files/"+getPicName(pic[i]));
							 FileOutputStream fos=new FileOutputStream("data/data/com.ktc.tvlauncher/files/teleplaysImages/"+i + ".jpg");
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
							.getSharedPreferences(Constant.SAVE_TELEPLAYSINFO,
									context.MODE_PRIVATE).edit();
					for (int i = 0, j = 0; i < picUrl.length; i++) {
						if (picUrl[i] != null) {
			                   
		                	editor.putString("teleplay_pic_name_" + j, i + ".jpg");//图片的名字
		                	editor.putInt("teleplay_id_" + j, id[i]);
		                    editor.putString("teleplay_actor_" + j, actor[i]);
		                    editor.putString("teleplay_description_" + j, description[i]);
		                    editor.putString("teleplay_director_" + j, director[i]);
		                    editor.putInt("teleplay_picurl_" + j, id[i]);
		                    editor.putString("teleplay_linkUrl_" + j, linkUrl[i]);
		                    editor.putString("teleplay_picUrl_" + j, picUrl[i]);
		                    editor.putString("teleplay_picUrl2_" + j, picUrl2[i]);
		                    editor.putString("teleplay_picUrl3_" + j, picUrl3[i]);
		                    editor.putString("teleplay_showType_" + j, showType[i]);
		                    editor.putString("teleplay_title_" + j, title[i]);
		                    editor.putString("teleplay_viewPoint_" + j, viewPoint[i]);
		                    editor.putString("teleplay_year_" + j, year[i]);

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
					editor.putBoolean("teleplay_update", true);
					editor.commit();
				}
				else{
					//没有更新成功
					Log.d("Jason","更新Teleplay数据没有成功validTime ");
					SharedPreferences.Editor editor = context
							.getSharedPreferences(Constant.SAVE_TELEPLAYSINFO,
									context.MODE_PRIVATE).edit();
					editor.putBoolean("teleplay_update", false);
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
