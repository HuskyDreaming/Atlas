package com.huskydreaming.claims.model.claims;

import com.huskydreaming.claims.model.flags.ClaimFlag;
import com.huskydreaming.claims.model.flags.ClaimPermissions;
import com.huskydreaming.claims.model.positions.BlockPosition;
import com.huskydreaming.claims.model.positions.ChunkPosition;
import com.huskydreaming.claims.registry.ClaimFlagRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChunkClaimTest {

    private static ClaimFlag BUILD;
    private static ClaimFlag BREAK;
    private static ClaimFlag INTERACT;
    private static ClaimFlag CONTAINERS;
    private static ClaimFlag PVP;
    private static ClaimFlag EXPLOSIONS;
    private static ClaimFlag PROJECTILES;

    @BeforeAll
    static void registerFlags() {
        BUILD = ClaimFlagRegistry.register("build");
        BREAK = ClaimFlagRegistry.register("break");
        INTERACT = ClaimFlagRegistry.register("interact");
        CONTAINERS = ClaimFlagRegistry.register("containers");
        PVP = ClaimFlagRegistry.register("pvp");
        EXPLOSIONS = ClaimFlagRegistry.register("explosions");
        PROJECTILES = ClaimFlagRegistry.register("projectiles");
    }

    @Test
    void allowsReflectsPermissions() {
        UUID worldId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        ClaimPermissions permissions = new ClaimPermissions();
        permissions.allow(BUILD);
        permissions.allow(INTERACT);

        BlockPosition block = new BlockPosition(0, 0, 0);
        ChunkPosition chunkPosition = ChunkPosition.fromBlock(block);

        ChunkClaim claim = new ChunkClaim(
                worldId,
                ownerId,
                chunkPosition,
                permissions
        );

        assertTrue(claim.allows(BUILD));
        assertTrue(claim.allows(INTERACT));

        assertFalse(claim.allows(BREAK));
        assertFalse(claim.allows(CONTAINERS));
        assertFalse(claim.allows(PVP));
        assertFalse(claim.allows(EXPLOSIONS));
        assertFalse(claim.allows(PROJECTILES));
    }

    @Test
    void canOwnerAlwaysAllowedEvenIfNoPermissions() {
        UUID worldId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        ClaimPermissions permissions = new ClaimPermissions();

        BlockPosition block = new BlockPosition(5, 0, -2);
        ChunkPosition chunkPosition = ChunkPosition.fromBlock(block);

        ChunkClaim claim = new ChunkClaim(
                worldId,
                ownerId,
                chunkPosition,
                permissions
        );

        assertTrue(claim.can(ownerId, BUILD));
        assertTrue(claim.can(ownerId, BREAK));
        assertTrue(claim.can(ownerId, INTERACT));
        assertTrue(claim.can(ownerId, CONTAINERS));
        assertTrue(claim.can(ownerId, PVP));
        assertTrue(claim.can(ownerId, EXPLOSIONS));
        assertTrue(claim.can(ownerId, PROJECTILES));
    }

    @Test
    void canNonOwnerDependsOnPermissions() {
        UUID worldId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID nonOwnerId = UUID.randomUUID();

        ClaimPermissions permissions = new ClaimPermissions();
        permissions.allow(INTERACT);
        permissions.allow(CONTAINERS);

        BlockPosition block = new BlockPosition(0, 0, 0);
        ChunkPosition chunkPosition = ChunkPosition.fromBlock(block);

        ChunkClaim claim = new ChunkClaim(
                worldId,
                ownerId,
                chunkPosition,
                permissions
        );

        assertTrue(claim.can(nonOwnerId, INTERACT));
        assertTrue(claim.can(nonOwnerId, CONTAINERS));

        assertFalse(claim.can(nonOwnerId, BUILD));
        assertFalse(claim.can(nonOwnerId, BREAK));
        assertFalse(claim.can(nonOwnerId, PVP));
        assertFalse(claim.can(nonOwnerId, EXPLOSIONS));
        assertFalse(claim.can(nonOwnerId, PROJECTILES));
    }
}
