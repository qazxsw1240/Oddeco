package org.hansung.oddeco.butcher;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ButcherMeat extends Meat {
    private final Butcher butcher; // 플레이어의 레벨입니다.
    private final LivingEntity entity;

    private final Random random = new Random();

    public ButcherMeat(Butcher butcher, int rank) {
        this(butcher, rank, null);
    }

    public ButcherMeat(Butcher butcher, int rank, LivingEntity entity) {
        super(rank);
        // 변수 초기화
        this.butcher = butcher;
        this.rank = rank;
        this.entity = entity;
        this.type = ButcherReward.getRewardByEntity(this.butcher, this.entity);
        setItem();
    }
}