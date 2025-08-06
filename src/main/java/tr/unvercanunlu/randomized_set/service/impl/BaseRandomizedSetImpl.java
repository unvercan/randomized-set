package tr.unvercanunlu.randomized_set.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import tr.unvercanunlu.randomized_set.exception.EmptySetException;
import tr.unvercanunlu.randomized_set.service.RandomizedSet;

public abstract class BaseRandomizedSetImpl<T> implements RandomizedSet<T> {

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  // no need synchronized collections when read-write lock used appropriately
  private final List<T> list = new ArrayList<>(); // Collections.synchronizedList(new ArrayList<>())
  private final Map<T, Integer> map = new HashMap<>(); // new ConcurrentHashMap<>()

  @Override
  public T getRandom() {
    lock.readLock().lock();

    try {
      if (map.isEmpty()) {
        throw new EmptySetException();
      }

      Random random = ThreadLocalRandom.current();
      int index = random.nextInt(0, list.size());

      return list.get(index);

    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void add(T item) {
    lock.writeLock().lock();

    try {
      if (!map.containsKey(item)) {
        list.add(item);
        int itemIndex = list.size() - 1;
        map.put(item, itemIndex);
      }

    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void remove(T item) {
    lock.writeLock().lock();

    try {
      if (map.containsKey(item)) {
        int itemIndex = map.get(item);
        int otherIndex = list.size() - 1;

        if (itemIndex == otherIndex) {
          list.remove(itemIndex);
          map.remove(item);
        } else {
          T other = list.get(otherIndex);
          list.set(itemIndex, other);
          list.remove(otherIndex);
          map.put(other, itemIndex);
          map.remove(item);
        }
      }

    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public int size() {
    lock.readLock().lock();

    try {
      return map.size();

    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void clear() {
    lock.writeLock().lock();

    try {
      list.clear();
      map.clear();

    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean contains(T item) {
    lock.readLock().lock();

    try {
      return map.containsKey(item);

    } finally {
      lock.readLock().unlock();
    }
  }

}
