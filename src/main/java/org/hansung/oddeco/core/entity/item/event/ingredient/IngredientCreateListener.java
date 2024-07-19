package org.hansung.oddeco.core.entity.item.event.ingredient;

import org.hansung.oddeco.core.entity.item.Ingredient;

public interface IngredientCreateListener extends IngredientAttachableListener {
    public void onIngredientCreate(Ingredient ingredient);
}
