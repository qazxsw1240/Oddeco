package org.hansung.oddeco.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hansung.oddeco.core.ReadonlyRepository;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class NutritionFactRepository implements ReadonlyRepository<NutritionFacts, String> {
    private final Map<String, NutritionFacts> nutritionFacts;

    public NutritionFactRepository(JsonObject jsonObject) {
        this.nutritionFacts = new TreeMap<>();
        jsonObject.entrySet()
                .forEach(entry -> {
                    String key = entry.getKey();
                    JsonElement element = entry.getValue();
                    if (!element.isJsonArray()) {
                        throw new IllegalArgumentException("nutrition fact data is not an array");
                    }
                    JsonArray array = element.getAsJsonArray();
                    int carbohydrate = array.get(0).getAsInt();
                    int protein = array.get(1).getAsInt();
                    int fat = array.get(2).getAsInt();
                    int vitamin = array.get(3).getAsInt();
                    NutritionFacts nutritionFacts = NutritionFacts.of(carbohydrate, protein, fat, vitamin);
                    this.nutritionFacts.put(key, nutritionFacts);
                });
    }

    @Override
    public boolean contains(String key) {
        return this.nutritionFacts.containsKey(key);
    }

    @Override
    public Optional<NutritionFacts> get(String key) {
        return Optional.ofNullable(this.nutritionFacts.get(key));
    }

    @Override
    public Collection<NutritionFacts> getAll() {
        return this.nutritionFacts.values();
    }
}
