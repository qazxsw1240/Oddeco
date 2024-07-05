package org.hansung.oddeco.butcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.hansung.oddeco.core.ReadonlyRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ButcherRepository implements ReadonlyRepository<String[], Meat> {
    private final ConcurrentMap<Meat, String[]> butcherRecipeMap;
    private final ConcurrentMap<Integer, Material> materialMap;

    ButcherRepository(JsonObject object) {
        // init variables
        this.butcherRecipeMap = new ConcurrentHashMap<>();
        this.materialMap = new ConcurrentHashMap<>();
        setMaterialMap();

        // read data
        object.entrySet().forEach(entry -> {
            // get meat
            String name = entry.getKey();
            int a = Integer.parseInt(name.substring(0, 1));
            int b = Integer.parseInt(name.substring(1, 2));
            int c = Integer.parseInt(name.substring(2, 3));

            // get values
            JsonElement element = entry.getValue();
            JsonArray array = element.getAsJsonArray();
            String[] strings = new String[3];
            strings[0] = array.get(0).getAsString();
            strings[1] = array.get(1).getAsString();
            strings[2] = array.get(2).getAsString();

            // put to map
            butcherRecipeMap.put(new Meat(a, materialMap.get(b), c), strings);
        });
    }

    private void setMaterialMap() {
        materialMap.put(0, Material.CHICKEN);
        materialMap.put(1, Material.PORKCHOP);
        materialMap.put(2, Material.MUTTON);
        materialMap.put(3, Material.BEEF);
        materialMap.put(4, Material.RABBIT);
    }

    @Override
    public boolean contains(Meat key) {
        return this.butcherRecipeMap.containsKey(key);
    }

    @Override
    public Optional<String[]> get(Meat key) {
        return Optional.ofNullable(this.butcherRecipeMap.get(key));
    }

    public Set<Meat> getKeys() {
        return this.butcherRecipeMap.keySet();
    }

    @Override
    public Collection<String[]> getAll() {
        return this.butcherRecipeMap.values();
    }
}
