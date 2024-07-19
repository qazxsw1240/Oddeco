package org.hansung.oddeco.core.entity.item.event.food;

import org.hansung.oddeco.core.entity.item.Food;

public interface FoodConsumeListener extends FoodAttachableListener {
    public void onFoodConsume(Food food);
}
