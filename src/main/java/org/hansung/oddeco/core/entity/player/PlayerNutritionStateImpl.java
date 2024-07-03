package org.hansung.oddeco.core.entity.player;

import org.bukkit.entity.Player;
import org.hansung.oddeco.core.entity.nutrition.Nutrition;
import org.hansung.oddeco.core.entity.nutrition.NutritionFacts;

import java.util.*;

class PlayerNutritionStateImpl implements PlayerNutritionState {
    private final Player player;
    private final int[] nutritionStates;
    private volatile Map<Nutrition, Integer> map;

    public PlayerNutritionStateImpl(Player player) {
        Nutrition[] values = Nutrition.values();
        this.player = player;
        this.nutritionStates = new int[values.length];
        this.map = null;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public int getAmount(Nutrition nutrition) {
        return this.nutritionStates[nutrition.ordinal()];
    }

    @Override
    public void setAmount(Nutrition nutrition, int amount) {
        this.nutritionStates[nutrition.ordinal()] = amount;
    }

    @Override
    public Map<Nutrition, Integer> getNutritionState() {
        if (this.map == null) {
            synchronized (this) {
                if (this.map == null) {
                    this.map = new PlayerNutritionStateMap(this.nutritionStates);
                }
            }
        }
        return this.map;
    }

    @Override
    public NutritionFacts asNutritionFacts() {
        return NutritionFacts.of();
    }

    @Override
    public String toString() {
        return String.format(
                "PlayerNutritionState(carbohydrate=%d, protein=%d, fat=%d, vitamin=%d)",
                this.nutritionStates[0],
                this.nutritionStates[1],
                this.nutritionStates[2],
                this.nutritionStates[3]);
    }

    private static class PlayerNutritionStateMap implements Map<Nutrition, Integer> {
        private final int[] nutritionState;
        private volatile Collection<Integer> values;
        private volatile Set<Entry<Nutrition, Integer>> entries;

        public PlayerNutritionStateMap(int[] nutritionState) {
            this.nutritionState = nutritionState;
            this.values = null;
            this.entries = null;
        }

        @Override
        public int size() {
            return this.nutritionState.length;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return key instanceof Nutrition;
        }

        @Override
        public boolean containsValue(Object value) {
            if (!(value instanceof Integer amount)) {
                return false;
            }
            for (int j : this.nutritionState) {
                if (j == amount) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Integer get(Object key) {
            if (key instanceof Nutrition nutrition) {
                return this.nutritionState[nutrition.ordinal()];
            }
            return 0;
        }

        @Override
        public Integer put(Nutrition key, Integer value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Integer remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends Nutrition, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Nutrition> keySet() {
            return Set.of(Nutrition.values());
        }

        @Override
        public Collection<Integer> values() {
            if (this.values == null) {
                synchronized (this) {
                    if (this.values == null) {
                        this.values = new AbstractCollection<>() {
                            @Override
                            public Iterator<Integer> iterator() {
                                int[] nutritionStates = PlayerNutritionStateMap.this.nutritionState;
                                return new Iterator<>() {
                                    private int current = 0;

                                    @Override
                                    public boolean hasNext() {
                                        return this.current < nutritionStates.length;
                                    }

                                    @Override
                                    public Integer next() {
                                        if (this.current >= nutritionStates.length) {
                                            throw new NoSuchElementException();
                                        }
                                        return nutritionStates[this.current++];
                                    }
                                };
                            }

                            @Override
                            public int size() {
                                return PlayerNutritionStateMap.this.size();
                            }
                        };
                    }
                }
            }
            return this.values;
        }

        @Override
        public Set<Entry<Nutrition, Integer>> entrySet() {
            if (this.entries == null) {
                synchronized (this) {
                    if (this.entries == null) {
                        this.entries = new AbstractSet<>() {
                            @Override
                            public Iterator<Entry<Nutrition, Integer>> iterator() {
                                Nutrition[] values = Nutrition.values();
                                int[] nutritionStates = PlayerNutritionStateMap.this.nutritionState;
                                return new Iterator<>() {
                                    private int current = 0;

                                    @Override
                                    public boolean hasNext() {
                                        return this.current < nutritionStates.length;
                                    }

                                    @Override
                                    public Entry<Nutrition, Integer> next() {
                                        Nutrition value = values[this.current];
                                        int amount = nutritionStates[this.current];
                                        Entry<Nutrition, Integer> entry = Map.entry(value, amount);
                                        this.current++;
                                        return entry;
                                    }
                                };
                            }

                            @Override
                            public int size() {
                                return PlayerNutritionStateMap.this.size();
                            }
                        };
                    }
                }
            }
            return this.entries;
        }
    }
}
