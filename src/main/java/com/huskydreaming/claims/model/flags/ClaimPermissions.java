package com.huskydreaming.claims.model.flags;

import com.huskydreaming.claims.registry.ClaimFlagRegistry;

import java.util.BitSet;

public final class ClaimPermissions {

    private final BitSet bits;

    public ClaimPermissions() {
        this.bits = new BitSet();
    }

    public void allow(ClaimFlag flag) {
        bits.set(ClaimFlagRegistry.indexOf(flag));
    }

    public void deny(ClaimFlag flag) {
        bits.clear(ClaimFlagRegistry.indexOf(flag));
    }

    public boolean allows(ClaimFlag flag) {
        return bits.get(ClaimFlagRegistry.indexOf(flag));
    }

    public BitSet toBitSet() {
        return (BitSet) bits.clone();
    }
}