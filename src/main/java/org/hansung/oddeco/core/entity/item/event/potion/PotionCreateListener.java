package org.hansung.oddeco.core.entity.item.event.potion;

import org.hansung.oddeco.core.entity.item.Potion;

public interface PotionCreateListener extends PotionAttachableListener {
    public abstract void onPotionCreate(Potion potion);
}
