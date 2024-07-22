package org.hansung.oddeco.butcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.core.json.JsonUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ButcherListener implements Listener {
    private Random random = new Random();

    private final JavaPlugin plugin;
    private final Probability probability;
    private final ConcurrentMap<LivingEntity, Butcher> butchers = new ConcurrentHashMap<>();
    private final ArrayList<Recipe> recipes = new ArrayList<>();

    public ButcherListener(JavaPlugin plugin) {
        Probability probability1;
        this.plugin = plugin;

        // /resources 폴더 파일 읽기 (수정 필요)
        try (InputStream in = getClass().getResourceAsStream("/butcher_probability.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String content = reader.lines().collect(Collectors.joining());
            // plugin.getLogger().info(String.format("content find: %s", content));
            probability1 = new ObjectMapper().readValue(content, Probability.class);
        } catch (IOException e) {
            probability1 = null;
            plugin.getLogger().info(String.format("%s", e.getMessage()));
        }
        probability = probability1;

        // add meat recipe
        setMeatPieceRecipe();
        ConcurrentMap<Character, Object> data = new ConcurrentHashMap<>();
        data.put('1', Material.GOLD_INGOT);
        data.put('2', Material.GLOWSTONE_DUST);
        data.put('M', Material.RED_DYE);

        JsonElement jsonElement = JsonUtil.of("/butcher_recipe.json");
        ButcherRepository repository = new ButcherRepository(jsonElement.getAsJsonObject());
        repository.getAll().forEach(entry -> plugin.getServer().addRecipe(entry.build()));

        plugin.getLogger().info("Butcher Listener Registed.");
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
        Player player = event.getEntity().getKiller(); // check who killed mob
        if (player == null || !butchers.containsKey(player)) return; // check what player is butcher

        if (event.getEntity() instanceof Mob) {
            ButcherMeat meat; // meat object
            int rank; // meat's rank
            boolean isAnimal = false; // if Player kill animal

            // set rank of meat
            Random random = new Random(System.currentTimeMillis()); // random value generator

            Butcher butcher = butchers.get(player); // get butcher from killer's player object
            rank = setRandomRank(butcher);

            // 랭크 지정이 끝 났으면 이 녀석이 뭔지 확인해 고기를 생성.
            // 동물을 죽였을 경우 생성된 고기로 대체, 아닐 경우 생성된 고기와 같이 드랍.
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
            recipes.add(recipe);
            i++;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (Objects.equals(item.getItemMeta().displayName(), Component.text("덫"))) {
            event.getBlock().setMetadata("butcher_trap", new FixedMetadataValue(plugin, "true"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || !event.getAction().isRightClick()) return;
        if (Objects.requireNonNull(event.getClickedBlock()).getType() != Material.COBWEB) return;
        if (!butchers.containsKey(event.getPlayer())) return;
        if (!event.getClickedBlock().getMetadata("butcher_trap").isEmpty()) {
            Butcher butcher = butchers.get(event.getPlayer());
            ButcherMeat meat = new ButcherMeat(butcher, setRandomRank(butcher));
            event.getPlayer().getInventory().addItem(meat.getItem());
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        for (LivingEntity player : event.getViewers()) {
            if (recipes.contains(event.getRecipe()) && !butchers.containsKey(player)) {
                event.setCancelled(true);
            }
        }
    }

    private int setRandomRank(Butcher butcher) {
        int rank = 0;
        int pick = random.nextInt(100); // generate random number
        Probability.ProbabilityField.Probabilities probabilities = probability.data.get(butcher.getLevel() - 1).probabilities;
        if (pick < probabilities.normal)
            rank = 0; // 지방 1
        else if (pick - probabilities.normal < probabilities.rare)
            rank = 1; // 지방 2
        else
            rank = 2; // 지방 3
        return rank;
    }
}