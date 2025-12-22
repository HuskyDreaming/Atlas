package com.huskydreaming.claims.helpers;

import com.huskydreaming.claims.enumeration.ClaimFlag;

public final class ClaimFlags {

    private ClaimFlags() {}

    public static int none() {
        return 0;
    }

    public static int all() {
        int mask = 0;
        for (var flag : ClaimFlag.values()) {
            mask |= flag.getBit();
        }
        return mask;
    }

    public static int of(ClaimFlag... flags) {
        int mask = 0;
        for (var flag : flags) {
            mask |= flag.getBit();
        }
        return mask;
    }

    public static boolean has(int mask, ClaimFlag flag) {
        return flag != null && (mask & flag.getBit()) != 0;
    }

    public static int add(int mask, ClaimFlag flag) {
        return mask | flag.getBit();
    }

    public static int remove(int mask, ClaimFlag flag) {
        return mask & ~flag.getBit();
    }

    public static int toggle(int mask, ClaimFlag flag) {
        return mask ^ flag.getBit();
    }
}
