package org.hansung.oddeco.core;

public interface CrudRepository<T, K> extends ReadonlyRepository<T, K> {
    public abstract T create(K key);

    public abstract void insert(T element);

    public abstract void update(T element);

    public abstract void remove(K key);
}
