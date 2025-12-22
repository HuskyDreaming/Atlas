package com.huskydreaming.claims.model.owners;

import java.util.UUID;

public record PlayerOwner(UUID playerId) implements ClaimOwner {

    @Override
    public UUID uniqueId() {
        return playerId;
    }

    @Override
    public boolean isOwner(UUID playerId) {
        return this.playerId.equals(playerId);
    }
}