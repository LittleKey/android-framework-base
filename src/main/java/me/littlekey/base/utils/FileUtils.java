package me.littlekey.base.utils;

import java.io.File;

/**
 * Created by nengxiangzhou on 15/9/24.
 */
public class FileUtils {
  private FileUtils() {}

  public static boolean delete(File file) {
    if (!file.exists()) {
      return true;
    }
    if (file.isFile()) {
      return file.delete();
    }
    if (file.isDirectory()) {
      for (File subFile : file.listFiles()) {
        delete(subFile);
      }
      return file.delete();
    }
    return false;
  }
}
