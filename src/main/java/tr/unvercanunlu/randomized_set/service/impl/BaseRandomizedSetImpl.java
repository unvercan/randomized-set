package tr.unvercanunlu.randomized_set.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.randomized_set.service.RandomizedSet;

@Slf4j
public abstract class BaseRandomizedSetImpl<T> implements RandomizedSet<T> {

  private final List<T> itemList = Collections.synchronizedList(new ArrayList<>());
  private final ConcurrentMap<T, Integer> indexMap = new ConcurrentHashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  protected abstract boolean isItemValid(T item);

  @Override
  public T getRandom() {
    lock.readLock().lock();
    try {
      if (indexMap.isEmpty()) {
        String message = "Set empty!";
        log.error(message);
        throw new NoSuchElementException(message);
      }

      int randomIndex = getRandomIndex();
      log.debug("Item with index={} is selected randomly from the set.", randomIndex);

      T randomItem = itemList.get(randomIndex);
      log.info("item={} is retrieved from the set randomly.", randomItem);

      return randomItem;
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public boolean add(T item) {
    validateItem(item);

    lock.writeLock().lock();
    try {
      if (indexMap.containsKey(item)) {
        log.warn("Set already contains! item={}", item);
        return false;
      }

      itemList.add(item);
      int itemIndex = itemList.size() - 1;
      log.debug("Item with index={} is added to the set.", itemIndex);

      indexMap.put(item, itemIndex);
      log.info("item={} is added to the set.", item);

      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean remove(T item) {
    validateItem(item);

    lock.writeLock().lock();
    try {
      Integer itemIndex = indexMap.get(item);
      if (itemIndex == null) {
        log.warn("Set does not contain! item={}", item);
        return false;
      }

      log.debug("item={} is at index={}", item, itemIndex);

      int lastIndex = itemList.size() - 1;
      if (itemIndex != lastIndex) {
        T lastItem = itemList.get(lastIndex);
        Collections.swap(itemList, itemIndex, lastIndex);
        indexMap.put(lastItem, itemIndex);
        log.debug("Item with index={} is swapped with item with index={}", itemIndex, lastIndex);
      }

      itemList.remove(lastIndex);
      log.debug("The last item is removed.");

      indexMap.remove(item);
      log.info("item={} is removed from the set.", item);

      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public int size() {
    lock.readLock().lock();
    try {
      int size = indexMap.size();
      log.info("The set contains {} items.", size);

      return size;
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void clear() {
    lock.writeLock().lock();
    try {
      itemList.clear();
      indexMap.clear();
      log.info("The set is cleared.");
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean contains(T item) {
    lock.readLock().lock();
    try {
      boolean result = indexMap.containsKey(item);

      if (result) {
        log.info("The set contains item={}", item);
      } else {
        log.info("The set does not contain item={}", item);
      }

      return result;
    } finally {
      lock.readLock().unlock();
    }
  }

  private int getRandomIndex() {
    return ThreadLocalRandom.current()
        .nextInt(itemList.size());
  }

  private void validateItem(T item) {
    if (!isItemValid(item)) {
      String message = "Item invalid! item=%s".formatted(item);
      log.error(message);
      throw new IllegalArgumentException(message);
    }
  }

}
