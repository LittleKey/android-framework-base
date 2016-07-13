package com.yuanqi.base.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by nengxiangzhou on 15/7/31.
 */
public class DeviceConfig {
  public static final int DEFAULT_TIMEZONE = 8;
  protected static final String UNKNOW = "Unknown";
  private static final String MOBILE_NETWORK = "2G/3G";
  private static final String WIFI = "Wi-Fi";

  private DeviceConfig() {}

  /**
   * 获取应用的版本号 (versionCode)
   *
   * @param context ApplicationContext
   * @return 应用程序的版本号，即Mandifest 中的 versionCode 如果没指定则返回“Unknown”
   */
  public static int getAppVersionCode(Context context) {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(
          context.getPackageName(), 0);
      return pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      return 1;
    }
  }

  /**
   * 获取应用的版本号 (versionCode)
   *
   * @param context ApplicationContext
   * @return 应用程序的版本号，即Mandifest 中的 versionCode 如果没指定则返回“Unknown”
   */
  public static String getAppVersionCodeString(Context context) {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(
          context.getPackageName(), 0);
      int version_code = pInfo.versionCode;
      return String.valueOf(version_code);
    } catch (PackageManager.NameNotFoundException e) {
      return DeviceConfig.UNKNOW;
    }
  }

  /**
   * 读取应用的版本（version name）
   * 
   * @param context ApplicationContext
   * @return 返回 version name， 没有返回“Unknown”
   */
  public static String getAppVersionName(Context context) {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(
          context.getPackageName(), 0);
      return pInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      return DeviceConfig.UNKNOW;
    }
  }

  public static boolean checkPermission(Context context, String permission) {
    PackageManager pm = context.getPackageManager();
    return pm.checkPermission(permission,
        context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
  }

  public static String getDeviceId(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    String imei = "";
    if (tm == null) {
      Timber.e("No IMEI.");
    } else {
      try {
        if (checkPermission(context, "android.permission.READ_PHONE_STATE")) {
          imei = tm.getDeviceId();
        }
      } catch (Exception e) {
        Timber.e("No IMEI.", e.getMessage());
      }
    }
    if (TextUtils.isEmpty(imei)) {
      Timber.e("No IMEI.");
      imei = getMac(context);
      if (TextUtils.isEmpty(imei)) {
        Timber.e("Failed to take mac as IMEI. Try to use Secure.ANDROID_ID instead.");
        imei = Settings.Secure.getString(context.getContentResolver(), "android_id");
        Timber.i("getDeviceId: Secure.ANDROID_ID: " + imei);
        return imei;
      }
    }
    return imei;
  }

  public static String getDeviceIdUmengMD5(Context context) {
    return FormatUtils.getUmengMD5(getDeviceId(context));
  }

  /**
   * 获取运营商信息
   *
   * @param context ApplicationContext
   * @return the alphabetic name of current registered operator. 出错返回"Unknow"
   */
  public static String getNetworkOperatorName(Context context) {
    try {
      TelephonyManager tm = (TelephonyManager) context
          .getSystemService(Context.TELEPHONY_SERVICE);

      if (tm == null) {
        return DeviceConfig.UNKNOW;
      }
      return tm.getNetworkOperatorName();
    } catch (Exception e) {
      e.printStackTrace();
      return DeviceConfig.UNKNOW;
    }
  }

  /**
   * 获取设备屏幕分辨率
   *
   * @param context ApplicationContext
   * @return 如:800*480 出错返回"Unknow"
   */
  public static String getDisplayResolution(Context context) {
    try {
      DisplayMetrics metrics = new DisplayMetrics();
      WindowManager wm = (WindowManager) (context
          .getSystemService(Context.WINDOW_SERVICE));
      wm.getDefaultDisplay().getMetrics(metrics);

      int width = metrics.widthPixels;
      int height = metrics.heightPixels;

      return String.valueOf(height) + "*" + String.valueOf(width);
    } catch (Exception e) {
      e.printStackTrace();
      return DeviceConfig.UNKNOW;
    }
  }

  /**
   * Get the mobile network access mode.
   *
   * @param context ApplicationContext
   * @return A 2-elements String array, 1st specifies the network type, the
   *         2nd specifies the network subtype. If the network cannot be
   *         retrieved, "Unknown" is filled instead.
   */
  public static String[] getNetworkAccessMode(Context context) {
    String[] res = new String[] {DeviceConfig.UNKNOW, DeviceConfig.UNKNOW};

    try {
      PackageManager pm = context.getPackageManager();
      if (pm.checkPermission(Manifest.permission.ACCESS_NETWORK_STATE,
          context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
        res[0] = DeviceConfig.UNKNOW;
        return res;
      }

      ConnectivityManager connectivity = (ConnectivityManager) context
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivity == null) {
        res[0] = DeviceConfig.UNKNOW;
        return res;
      } else {
        NetworkInfo wifi_network = connectivity
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi_network.getState() == NetworkInfo.State.CONNECTED) {
          res[0] = DeviceConfig.WIFI;
          return res;
        }
        NetworkInfo mobile_network = connectivity
            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile_network.getState() == NetworkInfo.State.CONNECTED) {
          res[0] = DeviceConfig.MOBILE_NETWORK;
          res[1] = mobile_network.getSubtypeName();
          return res;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return res;
  }

  public static boolean isWiFiAvailable(Context var0) {
    return WIFI.equals(getNetworkAccessMode(var0)[0]);
  }

  /**
   * <p>
   * True if the device is connected or connection to network.
   * </p>
   * 需要权限: <code>android.permission.ACCESS_NETWORK_STATE</code>
   * </p>
   *
   * @param context ApplicationContext
   * @return 如果当前有网络连接返回 true 如果网络状态访问权限或没网络连接返回false
   */
  public static boolean isOnline(Context context) {
    try {
      ConnectivityManager cm = (ConnectivityManager) context
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo ni = cm.getActiveNetworkInfo();
      return ni != null && ni.isConnectedOrConnecting();
    } catch (Exception e) {
      return true;
    }
  }

  public static boolean isSdCardWritable() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  /**
   * 根据系统设置的区域(locale)获取时区
   *
   * @param context ApplicationContext
   * @return 返回所在时区 如 locale 为 zh_CN 将返回: 8
   */
  public static int getTimeZone(Context context) {
    // Get country code and locale.
    try {
      Locale locale = getLocale(context);
      Calendar calendar = Calendar.getInstance(locale);
      return calendar.getTimeZone().getRawOffset() / (3600 * 1000);
    } catch (Exception e) {
      Timber.i("error in getTimeZone", e);
    }

    return DEFAULT_TIMEZONE;
  }

  /**
   * 读取国家和语言
   * 
   * @param context ApplicationContext
   * @return 返回数组 String[]{Country, Language}, 读不到返回 String[]{"Unknown","Unknown"}
   */
  public static String[] getLocaleInfo(Context context) {
    String[] cl = new String[2];

    try {
      Locale locale = getLocale(context);

      if (locale != null) {
        cl[0] = locale.getCountry();
        cl[1] = locale.getLanguage();
      }

      if (TextUtils.isEmpty(cl[0]))
        cl[0] = "Unknown";
      if (TextUtils.isEmpty(cl[1]))
        cl[1] = "Unknown";

      return cl;
    } catch (Exception e) {
      Timber.e("error in getLocaleInfo" + e.getMessage());
    }

    return cl;

  }

  /**
   * 读取 user config locale , 取不到 返回 default locale
   * 
   * @param context ApplicationContext
   * @return
   */
  private static Locale getLocale(Context context) {
    Locale locale = null;
    try {
      Configuration userConfig = new Configuration();
      Settings.System.getConfiguration(context.getContentResolver(),
          userConfig);
      locale = userConfig.locale;
    } catch (Exception e) {
      Timber.e("fail to read user config locale");
    }

    if (locale == null) {
      locale = Locale.getDefault();
    }

    return locale;
  }

  /**
   * 读取 Umeng Appkey
   * 
   * @param context
   * @return 返回 Appkey
   */
  public static String getAppkey(Context context) {
    return getMetaData(context, "UMENG_APPKEY");
  }

  public static String getMetaData(Context var0, String var1) {
    try {
      PackageManager var2 = var0.getPackageManager();
      ApplicationInfo var3 = var2.getApplicationInfo(var0.getPackageName(), PackageManager.GET_META_DATA);
      if (var3 != null && var3.metaData != null) {
        String var4 = var3.metaData.getString(var1);
        if (var4 != null) {
          return var4.trim();
        }
      }
    } catch (Exception var5) {
      var5.printStackTrace();
    }

    Timber.e("Could not read meta-data %s from AndroidManifest.xml.", var1);
    return null;
  }

  /**
   * 读取手机MAC地址
   * 
   * @param context
   * @return 返回mac地址
   */
  public static String getMac(Context context) {
    try {
      WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      if (checkPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
      } else {
        Timber.w("Could not get mac address.[no permission android.permission.ACCESS_WIFI_STATE");
      }
    } catch (Exception e) {
      Timber.w("Could not get mac address." + e.toString());
    }
    return "";
  }

  /**
   * 读取分辨率
   * 
   * @param context
   * @return 返回分辨率 width*height ,否则返回 Unknown
   */
  public static String getResolution(Context context) {
    try {
      DisplayMetrics metrics = new DisplayMetrics();
      WindowManager wm = (WindowManager) (context
          .getSystemService(Context.WINDOW_SERVICE));
      wm.getDefaultDisplay().getMetrics(metrics);

      int width = -1, height = -1;

      if ((context.getApplicationInfo().flags
          & ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES) == 0) {
        width = reflectMetrics(metrics, "noncompatWidthPixels");
        height = reflectMetrics(metrics, "noncompatHeightPixels");
      }

      if (width == -1 || height == -1) {
        width = metrics.widthPixels;
        height = metrics.heightPixels;
      }

      StringBuffer msb = new StringBuffer();
      msb.append(width);
      msb.append("*");
      msb.append(height);

      return msb.toString();
    } catch (Exception e) {
      Timber.e("read resolution fail" + e.getMessage());
    }
    return "Unknown";

  }

  private static int reflectMetrics(Object metrics, String field) {
    try {
      Field f = DisplayMetrics.class.getDeclaredField(field);
      f.setAccessible(true);
      return f.getInt(metrics);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * 读取运营商信息
   * 
   * @param context
   * @return 返回运营商信息，否则返回 Unknown
   */
  public static String getOperator(Context context) {
    // Get Carrier
    try {
      return ((TelephonyManager) context
          .getSystemService(Context.TELEPHONY_SERVICE))
              .getNetworkOperatorName();
    } catch (Exception e) {
      Timber.i("read carrier fail" + e.getMessage());
    }
    return "Unknown";
  }

  /**
   * 读取渠道信息
   * 
   * @param context
   * @return 返回渠道号，否则返回 Unknown
   */
  public static String getChannel(Context context) {
    String channel = getMetaData(context, "UMENG_CHANNEL");
    return channel == null ? "Unknown" : channel;
  }

  /**
   * 返回包名
   * 
   * @param context
   * @return
   */
  public static String getPackageName(Context context) {
    return context.getPackageName();
  }

  /**
   * 返回应用名
   * 
   * @param context
   * @return
   */
  public static String getApplicationLabel(Context context) {
    return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
  }

  /**
   * 返回当前应用所在工程的状态
   * 
   * @param context
   * @return True 工程在Debug 状态， False release 状态
   */
  public static boolean isDebug(Context context) {
    try {
      return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    } catch (Exception e) {
      return false;
    }
  }

}
