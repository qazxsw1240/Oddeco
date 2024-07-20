package org.hansung.oddeco.core.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RecipeBuilder {
    private ShapedRecipe recipe;
    private NamespacedKey key;
    private final ItemStack item;
    private String[] shape;

    protected RecipeBuilder(ItemStack item) {
        this.shape = new String[3];
        this.item = item;
    }

    public RecipeBuilder(NamespacedKey key, ItemStack item) {
        this(item);
        this.key = key;
        recipe = new ShapedRecipe(this.key, this.item);
    }

    public RecipeBuilder(NamespacedKey key, Material material) {
        this(new ItemStack(material));
        this.key = key;
        this.recipe = new ShapedRecipe(this.key, this.item);
    }

    public RecipeBuilder(String namespacedKey, ItemStack item) {
        this(item);
        this.key = new NamespacedKey(namespacedKey, namespacedKey);
        this.recipe = new ShapedRecipe(this.key, this.item);
    }

    public RecipeBuilder(String namespacedKey, Material material) {
        this(new ItemStack(material));
        this.key = new NamespacedKey(namespacedKey, namespacedKey);
        this.recipe = new ShapedRecipe(this.key, this.item);
    }

    // add all line's shape to recipe
    public void setShape(String[] shape) {
        recipe.shape(shape);
    }

    // set ingredient to recipe
    public void setIngredient(Character key, Material value) {
        recipe.setIngredient(key, value);
    }

    // set ingredient to recipe
    public void setIngredient(Character key, ItemStack value) {
        recipe.setIngredient(key, value);
    }

    // set amount to recipe result
    public void setAmount(int amount) {
        item.setAmount(amount);
    }

    // build recipe (to add server recipe)
    public ShapedRecipe build() {
        if (item == null) throw new NullPointerException("레시피로 제작할 아이템이 지정되지 않았습니다.");
        recipe.shape(shape); // set recipe's shape
        return recipe;
    }
}
