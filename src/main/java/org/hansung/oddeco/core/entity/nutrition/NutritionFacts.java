package org.hansung.oddeco.core.entity.nutrition;

public interface NutritionFacts extends Iterable<NutritionFacts.Info> {
    public abstract int getNutrition(Nutrition nutrition);

    public static interface Info {
        public abstract Nutrition getNutrition();

        public abstract int getAmount();
    }
}
