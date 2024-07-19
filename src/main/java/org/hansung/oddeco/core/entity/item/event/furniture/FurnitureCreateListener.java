package org.hansung.oddeco.core.entity.item.event.furniture;

import org.hansung.oddeco.core.entity.item.Furniture;

public interface FurnitureCreateListener extends FurnitureAttachableListener {
    public abstract void onFurnitureCreate(Furniture furniture);
}
