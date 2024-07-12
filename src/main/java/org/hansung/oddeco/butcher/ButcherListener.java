package org.hansung.oddeco.butcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.loot.LootTable;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.core.json.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

// 배서현 씨의 재밌넥 참여를 온 힘을 다해 축하드립니다!!!
public class ButcherListener implements Listener {
    private Random random = new Random();

    private final JavaPlugin plugin;
    private final Probability probability;
    private final ConcurrentMap<Player, Butcher> butchers = new ConcurrentHashMap<>();

    public ButcherListener(JavaPlugin plugin) {
        Probability probability1;
        this.plugin = plugin;

        plugin.getLogger().info("Butcher Listener Registed.");

        // 갓=태명님이 짜주신 /resources 폴더의 파일 읽는 개쩌는 코드 (뭔지 모름)
        try (InputStream in = getClass().getResourceAsStream("/ButcherProbability.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String content = reader.lines().collect(Collectors.joining());
            // plugin.getLogger().info(String.format("content find: %s", content));
            probability1 = new ObjectMapper().readValue(content, Probability.class);
        } catch (IOException e) {
            probability1 = null;
            plugin.getLogger().info(String.format("%s", e.getMessage()));
        }
        probability = probability1;

        // 레시피 추가를 위한 뭐시깽이
        setMeatPieceRecipe();
        ConcurrentMap<Character, Object> data = new ConcurrentHashMap<>();
        data.put('1', Material.GOLD_INGOT);
        data.put('2', Material.GLOWSTONE_DUST);
        data.put('M', Material.RED_DYE);

        JsonElement jsonElement = JsonUtil.of("/butcher_recipes.json");
        ButcherRepository repository = new ButcherRepository(jsonElement.getAsJsonObject());
        repository.getKeys().forEach(meat -> createRecipe(meat.getIdentityText(), meat.getItem(), repository.get(meat).get(), data));
    }

    // 최적화 필요
    public void createRecipe(String key, ItemStack item, String[] values, ConcurrentMap<Character, Object> ingredient) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, item);
        recipe.shape(values[0], values[1], values[2]); // ㅖ?
        ingredient.forEach((key1, value) -> {
            for (String string : recipe.getShape()) {
                if (!string.contains(key1.toString())) continue;
                if (value instanceof Material) recipe.setIngredient(key1, (Material)value);
                else if (value instanceof ItemStack) recipe.setIngredient(key1, (ItemStack)value);
                else throw new RuntimeException("레시피 아이템이 잘못됨");
            }
        });
        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        butchers.put(event.getPlayer(), new Butcher(1));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // 일단 이 녀석이 Butcher 인지 확인해야 하는데 이건 플레이어 관련 클래스를 제작한 태명님 몫일 겁니다... 아마도

        // Butcher인 경우 아래 코드 실행
        if (event.getEntity() instanceof Mob) {
            ButcherMeat meat; // 이것이 고기다! 절망편
            int rank; // 고기의 질이 아주 좋구만...
            boolean isAnimal = false; // 동물을 죽였어!!! 동물학대야 이거!!! 당신을 동물학대로 싱고합니다...

            // 먼저 이 녀석의 랭크를 지정 합니다.
            Random random = new Random(System.currentTimeMillis()); // 랜덤값 생성을 위한 랜덤값 변수 생성
            Player player = event.getEntity().getKiller(); // 몹을 죽인 사람을 확인해용

            if (player == null || !butchers.containsKey(player)) return; // 만약 이 사람이 도축업자가 아니면 그냥 끝내용

            Butcher butcher = butchers.get(player); // 도축업자면 그 샊이를 멱살잡고 델고와용
            int pick = random.nextInt(100); // 그리고 랜덤값을 확인해용
            Probability.ProbabilityField.Probabilities probabilities = probability.data.get(butcher.getLevel() - 1).probabilities;
            if (pick < probabilities.normal)
                rank = 0; // 지방 1
            else if (pick - probabilities.normal < probabilities.rare)
                rank = 1; // 지방 2
            else
                rank = 2; // 지방 3

            // 랭크 지정이 끝 났으면 이 녀석이 뭔지 확인해 고기를 생성.
            // 동물을 죽였다면 원래 드랍할걸 제거하고 아니면 그냥 같이 사랑에 드롭드롭
            if (event.getEntity() instanceof Animals) {
                meat = new ButcherMeat(butcher, rank, event.getEntity());
                event.getDrops().clear();
            } else {
                meat = new ButcherMeat(butcher, rank);
            }

            if (meat.getItem() != null) event.getDrops().add(meat.getItem());
        }
    }

    private void setMeatPieceRecipe() {
        MeatPiece piece = new MeatPiece();

        ArrayList<Material> materials = new ArrayList<>();
        materials.add(Material.BEEF);
        materials.add(Material.PORKCHOP);
        materials.add(Material.MUTTON);
        materials.add(Material.CHICKEN);
        materials.add(Material.RABBIT);

        int i = 0;
        for (Material material: materials) {
            NamespacedKey key = new NamespacedKey(plugin, "meat_piece_" + Integer.toString(i));
            ShapedRecipe recipe = new ShapedRecipe(key, piece.getItem());
            recipe.shape("   ", " A ", "   ");
            recipe.setIngredient('A', material);
            plugin.getServer().addRecipe(recipe);
            i++;
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        for (LivingEntity player : event.getViewers()) {
            if (!butchers.containsKey(player)) {
                event.setCancelled(true);
            }
        }
    }
}