package com.huskydreaming.claims.claims;

import com.huskydreaming.claims.helpers.SpatialGrid;
import com.huskydreaming.claims.model.claims.ChunkClaim;
import com.huskydreaming.claims.model.flags.ClaimFlag;
import com.huskydreaming.claims.model.flags.ClaimPermissions;
import com.huskydreaming.claims.model.positions.BlockPosition;
import com.huskydreaming.claims.model.positions.ChunkPosition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ChunkClaims {

    private final UUID worldId;

    private final Map<Long, ChunkClaim> claims = new ConcurrentHashMap<>();
    private final Map<UUID, Set<Long>> ownerKeys = new ConcurrentHashMap<>();

    public ChunkClaims(UUID worldId) {
        this.worldId = Objects.requireNonNull(worldId, "worldId");
    }

    public UUID worldId() {
        return worldId;
    }

    public boolean claim(UUID ownerId, BlockPosition blockPosition, ClaimPermissions permissions) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(blockPosition, "blockPosition");
        Objects.requireNonNull(permissions, "permissions");

        ChunkPosition chunkPosition = ChunkPosition.fromBlock(blockPosition);
        return claim(ownerId, chunkPosition, permissions);
    }

    public boolean claim(UUID ownerId, ChunkPosition chunkPosition, ClaimPermissions permissions) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(chunkPosition, "chunkPosition");
        Objects.requireNonNull(permissions, "permissions");

        ChunkClaim claim = new ChunkClaim(worldId, ownerId, chunkPosition, permissions);
        long key = SpatialGrid.chunkKey(chunkPosition);

        ChunkClaim existing = claims.putIfAbsent(key, claim);
        if (existing != null) {
            return false;
        }

        ownerKeys.computeIfAbsent(ownerId, uuid -> ConcurrentHashMap.newKeySet()).add(key);
        return true;
    }

    public boolean unclaim(BlockPosition blockPosition) {
        Objects.requireNonNull(blockPosition, "blockPosition");
        return unclaim(ChunkPosition.fromBlock(blockPosition));
    }

    public boolean unclaim(ChunkPosition chunkPosition) {
        Objects.requireNonNull(chunkPosition, "chunkPosition");

        long key = SpatialGrid.chunkKey(chunkPosition);
        ChunkClaim removed = claims.remove(key);
        if (removed == null) {
            return false;
        }

        UUID ownerId = removed.ownerId();
        Set<Long> set = ownerKeys.get(ownerId);
        if (set != null) {
            set.remove(key);
            if (set.isEmpty()) {
                ownerKeys.remove(ownerId);
            }
        }

        return true;
    }

    public ChunkClaim getAt(BlockPosition position) {
        Objects.requireNonNull(position, "position");

        ChunkPosition chunkPosition = ChunkPosition.fromBlock(position);
        long key = SpatialGrid.chunkKey(chunkPosition);
        return claims.get(key);
    }

    public ChunkClaim getAt(ChunkPosition chunkPosition) {
        Objects.requireNonNull(chunkPosition, "chunkPosition");
        long key = SpatialGrid.chunkKey(chunkPosition);
        return claims.get(key);
    }

    public boolean isClaimed(ChunkPosition chunkPosition) {
        Objects.requireNonNull(chunkPosition, "chunkPosition");
        long key = SpatialGrid.chunkKey(chunkPosition);
        return claims.containsKey(key);
    }

    public boolean canPerformAction(UUID playerId, BlockPosition position, ClaimFlag flag) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(flag, "flag");

        return getAt(position).can(playerId, flag);
    }

    public int countOwned(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return ownerKeys.getOrDefault(ownerId, Set.of()).size();
    }

    public Collection<ChunkClaim> allClaims() {
        return List.copyOf(claims.values());
    }

    public void clear() {
        claims.clear();
        ownerKeys.clear();
    }
}