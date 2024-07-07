package org.hansung.oddeco.core.hunter;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.core.json.JsonUtil;
import org.hansung.oddeco.core.util.logging.FormattedLogger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HunterListener implements Listener {
    private final JavaPlugin plugin;
    private final FormattedLogger logger;
    private final ConcurrentMap<LivingEntity, Hunter> hunters;


    public HunterListener(JavaPlugin plugin, FormattedLogger logger) {
        // init variables
        this.plugin = plugin;
        this.logger = logger;
        this.hunters = new ConcurrentHashMap<>();

        // init recipes
        JsonObject object = JsonUtil.of("/hunter_recipe.json").getAsJsonObject();
        HunterRepository repository = new HunterRepository(object);
        repository.getAll().forEach(entry -> {
            NamespacedKey namespacedKey = entry.getResult().getKey();
            ShapedRecipe recipe = new ShapedRecipe(namespacedKey, entry.getItem());
            recipe.shape(entry.getShape());
            entry.getIngredients().forEach((key, value) -> recipe.setIngredient(key, value));
            plugin.getServer().addRecipe(recipe);
        });

        logger.info("HunterListener Registed.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        hunters.put(event.getPlayer(), new Hunter(1));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (hunters.containsKey(player)) {
            Hunter hunter = hunters.get(player);
            player.heal(1);
            // 아이템 드랍율 증가
            if (hunter.getLevel() >= 5) {

            } else if (hunter.getLevel() >= 2) {

            }
        }
    }
}
