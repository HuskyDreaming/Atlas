package com.huskydreaming.claims.model.owners;

import java.util.UUID;

public record GroupOwner(UUID groupId) implements ClaimOwner {

    @Override
    public UUID uniqueId() {
        return groupId;
    }

    @Override
    public boolean isOwner(UUID groupId) {
        return this.groupId.equals(groupId);
    }
}