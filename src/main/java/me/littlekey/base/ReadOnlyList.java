package me.littlekey.base;

/**
 * Created by nengxiangzhou on 15/9/16.
 */
public interface ReadOnlyList<T> {
  T getItem(int position);
  int size();
}
