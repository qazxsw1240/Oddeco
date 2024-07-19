package org.hansung.oddeco.core.entity.career;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "player_career_coins")
@ToString
public class CareerCoinsData {
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private String id;

    @Getter
    @Setter
    @Column(name = "amount", nullable = false)
    private int coins;

    public CareerCoinsData() {}

    public CareerCoinsData(String id, int coins) {
        this.id = id;
        this.coins = coins;
    }

    public UUID getUUID() {
        return UUID.fromString(this.id);
    }
}
