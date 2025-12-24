package com.huskydreaming.claims.model.positions;

import com.huskydreaming.claims.helpers.SpatialGrid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChunkPositionTest {

    @Test
    void fromBlockBoundariesPositive() {
        assertChunkFromBlock(0, 0, 0, 0);
        assertChunkFromBlock(31, 31, 0, 0);

        assertChunkFromBlock(32, 0, 1, 0);
        assertChunkFromBlock(0, 32, 0, 1);

        assertChunkFromBlock(32, 32, 1, 1);
        assertChunkFromBlock(63, 63, 1, 1);

        assertChunkFromBlock(64, 64, 2, 2);
    }

    @Test
    void fromBlockBoundariesNegative() {
        assertChunkFromBlock(-1, -1, -1, -1);
        assertChunkFromBlock(-32, -32, -1, -1);
        assertChunkFromBlock(-33, -33, -2, -2);

        assertChunkFromBlock(-1, 0, -1, 0);
        assertChunkFromBlock(0, -1, 0, -1);

        assertChunkFromBlock(-32, 0, -1, 0);
        assertChunkFromBlock(0, -32, 0, -1);
    }

    @Test
    void containsIgnoresYAndMatchesXZColumn() {
        ChunkPosition chunk = new ChunkPosition(0, 0);

        // Inside
        assertTrue(chunk.contains(new BlockPosition(0, -999, 0)));
        assertTrue(chunk.contains(new BlockPosition(31, 999, 31)));
        assertTrue(chunk.contains(new BlockPosition(15, 0, 20)));

        // Outside X
        assertFalse(chunk.contains(new BlockPosition(-1, 0, 0)));
        assertFalse(chunk.contains(new BlockPosition(32, 0, 0)));

        // Outside Z
        assertFalse(chunk.contains(new BlockPosition(0, 0, -1)));
        assertFalse(chunk.contains(new BlockPosition(0, 0, 32)));
    }

    @Test
    void keyRoundTripIsLossless() {
        ChunkPosition chunkPosition = new ChunkPosition(-12345, 67890);
        long key = SpatialGrid.chunkKey(chunkPosition);
        ChunkPosition unpacked = ChunkPosition.fromKey(key);

        assertEquals(chunkPosition, unpacked);
    }

    private static void assertChunkFromBlock(int blockX, int blockZ, int expectedChunkX, int expectedChunkZ) {
        BlockPosition block = new BlockPosition(blockX, 0, blockZ);
        ChunkPosition chunk = ChunkPosition.fromBlock(block);

        assertEquals(expectedChunkX, chunk.x(), "chunkX");
        assertEquals(expectedChunkZ, chunk.z(), "chunkZ");
    }
}
