# Randomized Set

An efficient, thread-safe Java implementation of a `RandomizedSet` data structure that supports:

- `O(1)` average time complexity for:
    - Insertion (`add`)
    - Deletion (`remove`)
    - Lookup (`contains`)
    - Random element retrieval (`getRandom`)

## Features

- Thread-safe: All public methods are synchronized
- Uses `ArrayList` + `HashMap` for efficient operations
- Logging support via SLF4J
- Graceful handling of empty state and internal inconsistencies

## Interface

```java
interface RandomizedSet<T> {
  void add(T item);
  void remove(T item);
  boolean contains(T item);
  int size();
  void clear();
  T getRandom(); // Throws NoSuchElementException if empty
}
````

## Example Usage

```java
RandomizedSet<Integer> set = new RandomizedSetImpl<>();
set.add(10);
set.add(20);
set.remove(10);
int random = set.getRandom(); // returns 20
```

## Technologies

* Java 21
* Maven
* SLF4J (logging)
* JUnit 5 + Mockito (for testing)
* JaCoCo (code coverage)
* Lombok

## Build

```bash
mvn clean install
```
