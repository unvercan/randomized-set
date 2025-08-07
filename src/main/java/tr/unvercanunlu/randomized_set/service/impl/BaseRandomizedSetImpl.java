package tr.unvercanunlu.randomized_set.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.randomized_set.service.RandomizedSet;

@Slf4j
public abstract class BaseRandomizedSetImpl<T> implements RandomizedSet<T> {

  // no need synchronized collections when all methods synchronized
  private final List<T> itemList = new ArrayList<>(); // Collections.synchronizedList(new ArrayList<>())
  private final Map<T, Integer> indexMap = new HashMap<>(); // new ConcurrentHashMap<>()

  @Override
  public synchronized T getRandom() {
    if (indexMap.isEmpty()) {
      String message = "Set empty!";

      log.error(message);

      throw new NoSuchElementException(message);
    }

    int gapStart = 0;
    int gapEnd = itemList.size();

    int randomIndex = ThreadLocalRandom.current()
        .nextInt(gapStart, gapEnd);

    return itemList.get(randomIndex);
  }

  @Override
  public synchronized void add(T item) {
    if (indexMap.containsKey(item)) {
      log.info("Set already contains item={}", Objects.toString(item, "null"));

      return;
    }

    itemList.add(item);

    int itemIndex = itemList.size() - 1;

    indexMap.put(item, itemIndex);
  }

  @Override
  public synchronized void remove(T item) {
    if (!indexMap.containsKey(item)) {
      log.info("Set does not contain item={}", Objects.toString(item, "null"));

      return;
    }

    Integer itemIndex = indexMap.get(item);

    if (itemIndex == null) {
      String message = "Internal problem: item index not found!";

      log.error(message);

      throw new IllegalStateException(message);
    }

    int lastIndex = itemList.size() - 1;

    if (itemIndex != lastIndex) {
      T lastItem = itemList.get(lastIndex);

      Collections.swap(itemList, itemIndex, lastIndex);

      indexMap.put(lastItem, itemIndex);
    }

    itemList.remove(lastIndex);
    indexMap.remove(item);
  }

  @Override
  public synchronized int size() {
    return indexMap.size();
  }

  @Override
  public synchronized void clear() {
    itemList.clear();
    indexMap.clear();
  }

  @Override
  public synchronized boolean contains(T item) {
    return indexMap.containsKey(item);
  }

}
