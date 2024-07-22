package org.hansung.oddeco.butcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hansung.oddeco.core.ReadonlyRepository;
import org.hansung.oddeco.core.recipe.RecipeBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

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

        ButcherIngredients ingredients = new ButcherIngredients();
        object.entrySet().forEach(entry -> {
            // get recipe from json file
            if (meat_texts.contains(entry.getKey().split("_")[0])) {
                RecipeBuilder builder;
                ItemStack item;
                for (int i = 0; i < 3; i++) {
                    Meat meat = new Meat(i, ingredients
                            .getIngredient(entry.getKey().split("_")[0])
                            .getType(),
                            Integer.parseInt(entry.getKey().split("_")[1]));
                    item = meat.getItem();
                    builder = new RecipeBuilder(String.format("%s_%d", entry.getKey(), i), item);
                    parseData(entry.getValue(), builder, ingredients, i);
                    recipes.put(item, builder);
                }
            } else {
                RecipeBuilder builder;
                ItemStack item;
                item = ingredients.getIngredient(entry.getKey());
                if (Objects.equals(entry.getKey(), "butcher_trap")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.displayName(Component.text("덫"));
                    item.setItemMeta(itemMeta);
                } else if (Objects.equals(entry.getKey(), "meat_piece_vegan")) {
                    MeatPiece meatPiece = new MeatPiece();
                    item.setItemMeta(meatPiece.getItem().getItemMeta());
                }
                builder = new RecipeBuilder(entry.getKey(), item);
                parseData(entry.getValue(), builder, ingredients, -1);
                recipes.put(item, builder);
            }
        });
    }

    private void parseData(JsonElement entryValue, RecipeBuilder builder, ButcherIngredients ingredients, int rank) {
        entryValue.getAsJsonObject().entrySet().forEach(data -> {
            if (data.getKey().equals("recipe")) {
                JsonArray array = data.getValue().getAsJsonArray();
                String[] value = new String[3];
                for (int j = 0; j < 3; j++) {
                    value[j] = array.get(j).getAsString();
                }
                builder.setShape(value);
            } else if (data.getKey().equals("ingredient")) {
                data.getValue().getAsJsonObject().entrySet().forEach(ingredient -> {
                    if (Objects.equals(ingredient.getValue().getAsString(), "special_core")) {
                        builder.setIngredient(ingredient.getKey().charAt(0),
                                ingredients.getSpecialCore(rank));
                    } else {
                        builder.setIngredient(ingredient.getKey().charAt(0),
                                ingredients.getIngredient(ingredient.getValue().getAsString()));
                    }
                });
            } else if (data.getKey().equals("count")) {
                builder.setAmount(data.getValue().getAsInt());
            }
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
