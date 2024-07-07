package org.hansung.oddeco.core.hunter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;
import org.hansung.oddeco.core.ReadonlyRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HunterRepository implements ReadonlyRepository<HunterRecipe, ItemStack> {
    private final ConcurrentMap<ItemStack, HunterRecipe> hunterRecipes;

    public HunterRepository(JsonObject object) {
        hunterRecipes = new ConcurrentHashMap<>();

        HunterIngredients hunterIngredients = new HunterIngredients();
        object.entrySet().forEach(entry -> {
            // get recipe from json file
            HunterRecipe recipe = new HunterRecipe(hunterIngredients.getItem(entry.getKey()));
            JsonElement element = entry.getValue();
            element.getAsJsonObject().entrySet().forEach(data -> {
                if (data.getKey().equals("recipe")) {
                    JsonArray array = data.getValue().getAsJsonArray();
                    for (int i = 0; i < 3; i++)
                        recipe.addShape(i, array.get(i).getAsString());
                } else if (data.getKey().equals("ingredient")) {
                    data.getValue().getAsJsonObject().entrySet().forEach(ingredient -> {
                        recipe.addIngredient(ingredient.getKey().charAt(0),
                                hunterIngredients.getItem(ingredient
                                        .getValue()
                                        .getAsString()));
                    });
                } else if (data.getKey().equals("count")) {
                    recipe.setAmount(data.getValue().getAsInt());
                }
            });

            // create item
            ItemStack item = new ItemStack(recipe.getResult(), recipe.getAmount());
            recipe.setItem(item);
            hunterRecipes.put(item, recipe);
        });
    }

    @Override
    public boolean contains(ItemStack key) {
        return hunterRecipes.containsKey(key);
    }

    @Override
    public Optional<HunterRecipe> get(ItemStack key) {
        return Optional.ofNullable(hunterRecipes.get(key));
    }

    @Override
    public Collection<HunterRecipe> getAll() {
        return hunterRecipes.values();
    }
}
