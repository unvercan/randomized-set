# Randomized Set (Java)
Thread-safe **RandomizedSet** with O(1) `add`, `remove`, `contains`, `size`, and `getRandom`.

## Features
- Constant-time operations  
- Swap-remove trick for efficient deletion  
- Thread-safe with `ReadWriteLock`  
- Abstract validation via `isItemValid`  

## Usage
```java
public class DefaultRandomizedSet<T> extends BaseRandomizedSetImpl<T> {
    @Override
    protected boolean isItemValid(T item) {
        return item != null;
    }
}

RandomizedSet<Integer> set = new DefaultRandomizedSet<>();
set.add(1);
set.add(2);
System.out.println(set.getRandom()); // e.g. 2
set.remove(1);
````

## Complexity
* `add`, `remove`, `contains`, `size`, `getRandom` → **O(1)**
