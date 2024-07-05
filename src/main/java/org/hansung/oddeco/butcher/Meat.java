package org.hansung.oddeco.butcher;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Meat {
    protected int rank; // 고기의 등급 입니다. 0~2 사이의 값 입니다.
    protected Material type = null; // 고기의 분류 입니다. 에라이 진짜 못해먹겠네
    protected int part; // 고기의 부위 입니다. 0~2 사이의 값 입니다.
    protected ItemStack item; // 아이템 입니다.
    protected final ConcurrentMap<Material, ArrayList<String>> meatTexts = new ConcurrentHashMap<>(); // 고기 텍스트 목록

    protected Random random;

    public Meat(int rank) {
        random = new Random(System.currentTimeMillis());
        this.rank = rank;
        this.type = null;
        this.part = random.nextInt(3);
        this.item = null;
        initListData();
    }

    public Meat(int rank, Material type) {
        this(rank);
        this.type = type;
        setItem();
    }

    public Meat(int rank, Material type, int part) {
        this(rank);
        this.type = type;
        this.part = part;
        setItem();
    }

    protected void setItem() {
        if (type == null) return;
        item = new ItemStack(type);
        ItemMeta data = item.getItemMeta();
        data.displayName(Component.text(meatTexts.get(type).get(part)).append(setRankText()));
        item.setItemMeta(data);
    }

    protected void initListData() {
        // 타입별 고기 목록
        meatTexts.put(Material.PORKCHOP, new ArrayList<>(List.of("뒷다리살", "항정살", "베이컨"))); // 돼지 고기
        meatTexts.put(Material.MUTTON, new ArrayList<>(List.of("소 등심", "목살", "안심"))); // 양고기
        meatTexts.put(Material.RABBIT, new ArrayList<>(List.of("토끼 앞다리살", "토끼 뒷다리살", "토끼 갈비살"))); // 토끼 고기
        meatTexts.put(Material.CHICKEN, new ArrayList<>(List.of("닭가슴살", "닭날개", "닭다리"))); // 닭고기
        meatTexts.put(Material.BEEF, new ArrayList<>(List.of("사태", "갈비", "소 등심살"))); // 소고기
    }

    public ItemStack getItem() {
        return item;
    }

    public String getIdentityText() {
        return String.format("special_%d_%s_%d", rank, this.type.getKey().getKey(), part);
    }

    private @NotNull Component setRankText() {
        if (rank == 1) return Component.text(" ★", TextColor.color(170, 170, 170));
        else if (rank == 2) return Component.text(" ★", TextColor.color(255, 170, 0));
        else return Component.text("");
    }
}
