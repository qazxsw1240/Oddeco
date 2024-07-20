package org.hansung.oddeco.butcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.hansung.oddeco.core.ReadonlyRepository;
import org.hansung.oddeco.core.recipe.RecipeBuilder;
import org.hansung.oddeco.hunter.HunterRecipe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ButcherRepository implements ReadonlyRepository<RecipeBuilder, ItemStack> {
    private final ConcurrentMap<ItemStack, RecipeBuilder> recipes;
    private final ArrayList<String> meat_texts;

    public ButcherRepository(JsonObject object) {
        this.recipes = new ConcurrentHashMap<>();
        this.meat_texts = new ArrayList<>();
        meat_texts.add("chicken");
        meat_texts.add("porkchop");
        meat_texts.add("mutton");
        meat_texts.add("beef");
        meat_texts.add("rabbit");

        int rank = 0;
        ButcherIngredients ingredients = new ButcherIngredients();
        object.entrySet().forEach(entry -> {
            // get recipe from json file
            RecipeBuilder builder;
            ItemStack item;
            if (meat_texts.contains(entry.getKey().split("_")[0])) {
                Meat meat = new Meat(rank, ingredients
                        .getIngredient(entry.getKey().split("_")[0])
                        .getType(),
                        Integer.parseInt(entry.getKey().split("_")[1]));
                item = meat.getItem();
                builder = new RecipeBuilder(String.format("%s_%d", entry.getKey(), rank), item);
            } else {
                item = ingredients.getIngredient(entry.getKey());
                builder = new RecipeBuilder(entry.getKey(), item);
            }
            JsonElement element = entry.getValue();
            element.getAsJsonObject().entrySet().forEach(data -> {
                if (data.getKey().equals("recipe")) {
                    JsonArray array = data.getValue().getAsJsonArray();
                    String[] value = new String[3];
                    for (int i = 0; i < 3; i++) {
                        value[i] = array.get(i).getAsString();
                    }
                    builder.setShape(value);
                } else if (data.getKey().equals("ingredient")) {
                    data.getValue().getAsJsonObject().entrySet().forEach(ingredient -> {
                        builder.setIngredient(ingredient.getKey().charAt(0),
                                ingredients.getIngredient(ingredient.getValue().getAsString()));
                    });
                } else if (data.getKey().equals("count")) {
                    builder.setAmount(data.getValue().getAsInt());
                }
            });
            recipes.put(item, builder);
        });
    }

    @Override
    public boolean contains(ItemStack key) {
        return this.recipes.containsKey(key);
    }

    @Override
    public Optional<RecipeBuilder> get(ItemStack key) {
        return Optional.ofNullable(this.recipes.get(key));
    }

    @Override
    public Collection<RecipeBuilder> getAll() {
        return this.recipes.values();
    }
}
