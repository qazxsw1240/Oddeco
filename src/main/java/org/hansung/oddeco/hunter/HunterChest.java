package org.hansung.oddeco.hunter;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface HunterChest {
    ArrayList<Chest> chests = new ArrayList<>();
    public static void addChest(Chest chest) {
        chests.add(chest);
    }
    public static void updateChest() {
        for (Chest chest: chests) {
            chest.getBlockInventory().addItem(new ItemStack(Material.ARROW, 10));
        }
    }
}
