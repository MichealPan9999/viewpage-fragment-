package com.example.viewpagerdemo2.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

public class UpdateApk {

	private Context mContext;
	private String mCurrentPkg;
	private String mServerVersionUrl;
	private Version mVersion;
	private static final boolean DEBUG = true;
	private static final String NOT_USB_DEVICE = "notusbdevicektc";
	private static final String LOG_TAG = UpdateApk.class.getSimpleName();
	private DownloadTask mDownloadTask;
	private ProgressDialog mProgressDialog;

	public UpdateApk(Context context) {
		this(context, context.getApplicationInfo().packageName);
	}

	/**
	 * 升级指定报名的应用程序
	 * 
	 * @param context
	 * @param pkgName
	 */
	public UpdateApk(Context context, String pkgName) {
		this.mContext = context;
		this.mCurrentPkg = pkgName;
		// mServerVersionUrl 可以自己写一个网址。自己搭建Apache
		this.mServerVersionUrl = SystemProperties.get(
				"ro.product.launcher.update", "");
	}

	public String getmServerVersionUrl() {
		return mServerVersionUrl;
	}

	public void setmServerVersionUrl(String mServerVersionUrl) {
		this.mServerVersionUrl = mServerVersionUrl;
	}

	public Version getmVersion() {
		return mVersion;
	}

	public void setmVersion(Version mVersion) {
		this.mVersion = mVersion;
	}

	/**
	 * 判断是否有新版本需要访问网络，在UI中需要异步调用，否则会造成UI线程阻塞
	 */
	public Version hasNewVersion() {
		Version lastVersion = getUpdateVersion();
		if (lastVersion != null) {
			PackageVersion currentVersion = new PackageVersion(mContext,
					mCurrentPkg);
			int lastVerCode = lastVersion.getVerCode();
			int currentCode = currentVersion.getVerCode();
			if (currentCode > 0 && currentCode < lastVerCode) {
				return lastVersion;
			}
		}
		return null;
	}

	/**
	 * 从服务器获取远程当前程序版本信息
	 * 
	 * @param serverUrl
	 * @param pkgName
	 * @return
	 */
	public Version getUpdateVersion() {
		DefaultHttpClient http = new DefaultHttpClient();
		HttpResponse response = null;

		try {
			if (!mServerVersionUrl.equals("")) {
				HttpGet method = new HttpGet(mServerVersionUrl);
				response = http.execute(method);
				InputStream data = response.getEntity().getContent();

				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(data));
				StringBuffer sb = new StringBuffer();
				String line = bufferedReader.readLine();
				while (line != null) {
					sb.append(line).append("\r\n");
					line = bufferedReader.readLine();
				}
				bufferedReader.close();
				String rlt = sb.toString();
				// Log.i(Constants.TAG, rlt);
				List<Version> versions = Version.constructVersion(rlt);
				if (versions != null) {
					for (Version version : versions) {
						if (mCurrentPkg.equals(version.getPkgName())) {
							return version;
						}
					}
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据包名获取指定应用程序的版本信息
	 * 
	 * @author yejb
	 * 
	 */
	private static class PackageVersion {

		private Context mContext;
		private String mPkgName;
		private int mVerCode;
		private String mVerName;

		public PackageVersion(Context context, String pkgName) {
			this.mContext = context;
			this.mPkgName = pkgName;

			init();
		}

		private void init() {
			try {
				mVerCode = mContext.getPackageManager().getPackageInfo(
						mPkgName, 0).versionCode;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				mVerCode = -1;
			}

			try {
				mVerName = mContext.getPackageManager().getPackageInfo(
						mPkgName, 0).versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				mVerName = null;
			}
		}

		/**
		 * 获取应用程序包名
		 * 
		 * @return
		 */
		public String getPkgName() {
			return this.mPkgName;
		}

		/**
		 * 获取版本号
		 * 
		 * @return
		 */
		public int getVerCode() {
			return this.mVerCode;
		}

		/**
		 * 获取版本名
		 * 
		 * @return
		 */
		public String getVerName() {
			return this.mVerName;
		}
	}

	public void checkUpdate() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setTitle("进度条窗口");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (DEBUG)
							Log.d(LOG_TAG, "dialog dismiss");
						if (mDownloadTask != null
								&& mDownloadTask.getStatus() != AsyncTask.Status.FINISHED) {
							mDownloadTask.cancel(true);
						}
					}
				});
		mProgressDialog.show();

		// 不显示正在检测界面，直接显示下载界面
		if (mVersion != null) {
			if (mDownloadTask != null
					&& mDownloadTask.getStatus() != AsyncTask.Status.FINISHED) {
				mDownloadTask.cancel(true);
			}
			mDownloadTask = new DownloadTask();
			mDownloadTask.execute(mVersion.getUrl());
		}
		// if (mCheckUpdateTask != null
		// && mCheckUpdateTask.getStatus() != AsyncTask.Status.FINISHED) {
		// mCheckUpdateTask.cancel(true);
		// }
		// mCheckUpdateTask = new CheckUpdateTask();
		// mCheckUpdateTask.execute();
	}

	/**
	 * 文件下载异步任务
	 * 
	 * @author yejb
	 * 
	 */
	private class DownloadTask extends AsyncTask<String, Integer, String> {

		private boolean taskCancelled = false;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		private int getApkVersion(String path) {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(path,
					PackageManager.GET_ACTIVITIES);
			return info.versionCode;
		}

		@TargetApi(Build.VERSION_CODES.FROYO)
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String fileUrl = params[0];
			int retryCount = 3;

			int retriedCount;
			for (retriedCount = 0; retriedCount < retryCount; retriedCount++) {
				RandomAccessFile savedFile = null;
				try {
					File file;
					boolean usedSD;
					// 系统为2.2及以上版本并且系统已挂载SD卡，将文件存放到SD中
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						/*
						 * file = new File(mContext.getFilesDir()+"/"+
						 * mVersion.getFileName());
						 */
						file = new File(mContext.getCacheDir() + "/"
								+ mVersion.getFileName());
						usedSD = true;
						long offset = 0;
						if (file.exists()) {
							try {
								// 判断APK文件是否完整
								int apkVersion = getApkVersion(file.getPath());
								if (DEBUG)
									Log.d(LOG_TAG, "apkVersion: " + apkVersion);
								// 完整，再判断是否和服务器更新文件相同
								String md5 = MD5.md5sum(file.getPath());
								if (DEBUG)
									Log.d(LOG_TAG, "md5: " + md5);
								if (md5.equals(mVersion.getMd5sum())) {
									return file.getPath();
								} else {
									file.delete();
								}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

							offset = file.length();
						}
						// Log.i(Constants.TAG,
						// "chmod,604,"+file.getAbsolutePath());
						String[] args = { "chmod", "604",
								file.getAbsolutePath() };

						if (DEBUG)
							Log.d(LOG_TAG, "offset: " + offset);
						URL url = new URL(fileUrl);
						URLConnection con = url.openConnection();
						con.setConnectTimeout(5000);
						con.setReadTimeout(5000);
						con.setRequestProperty("RANGE", "bytes=" + offset + "-");
						// if
						// ("bytes".equals(con.getHeaderField("Accept-Ranges")))
						// {
						// con.setRequestProperty("RANGE","bytes=" + offset +
						// "-");
						// }
						con.connect();
						long fileSize = con.getContentLength();

						if (DEBUG)
							Log.d(LOG_TAG, "fileSize: " + fileSize);
						fileSize += offset;
						if (usedSD) {
							double sdFree = freeSpaceOnSd();
							if (sdFree <= fileSize) {
								if (DEBUG)
									Log.e(LOG_TAG,
											"SD card is not enough free space");
								return null;
							}
						}
						InputStream raw = con.getInputStream();
						InputStream is = new BufferedInputStream(raw);
						savedFile = new RandomAccessFile(file, "rw");
						// 从文件尾开始追加
						savedFile.seek(offset);
						int bytesRead = -1;
						byte[] buf = new byte[1024 * 5];
						int percent = 0;
						while (!taskCancelled) {
							bytesRead = is.read(buf);
							if (bytesRead < 0) {
								break;
							}
							savedFile.write(buf, 0, bytesRead);
							offset += bytesRead;
							percent = (int) (offset * 100.0 / fileSize);
							publishProgress(percent);
							if (DEBUG)
								Log.d(LOG_TAG, "bytesRead: " + bytesRead);
						}
						if (offset == fileSize) {
							String result = exec(args);
							// Log.i(Constants.TAG, "chmod,result,"+result);
							return file.getPath();
						} else {
							return null;
						}
					} else {
						return NOT_USB_DEVICE;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					break;
				} catch (UnsupportedEncodingException e) {
					// TODO: handle exception
					e.printStackTrace();
					break;
				} catch (SocketTimeoutException e) {
					if (DEBUG)
						Log.e(LOG_TAG, "downImageView:timeout");
				} catch (IOException e) {
					e.printStackTrace();
					break;
				} finally {
					if (savedFile != null) {
						try {
							savedFile.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}

			return null;
		}

		private String exec(String[] args) {
			String result = "";
			ProcessBuilder processBuilder = new ProcessBuilder(args);
			Process process = null;
			InputStream errIs = null;
			InputStream inIs = null;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int read = -1;
				process = processBuilder.start();
				errIs = process.getErrorStream();
				while ((read = errIs.read()) != -1) {
					baos.write(read);
				}
				baos.write('n');
				inIs = process.getInputStream();
				while ((read = inIs.read()) != -1) {
					baos.write(read);
				}
				byte[] data = baos.toByteArray();
				result = new String(data);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (errIs != null) {
						errIs.close();
					}
					if (inIs != null) {
						inIs.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (process != null) {
					process.destroy();
				}
			}
			return result;
		}

		private double freeSpaceOnSd() {
			StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
			return (double) stat.getAvailableBlocks()
					* (double) stat.getBlockSize();
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			taskCancelled = true;
			if (DEBUG)
				Log.d(LOG_TAG, "download cancelled");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			setProgressPercent(values[0]);
		}

		private void setProgressPercent(Integer integer) {
			mProgressDialog.setProgress(integer);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			mProgressDialog.dismiss();
			if (NOT_USB_DEVICE.equals(result)) {
				Toast.makeText(mContext, "download_failed", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (!TextUtils.isEmpty(result)) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.fromFile(new File(result)),
							"application/vnd.android.package-archive");
					mContext.startActivity(intent);
				} else {
					Toast.makeText(mContext, "download_failed",
							Toast.LENGTH_SHORT).show();
				}
			}

			super.onPostExecute(result);
		}
	}
}
