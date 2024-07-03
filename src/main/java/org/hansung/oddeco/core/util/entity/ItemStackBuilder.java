package org.hansung.oddeco.core.util.entity;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.function.Consumer;

public class ItemStackBuilder {
    private Material material;
    private int amount;
    private Consumer<? super ItemMeta> metaBuilder;
    private Consumer<? super PersistentDataContainer> metaContainerBuilder;

    private ItemStackBuilder(Material material) {
        this(material, 1, null, null);
    }

    private ItemStackBuilder(Material material, int amount) {
        this(material, amount, null, null);
    }

    private ItemStackBuilder(
            Material material,
            int amount,
            Consumer<? super ItemMeta> metaBuilder,
            Consumer<? super PersistentDataContainer> metaContainerBuilder) {
        this.material = material;
        this.amount = amount;
        this.metaBuilder = metaBuilder;
        this.metaContainerBuilder = metaContainerBuilder;
    }

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(material);
    }

    public static ItemStackBuilder of(Material material, int amount) {
        return new ItemStackBuilder(material, amount);
    }

    public ItemStackBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder setMetaBuilder(Consumer<? super ItemMeta> metaBuilder) {
        this.metaBuilder = metaBuilder;
        return this;
    }

    public ItemStackBuilder setMetaContainerBuilder(Consumer<? super PersistentDataContainer> metaContainerBuilder) {
        this.metaContainerBuilder = metaContainerBuilder;
        return this;
    }

    public ItemStack build() {
        if (this.material == null) {
            throw new IllegalArgumentException("Material is not set.");
        }
        if (this.amount < 1) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        ItemStack itemStack = new ItemStack(this.material, this.amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (this.metaBuilder != null) {
            this.metaBuilder.accept(itemMeta);
        }
        if (this.metaContainerBuilder != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            this.metaContainerBuilder.accept(container);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
