package com.example.viewpagerdemo2.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
}
