package org.hansung.oddeco.hunter;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HunterRecipe {
    private ItemStack item;
    private final Material result;
    private final String[] shape;
    private final ConcurrentMap<Character, Material> ingredients;
    private int amount;

    public HunterRecipe(Material result) {
        this.result = result;
        shape = new String[3];
        ingredients = new ConcurrentHashMap<>();
    }

    public void addShape(int index, String shape) {
        if (index >= this.shape.length)
            throw new RuntimeException(String.format("index의 최댓값은 3인데, %d번째 값에 접근하려고 합니다.", index));
        this.shape[index] = shape;
    }

    public void addIngredient(Character key, Material value) {
        this.ingredients.put(key, value);
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getAmount() { return amount; }
    public String[] getShape() { return shape; }
    public ConcurrentMap<Character, Material> getIngredients() { return ingredients; }
    public Material getResult() { return result; }
    public ItemStack getItem() { return item; }
}
