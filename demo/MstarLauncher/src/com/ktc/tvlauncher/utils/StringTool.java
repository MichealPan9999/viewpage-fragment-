package com.ktc.tvlauncher.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ktc.tvlauncher.R;
import com.ktc.tvlauncher.domain.ForecastWeatherInfo;
import com.ktc.tvlauncher.image.domain.AppsReInfo;
import com.ktc.tvlauncher.image.domain.ArtsReInfo;
import com.ktc.tvlauncher.image.domain.MoviesReInfo;
import com.ktc.tvlauncher.image.domain.TeleplaysReInfo;

/**
 * 
 * 
 * @ClassName: JsonTool
 * 
 * @Description: 完成对JSON数据的解析
 * 
 * @author Nathan.Liao
 * 
 * @date 2013-3-18 下午4:39:14
 */
public class StringTool {
	public static final int DAY_COUNT = 3;

	public StringTool() {

		// TODO Auto-generated constructor stub
	}

	public static AppsReInfo getWasuAppsInfo(String jsonString) {

		JSONObject jsonObject = null;
		AppsReInfo wasuInfo = null;
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray movieInfoArray = jsonObject.getJSONArray("videolist");
			int[] id = new int[32];
			String[] linkUrl = new String[32];
			String[] name = new String[32];
			String[] picUrl = new String[32];
			String[] program_id = new String[32];
			String[] showType = new String[32];
			int[] totalsize = new int[32];
			String[] videoUrl = new String[32];
			String[] viewPoint = new String[32];

			int count = 0;
			int lenght = movieInfoArray.length();
			for (int i = 0; i < lenght; i++) {
				JSONObject movie = (JSONObject) movieInfoArray.opt(i);
				id[count] = movie.getInt("id");
				linkUrl[count] = movie.getString("linkUrl");
				name[count] = movie.getString("name");
				picUrl[count] = movie.getString("picUrl");
				program_id[count] = movie.getString("program_id");
				showType[count] = movie.getString("showType");
				totalsize[count] = movie.getInt("totalsize");
				videoUrl[count] = movie.getString("videoUrl");				
				//Hisa 2015.12.17 去掉wasu过长的描述文字 start
				//viewPoint[count] = movie.getString("viewPoint");
				String point = movie.getString("viewPoint");
				int len = point.length() > 20 ? 20 : point.length() - 1;
				viewPoint[count] = point.length() > 20 ? point.substring(0, len) + "..." : point.substring(0, len);
				//Hisa 2015.12.17 去掉wasu过长的描述文字  end
				count++;

			}
			wasuInfo = new AppsReInfo();
			wasuInfo.setId(id);
			wasuInfo.setLinkUrl(linkUrl);
			wasuInfo.setName(name);
			wasuInfo.setPicUrl(picUrl);
			wasuInfo.setProgram_id(program_id);
			wasuInfo.setShowType(showType);
			wasuInfo.setTotalsize(totalsize);
			wasuInfo.setVideoUrl(videoUrl);
			wasuInfo.setViewPoint(viewPoint);

		} catch (JSONException e) {
			Log.e("StringTool", "解析推荐电影信息异常！！！！！！！！！！！");
		}

		return wasuInfo;

	}
	
	
	public static MoviesReInfo getWasuMoviesInfo(String jsonString) {

		JSONObject jsonObject = null;
		MoviesReInfo wasuMoviesInfo = null;
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray movieInfoArray = jsonObject.getJSONArray("data");
			String[] actors = new String[32];
			String[] description = new String[32];
			String[] director = new String[32];
			int[] id = new int[32];
			String[] linkUrl = new String[32];
			String[] picUrl = new String[32];
			String[] picUrl2 = new String[32];
			String[] picUrl3 = new String[32];
			String[] showType = new String[32];
			String[] title = new String[32];
			String[] viewPoint = new String[32];
			String[] year = new String[32];
			
			int count = 0;
			int lenght = movieInfoArray.length();
			for (int i = 0; i < lenght; i++) {
				JSONObject movie = (JSONObject) movieInfoArray.opt(i);
				
				actors[count] = movie.getString("actors");
				description[count] = movie.getString("description");
				director[count] = movie.getString("director");
				id[count] = movie.getInt("id");
				linkUrl[count] = movie.getString("linkUrl");
				picUrl[count] = movie.getString("picUrl");
				picUrl2[count] = movie.getString("picUrl2");
				picUrl3[count] = movie.getString("picUrl3");
				showType[count] = movie.getString("showType");
				title[count] = movie.getString("title");	
				//Hisa 2015.12.17 去掉wasu过长的描述文字 start
				//viewPoint[count] = movie.getString("viewPoint");
				String point = movie.getString("viewPoint");
				int len = point.length() > 20 ? 20 : point.length() - 1;
				viewPoint[count] = point.length() > 20 ? point.substring(0, len) + "..." : point.substring(0, len);
				//Hisa 2015.12.17 去掉wasu过长的描述文字  end
				year[count] = movie.getString("year");
				count++;

			}
			wasuMoviesInfo = new MoviesReInfo();
			wasuMoviesInfo.setActors(actors);
			wasuMoviesInfo.setDescription(description);
			wasuMoviesInfo.setDirector(director);
			wasuMoviesInfo.setId(id);
			wasuMoviesInfo.setLinkUrl(linkUrl);
			wasuMoviesInfo.setPicUrl(picUrl);
			wasuMoviesInfo.setPicUrl2(picUrl2);
			wasuMoviesInfo.setPicUrl3(picUrl3);
			wasuMoviesInfo.setShowType(showType);
			wasuMoviesInfo.setTitle(title);
			wasuMoviesInfo.setViewPoint(viewPoint);
			wasuMoviesInfo.setYear(year);

		} catch (JSONException e) {
			Log.e("StringTool", "解析推荐电影信息异常！！！！！！！！！！！");
		}

		return wasuMoviesInfo;

	}
	
	public static TeleplaysReInfo getWasuTeleplaysInfo(String jsonString) {

		JSONObject jsonObject = null;
		TeleplaysReInfo wasuTeleplaysInfo = null;
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray teleplaysInfoArray = jsonObject.getJSONArray("data");
			String[] actors = new String[32];
			String[] description = new String[32];
			String[] director = new String[32];
			int[] id = new int[32];
			String[] linkUrl = new String[32];
			String[] picUrl = new String[32];
			String[] picUrl2 = new String[32];
			String[] picUrl3 = new String[32];
			String[] showType = new String[32];
			String[] title = new String[32];
			String[] viewPoint = new String[32];
			String[] year = new String[32];
			
			int count = 0;
			int lenght = teleplaysInfoArray.length();
			for (int i = 0; i < lenght; i++) {
				JSONObject teleplays = (JSONObject) teleplaysInfoArray.opt(i);
				
				actors[count] = teleplays.getString("actors");
				description[count] = teleplays.getString("description");
				director[count] = teleplays.getString("director");
				id[count] = teleplays.getInt("id");
				linkUrl[count] = teleplays.getString("linkUrl");
				picUrl[count] = teleplays.getString("picUrl");
				picUrl2[count] = teleplays.getString("picUrl2");
				picUrl3[count] = teleplays.getString("picUrl3");
				showType[count] = teleplays.getString("showType");
				title[count] = teleplays.getString("title");
				//Hisa 2015.12.17 去掉wasu过长的描述文字 start
				//viewPoint[count] = teleplays.getString("viewPoint");
				String point = teleplays.getString("viewPoint");
				int len = point.length() > 20 ? 20 : point.length() - 1;
				viewPoint[count] = point.length() > 20 ? point.substring(0, len) + "..." : point.substring(0, len);
				//Hisa 2015.12.17 去掉wasu过长的描述文字  end
				year[count] = teleplays.getString("year");
				count++;

			}
			wasuTeleplaysInfo = new TeleplaysReInfo();
			wasuTeleplaysInfo.setActors(actors);
			wasuTeleplaysInfo.setDescription(description);
			wasuTeleplaysInfo.setDirector(director);
			wasuTeleplaysInfo.setId(id);
			wasuTeleplaysInfo.setLinkUrl(linkUrl);
			wasuTeleplaysInfo.setPicUrl(picUrl);
			wasuTeleplaysInfo.setPicUrl2(picUrl2);
			wasuTeleplaysInfo.setPicUrl3(picUrl3);
			wasuTeleplaysInfo.setShowType(showType);
			wasuTeleplaysInfo.setTitle(title);
			wasuTeleplaysInfo.setViewPoint(viewPoint);
			wasuTeleplaysInfo.setYear(year);

		} catch (JSONException e) {
			Log.e("StringTool", "解析推荐电影信息异常！！！！！！！！！！！");
		}

		return wasuTeleplaysInfo;
	}
	
	public static ArtsReInfo getWasuArtsInfo(String jsonString) {
		JSONObject jsonObject = null;
		ArtsReInfo wasuArtsInfo = null;
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray artsInfoArray = jsonObject.getJSONArray("morehot");
	
			int[] id = new int[32];
			String[] linkUrl = new String[32];
			String[] picUrl = new String[32];
			String[] title = new String[32];

			int count = 0;
			int lenght = artsInfoArray.length();
			for (int i = 0; i < lenght; i++) {
				JSONObject arts = (JSONObject) artsInfoArray.opt(i);

				id[count] = arts.getInt("id");
				linkUrl[count] = arts.getString("linkUrl");
				picUrl[count] = arts.getString("picUrl");
				title[count] = arts.getString("title");
	
				count++;

			}
			wasuArtsInfo = new ArtsReInfo();

			wasuArtsInfo.setId(id);
			wasuArtsInfo.setLinkUrl(linkUrl);
			wasuArtsInfo.setPicUrl(picUrl);
;
			wasuArtsInfo.setTitle(title);


		} catch (JSONException e) {
			Log.e("StringTool", "解析推荐电影信息异常！！！！！！！！！！！");
		}

		return wasuArtsInfo;
	}
	
	public static ForecastWeatherInfo getWeatherInfo(String jsonString) {
		ForecastWeatherInfo weatherInfo = new ForecastWeatherInfo();

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject weatherInfoObject = jsonObject.getJSONObject("f");
			// JSONObject forcastAllInfo = weatherInfoObject.getJSONObject("f");
			JSONArray weatherInfoArray = weatherInfoObject.getJSONArray("f1");
			int length = weatherInfoArray.length() >= DAY_COUNT ? DAY_COUNT
					: weatherInfoArray.length();
			String[] day_temp = new String[length];
			String[] night_temp = new String[length];
			String[] day_weather_status = new String[length];
			String[] night_weather_status = new String[length];
			String[] day_wind_direct = new String[length];
			String[] night_wind_direct = new String[length];
			String[] day_wind_strong = new String[length];
			String[] night_wind_strong = new String[length];
			String[] switch_time = new String[length];
			for (int i = 0; i < length; i++) {
				JSONObject object = weatherInfoArray.getJSONObject(i);
				day_weather_status[i] = object.getString("fa");
				night_weather_status[i] = object.getString("fb");
				day_temp[i] = object.getString("fc");
				night_temp[i] = object.getString("fd");
				day_wind_direct[i] = object.getString("fe");
				night_wind_direct[i] = object.getString("ff");
				day_wind_strong[i] = object.getString("fg");
				night_wind_strong[i] = object.getString("fh");
				switch_time[i] = object.getString("fi");
			}
			weatherInfo.setDay_weather_status(day_weather_status);
			weatherInfo.setNight_weather_status(night_weather_status);
			weatherInfo.setDay_temp(day_temp);
			weatherInfo.setNight_temp(night_temp);
			weatherInfo.setDay_wind_direct(day_wind_direct);
			weatherInfo.setNight_wind_direct(night_wind_direct);
			weatherInfo.setDay_wind_strong(day_wind_strong);
			weatherInfo.setNight_wind_strong(night_wind_strong);
			weatherInfo.setSwitch_time(switch_time);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return weatherInfo;
	}


	public static String getWeatherStatusTransform(String weatherStatusSign,
			Context mContext) {
		String weatherStatus = "";
		String weatherStatusArray[];
		int index = 0;
		int sign = 0;
		weatherStatusArray = mContext.getResources().getStringArray(
				R.array.str_array_weather_status);
		if (weatherStatusSign != null && weatherStatusSign.length() > 0)
			sign = Integer.parseInt(weatherStatusSign);
		if (sign >= 0 && sign <= 31) {
			index = sign;
		} else if (sign == 53) {
			index = 32;
		} else {
			index = 33;
		}
		if (weatherStatusArray != null && weatherStatusArray.length > 0
				&& index < weatherStatusArray.length)
			weatherStatus = weatherStatusArray[index];
		return weatherStatus;
	}





}
