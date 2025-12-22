package com.huskydreaming.claims.managers;

import com.huskydreaming.claims.enumeration.ClaimFlag;
import com.huskydreaming.claims.model.claim.AreaClaim;
import com.huskydreaming.claims.model.position.BlockPosition;
import com.huskydreaming.claims.model.position.ChunkPosition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stores and resolves {@link AreaClaim} (AABB) claims.
 *
 * <p>Claims are bucketed by {@link ChunkPosition} for fast lookups.</p>
 *
 * <p><strong>Threading:</strong> call write methods from a single thread (typically the main server thread).</p>
 */
public final class AreaClaimManager {

    /**
     * World UUID -> {@link ChunkPosition} -> claims touching that chunk.
     */
    private final Map<UUID, Map<ChunkPosition, CopyOnWriteArrayList<AreaClaim>>> claimsByWorldChunk = new ConcurrentHashMap<>();

    /**
     * Adds a claim if it does not overlap an existing area claim in the same world.
     *
     * @param claim the claim to add
     * @return {@code true} if created, {@code false} if it overlaps an existing claim
     */
    public boolean createClaim(AreaClaim claim) {
        Objects.requireNonNull(claim, "claim");

        for (var existing : getPotentialOverlaps(claim)) {
            if (existing.intersects(claim)) return false;
        }

        attachClaim(claim);
        return true;
    }

    /**
     * Removes a claim from the index (no-op if not present).
     *
     * @param claim the claim to remove
     */
    public void removeClaim(AreaClaim claim) {
        Objects.requireNonNull(claim, "claim");
        detachClaim(claim);
    }

    /**
     * Returns the highest-priority claim at a block position.
     *
     * @param worldId  the world UUID
     * @param position the block position
     * @return the matching {@link AreaClaim}, or {@code null} if none apply
     */
    public AreaClaim getClaimAt(UUID worldId, BlockPosition position) {
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(position, "position");

        var worldMap = claimsByWorldChunk.get(worldId);
        if (worldMap == null) return null;

        var chunk = ChunkPosition.fromBlock(position);
        var candidates = worldMap.get(chunk);
        if (candidates == null || candidates.isEmpty()) return null;

        AreaClaim best = null;
        for (var claim : candidates) {
            if (!claim.contains(position)) continue;
            if (best == null || claim.priority() > best.priority()) best = claim;
        }
        return best;
    }

    /**
     * Checks whether a player can perform an action at a position with respect to area claims only.
     * If no {@link AreaClaim} applies, this returns {@code true}.
     *
     * @param playerId the player UUID
     * @param worldId  the world UUID
     * @param position the block position
     * @param flag     the action to check
     * @return {@code true} if allowed, otherwise {@code false}
     */
    public boolean canPerformAction(
            UUID playerId,
            UUID worldId,
            BlockPosition position,
            ClaimFlag flag
    ) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(flag, "flag");

        var claim = getClaimAt(worldId, position);
        return claim == null || claim.can(playerId, flag);
    }

    /**
     * Removes all area claims in a world.
     *
     * @param worldId the world UUID
     */
    public void clearWorld(UUID worldId) {
        Objects.requireNonNull(worldId, "worldId");
        claimsByWorldChunk.remove(worldId);
    }

    /**
     * Clears all area claims across all worlds.
     */
    public void clearAll() {
        claimsByWorldChunk.clear();
    }

    /**
     * Adds a claim to the chunk buckets it touches.
     *
     * @param claim the claim to index
     */
    private void attachClaim(AreaClaim claim) {
        var worldMap = claimsByWorldChunk.computeIfAbsent(claim.worldId(), _ -> new ConcurrentHashMap<>());

        for (var chunk : claim.touchedChunks()) {
            worldMap.computeIfAbsent(chunk, _ -> new CopyOnWriteArrayList<>())
                    .add(claim);
        }
    }

    /**
     * Removes a claim from all chunk buckets it touches.
     *
     * @param claim the claim to unindex
     */
    private void detachClaim(AreaClaim claim) {
        var worldMap = claimsByWorldChunk.get(claim.worldId());
        if (worldMap == null) return;

        for (var chunk : claim.touchedChunks()) {
            var list = worldMap.get(chunk);
            if (list == null) continue;

            list.remove(claim);
            if (list.isEmpty()) worldMap.remove(chunk);
        }

        if (worldMap.isEmpty()) claimsByWorldChunk.remove(claim.worldId());
    }

    /**
     * Returns claims that share at least one touched chunk with {@code claim}.
     * Used as a fast pre-filter before {@link AreaClaim#intersects(AreaClaim)} checks.
     *
     * @param claim the claim being added
     * @return potential overlaps (may contain non-overlapping claims)
     */
    private Set<AreaClaim> getPotentialOverlaps(AreaClaim claim) {
        var worldMap = claimsByWorldChunk.get(claim.worldId());
        if (worldMap == null) return Set.of();

        var out = new HashSet<AreaClaim>();
        for (var chunk : claim.touchedChunks()) {
            var list = worldMap.get(chunk);
            if (list != null) out.addAll(list);
        }
        return out;
    }
}