package me.littlekey.base.utils;

import android.content.res.Resources;
import android.util.TypedValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

/**
 * Created by nengxiangzhou on 15/7/31.
 */
public class FormatUtils {
  public static String getMD5(String str) {
    if (str == null)
      return null;
    try {
      byte[] defaultBytes = str.getBytes();
      MessageDigest algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update(defaultBytes);
      byte messageDigest[] = algorithm.digest();
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++) {
        hexString.append(String.format("%02X", messageDigest[i]));
      }

      return hexString.toString();

    } catch (Exception e) {
      return str.replaceAll("[^[a-z][A-Z][0-9][.][_]]", "");
    }
  }

  public static String getUmengMD5(String input) {
    try {
      // Create MD5 Hash
      MessageDigest digest = java.security.MessageDigest
          .getInstance("MD5");
      digest.update(input.getBytes());
      byte messageDigest[] = digest.digest();

      // Create Hex String
      StringBuilder hexString = new StringBuilder();
      for (byte aMessageDigest : messageDigest) {
        int var = 0xFF & aMessageDigest;
        hexString.append(Integer.toHexString(var));
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
      Timber.e("getMD5 error:" + e.getMessage());
    }
    return "";
  }

  /**
   * Get pixel size of dips.
   *
   * @param dps
   * @return converted pixel size.
   */
  public static int dipsToPix(float dps) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
        Resources.getSystem().getDisplayMetrics());
  }
}
