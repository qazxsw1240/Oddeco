package org.hansung.oddeco.core;

import java.util.Collection;
import java.util.Optional;

public interface ReadonlyRepository<T, K> {
    public abstract boolean contains(K key);

    public abstract Optional<T> get(K key);

    public abstract Collection<T> getAll();
}
