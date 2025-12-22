package com.huskydreaming.claims.model.position;

public record ChunkPosition(int x, int y, int z) {

    public static final int CHUNK_SIZE = 32;

    public static ChunkPosition fromBlock(BlockPosition blockPosition) {
        var x = Math.floorDiv(blockPosition.x(), CHUNK_SIZE);
        var y = Math.floorDiv(blockPosition.y(), CHUNK_SIZE);
        var z = Math.floorDiv(blockPosition.z(), CHUNK_SIZE);

        return new ChunkPosition(x, y, z);
    }

    public int minBlockX() {
        return x * CHUNK_SIZE;
    }

    public int minBlockY() {
        return y * CHUNK_SIZE;
    }

    public int minBlockZ() {
        return z * CHUNK_SIZE;
    }

    public int maxBlockX() {
        return minBlockX() + CHUNK_SIZE - 1;
    }

    public int maxBlockY() {
        return minBlockY() + CHUNK_SIZE - 1;
    }

    public int maxBlockZ() {
        return minBlockZ() + CHUNK_SIZE - 1;
    }

    public boolean contains(BlockPosition block) {
        return block.x() >= minBlockX() && block.x() <= maxBlockX()
                && block.y() >= minBlockY() && block.y() <= maxBlockY()
                && block.z() >= minBlockZ() && block.z() <= maxBlockZ();
    }
}