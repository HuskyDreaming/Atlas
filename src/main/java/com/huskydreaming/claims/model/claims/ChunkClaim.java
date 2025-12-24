package com.huskydreaming.claims.model.claims;

import com.huskydreaming.claims.model.flags.ClaimFlag;
import com.huskydreaming.claims.model.flags.ClaimPermissions;
import com.huskydreaming.claims.model.positions.ChunkPosition;

import java.util.Objects;
import java.util.UUID;

public record ChunkClaim(
        UUID worldId,
        UUID ownerId,
        ChunkPosition chunkPosition,
        ClaimPermissions permissions
) {

    public ChunkClaim {
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(ownerId, "ownerId");
    }

    public boolean allows(ClaimFlag flag) {
        return permissions.allows(flag);
    }

    public boolean can(UUID playerId, ClaimFlag flag) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(flag, "flag");
        return ownerId.equals(playerId) || allows(flag);
    }
}
