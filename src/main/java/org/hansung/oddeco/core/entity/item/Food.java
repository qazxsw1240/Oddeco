package org.hansung.oddeco.core.entity.item;

import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;

public interface Food extends Item {
    public abstract NutritionFacts getNutritionFacts();
}
