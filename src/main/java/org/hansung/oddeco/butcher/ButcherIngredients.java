package org.hansung.oddeco.butcher;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ButcherIngredients {
    private final ConcurrentMap<String, ItemStack> items;

    public ButcherIngredients() {
        items = new ConcurrentHashMap<>();
        items.put("meat_piece", new MeatPiece().getItem());
        items.put("chicken", new ItemStack(Material.CHICKEN));
        items.put("porkchop", new ItemStack(Material.PORKCHOP));
        items.put("mutton", new ItemStack(Material.MUTTON));
        items.put("beef", new ItemStack(Material.BEEF));
        items.put("rabbit", new ItemStack(Material.RABBIT));
        items.put("meat_piece_vegan", new ItemStack(Material.RED_DYE));
        items.put("butcher_trap", new ItemStack(Material.COBWEB));
        items.put("cobweb", new ItemStack(Material.COBWEB));
        items.put("trapped_chest", new ItemStack(Material.TRAPPED_CHEST));
        items.put("potato", new ItemStack(Material.POTATO));
        items.put("carrot", new ItemStack(Material.CARROT));
        items.put("melon_slice", new ItemStack(Material.MELON_SLICE));
        items.put("cocoa_beans", new ItemStack(Material.COCOA_BEANS));
        items.put("wheat", new ItemStack(Material.WHEAT));
    }

    @Nullable
    public ItemStack getIngredient(String ingredient) {
        return items.get(ingredient);
    }

    public ItemStack getSpecialCore(int rank) {
        ItemStack item = switch (rank) {
            case 0 -> new ItemStack(Material.GOLD_NUGGET);
            case 1 -> new ItemStack(Material.GOLD_INGOT);
            case 2 -> new ItemStack(Material.DIAMOND);
            default -> throw new IndexOutOfBoundsException("rank 값은 0부터 2 사이의 값을 가집니다.");
        };
        return item;
    }
}
