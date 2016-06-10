package com.yuanqi.base.utils;

import java.util.Collection;

/**
 * Created by nengxiangzhou on 15/5/9.
 */
public class CollectionUtils {
  private CollectionUtils() {}

  public static <T> boolean isEmpty(Collection<T> collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T> void add(Collection<T> collection, T item) {
    if (item != null) {
      collection.add(item);
    }
  }

  public static <T> void addAll(Collection<T> collection, Collection<T> items) {
    if (items != null) {
      collection.addAll(items);
    }
  }
}
