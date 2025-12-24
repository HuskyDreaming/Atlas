package com.huskydreaming.claims.claims;

import com.huskydreaming.claims.helpers.SpatialGrid;
import com.huskydreaming.claims.model.claims.AreaClaim;
import com.huskydreaming.claims.model.positions.BlockPosition;
import com.huskydreaming.claims.model.positions.BoundingBox;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AreaClaims {

    private final UUID worldId;

    private final Map<Long, List<AreaClaim>> claims = new ConcurrentHashMap<>();

    public AreaClaims(UUID worldId) {
        this.worldId = Objects.requireNonNull(worldId, "worldId");
    }

    public boolean add(AreaClaim claim) {
        Objects.requireNonNull(claim, "claim");

        if (!claim.worldId().equals(worldId)) {
            throw new IllegalArgumentException("Claim belongs to another world");
        }

        BoundingBox bounds = claim.bounds();
        for (AreaClaim existing : potentialOverlaps(claim)) {
            if (existing.bounds().intersects(bounds)) {
                return false;
            }
        }

        for (long cellKey : SpatialGrid.touchedCells(bounds)) {
            claims.computeIfAbsent(cellKey, l -> new CopyOnWriteArrayList<>())
                    .add(claim);
        }

        return true;
    }

    public boolean remove(AreaClaim claim) {
        Objects.requireNonNull(claim, "claim");

        boolean removed = false;

        for (long cellKey : SpatialGrid.touchedCells(claim.bounds())) {
            List<AreaClaim> list = claims.get(cellKey);
            if (list == null) continue;

            removed |= list.remove(claim);
            if (list.isEmpty()) {
                claims.remove(cellKey);
            }
        }

        return removed;
    }

    public AreaClaim getAt(BlockPosition position) {
        Objects.requireNonNull(position, "position");

        long cellKey = SpatialGrid.cellKey(position.x(), position.z());
        List<AreaClaim> candidates = claims.get(cellKey);
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        AreaClaim best = null;
        for (AreaClaim claim : candidates) {
            if (!claim.bounds().contains(position)) continue;

            if (best == null || claim.priority() > best.priority()) {
                best = claim;
            }
        }

        return best;
    }

    public void clear() {
        claims.clear();
    }

    private Set<AreaClaim> potentialOverlaps(AreaClaim claim) {
        Set<AreaClaim> out = new HashSet<>();

        BoundingBox bounds = claim.bounds();
        for (long cellKey : SpatialGrid.touchedCells(bounds)) {
            List<AreaClaim> list = claims.get(cellKey);
            if (list != null) out.addAll(list);
        }

        return out;
    }
}