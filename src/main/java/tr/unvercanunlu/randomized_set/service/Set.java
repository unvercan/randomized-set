package tr.unvercanunlu.randomized_set.service;

public interface Set<T> {

  boolean add(T item);

  boolean remove(T item);

  int size();

  void clear();

  boolean contains(T item);

}
