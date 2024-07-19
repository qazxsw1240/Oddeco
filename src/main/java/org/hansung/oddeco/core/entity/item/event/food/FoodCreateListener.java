package org.hansung.oddeco.core.entity.item.event.food;

import org.hansung.oddeco.core.entity.item.Food;

public interface FoodCreateListener extends FoodAttachableListener {
    public void onFoodCreate(Food food);
}
