package tr.unvercanunlu.randomized_set.service;

public interface Set<T> {

  void add(T item);

  void remove(T item);

  int size();

  void clear();

  boolean contains(T item);

}
