package com.huskydreaming.claims.model.positions;

import com.huskydreaming.claims.helpers.SpatialGrid;

import java.util.Objects;

public record ChunkPosition(int x, int z) {

    public static ChunkPosition fromBlock(BlockPosition blockPosition) {
        Objects.requireNonNull(blockPosition, "blockPosition");

        int cx = SpatialGrid.chunkCoord(blockPosition.x());
        int cz = SpatialGrid.chunkCoord(blockPosition.z());
        return new ChunkPosition(cx, cz);
    }

    public static ChunkPosition fromKey(long key) {
        int x = SpatialGrid.unpackX(key);
        int z = SpatialGrid.unpackZ(key);
        return new ChunkPosition(x, z);
    }

    public int minBlockX() {
        return x * SpatialGrid.CHUNK_SIZE;
    }

    public int minBlockZ() {
        return z * SpatialGrid.CHUNK_SIZE;
    }

    public int maxBlockX() {
        return minBlockX() + SpatialGrid.CHUNK_SIZE - 1;
    }

    public int maxBlockZ() {
        return minBlockZ() + SpatialGrid.CHUNK_SIZE - 1;
    }

    public boolean contains(BlockPosition block) {
        Objects.requireNonNull(block, "block");

        return block.x() >= minBlockX() && block.x() <= maxBlockX()
                && block.z() >= minBlockZ() && block.z() <= maxBlockZ();
    }
}
