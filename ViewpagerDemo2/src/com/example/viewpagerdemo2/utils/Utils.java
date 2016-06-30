package com.example.viewpagerdemo2.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.example.viewpagerdemo2.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.Time;
import android.util.Log;

public class Utils {

	private static final String TAG = "utils";
	private final static String DEF_ZH_PATTERN = "[\u4e00-\u9fa5]+";//至少匹配一个汉字
	/**
	 * 获取当前应用版本号
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String getVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}
	/**
     * @brief     中文字符串转换函数。
     * @author    joychang
     * @param[in] str 要转换的字符串。
     * @param[in] charset 字符串编码。
     * @return    UTF8形式字符串。
     * @note      将str中的中文字符转换为UTF8编码形式。
     * @throws    UnsupportedEncodingException 不支持的字符集
     */
    public static String encode(String str, String charset) throws UnsupportedEncodingException {
        Log.d(TAG, "_encode() start");

        String result = null;

        if ((str != null) && (charset != null)) {
            try {
                Pattern p = Pattern.compile(DEF_ZH_PATTERN, 0);
                Matcher m = p.matcher(str);

                StringBuffer b = new StringBuffer();
                while (m.find()) {
                    m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
                }

                m.appendTail(b);

                result = b.toString();
            }
            catch (PatternSyntaxException e) {
                e.printStackTrace();
            }
        }
        else {
            if (str == null) {
                Log.e(TAG, "encode(): str is null");
            }

            if (charset == null) {
                Log.e(TAG, "encode(): charset is null");
            }
        }

        Log.d(TAG, "encode() end");

        return result;
    }
    public static String getStringTime(String type)
    {
    	Time t = new Time();
    	t.setToNow();
    	String hour = t.hour < 10?"0"+(t.hour):t.hour+"";
    	String minute = t.minute < 10 ?"0"+(t.minute):t.minute+"";
    	return hour+type+minute;
    }
    public static String getStringData(Context context)
    {
    	final Calendar c = Calendar.getInstance();
    	c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    	String mMonth = String.valueOf(c.get(Calendar.MONTH)+1);
    	String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
    	String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
    	String[] monthName = context.getResources().getStringArray(R.array.str_array_month);
    }
}
