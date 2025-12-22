package com.huskydreaming.claims.model.claim;

import com.huskydreaming.claims.enumeration.ClaimFlag;
import com.huskydreaming.claims.model.owners.ClaimOwner;
import com.huskydreaming.claims.model.world.WorldChunk;

import java.util.UUID;

public record ChunkClaim(ClaimOwner claimOwner, WorldChunk worldChunk, int flagMask) {

    public boolean allows(ClaimFlag flag) {
        return (flagMask & flag.getBit()) != 0;
    }

    public boolean can(UUID ownerId, ClaimFlag flag) {
        if (claimOwner.isOwner(ownerId)) return true;
        return allows(flag);
    }
}