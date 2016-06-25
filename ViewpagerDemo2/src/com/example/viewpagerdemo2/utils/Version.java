package com.example.viewpagerdemo2.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Version {

	private String appName;
	private String fileName;
	private String pkgName;
	private String verName;
	private int verCode;
	private String url;
	private String introduction;
	private String md5sum;
	public Version(JSONObject jsonObj) {
		try {
			this.appName = jsonObj.getString("app_name");
			this.fileName = jsonObj.getString("file_name");
			this.pkgName = jsonObj.getString("pkg_name");
			this.verName = jsonObj.getString("ver_name");
			this.verCode = jsonObj.getInt("ver_code");
			this.url = jsonObj.getString("url");
			this.introduction = jsonObj.getString("introduction");
			this.md5sum = jsonObj.getString("MD5");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Version> constructVersion(String str)
	{
		try {
			JSONObject jsonObj = new JSONObject(str);
			JSONArray array = jsonObj.getJSONArray("version");
			int size = array.length();
			List<Version> versions = new ArrayList<Version>(size);
			for (int i = 0; i < size; i++) {
				versions.add(new Version(array.getJSONObject(i)));
			}
			return versions;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getVerName() {
		return verName;
	}

	public void setVerName(String verName) {
		this.verName = verName;
	}

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	@Override
	public String toString() {
		return "Version [appName=" + appName + ", fileName=" + fileName
				+ ", pkgName=" + pkgName + ", verName=" + verName
				+ ", verCode=" + verCode + ", url=" + url + ", introduction="
				+ introduction + ", md5sum=" + md5sum + "]";
	}
	
	
	
}
