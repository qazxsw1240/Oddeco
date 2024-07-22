package org.hansung.oddeco.butcher;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ButcherTrap {
    ItemStack item;

    public ButcherTrap() {
        item = new ItemStack(Material.COBWEB, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("덫"));
        item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return item;
    }
}
