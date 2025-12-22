package com.huskydreaming.claims.model.world;

import com.huskydreaming.claims.model.position.BlockPosition;
import com.huskydreaming.claims.model.position.ChunkPosition;

import java.util.UUID;

public record WorldChunk(UUID worldId, ChunkPosition chunkPosition) {

    public static WorldChunk fromBlock(UUID worldId, BlockPosition blockPosition) {
        return new WorldChunk(worldId, ChunkPosition.fromBlock(blockPosition));
    }
}