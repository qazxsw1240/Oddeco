package org.hansung.oddeco.butcher;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MeatPiece {
    private ItemStack item;

    public MeatPiece() {
        item = new ItemStack(Material.RED_DYE, 2);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("고깃조각"));
        item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return item;
    }
}
