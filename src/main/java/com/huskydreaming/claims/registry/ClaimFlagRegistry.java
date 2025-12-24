package com.huskydreaming.claims.registry;

import com.huskydreaming.claims.model.flags.ClaimFlag;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ClaimFlagRegistry {

    private static final Map<String, Integer> indexById = new ConcurrentHashMap<>();
    private static final List<ClaimFlag> flags = new ArrayList<>();

    private ClaimFlagRegistry() {}

    public static synchronized ClaimFlag register(String id) {
        return register(new ClaimFlag(id));
    }

    public static synchronized ClaimFlag register(ClaimFlag flag) {
        if (indexById.containsKey(flag.id())) {
            return flag;
        }

        int index = flags.size();
        indexById.put(flag.id(), index);
        flags.add(flag);
        return flag;
    }

    public static int indexOf(ClaimFlag flag) {
        Integer idx = indexById.get(flag.id());
        if (idx == null) {
            throw new IllegalStateException("Flag not registered: " + flag.id());
        }
        return idx;
    }

    public static List<ClaimFlag> all() {
        return List.copyOf(flags);
    }
}
