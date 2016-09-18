package me.littlekey.base.utils;


import android.util.Log;

import timber.log.Timber;

/**
 * Created by nengxiangzhou on 15/5/19.
 */
public class LogUtils {
  private LogUtils() {}

  public static void init(boolean debug) {
    if (debug) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new CrashReportingTree());
    }
  }

  /** A tree which logs important information for crash reporting. */
  private static class CrashReportingTree extends Timber.DebugTree {
    @Override protected void log(int priority, String tag, String message, Throwable t) {
      if (priority == Log.VERBOSE || priority == Log.DEBUG) {
        return;
      }

      if (t != null) {
        if (priority == Log.ERROR) {
          super.log(priority, tag, message, t);
        }
      }
    }
  }
}
