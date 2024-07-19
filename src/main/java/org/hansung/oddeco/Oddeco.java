package org.hansung.oddeco;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
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
import org.hansung.oddeco.butcher.ButcherListener;
import org.hansung.oddeco.core.Plugin;
import org.hansung.oddeco.core.entity.career.CareerCoinsData;
import org.hansung.oddeco.core.entity.nutrition.PlayerNutritionFactRewardData;
import org.hansung.oddeco.core.entity.player.PlayerNutritionStateData;
import org.hansung.oddeco.core.json.JsonUtil;
import org.hansung.oddeco.core.util.entity.ItemStackBuilder;
import org.hansung.oddeco.core.util.logging.FormattedLogger;
import org.hansung.oddeco.core.util.logging.PluginLoggerFactory;
import org.hansung.oddeco.hunter.HunterListener;
import org.hansung.oddeco.repository.*;
import org.hansung.oddeco.service.PlayerAdvancementRewardService;
import org.hansung.oddeco.service.PlayerCareerService;
import org.hansung.oddeco.service.PlayerNutritionService;

import java.time.Duration;

@Plugin
public final class Oddeco extends JavaPlugin {
    private static final String DB_URL = "jdbc:sqlite:plugins/Oddeco/database.db";

    private static final Class<?>[] ENTITIES = new Class[]{
            PlayerNutritionStateData.class,
            PlayerNutritionFactRewardData.class,
            CareerCoinsData.class,
    };

    private final FormattedLogger logger;

    private final NutritionFactRepository nutritionFactRepository;
    private final AdvancementRewardRepository advancementRewardRepository;

    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public Oddeco() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.logger = PluginLoggerFactory.getLogger(this);

        JsonObject object = JsonUtil
                .of("/nutrition_fact_data.json")
                .getAsJsonObject();

        JsonArray array = JsonUtil
                .of("/advancement_reward_data.json")
                .getAsJsonArray();

        this.nutritionFactRepository = new NutritionFactRepository(object);
        this.advancementRewardRepository = new AdvancementRewardRepository(array);
        this.entityManagerFactory = new OddecoEntityManagerBuilder(DB_URL, ENTITIES);
        this.entityManager = this.entityManagerFactory.createEntityManager();
        this.entityManager.setFlushMode(FlushModeType.AUTO);
    }

    @Override
    public void onDisable() {
        this.entityManager.close();
        this.entityManagerFactory.close();
        this.logger.info("Disabled Plugin [Oddeco]");
    }

    @Override
    public void onEnable() {
        this.logger.info("Enabled Plugin [%s]", getName());

        PlayerNutritionService playerNutritionService = new PlayerNutritionService(
                this,
                this.nutritionFactRepository,
                new PlayerNutritionRepository(this.entityManager),
                new PlayerNutritionFactRewardDataRepository(this.entityManager),
                this.logger);

        playerNutritionService.setExhaustionDecrement(3);
        playerNutritionService.setExhaustionDelay(Duration.ofSeconds(10));

        PlayerAdvancementRewardService playerAdvancementRewardService = new PlayerAdvancementRewardService(
                this.advancementRewardRepository,
                this.logger);

        NamespacedKey nutritionKey = playerNutritionService.getNutritionKey();

        PlayerCareerService playerCareerService = new PlayerCareerService(
                this,
                new CareerCoinsRepository(this.entityManager),
                this.advancementRewardRepository,
                this.logger);

        getServer()
                .getPluginManager()
                .registerEvents(playerNutritionService, this);
        getServer()
                .getPluginManager()
                .registerEvents(playerCareerService, this);
        getServer()
                .getPluginManager()
                .registerEvents(new ButcherListener(this), this);
        getServer()
                .getPluginManager()
                .registerEvents(new HunterListener(this, this.logger), this);

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
