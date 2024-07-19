package org.hansung.oddeco.core.entity.item.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class ItemAttachableListenerManagerBase implements ItemAttachableListenerManager {
    protected final Set<ItemAttachableListener> listeners;

    protected ItemAttachableListenerManagerBase() {
        this.listeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public <L extends ItemAttachableListener> void addListener(L listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(ItemAttachableListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        this.listeners.clear();
    }

    @Override
    public <L extends ItemAttachableListener> Optional<L> getListener(Class<L> listenerClass) {
        return this.listeners
                .stream()
                .filter(listenerClass::isInstance)
                .map(listenerClass::cast)
                .findFirst();
    }

    @Override
    public <L extends ItemAttachableListener> List<L> getListeners(Class<L> listenerClass) {
        return this.listeners
                .stream()
                .filter(listenerClass::isInstance)
                .map(listenerClass::cast)
                .toList();
    }
}
