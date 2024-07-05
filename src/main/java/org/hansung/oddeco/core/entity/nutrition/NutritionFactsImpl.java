package org.hansung.oddeco.core.entity.nutrition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

class NutritionFactsImpl implements NutritionFacts {
    private final int[] nutritionFacts;

    public NutritionFactsImpl() {
        Nutrition[] values = Nutrition.values();
        this.nutritionFacts = new int[values.length];
    }

    public NutritionFactsImpl(int carbohydrate, int protein, int fat, int vitamin) {
        this.nutritionFacts = new int[]{carbohydrate, protein, fat, vitamin};
    }

    public NutritionFactsImpl(NutritionFacts nutritionFacts) {
        if (nutritionFacts instanceof NutritionFactsImpl impl) {
            this.nutritionFacts = impl.nutritionFacts.clone();
        } else {
            Nutrition[] values = Nutrition.values();
            this.nutritionFacts = new int[values.length];
            for (Info info : nutritionFacts) {
                Nutrition nutrition = info.getNutrition();
                this.nutritionFacts[nutrition.ordinal()] = info.getAmount();
            }
        }
    }

    public NutritionFactsImpl(Info... infos) {
        this();
        for (Info info : Set.of(infos)) {
            Nutrition nutrition = info.getNutrition();
            this.nutritionFacts[nutrition.ordinal()] += info.getAmount();
        }
    }

    @Override
    public int getNutrition(Nutrition nutrition) {
        return this.nutritionFacts[nutrition.ordinal()];
    }

    @Override
    public Iterator<Info> iterator() {
        return Arrays.stream(Nutrition.values())
                .map(nutrition -> Info.of(nutrition, this.nutritionFacts[nutrition.ordinal()]))
                .iterator();
    }

    @Override
    public String toString() {
        return String.format("NutritionFacts(carbohydrate=%d, protein=%d, fat=%d, vitamin=%d)",
                this.nutritionFacts[0],
                this.nutritionFacts[1],
                this.nutritionFacts[2],
                this.nutritionFacts[3]);
    }

    static class InfoImpl implements Info {
        private final Nutrition nutrition;
        private final int amount;

        public InfoImpl(Nutrition nutrition, int amount) {
            this.nutrition = nutrition;
            this.amount = amount;
        }

        @Override
        public Nutrition getNutrition() {
            return this.nutrition;
        }

        @Override
        public int getAmount() {
            return this.amount;
        }
    }
}
