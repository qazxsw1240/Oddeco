package org.hansung.oddeco.core.entity.item.event.weapon;

import org.hansung.oddeco.core.entity.item.Weapon;

public interface WeaponInteractListener extends WeaponAttachableListener {
    public abstract void onWeaponInteract(Weapon weapon);
}
