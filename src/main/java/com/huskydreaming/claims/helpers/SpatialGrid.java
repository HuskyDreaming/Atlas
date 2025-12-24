package com.huskydreaming.claims.helpers;

import com.huskydreaming.claims.model.positions.BlockPosition;
import com.huskydreaming.claims.model.positions.BoundingBox;
import com.huskydreaming.claims.model.positions.ChunkPosition;

import java.util.ArrayList;
import java.util.List;

public final class SpatialGrid {

    public static final int CHUNK_SIZE = 32;
    public static final int CELL_SIZE = 8;

    public static int chunkCoord(int blockCoord) {
        return Math.floorDiv(blockCoord, CHUNK_SIZE);
    }

    public static long chunkKey(ChunkPosition chunkPosition) {
        return chunkKey(chunkPosition.x(), chunkPosition.z());
    }

    public static long chunkKey(int chunkX, int chunkZ) {
        return pack(chunkX, chunkZ);
    }

    public static long chunkKeyFromBlock(int blockX, int blockZ) {
        return pack(chunkCoord(blockX), chunkCoord(blockZ));
    }

    public static int cellCoord(int blockCoord) {
        return Math.floorDiv(blockCoord, CELL_SIZE);
    }

    public static long cellKey(int cellX, int cellZ) {
        return pack(cellX, cellZ);
    }


    public static long cellKeyFromBlock(BlockPosition blockPosition) {
        return pack(cellCoord(blockPosition.x()), cellCoord(blockPosition.z()));
    }

    public static long cellKeyFromBlock(int blockX, int blockZ) {
        return pack(cellCoord(blockX), cellCoord(blockZ));
    }

    public static long pack(int x, int z) {
        return (((long) x) << 32) ^ (z & 0xFFFF_FFFFL);
    }

    public static int unpackX(long key) {
        return (int) (key >> 32);
    }

    public static int unpackZ(long key) {
        return (int) key;
    }

    public static Iterable<Long> touchedCells(BoundingBox boundingBox) {
        int minX = cellCoord(boundingBox.minX());
        int minZ = cellCoord(boundingBox.minZ());
        int maxX = cellCoord(boundingBox.maxX());
        int maxZ = cellCoord(boundingBox.maxZ());

        List<Long> out = new ArrayList<>((maxX - minX + 1) * (maxZ - minZ + 1));

        for (int cx = minX; cx <= maxX; cx++) {
            for (int cz = minZ; cz <= maxZ; cz++) {
                out.add(pack(cx, cz));
            }
        }

        return out;
    }

    public static Iterable<ChunkPosition> touchedChunks(BoundingBox boundingBox) {
        int minChunkX = Math.floorDiv(boundingBox.minX(), CHUNK_SIZE);
        int minChunkZ = Math.floorDiv(boundingBox.minZ(), CHUNK_SIZE);
        int maxChunkX = Math.floorDiv(boundingBox.maxX(), CHUNK_SIZE);
        int maxChunkZ = Math.floorDiv(boundingBox.maxZ(), CHUNK_SIZE);

        List<ChunkPosition> out = new ArrayList<>((maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1));
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                out.add(new ChunkPosition(cx, cz));
            }
        }
        return out;
    }
}