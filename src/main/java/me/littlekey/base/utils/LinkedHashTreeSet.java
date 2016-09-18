package me.littlekey.base.utils;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * order, hash, set data structure, not thread safe
 * implements {@link List} and {@link ListIterator}
 * Created by littlekey on 15/8/10.
 */
public class LinkedHashTreeSet<V> implements List<V> {

  private HashMap<Integer, V> mHashMap;
  private ArrayList<Integer> mPositionList;

  /**
   * must be call this constructor for initialize
   */
  public LinkedHashTreeSet() {
    mHashMap = new HashMap<>();
    mPositionList = new ArrayList<>();
  }

  public LinkedHashTreeSet(List<V> data) {
    this();
    addAll(data);
  }

  @Override
  public int hashCode() {
    return mHashMap.hashCode() + mPositionList.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof LinkedHashTreeSet &&
        mHashMap.equals(((LinkedHashTreeSet)object).mHashMap) &&
        mPositionList.equals(((LinkedHashTreeSet)object).mPositionList);
  }

  @Override
  @NonNull
  public List<V> subList(int start, int end) {
    return getItems().subList(start, end);
  }

  @Override
  public boolean containsAll(@NonNull Collection<?> collection) {
    for (Object o: collection) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public LinkedTreeIterator<V> listIterator() {
    return new LinkedTreeIterator<>(this);
  }

  @Override
  @NonNull
  public ListIterator<V> listIterator(int location) {
    return new LinkedTreeIterator<>(this, location);
  }

  @Override
  @NonNull
  public Object[] toArray() {
    return getItems().toArray();
  }

  @Override
  @NonNull
  public <T> T[] toArray(@NonNull T[] array) {
    return getItems().toArray(array);
  }

  @Override
  @NonNull
  public LinkedTreeIterator<V> iterator() {
    return listIterator();
  }

  @Override
  public boolean contains(Object object) {
    // O(1)
    return mHashMap.containsKey(getKey(object));
  }

  @Override
  public int lastIndexOf(Object object) {
    return mPositionList.lastIndexOf(getKey(object));
  }

  @Override
  public boolean isEmpty() {
    return mPositionList.isEmpty();
  }

  @Override
  public V get(int position) {
    Integer key = mPositionList.get(position);
    return mHashMap.get(key);
  }

  @Override
  public void clear() {
    mPositionList.clear();
    mHashMap.clear();
  }

  @Override
  public int size() {
    return mPositionList.size();
  }

  @Override
  public int indexOf(Object value) {
    return mPositionList.indexOf(getKey(value));
  }

  @Override
  public V set(int position, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V remove(int position) {
    return mHashMap.remove(mPositionList.remove(position));
  }

  @Override
  public boolean remove(Object object) {
    return mPositionList.remove(getKey(mHashMap.remove(object)));
  }

  @Override
  public boolean add(V value) {
    Integer key = getKey(value);
    if (!mHashMap.containsKey(key)) {
      mHashMap.put(key, value);
      mPositionList.add(key);
    }
    return true;
  }

  @Override
  public void add(int position, V value) {
    Integer key = getKey(value);
    if (!mHashMap.containsKey(key)) {
      mHashMap.put(key, value);
      mPositionList.add(position, key);
    }
  }

  @Override
  public boolean removeAll(@NonNull Collection<?> list) {
    boolean rlt = false;
    LinkedTreeIterator iter = listIterator();
    while (iter.hasNext()) {
      Object value = iter.next();
      if (list.contains(value)) {
        rlt = true;
        iter.remove();
      }
    }
    return rlt;
  }

  @Override
  public boolean retainAll(@NonNull Collection<?> list) {
    boolean rlt = false;
    LinkedTreeIterator iter = listIterator();
    while (iter.hasNext()) {
      Object value = iter.next();
      if (!list.contains(value)) {
        rlt = true;
        iter.remove();
      }
    }
    return rlt;
  }

  @Override
  public boolean addAll(@NonNull Collection<? extends V> list) {
    if (list.isEmpty()) {
      return false;
    }
    for (V e: list) {
      add(e);
    }
    return true;
  }

  @Override
  public boolean addAll(int position, @NonNull Collection<? extends V> list) {
    if (list.isEmpty()) {
      return false;
    }
    for (V e: list) {
      add(position++, e);
    }
    return true;
  }

  public List<V> getItems() {
    Iterator<Integer> iter = mPositionList.iterator();
    ArrayList<V> result = new ArrayList<>();
    while (iter.hasNext()) {
      result.add(mHashMap.get(iter.next()));
    }
    return result;
  }

  /**
   * Override this method to custom Hash Algorithm {@link Object#hashCode()}
   * @param value hashable object
   * @return {@link Integer} hash value
   */
  public Integer getKey(Object value) {
    return value != null ? value.hashCode() : null;
  }

  public static final class LinkedTreeIterator<V> implements ListIterator<V> {

    /**
     * {@link LinkedTreeIterator#dirty}
     * for mark already {@link LinkedTreeIterator#remove} or {@link LinkedTreeIterator#add}
     * {@link LinkedTreeIterator#next}
     */
    private boolean dirty = false;
    private int mIndex;
    private WeakReference<LinkedHashTreeSet<V>> mWeakRefLinkedSet;

    public LinkedTreeIterator(LinkedHashTreeSet<V> set) {
      this(set, -1);
    }

    public LinkedTreeIterator(LinkedHashTreeSet<V> set, int location) {
      mWeakRefLinkedSet = new WeakReference<>(set);
      mIndex = location;
    }

    @Override
    public void set(V object) {
      LinkedHashTreeSet<V> set = mWeakRefLinkedSet.get();
      if (set != null) {
        set.set(mIndex, object);
      }
    }

    @Override
    public int nextIndex() {
      LinkedHashTreeSet<V> set = mWeakRefLinkedSet.get();
      if (set != null) {
        return Math.max(mIndex + 1, set.size());
      }
      throw new NoSuchElementException();
    }

    /** @hide */
    @Override
    public int previousIndex() {
//      LinkedHashTreeSet<V> set = mWeakRefLinkedSet.get();
//      if (set != null) {
//        return Math.max(mIndex - 1, -1);
//      }
//      throw new NoSuchElementException();
      throw new UnsupportedOperationException();
    }

    @Override
    public V next() {
      LinkedHashTreeSet<V> set = mWeakRefLinkedSet.get();
      if (set != null && hasNext()) {
        dirty = false;
        return set.get(++mIndex);
      }
      throw new NoSuchElementException();
    }

    @Override
    public void add(V object) {
      LinkedHashTreeSet<V> set = mWeakRefLinkedSet.get();
      if (mIndex == -1 || dirty || set == null) {
        throw new IllegalStateException();
      }
      dirty = true;
      set.add(mIndex++, object);
    }

    /** @hide */
    @Override
    public boolean hasPrevious() {
//      return mWeakRefLinkedSet.get() != null && mIndex > 0;
      throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
      LinkedHashTreeSet<V> set = mWeakRefLinkedSet.get();
      if (mIndex == -1 || dirty || set == null) {
        throw new IllegalStateException();
      }
      dirty = true;
      set.remove(mIndex--);
    }

    /** @hide */
    @Override
    public V previous() {
//      LinkedHashTreeSet<V> set = mWeakRefLinkedSet.get();
//      if (set != null && hasPrevious()) {
//        dirty = false;
//        return set.get(--mIndex);
//      }
//      throw new NoSuchElementException();
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
      LinkedHashTreeSet<V> set =mWeakRefLinkedSet.get();
      return set != null && mIndex + 1 < set.size();
    }

  }

}
