package org.hansung.oddeco.hunter;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.hansung.oddeco.core.json.JsonUtil;
import org.hansung.oddeco.core.util.logging.FormattedLogger;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HunterListener implements Listener {
    private final JavaPlugin plugin;
    private final FormattedLogger logger;
    private final ConcurrentMap<LivingEntity, Hunter> hunters;
    private final ArrayList<ItemStack> recipes;

    public HunterListener(JavaPlugin plugin, FormattedLogger logger) {
        // init variables
        this.plugin = plugin;
        this.logger = logger;
        this.hunters = new ConcurrentHashMap<>();
        this.recipes = new ArrayList<>();

        // init recipes
        JsonObject object = JsonUtil.of("/hunter_recipe.json").getAsJsonObject();
        HunterRepository repository = new HunterRepository(object);
        repository.getAll().forEach(entry -> {
            NamespacedKey namespacedKey;
            if (entry.getResult() == Material.WOODEN_SWORD) {
                namespacedKey = new NamespacedKey("hunter_sword", "hunter_sword");
            } else if (entry.getResult() == Material.CHEST) {
                namespacedKey = new NamespacedKey("hunter_chest", "hunter_chest");
            } else {
                namespacedKey = entry.getResult().getKey();
            }
            ShapedRecipe recipe = new ShapedRecipe(namespacedKey, entry.getItem());
            recipe.shape(entry.getShape());
            entry.getIngredients().forEach((key, value) -> recipe.setIngredient(key, value));
            recipes.add(recipe.getResult());
            plugin.getServer().addRecipe(recipe);
        });

        logger.info("HunterListener Registed.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        hunters.put(event.getPlayer(), new Hunter(7));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (hunters.containsKey(player)) {
            Hunter hunter = hunters.get(player);
            player.heal(1);
            // 아이템 드랍율 증가
            if (hunter.getLevel() < 2 || !(event.getEntity() instanceof Monster)) return;
            event.getDrops().forEach(item -> {
                if (hunter.getLevel() >= 5) {
                    item.setAmount(item.getAmount() * 2);
                } else if (hunter.getLevel() >= 2) {
                    item.setAmount((int)Math.ceil(item.getAmount() * 1.2));
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        for (LivingEntity player : event.getViewers()) {
            if (recipes.contains(event.getRecipe().getResult())) {
                if (!hunters.containsKey(player) || hunters.get(player).getLevel() < 3) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.CHEST) return;
        if (Objects.equals(((Chest) event.getInventory()
                .getHolder()).customName(), Component.text("화살 지급 도구"))) {
            if (event.getInventory().contains(Material.ARROW, 1)) return;
            event.getInventory().addItem(new ItemStack(Material.ARROW, 10));
        }
    }
}
