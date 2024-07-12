package org.hansung.oddeco.hunter;

import org.bukkit.Material;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HunterIngredients {
    private final ConcurrentMap<String, Material> items;

    public HunterIngredients() {
        items = new ConcurrentHashMap<>();
        // results
        items.put("zombie_spawn_egg", Material.ZOGLIN_SPAWN_EGG);
        items.put("skeleton_spawn_egg", Material.SKELETON_SPAWN_EGG);
        items.put("blaze_spawn_egg", Material.BLAZE_SPAWN_EGG);
        items.put("wither_skeleton_spawn_egg", Material.WITHER_SKELETON_SPAWN_EGG);
        items.put("shulker_spawn_egg", Material.SHULKER_SPAWN_EGG);
        items.put("enderman_spawn_egg", Material.ENDERMAN_SPAWN_EGG);
        items.put("spawner", Material.SPAWNER);
        // inputs
        items.put("glowstone_dust", Material.GLOWSTONE_DUST);
        items.put("rotten_flesh", Material.ROTTEN_FLESH);
        items.put("zombie_head", Material.ZOMBIE_HEAD);
        items.put("bone", Material.BONE);
        items.put("skeleton_skull", Material.SKELETON_SKULL);
        items.put("blaze_powder", Material.BLAZE_POWDER);
        items.put("piglin_head", Material.PIGLIN_HEAD);
        items.put("wither_skeleton_skull", Material.WITHER_SKELETON_SKULL);
        items.put("chorus_flower", Material.CHORUS_FLOWER);
        items.put("shulker_shell", Material.SHULKER_SHELL);
        items.put("dragon_head", Material.DRAGON_HEAD);
        items.put("ender_pearl", Material.ENDER_PEARL);
        items.put("ender_eye", Material.ENDER_EYE);
        items.put("netherite_ingot", Material.NETHERITE_INGOT);
        items.put("blaze_rod", Material.BLAZE_ROD);
        items.put("nether_star", Material.NETHER_STAR);

        items.put("hunter_wooden_sword", Material.WOODEN_SWORD);
        items.put("stripped_oak_log", Material.STRIPPED_OAK_LOG);
        items.put("iron_ingot", Material.IRON_INGOT);
        items.put("stick", Material.STICK);
    }

    public Material getItem(String string) {
        if (items.get(string) == null) throw new NullPointerException(String.format("%s에 대한 Material 값이 지정되지 않았습니다.", string));
        return items.get(string);
    }
}
