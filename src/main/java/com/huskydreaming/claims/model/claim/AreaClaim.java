package com.huskydreaming.claims.model.claim;

import com.huskydreaming.claims.enumeration.ClaimFlag;
import com.huskydreaming.claims.model.owners.ClaimOwner;
import com.huskydreaming.claims.model.position.BlockPosition;
import com.huskydreaming.claims.model.position.BoundingBox;
import com.huskydreaming.claims.model.position.ChunkPosition;

import java.util.ArrayList;
import java.util.UUID;

public record AreaClaim(UUID worldId, BoundingBox boundingBox, ClaimOwner claimOwner, int flagMask, int priority) {

    public boolean allows(ClaimFlag flag) {
        return (flagMask & flag.getBit()) != 0;
    }

    public boolean contains(BlockPosition position) {
        return boundingBox.contains(position);
    }

    public boolean intersects(AreaClaim other) {
        if (!worldId.equals(other.worldId())) return false;
        return boundingBox.intersects(other.boundingBox());
    }

    public boolean can(UUID playerId, ClaimFlag flag) {
        if (claimOwner.isOwner(playerId)) return true;
        return allows(flag);
    }

    public Iterable<ChunkPosition> touchedChunks() {
        var minCx = Math.floorDiv(boundingBox.minX(), ChunkPosition.CHUNK_SIZE);
        var minCy = Math.floorDiv(boundingBox.minY(), ChunkPosition.CHUNK_SIZE);
        var minCz = Math.floorDiv(boundingBox.minZ(), ChunkPosition.CHUNK_SIZE);

        var maxCx = Math.floorDiv(boundingBox.maxX(), ChunkPosition.CHUNK_SIZE);
        var maxCy = Math.floorDiv(boundingBox.maxY(), ChunkPosition.CHUNK_SIZE);
        var maxCz = Math.floorDiv(boundingBox.maxZ(), ChunkPosition.CHUNK_SIZE);

        var xCount = (maxCx - minCx) + 1;
        var yCount = (maxCy - minCy) + 1;
        var zCount = (maxCz - minCz) + 1;

        var chunkPositions = new ArrayList<ChunkPosition>(xCount * yCount * zCount);

        for (var cx = minCx; cx <= maxCx; cx++) {
            for (var cy = minCy; cy <= maxCy; cy++) {
                for (var cz = minCz; cz <= maxCz; cz++) {
                    chunkPositions.add(new ChunkPosition(cx, cy, cz));
                }
            }
        }
        return chunkPositions;
    }
}