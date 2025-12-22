package com.huskydreaming.claims.enumeration;

public enum ClaimFlag {
    BUILD(1),
    BREAK(1 << 1),
    INTERACT(1 << 2),
    CONTAINERS(1 << 3),
    PVP(1 << 4),
    EXPLOSIONS(1 << 5),
    PROJECTILES(1 << 6);

    private final int bit;

    ClaimFlag(int bit) {
        this.bit = bit;
    }

    public int getBit() {
        return bit;
    }
}