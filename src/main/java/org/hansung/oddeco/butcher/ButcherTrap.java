package org.hansung.oddeco.butcher;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public class ButcherTrap {
    ItemStack item;

    public ButcherTrap() {
        this.item = null;
    }

    // create butcher trap item
    public void createItem() {
        item = new ItemStack(Material.COBWEB, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("덫"));
        item.setItemMeta(meta);
    }

    // set butcher trap item
    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Nullable
    // get butcher trap item
    public ItemStack getItem() {
        return item;
    }
}
