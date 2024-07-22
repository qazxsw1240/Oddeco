package org.hansung.oddeco.core.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeBuilder {
    private ShapedRecipe recipe;
    private NamespacedKey key;
    private final ItemStack item;

    protected RecipeBuilder(ItemStack item) {
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

    // get ItemMeta to recipe result
    public ItemMeta getItemMeta() {
        return item.getItemMeta();
    }

    // set ItemMeta to recipe result
    public void setItemMeta(ItemMeta itemMeta) {
        item.setItemMeta(itemMeta);
    }

    // build recipe (to add server recipe)
    public ShapedRecipe build() {
        return recipe;
    }
}
