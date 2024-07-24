package org.hansung.oddeco.hunter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "hunters")
@ToString
public class HunterData {
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    String id;

    @Getter
    @Setter
    @Column(name = "level")
    private int level;

    public HunterData() {}

    public HunterData(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public UUID getUUID() { return UUID.fromString(this.id); }
}
