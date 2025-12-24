package com.huskydreaming.claims.model.claims;

import com.huskydreaming.claims.enumeration.ClaimType;
import com.huskydreaming.claims.model.flags.ClaimFlag;
import com.huskydreaming.claims.model.flags.ClaimPermissions;
import com.huskydreaming.claims.model.positions.BlockPosition;
import com.huskydreaming.claims.model.positions.BoundingBox;

import java.util.Objects;
import java.util.UUID;

public record PlotClaim(
        UUID worldId,
        UUID ownerId,
        BoundingBox bounds,
        ClaimPermissions permissions,
        int priority,
        ClaimType parentClaimType,
        UUID parentClaimId
) {

    public PlotClaim {
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(bounds, "bounds");
        Objects.requireNonNull(parentClaimType, "parentClaimType");
        Objects.requireNonNull(parentClaimId, "parentClaimId");
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
        return bounds.contains(pos);
    }

    public boolean isChunkPlot() {
        return parentClaimType == ClaimType.CHUNK;
    }

    public boolean isAreaPlot() {
        return parentClaimType == ClaimType.AREA;
    }
}
