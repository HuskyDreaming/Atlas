package com.huskydreaming.claims.model.claims;

import com.huskydreaming.claims.model.flags.ClaimFlag;
import com.huskydreaming.claims.model.flags.ClaimPermissions;
import com.huskydreaming.claims.model.positions.BlockPosition;
import com.huskydreaming.claims.model.positions.BoundingBox;

import java.util.Objects;
import java.util.UUID;

public record AreaClaim(
        UUID worldId,
        UUID ownerId,
        BoundingBox bounds,
        ClaimPermissions permissions,
        int priority
) {

    public AreaClaim {
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(bounds, "bounds");
    }

    public boolean allows(ClaimFlag flag) {
        return permissions.allows(flag);
    }

    public boolean can(UUID playerId, ClaimFlag flag) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(flag, "flag");
        return ownerId.equals(playerId) || allows(flag);
    }

    public boolean contains(BlockPosition pos) {
        Objects.requireNonNull(pos, "pos");
        return bounds.contains(pos);
    }

    public boolean intersects(AreaClaim other) {
        Objects.requireNonNull(other, "other");
        return worldId.equals(other.worldId) && bounds.intersects(other.bounds);
    }
}
