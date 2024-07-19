package org.hansung.oddeco.core.entity.item.event;

import java.util.List;
import java.util.Optional;

public interface ItemAttachableListenerManager {
    public abstract <L extends ItemAttachableListener> void addListener(L listener);

    public abstract void removeListener(ItemAttachableListener listener);

    public abstract void removeAllListeners();

    public abstract <L extends ItemAttachableListener> Optional<L> getListener(Class<L> listenerClass);

    public abstract <L extends ItemAttachableListener> List<L> getListeners(Class<L> listenerClass);
}
