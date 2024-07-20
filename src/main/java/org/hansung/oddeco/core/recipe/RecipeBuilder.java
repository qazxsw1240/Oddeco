package org.hansung.oddeco.core.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RecipeBuilder {
    private NamespacedKey key;
    private ItemStack item;
    private String[] shape;
    private final ConcurrentMap<Character, Material> ingredients;

    public RecipeBuilder() {
        this.shape = new String[3];
        this.ingredients = new ConcurrentHashMap<>();
    }

    public RecipeBuilder(NamespacedKey key) {
        this();
        this.key = key;
    }

    public RecipeBuilder(ItemStack item, NamespacedKey key) {
        this(key);
        this.item = item;
    }

    // add all line's shape to recipe
    public void setShape(String[] shape) {
        this.shape = shape;
    }

    // add one line's shape to recipe
    public void setShape(int line, String shape) {
        if (line >= this.shape.length)
            throw new RuntimeException(String.format("line 값은 0부터 2 사이의 값을 가지지만, %d로 지정되었습니다.", line));
        this.shape[line] = shape;
    }

    // set all ingredients to recipe
    public void setIngredient(Map<Character, Material> ingredients) {
        this.ingredients.putAll(ingredients);
    }

    // add one ingredient to recipe
    public void setIngredient(Character key, Material value) {
        this.ingredients.put(key, value);
    }

    // set item of recipe result
    public void setItem(ItemStack item) {
        this.item = item;
    }

    // build recipe (to add server recipe)
    public ShapedRecipe build() {
        if (item == null) throw new NullPointerException("레시피로 제작할 아이템이 지정되지 않았습니다.");
        if (key == null) key = item.getType().getKey();
        ShapedRecipe recipe = new ShapedRecipe(key, item).shape(shape); // set recipe's shape
        ingredients.forEach((key, value) -> recipe.setIngredient(key, value)); // set ingredients
        return recipe;
    }
}
