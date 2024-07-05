package org.hansung.oddeco.butcher;

import org.bukkit.event.Listener;

public class Butcher implements Listener {
    private int level;

    public Butcher(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
