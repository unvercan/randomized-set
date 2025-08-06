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

  // no need synchronized collections when read-write lock used appropriately
  private final List<T> list = new ArrayList<>(); // Collections.synchronizedList(new ArrayList<>())
  private final Map<T, Integer> map = new HashMap<>(); // new ConcurrentHashMap<>()

  // read-write lock
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  @Override
  public T getRandom() {
    readWriteLock.readLock().lock();

    try {
      if (map.isEmpty()) {
        throw new EmptySetException();
      }

      Random random = ThreadLocalRandom.current();
      int index = random.nextInt(0, list.size());

      return list.get(index);

    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override
  public void add(T item) {
    readWriteLock.writeLock().lock();

    try {
      if (!map.containsKey(item)) {
        list.add(item);
        int itemIndex = list.size() - 1;
        map.put(item, itemIndex);
      }

    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  @Override
  public void remove(T item) {
    readWriteLock.writeLock().lock();

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
      readWriteLock.writeLock().unlock();
    }
  }

  @Override
  public int size() {
    readWriteLock.readLock().lock();

    try {
      return map.size();

    } finally {
      readWriteLock.readLock().unlock();
    }
  }

  @Override
  public void clear() {
    readWriteLock.writeLock().lock();

    try {
      list.clear();
      map.clear();

    } finally {
      readWriteLock.writeLock().unlock();
    }
  }

  @Override
  public boolean contains(T item) {
    readWriteLock.readLock().lock();

    try {
      return map.containsKey(item);

    } finally {
      readWriteLock.readLock().unlock();
    }
  }

}
