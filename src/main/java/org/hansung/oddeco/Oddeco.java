package org.hansung.oddeco;

import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.butcher.ButcherListener;
import org.hansung.oddeco.core.Plugin;

@Plugin
public final class Oddeco extends JavaPlugin {
    @Override
    public void onDisable() {
        this.getServer().getLogger().info("Disabled");
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
        this.logger.info("Enabled Plugin [Oddeco]");
        Connection connection = this.connection.get();

        PlayerNutritionService playerNutritionService = new PlayerNutritionService(this, this.nutritionFactRepository, new PlayerNutritionRepository(connection), connection, this.logger);
        NamespacedKey nutritionKey = playerNutritionService.getNutritionKey();

        getServer()
                .getPluginManager()
                .registerEvents(playerNutritionService, this);

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

        ShapedRecipe mudCookieRecipe = new ShapedRecipe(new NamespacedKey(this, "MudCookie"), mudCookie)
                .shape(" A ", "AAA", " A ")
                .setIngredient('A', Material.DIRT);

        getServer().addRecipe(mudCookieRecipe, true);
        getServer().updateRecipes();
    }
}
