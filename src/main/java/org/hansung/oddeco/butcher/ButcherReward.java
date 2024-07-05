package org.hansung.oddeco.butcher;

import org.bukkit.Material;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Random;

public interface ButcherReward {
    public static Material getMaterialByEntity(LivingEntity entity) {
        Material type;
        switch (entity) {
            case Chicken chicken -> type = Material.CHICKEN;
            case Pig pig -> type = Material.PORKCHOP;
            case Rabbit rabbit -> type = Material.RABBIT;
            case Cow cow -> type = Material.BEEF;
            case Sheep sheep -> type = Material.MUTTON; // 이 Sheep Duck 같으니라고
            case null, default -> type = null;
        }
        return type;
    }

    public static Material getRewardByEntity(Butcher butcher, LivingEntity entity) {
        Material type;
        switch (entity) {
            case Chicken chicken -> type = Material.CHICKEN;
            case Pig pig when butcher.getLevel() >= 2 ->
                    type = Material.PORKCHOP;
            case Rabbit rabbit when butcher.getLevel() >= 2 ->
                    type = Material.RABBIT;
            case Cow cow when butcher.getLevel() >= 5 -> type = Material.BEEF;
            case Sheep sheep when butcher.getLevel() >= 6 ->
                    type = Material.MUTTON; // 이 Sheep Duck 같으니라고
            case null, default -> {
                if (entity instanceof Animals) type = null;
                else type = randomMeat(butcher);
            }
        }
        return type;
    }

    private static Material randomMeat(Butcher butcher) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        ArrayList<Material> meats = new ArrayList<>();
        if (butcher.getLevel() < 2) {
            return Material.CHICKEN;
        }

        if (butcher.getLevel() >= 6) {
            meats.add(Material.MUTTON);
        }
        if (butcher.getLevel() >= 5) {
            meats.add(Material.BEEF);
        }
        if (butcher.getLevel() >= 2) {
            meats.add(Material.PORKCHOP);
            meats.add(Material.RABBIT);
        }
        meats.add(Material.CHICKEN);

        return meats.get(random.nextInt(meats.size() - 1));
    }
}
