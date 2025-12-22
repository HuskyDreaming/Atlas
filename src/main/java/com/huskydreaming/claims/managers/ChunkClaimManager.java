package com.huskydreaming.claims.managers;

import com.huskydreaming.claims.enumeration.ClaimFlag;
import com.huskydreaming.claims.model.claim.ChunkClaim;
import com.huskydreaming.claims.model.owners.ClaimOwner;
import com.huskydreaming.claims.model.position.BlockPosition;
import com.huskydreaming.claims.model.world.WorldChunk;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages chunk-based land claims.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Create and remove chunk claims</li>
 *   <li>Fast claim lookup by chunk or by block position</li>
 *   <li>Maintain secondary indexes for owner/world queries</li>
 * </ul>
 *
 * <p>Wilderness is represented implicitly by the absence of a {@link ChunkClaim}.
 */
public final class ChunkClaimManager {

    /**
     * Primary index: WorldChunk -> ChunkClaim.
     * This is the authoritative source of truth.
     */
    private final Map<WorldChunk, ChunkClaim> claimsByChunk = new ConcurrentHashMap<>();

    /**
     * Secondary index: Owner UUID -> Set of WorldChunks owned by that owner.
     */
    private final Map<UUID, Set<WorldChunk>> chunksByOwner = new ConcurrentHashMap<>();

    /**
     * Secondary index: World UUID -> Set of WorldChunks claimed in that world.
     */
    private final Map<UUID, Set<WorldChunk>> chunksByWorld = new ConcurrentHashMap<>();

    /**
     * Creates a new chunk claim.
     *
     * <p>This operation is atomic and thread-safe. If the chunk is already claimed,
     * the operation fails.
     *
     * @param owner the owner of the claim
     * @param worldChunk the chunk being claimed
     * @param flagMask permission flags for the claim
     * @return {@code true} if created, {@code false} if already claimed
     */
    public boolean createClaim(ClaimOwner owner, WorldChunk worldChunk, int flagMask) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(worldChunk, "worldChunk");

        var claim = new ChunkClaim(owner, worldChunk, flagMask);
        var existing = claimsByChunk.putIfAbsent(worldChunk, claim);
        if (existing != null) return false;

        try {
            chunksByOwner.computeIfAbsent(owner.uniqueId(), _ -> ConcurrentHashMap.newKeySet())
                    .add(worldChunk);

            chunksByWorld.computeIfAbsent(worldChunk.worldId(), _ -> ConcurrentHashMap.newKeySet())
                    .add(worldChunk);

            return true;
        } catch (Exception e) {
            claimsByChunk.remove(worldChunk);
            throw e;
        }
    }

    /**
     * Removes a chunk claim.
     *
     * @param worldChunk the chunk to unclaim
     * @return {@code true} if removed, {@code false} if no claim existed
     */
    public boolean removeClaim(WorldChunk worldChunk) {
        Objects.requireNonNull(worldChunk, "worldChunk");

        var claim = claimsByChunk.remove(worldChunk);
        if (claim == null) return false;

        var ownerId = claim.claimOwner().uniqueId();
        var ownerChunks = chunksByOwner.get(ownerId);
        if (ownerChunks != null) {
            ownerChunks.remove(worldChunk);
            if (ownerChunks.isEmpty()) chunksByOwner.remove(ownerId);
        }

        var worldChunks = chunksByWorld.get(worldChunk.worldId());
        if (worldChunks != null) {
            worldChunks.remove(worldChunk);
            if (worldChunks.isEmpty()) chunksByWorld.remove(worldChunk.worldId());
        }

        return true;
    }

    /**
     * Gets the claim for a chunk.
     *
     * @param worldChunk the chunk to query
     * @return an Optional claim
     */
    public Optional<ChunkClaim> getClaimAt(WorldChunk worldChunk) {
        Objects.requireNonNull(worldChunk, "worldChunk");
        return Optional.ofNullable(claimsByChunk.get(worldChunk));
    }

    /**
     * Gets the claim at a block position.
     *
     * @param worldId world UUID
     * @param blockPosition block coordinates
     * @return an Optional claim
     */
    public Optional<ChunkClaim> getClaimAt(UUID worldId, BlockPosition blockPosition) {
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(blockPosition, "blockPosition");

        var worldChunk = WorldChunk.fromBlock(worldId, blockPosition);
        return getClaimAt(worldChunk);
    }

    /**
     * Checks whether a chunk is claimed.
     *
     * @param worldChunk the chunk
     * @return true if claimed
     */
    public boolean isClaimed(WorldChunk worldChunk) {
        Objects.requireNonNull(worldChunk, "worldChunk");
        return claimsByChunk.containsKey(worldChunk);
    }

    /**
     * Checks whether a player can perform an action at a block position.
     *
     * <p>Resolution:
     * <ol>
     *   <li>If unclaimed (wilderness), allowed</li>
     *   <li>If owner, allowed</li>
     *   <li>Otherwise, flags determine permission</li>
     * </ol>
     *
     * @return true if allowed
     */
    public boolean canPerformAction(UUID playerId, UUID worldId, BlockPosition blockPosition, ClaimFlag flag) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(worldId, "worldId");
        Objects.requireNonNull(blockPosition, "blockPosition");
        Objects.requireNonNull(flag, "flag");

        var claim = getClaimAt(worldId, blockPosition);
        return claim.map(c -> c.can(playerId, flag)).orElse(true);
    }

    /**
     * Updates the flag mask for an existing claim.
     *
     * @param worldChunk the chunk claim to update
     * @param newFlagMask new flags
     * @return true if updated, false if claim does not exist
     */
    public boolean updateFlags(WorldChunk worldChunk, int newFlagMask) {
        Objects.requireNonNull(worldChunk, "worldChunk");

        return claimsByChunk.computeIfPresent(worldChunk,
                (_, old) -> new ChunkClaim(old.claimOwner(), old.worldChunk(), newFlagMask)
        ) != null;
    }

    /**
     * Transfers ownership of an existing claim to a new owner.
     *
     * @param worldChunk the chunk
     * @param newOwner the new owner
     * @return true if successful, false if no claim exists
     */
    public boolean transferOwnership(WorldChunk worldChunk, ClaimOwner newOwner) {
        Objects.requireNonNull(worldChunk, "worldChunk");
        Objects.requireNonNull(newOwner, "newOwner");

        var existing = claimsByChunk.get(worldChunk);
        if (existing == null) return false;

        var oldOwnerId = existing.claimOwner().uniqueId();
        var oldOwnerChunks = chunksByOwner.get(oldOwnerId);
        if (oldOwnerChunks != null) {
            oldOwnerChunks.remove(worldChunk);
            if (oldOwnerChunks.isEmpty()) chunksByOwner.remove(oldOwnerId);
        }

        var updated = new ChunkClaim(newOwner, worldChunk, existing.flagMask());
        claimsByChunk.put(worldChunk, updated);

        chunksByOwner.computeIfAbsent(newOwner.uniqueId(), _ -> ConcurrentHashMap.newKeySet())
                .add(worldChunk);

        return true;
    }

    /**
     * Removes all claims owned by the specified owner.
     *
     * @param ownerId owner UUID
     * @return number of claims removed
     */
    public int removeAllClaimsByOwner(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");

        var chunks = chunksByOwner.remove(ownerId);
        if (chunks == null || chunks.isEmpty()) return 0;

        var removed = 0;
        for (var chunk : Set.copyOf(chunks)) {
            if (removeClaim(chunk)) removed++;
        }
        return removed;
    }

    /**
     * Gets a snapshot of all chunks owned by an owner.
     *
     * @param ownerId owner UUID
     * @return immutable snapshot
     */
    public Set<WorldChunk> getChunksByOwner(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return Set.copyOf(chunksByOwner.getOrDefault(ownerId, Set.of()));
    }

    /**
     * Gets a snapshot of all claimed chunks in a world.
     *
     * @param worldId world UUID
     * @return immutable snapshot
     */
    public Set<WorldChunk> getChunksInWorld(UUID worldId) {
        Objects.requireNonNull(worldId, "worldId");
        return Set.copyOf(chunksByWorld.getOrDefault(worldId, Set.of()));
    }

    /**
     * Gets the number of chunks owned by an owner.
     *
     * @param ownerId owner UUID
     * @return owned chunk count
     */
    public int getChunkCount(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        return chunksByOwner.getOrDefault(ownerId, Set.of()).size();
    }

    /**
     * Returns a snapshot of all chunk claims.
     *
     * @return immutable snapshot
     */
    public Collection<ChunkClaim> getAllClaims() {
        return List.copyOf(claimsByChunk.values());
    }

    /**
     * Clears all claims and indexes.
     *
     * <p><strong>Use with caution.</strong> Typically only for shutdown/testing.
     */
    public void clearAll() {
        claimsByChunk.clear();
        chunksByOwner.clear();
        chunksByWorld.clear();
    }
}