package org.hansung.oddeco;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.core.Plugin;
import org.hansung.oddeco.core.json.JsonUtil;
import org.hansung.oddeco.core.util.entity.ItemStackBuilder;
import org.hansung.oddeco.core.util.logging.FormattedLogger;
import org.hansung.oddeco.core.util.logging.PluginLoggerFactory;
import org.hansung.oddeco.repository.AdvancementRewardRepository;
import org.hansung.oddeco.repository.NutritionFactRepository;
import org.hansung.oddeco.repository.PlayerNutritionFactRewardDataRepository;
import org.hansung.oddeco.repository.PlayerNutritionRepository;
import org.hansung.oddeco.service.PlayerAdvancementRewardService;
import org.hansung.oddeco.service.PlayerNutritionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

@Plugin
public final class Oddeco extends JavaPlugin {
    private static final String DB_URL = "jdbc:sqlite:plugins/Oddeco/database.db";

    private final FormattedLogger logger;
    private final AtomicReference<Connection> connection;

    private final NutritionFactRepository nutritionFactRepository;
    private final AdvancementRewardRepository advancementRewardRepository;

    public Oddeco() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.logger = PluginLoggerFactory.getLogger(this);
        this.connection = new AtomicReference<>();

        JsonObject object = JsonUtil
                .of("/nutrition_fact_data.json")
                .getAsJsonObject();

        JsonArray array = JsonUtil
                .of("/advancement_reward_data.json")
                .getAsJsonArray();

        this.nutritionFactRepository = new NutritionFactRepository(object);
        this.advancementRewardRepository = new AdvancementRewardRepository(array);
    }

    @Override
    public void onDisable() {
        try {
            Connection connection = this.connection.get();
            if (connection != null) {
                connection.close();
            }
            this.logger.info("DB Connection successfully closed");
            this.logger.info("Disabled Plugin [Oddeco]");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            this.connection.set(connection);
            this.logger.info("DB Connection successfully established");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.logger.info("Enabled Plugin [%s]", getName());
        Connection connection = this.connection.get();

        PlayerNutritionService playerNutritionService = new PlayerNutritionService(
                this,
                this.nutritionFactRepository,
                new PlayerNutritionRepository(connection),
                new PlayerNutritionFactRewardDataRepository(connection),
                connection,
                this.logger);

        playerNutritionService.setExhaustionDecrement(3);
        playerNutritionService.setExhaustionDelay(Duration.ofSeconds(10));

        PlayerAdvancementRewardService playerAdvancementRewardService = new PlayerAdvancementRewardService(
                this.advancementRewardRepository,
                this.logger);

        NamespacedKey nutritionKey = playerNutritionService.getNutritionKey();

        getServer()
                .getPluginManager()
                .registerEvents(playerNutritionService, this);

        getServer()
                .getPluginManager()
                .registerEvents(playerAdvancementRewardService, this);

        ItemStack mudCookie = ItemStackBuilder
                .of(Material.COOKIE, 16)
                .setMetaBuilder(meta -> {
                    meta.displayName(Component
                            .text("Mud Cookie", Style.empty()
                                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .decorate(TextDecoration.BOLD)));
                    FoodComponent foodComponent = meta.getFood();
                    foodComponent.setCanAlwaysEat(true);
                    foodComponent.setEatSeconds(1.2f);
                    foodComponent.setNutrition(0);
                    foodComponent.setSaturation(0);
                    meta.setFood(foodComponent);
                })
                .setMetaContainerBuilder(container -> container.set(nutritionKey, PersistentDataType.STRING, "mud_cookie"))
                .build();

        ItemStack dirtCookie = ItemStackBuilder
                .of(Material.COARSE_DIRT, 16)
                .setMetaBuilder(meta -> {
                    meta.displayName(Component
                            .text("Dirt Cookie", Style.empty()
                                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                                    .decorate(TextDecoration.BOLD)));
                    FoodComponent foodComponent = meta.getFood();
                    foodComponent.setCanAlwaysEat(true);
                    foodComponent.setEatSeconds(2.4f);
                    foodComponent.setNutrition(0);
                    foodComponent.setSaturation(0);
                    meta.setFood(foodComponent);
                })
                .setMetaContainerBuilder(container -> container.set(nutritionKey, PersistentDataType.STRING, "dirt_cookie"))
                .build();

        ShapedRecipe mudCookieRecipe = new ShapedRecipe(new NamespacedKey(this, "MudCookie"), mudCookie)
                .shape(" A ", "AAA", " A ")
                .setIngredient('A', Material.DIRT);

        ShapedRecipe dirtCookieRecipe = new ShapedRecipe(new NamespacedKey(this, "DirtCookie"), dirtCookie)
                .shape(" AA", "AAA", " A ")
                .setIngredient('A', Material.DIRT);

        getServer().addRecipe(mudCookieRecipe, true);
        getServer().addRecipe(dirtCookieRecipe, true);
        getServer().updateRecipes();
    }
}
