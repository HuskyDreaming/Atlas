package com.huskydreaming.claims.model.owners;

import java.util.UUID;

public sealed interface ClaimOwner permits GroupOwner, PlayerOwner {
    UUID uniqueId();

    //TODO: Replaced with player object?
    boolean isOwner(UUID ownerId);
}