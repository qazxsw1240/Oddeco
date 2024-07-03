package org.hansung.oddeco.core.sql;

import java.util.Collection;
import java.util.Optional;

public interface CrudRepository<T, K> {
    public abstract boolean contains(K key);

    public abstract Optional<T> get(K key);

    public abstract Collection<T> getAll();

    public abstract T create(K key);

    public abstract void insert(T element);

    public abstract void update(T element);

    public abstract void remove(K key);
}
