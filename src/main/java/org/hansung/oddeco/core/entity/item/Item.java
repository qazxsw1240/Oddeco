package org.hansung.oddeco.core.entity.item;

import org.bukkit.inventory.ItemStack;
import org.hansung.oddeco.core.entity.item.event.ItemAttachableListenerManager;

public interface Item extends ItemAttachableListenerManager {
    public abstract String getId();

    public abstract String getName();

    public abstract ItemStack getItemStack();
}
