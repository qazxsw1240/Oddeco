package org.hansung.oddeco.core.entity.nutrition;

public interface NutritionFacts extends Iterable<NutritionFacts.Info> {
    public static NutritionFacts of() {
        return new NutritionFactsImpl();
    }

    public static NutritionFacts of(NutritionFacts other) {
        return new NutritionFactsImpl(other);
    }

    public static NutritionFacts of(int carbohydrate, int protein, int fat, int vitamin) {
        return new NutritionFactsImpl(carbohydrate, protein, fat, vitamin);
    }

    public abstract int getNutrition(Nutrition nutrition);

    public static interface Info {
        public static Info of(Nutrition nutrition, int amount) {
            return new NutritionFactsImpl.InfoImpl(nutrition, amount);
        }

        public abstract Nutrition getNutrition();

        public abstract int getAmount();
    }
}
