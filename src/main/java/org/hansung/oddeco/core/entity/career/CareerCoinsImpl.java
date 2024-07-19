package org.hansung.oddeco.core.entity.career;

class CareerCoinsImpl implements CareerCoins {
    private volatile int coins;

    public CareerCoinsImpl() {
        this(0);
    }

    public CareerCoinsImpl(int coins) {
        this.coins = coins;
    }

    @Override
    public int getCoins() {
        return this.coins;
    }

    @Override
    public void setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public String toString() {
        return String.format("CareerCoins(amount=%d)", this.coins);
    }
}
