package org.hansung.oddeco.core.entity.item;

import java.util.Optional;

public interface ItemCache {
    public abstract <T> Optional<T> get(String id, Class<T> itemClass);
}
