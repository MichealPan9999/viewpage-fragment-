
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

import com.ktc.tvlauncher.image.domain.ArtsReInfo;
import com.ktc.tvlauncher.utils.Constant;
import com.ktc.tvlauncher.utils.HttpUtil;
import com.ktc.tvlauncher.utils.Logger;
import com.ktc.tvlauncher.utils.StringTool;

public class UpdateArtsData implements Runnable {
	private Context context;
	private Handler handler;
	private boolean isBootUpdate;

	public UpdateArtsData(Context context, Handler handler, boolean isBootUpdate) {
		this.context = context;
		this.handler = handler;
		this.isBootUpdate = isBootUpdate;
	}

	@Override
	public void run() {

		SharedPreferences sp = context.getSharedPreferences(
				Constant.SAVE_ARTSINFO, context.MODE_PRIVATE);

		HttpUtil httpUtil = new HttpUtil();
		String content = httpUtil
				.getJsonContent(Constant.WASU_RECOMMEND_ARTS_URL);
		if (content != null) {
			ArtsReInfo artInfo = StringTool.getWasuArtsInfo(content);
			if (artInfo != null) {
	            int[] id = artInfo.getId();
	            String[] linkUrl = artInfo.getLinkUrl();
	            String[] picUrl = artInfo.getPicUrl();
	            String[] title = artInfo.getTitle();
		
				boolean isSucees = true;
				for (int i = 0; i < 10; i++) {
					try {
						if (picUrl[i] != null) {
/*							FileOutputStream fos = context
									.openFileOutput(getPicName(picUrl[i]),
											context.MODE_PRIVATE);
							// FileOutputStream fos=new
							// FileOutputStream("data/data/com.ktc.launcher/files/"+getPicName(pic[i]));
*/		
							 FileOutputStream fos=new FileOutputStream("data/data/com.ktc.tvlauncher/files/artsImages/"+i + ".jpg");
							isSucees = downImage(fos, Constant.WASU_BASE_URL + picUrl[i]);
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
							.getSharedPreferences(Constant.SAVE_ARTSINFO,
									context.MODE_PRIVATE).edit();
					for (int i = 0, j = 0; i < picUrl.length; i++) {
						if (picUrl[i] != null) {
			                   
		                    editor.putString("art_linkurl_" + j, linkUrl[i]);
		                    editor.putInt("art_id_" + j, id[i]);
		                    editor.putString("art_picname_" + j,i + ".jpg");	  	   
		                    editor.putString("art_title_" + j, title[i]);
		                    editor.putString("art_picUrk_" + j, picUrl[i]);
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
					editor.putBoolean("art_update", true);
					editor.commit();
				}
				else{
					//没有更新成功
					Log.d("Jason","更新art数据不成功validTime = ");
					SharedPreferences.Editor editor = context
							.getSharedPreferences(Constant.SAVE_ARTSINFO,
									context.MODE_PRIVATE).edit();
					editor.putBoolean("art_update", false);
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
			  msg.arg1 = Constant.UPDATE_ARTS_SUCESS;//表示
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
