package com.huskydreaming.claims.model.helpers;

import com.huskydreaming.claims.model.claims.AreaClaim;
import com.huskydreaming.claims.model.positions.BoundingBox;
import com.huskydreaming.claims.model.positions.ChunkPosition;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AreaClaimHelper {

    public static AreaClaim areaClaim(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return areaClaim(UUID.randomUUID(), minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static AreaClaim areaClaim(UUID worldId, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        UUID ownerId = UUID.randomUUID();
        BoundingBox bounds = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        return new AreaClaim(worldId, ownerId, bounds, null, 0);
    }

    public static Set<ChunkPosition> toSet(Iterable<ChunkPosition> iterable) {
        Set<ChunkPosition> chunkPositions = new HashSet<>();
        for (ChunkPosition chunkPosition : iterable) {
            chunkPositions.add(chunkPosition);
        }
        return chunkPositions;
    }

    public static Set<Long> toLongSet(Iterable<Long> iterable) {
        Set<Long> longSet = new HashSet<>();
        for (long longValue : iterable) {
            longSet.add(longValue);
        }
        return longSet;
    }
}
