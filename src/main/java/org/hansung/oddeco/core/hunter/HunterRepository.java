package org.hansung.oddeco.core.hunter;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.hansung.oddeco.core.ReadonlyRepository;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
            // 한방검 제작 레시피
            if (entry.getKey().equals("hunter_wooden_sword")) {
                Damageable damage = (Damageable) item.getItemMeta();
                Attribute attribute = Attribute.GENERIC_ATTACK_DAMAGE;
                AttributeModifier modifier = new AttributeModifier(UUID.nameUUIDFromBytes(new byte[]{(byte) 860454301, (byte) -84261649, (byte) -1183576394, (byte) 888873874}),
                        "generic.attack_damage", 12, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
                damage.addAttributeModifier(attribute, modifier);
                damage.setDamage(59);
                item.setItemMeta(damage);
            }
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
