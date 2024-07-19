package org.hansung.oddeco.core.entity.career;

public interface CareerCoins {
    public static CareerCoins of() {
        return new CareerCoinsImpl();
    }

    public static CareerCoins of(int coins) {
        return new CareerCoinsImpl(coins);
    }

    public abstract int getCoins();

    public abstract void setCoins(int coins);
}
